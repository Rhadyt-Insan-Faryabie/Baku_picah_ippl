"""Authentication routes."""
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy import or_, select
from sqlalchemy.orm import Session

from app.api.deps import get_current_user
from app.core.database import get_db
from app.core.security import create_access_token, hash_password, verify_password
from app.models.fighter import Fighter
from app.schemas.fighter import FighterCreate, FighterLogin, FighterOut, Token

router = APIRouter()


@router.post("/register", response_model=Token, status_code=status.HTTP_201_CREATED)
def register(payload: FighterCreate, db: Session = Depends(get_db)):
    """Register a new fighter account and return an access token."""
    existing = db.scalar(
        select(Fighter).where(
            or_(Fighter.email == payload.email, Fighter.username == payload.username)
        )
    )
    if existing is not None:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="Email atau username sudah terdaftar",
        )

    fighter = Fighter(
        username=payload.username,
        email=payload.email,
        password_hash=hash_password(payload.password),
        full_name=payload.full_name,
        city=payload.city,
        weight_kg=payload.weight_kg,
        height_cm=payload.height_cm,
        martial_arts=payload.martial_arts,
    )
    db.add(fighter)
    db.commit()
    db.refresh(fighter)

    token = create_access_token(str(fighter.id))
    return Token(access_token=token, fighter=FighterOut.model_validate(fighter))


@router.post("/login", response_model=Token)
def login(payload: FighterLogin, db: Session = Depends(get_db)):
    """Authenticate a fighter and issue an access token."""
    fighter = db.scalar(select(Fighter).where(Fighter.email == payload.email))
    if fighter is None or not verify_password(payload.password, fighter.password_hash):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Email atau password salah",
        )

    token = create_access_token(str(fighter.id))
    return Token(access_token=token, fighter=FighterOut.model_validate(fighter))


@router.get("/me", response_model=FighterOut)
def me(current_user: Fighter = Depends(get_current_user)):
    """Return the current authenticated fighter profile."""
    return current_user
