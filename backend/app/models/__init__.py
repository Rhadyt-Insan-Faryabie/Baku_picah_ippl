"""ORM models. Imported here so Alembic autogenerate can discover them."""
from app.models.bout import Bout
from app.models.fighter import Fighter
from app.models.message import Message
from app.models.report import Report
from app.models.swipe import Swipe

__all__ = ["Bout", "Fighter", "Message", "Report", "Swipe"]
