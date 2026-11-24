"""Pydantic schemas for reports (FR-015, FR-020, FR-021)."""
from datetime import datetime
from pydantic import BaseModel


class ReportCreate(BaseModel):
    target_id: int
    reason: str
    details: str | None = None
    severity: str = "Medium"


class SanctionApply(BaseModel):
    action: str  # warning, suspend, ban
    note: str | None = None


class ReportOut(BaseModel):
    id: int
    reporter_id: int
    target_id: int
    reporter_name: str | None = None
    target_name: str | None = None
    reason: str
    details: str | None = None
    severity: str
    status: str
    action_taken: str | None = None
    admin_note: str | None = None
    created_at: datetime
    resolved_at: datetime | None = None

    class Config:
        from_attributes = True
