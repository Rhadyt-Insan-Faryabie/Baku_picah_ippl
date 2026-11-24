"""Bout agreement & scheduling routes."""
from datetime import datetime

from fastapi import APIRouter, Depends, HTTPException, Query, status
from sqlalchemy import or_, select
from sqlalchemy.orm import Session

from app.api.deps import get_current_user
from app.core.database import get_db
from app.models.bout import (
    ACCEPTED,
    APPROVED,
    AWAITING_ADMIN,
    CANCELLED,
    COMPLETED,
    PENDING,
    REJECTED,
    Bout,
)
from app.models.fighter import Fighter
from app.schemas.bout import (
    BoutAccept,
    BoutAction,
    BoutCreate,
    BoutDeposit,
    BoutOut,
    BoutResultSubmit,
)
from app.services.weight_class import weight_class_for

router = APIRouter()


def _get_fighter(db: Session, fighter_id: int) -> Fighter:
    fighter = db.get(Fighter, fighter_id)
    if fighter is None:
        raise HTTPException(status_code=404, detail="Petarung tidak ditemukan")
    return fighter


def _preload_fighters(db: Session, bout: Bout) -> Bout:
    bout.challenger = db.get(Fighter, bout.challenger_id)
    bout.opponent = db.get(Fighter, bout.opponent_id)
    return bout


@router.post("/", response_model=BoutOut, status_code=status.HTTP_201_CREATED)
def create_bout(
    payload: BoutCreate,
    current_user: Fighter = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    """FR-011: Mengajukan Pertandingan / Tantangan."""
    if payload.opponent_id == current_user.id:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Tidak bisa mengajukan bout ke diri sendiri",
        )

    opponent = _get_fighter(db, payload.opponent_id)
    weight_class = weight_class_for(current_user.weight_kg) or weight_class_for(opponent.weight_kg)

    bout = Bout(
        challenger_id=current_user.id,
        opponent_id=opponent.id,
        location=payload.location or current_user.city or opponent.city,
        venue_name=payload.venue_name,
        weight_class=weight_class,
        status=PENDING,
        proposed_at=datetime.utcnow(),
        scheduled_at=payload.scheduled_at,
        notes=payload.notes,
        deposit_amount=150000,
        challenger_deposit_paid=False,
        opponent_deposit_paid=False,
    )
    db.add(bout)
    db.commit()
    db.refresh(bout)

    return _preload_fighters(db, bout)


