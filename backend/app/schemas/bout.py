"""Pydantic schemas for bouts."""
from datetime import datetime

from pydantic import BaseModel, Field

from app.schemas.fighter import FighterPublic

BOUT_STATUS = ["pending", "approved", "rejected", "completed"]


class BoutCreate(BaseModel):
    opponent_id: int
    location: str | None = None
    notes: str | None = None


class BoutOut(BaseModel):
    id: int
    challenger_id: int
    opponent_id: int
    challenger: FighterPublic | None = None
    opponent: FighterPublic | None = None
    rules: str
    location: str | None = None
    weight_class: str | None = None
    status: str
    proposed_at: datetime | None = None
    notes: str | None = None
    verified_by: int | None = None
    verified_at: datetime | None = None
    rejection_reason: str | None = None
    created_at: datetime
    updated_at: datetime

    model_config = {"from_attributes": True}


class BoutAction(BaseModel):
    rejection_reason: str | None = Field(default=None, max_length=255)
