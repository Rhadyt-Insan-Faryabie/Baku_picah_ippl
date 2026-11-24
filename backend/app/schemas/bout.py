"""Pydantic schemas for bouts."""
from datetime import datetime

from pydantic import BaseModel, Field

from app.schemas.fighter import FighterPublic

BOUT_STATUS = [
    "pending",
    "accepted",
    "awaiting_admin",
    "approved",
    "rejected",
    "completed",
    "cancelled",
]


class BoutCreate(BaseModel):
    opponent_id: int
    location: str | None = None
    venue_name: str | None = None
    scheduled_at: datetime | None = None
    notes: str | None = None


class BoutAccept(BaseModel):
    scheduled_at: datetime | None = None
    notes: str | None = None


class BoutDeposit(BaseModel):
    amount: int | None = 150000
    payment_method: str | None = "Bank Transfer"
    proof_url: str | None = None


class BoutResultSubmit(BaseModel):
    winner_id: int | None = None
    result_method: str = Field(..., description="KO/TKO, Submission, Decision, etc.")
    result_round: int | None = Field(default=None, ge=1, le=5)
    result_notes: str | None = None
    sportsmanship_rating: int | None = Field(default=None, ge=1, le=5)


class BoutOut(BaseModel):
    id: int
    challenger_id: int
    opponent_id: int
    challenger: FighterPublic | None = None
    opponent: FighterPublic | None = None
    rules: str
    location: str | None = None
    venue_name: str | None = None
    weight_class: str | None = None
    status: str
    proposed_at: datetime | None = None
    scheduled_at: datetime | None = None
    notes: str | None = None

    # Escrow & Confirmation
    opponent_accepted: bool = False
    deposit_amount: int = 150000
    challenger_deposit_paid: bool = False
    opponent_deposit_paid: bool = False

    # Verification
    verified_by: int | None = None
    verified_at: datetime | None = None
    rejection_reason: str | None = None

    # Result & Rating
    winner_id: int | None = None
    result_method: str | None = None
    result_round: int | None = None
    result_notes: str | None = None
    rating_challenger: int | None = None
    rating_opponent: int | None = None

    created_at: datetime
    updated_at: datetime

    model_config = {"from_attributes": True}


class BoutAction(BaseModel):
    rejection_reason: str | None = Field(default=None, max_length=255)
