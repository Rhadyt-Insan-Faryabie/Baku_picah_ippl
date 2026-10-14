"""Swipe ORM model.

A swipe records that one fighter liked or passed another. Two reciprocal
"like" swipes form a match.
"""
from datetime import datetime

from sqlalchemy import DateTime, ForeignKey, Integer, String, UniqueConstraint, func
from sqlalchemy.orm import Mapped, mapped_column

from app.core.database import Base

LIKE = "like"
PASS = "pass"


class Swipe(Base):
    __tablename__ = "swipes"
    __table_args__ = (
        UniqueConstraint("actor_id", "target_id", name="uq_swipe_actor_target"),
    )

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    actor_id: Mapped[int] = mapped_column(
        ForeignKey("fighters.id", ondelete="CASCADE"), index=True
    )
    target_id: Mapped[int] = mapped_column(
        ForeignKey("fighters.id", ondelete="CASCADE"), index=True
    )
    # "like" or "pass"
    action: Mapped[str] = mapped_column(String(10))
    created_at: Mapped[datetime] = mapped_column(
        DateTime, server_default=func.now()
    )
