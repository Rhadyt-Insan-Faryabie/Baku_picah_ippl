"""Seed the database with an initial admin account and sample fighters.

Usage (from the backend directory):

    python database/seeders/seed.py

Safe to run multiple times: existing records are left untouched.
"""
import sys
from pathlib import Path

# Make the backend package root importable when run as a plain script.
sys.path.append(str(Path(__file__).resolve().parents[2]))

from sqlalchemy import select  # noqa: E402

from app.core.database import SessionLocal  # noqa: E402
from app.core.security import hash_password  # noqa: E402
from app.models.fighter import Fighter  # noqa: E402

SEED_FIGHTERS = [
    {
        "username": "admin",
        "email": "admin@bakupicah.id",
        "password": "admin123",
        "role": "admin",
        "full_name": "Administrator",
    },
    {
        "username": "rambo",
        "email": "rambo@example.com",
        "password": "fighter123",
        "role": "fighter",
        "full_name": "Rambo Sitorus",
        "city": "Bandung",
        "weight_kg": 66.5,
        "height_cm": 172,
        "martial_arts": ["Boxing", "BJJ"],
        "wins": 8,
        "losses": 2,
        "draws": 1,
    },
    {
        "username": "dedi",
        "email": "dedi@example.com",
        "password": "fighter123",
        "role": "fighter",
        "full_name": 'Dedi "Si Petir"',
        "city": "Jakarta",
        "weight_kg": 67.0,
        "height_cm": 174,
        "martial_arts": ["Muay Thai", "Wrestling"],
        "wins": 7,
        "losses": 3,
        "draws": 0,
    },
]


def seed() -> None:
    db = SessionLocal()
    try:
        created = 0
        for entry in SEED_FIGHTERS:
            data = {**entry}
            exists = db.scalar(
                select(Fighter).where(Fighter.email == data["email"])
            )
            if exists is not None:
                continue

            password = data.pop("password")
            fighter = Fighter(**data, password_hash=hash_password(password))
            db.add(fighter)
            created += 1

        db.commit()
        print(f"Seeding complete. {created} account(s) created.")
    finally:
        db.close()


if __name__ == "__main__":
    seed()