@router.get("/", response_model=list[BoutOut])
def list_bouts(
    status_filter: str | None = Query(default=None, alias="status"),
    current_user: Fighter = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    """FR-016 & FR-018: Melihat daftar riwayat/jadwal pertandingan."""
    if current_user.role == "admin":
        stmt = select(Bout)
    else:
        stmt = select(Bout).where(
            or_(Bout.challenger_id == current_user.id, Bout.opponent_id == current_user.id)
        )

    if status_filter:
        stmt = stmt.where(Bout.status == status_filter)

    bouts = db.scalars(stmt.order_by(Bout.id.desc())).all()
    for b in bouts:
        _preload_fighters(db, b)
    return bouts


@router.get("/{bout_id}", response_model=BoutOut)
def get_bout(
    bout_id: int,
    current_user: Fighter = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    """Mendapatkan detail pertandingan."""
    bout = db.get(Bout, bout_id)
    if bout is None:
        raise HTTPException(404, "Bout tidak ditemukan")
    if current_user.role != "admin" and current_user.id not in (bout.challenger_id, bout.opponent_id):
        raise HTTPException(403, "Akses ditolak")
    return _preload_fighters(db, bout)


@router.post("/{bout_id}/accept", response_model=BoutOut)
def accept_bout(
    bout_id: int,
    payload: BoutAccept,
    current_user: Fighter = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    """FR-012 & FR-014: Lawan menyetujui tantangan & jadwal pertandingan."""
    bout = db.get(Bout, bout_id)
    if bout is None:
        raise HTTPException(404, "Bout tidak ditemukan")
    if current_user.id != bout.opponent_id:
        raise HTTPException(403, "Hanya lawan yang ditantang yang bisa menyetujui bout")
    if bout.status != PENDING:
        raise HTTPException(400, "Bout tidak dalam status pending proposal")

    bout.opponent_accepted = True
    bout.status = ACCEPTED
    if payload.scheduled_at:
        bout.scheduled_at = payload.scheduled_at
    if payload.notes:
        bout.notes = f"{bout.notes or ''}\nLawan: {payload.notes}".strip()

    db.commit()
    db.refresh(bout)
    return _preload_fighters(db, bout)


@router.post("/{bout_id}/deposit", response_model=BoutOut)
def pay_deposit(
    bout_id: int,
    payload: BoutDeposit,
    current_user: Fighter = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    """FR-013: Melakukan Deposit Escrow Pertandingan."""
    bout = db.get(Bout, bout_id)
    if bout is None:
        raise HTTPException(404, "Bout tidak ditemukan")
    if current_user.id not in (bout.challenger_id, bout.opponent_id):
        raise HTTPException(403, "Bukan peserta dalam bout ini")

    if current_user.id == bout.challenger_id:
        bout.challenger_deposit_paid = True
    elif current_user.id == bout.opponent_id:
        bout.opponent_deposit_paid = True

    # Jika kedua belah pihak sudah melunasi deposit (atau opponent sudah accept)
    if bout.challenger_deposit_paid and bout.opponent_deposit_paid:
        bout.status = AWAITING_ADMIN
    elif bout.status == PENDING and bout.challenger_deposit_paid:
        # Penantang sudah bayar jaminan
        pass

    db.commit()
    db.refresh(bout)
    return _preload_fighters(db, bout)


@router.post("/{bout_id}/approve", response_model=BoutOut)
def approve_bout(
    bout_id: int,
    current_user: Fighter = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    """FR-019: Admin memverifikasi dan menyetujui pertandingan."""
    if current_user.role != "admin":
        raise HTTPException(status_code=403, detail="Hanya admin yang bisa memverifikasi bout")

    bout = db.get(Bout, bout_id)
    if bout is None:
        raise HTTPException(404, "Bout tidak ditemukan")

    bout.status = APPROVED
    bout.verified_by = current_user.id
    bout.verified_at = datetime.utcnow()
    bout.rejection_reason = None
    db.commit()
    db.refresh(bout)
    return _preload_fighters(db, bout)


@router.post("/{bout_id}/reject", response_model=BoutOut)
def reject_bout(
    bout_id: int,
    payload: BoutAction,
    current_user: Fighter = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    """FR-019 & FR-014: Batalkan / tolak pertandingan (Admin atau Peserta)."""
    bout = db.get(Bout, bout_id)
    if bout is None:
        raise HTTPException(404, "Bout tidak ditemukan")

    is_admin = current_user.role == "admin"
    is_participant = current_user.id in (bout.challenger_id, bout.opponent_id)

    if not is_admin and not is_participant:
        raise HTTPException(403, "Tidak berhak membatalkan bout ini")

    bout.status = REJECTED if is_admin else CANCELLED
    if is_admin:
        bout.verified_by = current_user.id
        bout.verified_at = datetime.utcnow()
    bout.rejection_reason = payload.rejection_reason or "Dibatalkan oleh pengguna"
    db.commit()
    db.refresh(bout)
    return _preload_fighters(db, bout)


@router.post("/{bout_id}/result", response_model=BoutOut)
def submit_bout_result(
    bout_id: int,
    payload: BoutResultSubmit,
    current_user: Fighter = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    """FR-007 & FR-017: Menentukan Hasil Pertandingan & Memberikan Rating."""
    bout = db.get(Bout, bout_id)
    if bout is None:
        raise HTTPException(404, "Bout tidak ditemukan")

    is_admin = current_user.role == "admin"
    is_participant = current_user.id in (bout.challenger_id, bout.opponent_id)
    if not is_admin and not is_participant:
        raise HTTPException(403, "Tidak berhak mengirimkan hasil pertandingan")

    bout.winner_id = payload.winner_id
    bout.result_method = payload.result_method
    bout.result_round = payload.result_round
    bout.result_notes = payload.result_notes
    bout.status = COMPLETED

    # Simpan rating sportivitas jika ada
    if payload.sportsmanship_rating:
        if current_user.id == bout.challenger_id:
            bout.rating_opponent = payload.sportsmanship_rating
        elif current_user.id == bout.opponent_id:
            bout.rating_challenger = payload.sportsmanship_rating

    # Perbarui rekor kemenangan/kekalahan petarung jika baru pertama kali di-set
    challenger = db.get(Fighter, bout.challenger_id)
    opponent = db.get(Fighter, bout.opponent_id)
    if challenger and opponent:
        if payload.winner_id == challenger.id:
            challenger.wins += 1
            opponent.losses += 1
        elif payload.winner_id == opponent.id:
            opponent.wins += 1
            challenger.losses += 1
        elif payload.winner_id is None:
            challenger.draws += 1
            opponent.draws += 1

    db.commit()
    db.refresh(bout)
    return _preload_fighters(db, bout)
