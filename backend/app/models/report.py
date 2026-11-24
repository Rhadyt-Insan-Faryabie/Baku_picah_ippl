"""Report ORM model for reporting bad behavior or rule violations (FR-015, FR-020, FR-021)."""
from datetime import datetime

from sqlalchemy import DateTime, ForeignKey, Integer, String, Text, func
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.core.database import Base


class Report(Base):
    __tablename__ = "reports"

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    reporter_id: Mapped[int] = mapped_column(
        ForeignKey("fighters.id", ondelete="CASCADE"), index=True
    )
    target_id: Mapped[int] = mapped_column(
        ForeignKey("fighters.id", ondelete="CASCADE"), index=True
    )
    reason: Mapped[str] = mapped_column(String(100), nullable=False)
    details: Mapped[str | None] = mapped_column(Text, nullable=True)
    severity: Mapped[str] = mapped_column(String(20), default="Medium")  # Low, Medium, High
    status: Mapped[str] = mapped_column(String(20), default="Pending")  # Pending, Resolved, Dismissed
    action_taken: Mapped[str | None] = mapped_column(String(50), nullable=True)  # warning, suspend, ban
    admin_note: Mapped[str | None] = mapped_column(Text, nullable=True)
    created_at: Mapped[datetime] = mapped_column(DateTime, server_default=func.now())
    resolved_at: Mapped[datetime | None] = mapped_column(DateTime, nullable=True)

    reporter = relationship("Fighter", foreign_keys=[reporter_id])
    target = relationship("Fighter", foreign_keys=[target_id])
