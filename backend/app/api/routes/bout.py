"""Bout agreement & scheduling routes."""
from datetime import datetime

from fastapi import APIRouter, Depends, HTTPException, Query, status
from sqlalchemy import or_, select
from sqlalchemy.orm import Session

from app.api.deps import get_current_user
from app.core.database import get_db
from app.models.bout import APPROVED, PENDING, REJECTED, Bout
from app.models.fighter import Fighter
from app.schemas.bout import BoutAction, BoutCreate, BoutOut
from app.services.weight_class import weight_class_for

router = APIRouter()


def _get_fighter(db: Session, fighter_id: int) -> Fighter:
    fighter = db.get(Fighter, fighter_id)
    if fighter is None:
        raise HTTPException(status_code=404, detail="Petarung tidak ditemukan")
    return fighter


@router.post("/", response_model=BoutOut, status_code=status.HTTP_201_CREATED)
def create_bout(
    payload: BoutCreate,
    current_user: Fighter = Depends(get_current_user),
    db: Session = Depends(get_db),
):
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
        location=payload.location,
        weight_class=weight_class,
        status=PENDING,
        proposed_at=datetime.utcnow(),
        notes=payload.notes,
    )
    db.add(bout)
    db.commit()
    db.refresh(bout)

    # preload relations
    bout.challenger = current_user
    bout.opponent = opponent
    return bout


@router.get("/", response_model=list[BoutOut])
def list_bouts(
    status_filter: str | None = Query(default=None, alias="status"),
    current_user: Fighter = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    stmt = select(Bout).where(
        or_(Bout.challenger_id == current_user.id, Bout.opponent_id == current_user.id)
    )
    if status_filter in {PENDING, APPROVED, REJECTED, "completed"}:
        stmt = stmt.where(Bout.status == status_filter)
    bouts = db.scalars(stmt.order_by(Bout.id.desc())).all()

    # preload fighters
    for bout in bouts:
        bout.challenger = db.get(Fighter, bout.challenger_id)
        bout.opponent = db.get(Fighter, bout.opponent_id)
    return bouts


@router.post("/{bout_id}/approve", response_model=BoutOut)
def approve_bout(
    bout_id: int,
    current_user: Fighter = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    if current_user.role != "admin":
        raise HTTPException(status_code=403, detail="Hanya admin yang bisa memverifikasi bout")

    bout = db.get(Bout, bout_id)
    if bout is None:
        raise HTTPException(404, "Bout tidak ditemukan")
    if bout.status != PENDING:
        raise HTTPException(400, "Hanya bout berstatus pending yang bisa disetujui")

    bout.status = APPROVED
    bout.verified_by = current_user.id
    bout.verified_at = datetime.utcnow()
    bout.rejection_reason = None
    db.commit()
    db.refresh(bout)
    bout.challenger = db.get(Fighter, bout.challenger_id)
    bout.opponent = db.get(Fighter, bout.opponent_id)
    return bout


@router.post("/{bout_id}/reject", response_model=BoutOut)
def reject_bout(
    bout_id: int,
    payload: BoutAction,
    current_user: Fighter = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    if current_user.role != "admin":
        raise HTTPException(403, "Hanya admin yang bisa memverifikasi bout")

    bout = db.get(Bout, bout_id)
    if bout is None:
        raise HTTPException(404, "Bout tidak ditemukan")
    if bout.status != PENDING:
        raise HTTPException(400, "Hanya bout berstatus pending yang bisa ditolak")

    bout.status = REJECTED
    bout.verified_by = current_user.id
    bout.verified_at = datetime.utcnow()
    bout.rejection_reason = payload.rejection_reason
    db.commit()
    db.refresh(bout)
    bout.challenger = db.get(Fighter, bout.challenger_id)
    bout.opponent = db.get(Fighter, bout.opponent_id)
    return bout
