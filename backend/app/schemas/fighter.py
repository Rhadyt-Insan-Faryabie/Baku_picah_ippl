"""Pydantic schemas for fighters and authentication."""
from datetime import datetime

from pydantic import BaseModel, EmailStr, Field


class FighterBase(BaseModel):
    username: str = Field(min_length=3, max_length=50)
    email: EmailStr
    full_name: str | None = None
    city: str | None = None
    weight_kg: float | None = None
    height_cm: float | None = None
    martial_arts: list[str] | None = None


class FighterCreate(FighterBase):
    password: str = Field(min_length=6, max_length=128)


class FighterLogin(BaseModel):
    email: EmailStr
    password: str


class FighterUpdate(BaseModel):
    """Partial profile update. Only provided fields are changed."""

    full_name: str | None = None
    city: str | None = None
    weight_kg: float | None = None
    height_cm: float | None = None
    martial_arts: list[str] | None = None


class FighterOut(FighterBase):
    id: int
    role: str
    weight_class: str | None = None
    wins: int
    losses: int
    draws: int
    created_at: datetime

    model_config = {"from_attributes": True}


class FighterPublic(BaseModel):
    """Public view of a fighter, safe to expose to other fighters.

    Unlike FighterOut this omits the email address.
    """

    id: int
    username: str
    full_name: str | None = None
    city: str | None = None
    weight_kg: float | None = None
    height_cm: float | None = None
    martial_arts: list[str] | None = None
    weight_class: str | None = None
    wins: int
    losses: int
    draws: int
    created_at: datetime

    model_config = {"from_attributes": True}


class Token(BaseModel):
    access_token: str
    token_type: str = "bearer"
    fighter: FighterOut
