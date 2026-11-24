"""1-on-1 chat routes (FR-010)."""
from datetime import datetime

from fastapi import APIRouter, Depends, HTTPException, Query, status
from sqlalchemy import or_, select
from sqlalchemy.orm import Session

from app.api.deps import get_current_user
from app.core.database import get_db
from app.models.fighter import Fighter
from app.models.message import Message
from app.schemas.fighter import FighterPublic
from app.schemas.message import ConversationOut, MessageCreate, MessageOut

router = APIRouter()


def _preload_message_users(db: Session, msg: Message) -> Message:
    msg.sender = db.get(Fighter, msg.sender_id)
    msg.receiver = db.get(Fighter, msg.receiver_id)
    return msg


@router.get("/conversations", response_model=list[ConversationOut])
def list_conversations(
    current_user: Fighter = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    """Mendapatkan daftar percakapan aktif dengan lawan tanding/partner."""
    # Cari semua pesan yang melibatkan user saat ini
    stmt = (
        select(Message)
        .where(or_(Message.sender_id == current_user.id, Message.receiver_id == current_user.id))
        .order_by(Message.id.desc())
    )
    all_msgs = db.scalars(stmt).all()

    # Kelompokkan berdasarkan partner_id
    conversations_map: dict[int, dict] = {}
    for m in all_msgs:
        partner_id = m.receiver_id if m.sender_id == current_user.id else m.sender_id
        if partner_id not in conversations_map:
            conversations_map[partner_id] = {
                "last_message": m,
                "unread_count": 0,
            }
        if m.receiver_id == current_user.id and not m.is_read:
            conversations_map[partner_id]["unread_count"] += 1

    result = []
    for partner_id, data in conversations_map.items():
        partner = db.get(Fighter, partner_id)
        if partner:
            last_msg = _preload_message_users(db, data["last_message"])
            result.append(
                ConversationOut(
                    partner_id=partner_id,
                    partner=FighterPublic.model_validate(partner),
                    last_message=MessageOut.model_validate(last_msg),
                    unread_count=data["unread_count"],
                )
            )
    return result


@router.get("/{partner_id}/messages", response_model=list[MessageOut])
def get_messages(
    partner_id: int,
    limit: int = Query(default=50, ge=1, le=100),
    current_user: Fighter = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    """Mengambil riwayat pesan antara pengguna saat ini dan lawan tanding."""
    partner = db.get(Fighter, partner_id)
    if not partner:
        raise HTTPException(404, "Pengguna tidak ditemukan")

    stmt = (
        select(Message)
        .where(
            or_(
                (Message.sender_id == current_user.id) & (Message.receiver_id == partner_id),
                (Message.sender_id == partner_id) & (Message.receiver_id == current_user.id),
            )
        )
        .order_by(Message.id.asc())
        .limit(limit)
    )
    messages = db.scalars(stmt).all()

    # Tandai pesan masuk sebagai sudah dibaca
    for m in messages:
        if m.receiver_id == current_user.id and not m.is_read:
            m.is_read = True
        _preload_message_users(db, m)
    db.commit()

    return messages


@router.post("/{partner_id}/messages", response_model=MessageOut, status_code=status.HTTP_201_CREATED)
def send_message(
    partner_id: int,
    payload: MessageCreate,
    current_user: Fighter = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    """Mengirim pesan chat 1-on-1 ke lawan tanding."""
    partner = db.get(Fighter, partner_id)
    if not partner:
        raise HTTPException(404, "Tujuan penerima pesan tidak ditemukan")

    msg = Message(
        sender_id=current_user.id,
        receiver_id=partner_id,
        bout_id=payload.bout_id,
        content=payload.content,
        is_read=False,
    )
    db.add(msg)
    db.commit()
    db.refresh(msg)

    return _preload_message_users(db, msg)
