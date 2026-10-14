"""Fighter profile routes."""
from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.api.deps import get_current_user
from app.core.database import get_db
from app.models.fighter import Fighter
from app.schemas.fighter import FighterOut, FighterUpdate

router = APIRouter()


@router.get("/me", response_model=FighterOut)
def get_my_profile(current_user: Fighter = Depends(get_current_user)):
    """Return the current fighter's profile."""
    return current_user


@router.put("/me", response_model=FighterOut)
def update_my_profile(
    payload: FighterUpdate,
    current_user: Fighter = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    """Update the current fighter's profile fields."""
    data = payload.model_dump(exclude_unset=True)
    for field, value in data.items():
        setattr(current_user, field, value)

    db.add(current_user)
    db.commit()
    db.refresh(current_user)
    return current_user
