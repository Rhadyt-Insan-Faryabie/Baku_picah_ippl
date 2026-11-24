"""Bout ORM model."""
from datetime import datetime

from sqlalchemy import Boolean, DateTime, ForeignKey, Integer, String, Text, func
from sqlalchemy.orm import Mapped, mapped_column

from app.core.database import Base

PENDING = "pending"
ACCEPTED = "accepted"
AWAITING_ADMIN = "awaiting_admin"
APPROVED = "approved"
REJECTED = "rejected"
COMPLETED = "completed"
CANCELLED = "cancelled"


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
    venue_name: Mapped[str | None] = mapped_column(String(150), nullable=True)
    weight_class: Mapped[str | None] = mapped_column(String(40), nullable=True)
    status: Mapped[str] = mapped_column(String(40), default=PENDING, index=True)
    proposed_at: Mapped[datetime | None] = mapped_column(DateTime, nullable=True)
    scheduled_at: Mapped[datetime | None] = mapped_column(DateTime, nullable=True)
    notes: Mapped[str | None] = mapped_column(Text, nullable=True)

    # Escrow & Confirmation (FR-012, FR-013, FR-014)
    opponent_accepted: Mapped[bool] = mapped_column(Boolean, default=False)
    deposit_amount: Mapped[int] = mapped_column(Integer, default=150000)
    challenger_deposit_paid: Mapped[bool] = mapped_column(Boolean, default=False)
    opponent_deposit_paid: Mapped[bool] = mapped_column(Boolean, default=False)

    # Verification (FR-019)
    verified_by: Mapped[int | None] = mapped_column(
        ForeignKey("fighters.id", ondelete="SET NULL"), nullable=True
    )
    verified_at: Mapped[datetime | None] = mapped_column(DateTime, nullable=True)
    rejection_reason: Mapped[str | None] = mapped_column(Text, nullable=True)

    # Match Result & Rating (FR-007, FR-016, FR-017)
    winner_id: Mapped[int | None] = mapped_column(
        ForeignKey("fighters.id", ondelete="SET NULL"), nullable=True
    )
    result_method: Mapped[str | None] = mapped_column(String(50), nullable=True)
    result_round: Mapped[int | None] = mapped_column(Integer, nullable=True)
    result_notes: Mapped[str | None] = mapped_column(Text, nullable=True)
    rating_challenger: Mapped[int | None] = mapped_column(Integer, nullable=True)
    rating_opponent: Mapped[int | None] = mapped_column(Integer, nullable=True)

    created_at: Mapped[datetime] = mapped_column(DateTime, server_default=func.now())
    updated_at: Mapped[datetime] = mapped_column(
        DateTime, server_default=func.now(), onupdate=func.now()
    )
