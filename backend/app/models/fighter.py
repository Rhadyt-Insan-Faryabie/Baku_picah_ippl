"""Fighter ORM model."""
from datetime import datetime

from sqlalchemy import JSON, DateTime, Float, Integer, String, func
from sqlalchemy.orm import Mapped, mapped_column

from app.core.database import Base
from app.services.weight_class import weight_class_for


class Fighter(Base):
    __tablename__ = "fighters"

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    username: Mapped[str] = mapped_column(String(50), unique=True, index=True)
    email: Mapped[str] = mapped_column(String(120), unique=True, index=True)
    password_hash: Mapped[str] = mapped_column(String(255))
    role: Mapped[str] = mapped_column(String(20), default="fighter")

    full_name: Mapped[str | None] = mapped_column(String(120), nullable=True)
    city: Mapped[str | None] = mapped_column(String(100), nullable=True)
    weight_kg: Mapped[float | None] = mapped_column(Float, nullable=True)
    height_cm: Mapped[float | None] = mapped_column(Float, nullable=True)

    # Background martial arts (e.g. ["Boxing", "BJJ"]). Rules are always MMA.
    martial_arts: Mapped[list[str] | None] = mapped_column(JSON, nullable=True)

    wins: Mapped[int] = mapped_column(Integer, default=0)
    losses: Mapped[int] = mapped_column(Integer, default=0)
    draws: Mapped[int] = mapped_column(Integer, default=0)

    created_at: Mapped[datetime] = mapped_column(
        DateTime, server_default=func.now()
    )

    @property
    def weight_class(self) -> str | None:
        """MMA weight class derived from body weight."""
        return weight_class_for(self.weight_kg)

    @property
    def record(self) -> str:
        return f"{self.wins}-{self.losses}-{self.draws}"
