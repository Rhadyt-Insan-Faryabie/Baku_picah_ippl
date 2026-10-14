"""Bout ORM model."""
from datetime import datetime

from sqlalchemy import DateTime, ForeignKey, Integer, String, Text, func
from sqlalchemy.orm import Mapped, mapped_column

from app.core.database import Base

PENDING = "pending"
APPROVED = "approved"
REJECTED = "rejected"
COMPLETED = "completed"


class Bout(Base):
    __tablename__ = "bouts"

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    challenger_id: Mapped[int] = mapped_column(
        ForeignKey("fighters.id", ondelete="CASCADE"), index=True
    )
    opponent_id: Mapped[int] = mapped_column(
        ForeignKey("fighters.id", ondelete="CASCADE"), index=True
    )
    rules: Mapped[str] = mapped_column(String(20), default="MMA")
    location: Mapped[str | None] = mapped_column(String(200), nullable=True)
    weight_class: Mapped[str | None] = mapped_column(String(40), nullable=True)
    status: Mapped[str] = mapped_column(String(20), default=PENDING, index=True)
    proposed_at: Mapped[datetime | None] = mapped_column(DateTime, nullable=True)
    notes: Mapped[str | None] = mapped_column(Text, nullable=True)
    verified_by: Mapped[int | None] = mapped_column(
        ForeignKey("fighters.id", ondelete="SET NULL"), nullable=True
    )
    verified_at: Mapped[datetime | None] = mapped_column(DateTime, nullable=True)
    rejection_reason: Mapped[str | None] = mapped_column(Text, nullable=True)
    created_at: Mapped[datetime] = mapped_column(DateTime, server_default=func.now())
    updated_at: Mapped[datetime] = mapped_column(
        DateTime, server_default=func.now(), onupdate=func.now()
    )
