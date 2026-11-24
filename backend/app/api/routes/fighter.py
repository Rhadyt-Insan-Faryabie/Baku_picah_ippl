"""Fighter profile routes."""
from fastapi import APIRouter, Depends, HTTPException, Query, status
from sqlalchemy import select
from sqlalchemy.orm import Session

from app.api.deps import get_current_user
from app.core.database import get_db
from app.models.fighter import Fighter
from app.schemas.fighter import FighterOut, FighterPublic, FighterUpdate

router = APIRouter()


@router.get("/me", response_model=FighterOut)
def get_my_profile(current_user: Fighter = Depends(get_current_user)):
    """Return the current fighter's profile (FR-003)."""
    return current_user


@router.put("/me", response_model=FighterOut)
def update_my_profile(
    payload: FighterUpdate,
    current_user: Fighter = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    """Update the current fighter's profile fields (FR-003)."""
    data = payload.model_dump(exclude_unset=True)
    for field, value in data.items():
        setattr(current_user, field, value)

    db.add(current_user)
    db.commit()
    db.refresh(current_user)
    return current_user


@router.delete("/me", status_code=status.HTTP_200_OK)
def delete_my_profile(
    current_user: Fighter = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    """FR-004: Menghapus Data Diri Petarung."""
    db.delete(current_user)
    db.commit()
    return {"message": "Akun dan data diri petarung berhasil dihapus"}


@router.get("/list", response_model=list[FighterPublic])
def list_fighters(
    limit: int = Query(default=100, ge=1, le=200),
    current_user: Fighter = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    fighters = db.scalars(
        select(Fighter)
        .where(Fighter.role == "fighter")
        .order_by(Fighter.full_name, Fighter.username)
    ).all()
    return fighters


@router.get("/{fighter_id}", response_model=FighterPublic)
def get_fighter_detail(
    fighter_id: int,
    current_user: Fighter = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    """FR-006: Melihat Profil Lawan secara detail."""
    fighter = db.get(Fighter, fighter_id)
    if not fighter:
        raise HTTPException(404, "Petarung tidak ditemukan")
    return fighter
