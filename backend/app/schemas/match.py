"""Pydantic schemas for matchmaking (discovery, swipes, matches)."""
from pydantic import BaseModel

from app.schemas.fighter import FighterPublic


class OpponentCard(BaseModel):
    """A suggested opponent with its compatibility score and reasons."""

    fighter: FighterPublic
    score: float
    reasons: list[str] = []


class SwipeResult(BaseModel):
    """Outcome of a like/pass action."""

    action: str
    target_id: int
    matched: bool
    opponent: FighterPublic | None = None


class MatchItem(BaseModel):
    """A mutual like between the current fighter and another."""

    fighter: FighterPublic
    score: float
    reasons: list[str] = []
