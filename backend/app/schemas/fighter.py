"""Pydantic schemas for fighters (placeholder)."""
from pydantic import BaseModel


class FighterBase(BaseModel):
    username: str
    email: str
    discipline: str | None = None


class FighterOut(FighterBase):
    id: int

    model_config = {"from_attributes": True}
