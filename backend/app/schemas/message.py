"""Pydantic schemas for 1-on-1 chat."""
from datetime import datetime
from pydantic import BaseModel, Field

from app.schemas.fighter import FighterPublic


class MessageCreate(BaseModel):
    receiver_id: int
    bout_id: int | None = None
    content: str = Field(..., min_length=1, max_length=2000)


class MessageOut(BaseModel):
    id: int
    sender_id: int
    receiver_id: int
    bout_id: int | None = None
    content: str
    is_read: bool = False
    created_at: datetime
    sender: FighterPublic | None = None
    receiver: FighterPublic | None = None

    model_config = {"from_attributes": True}


class ConversationOut(BaseModel):
    partner_id: int
    partner: FighterPublic | None = None
    last_message: MessageOut | None = None
    unread_count: int = 0
