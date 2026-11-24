"""Routes for reporting fighters and admin moderation (FR-015, FR-020, FR-021)."""
from datetime import datetime
from typing import List

from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy import desc, select
from sqlalchemy.orm import Session

from app.api.deps import get_current_user
from app.core.database import get_db
from app.models.fighter import Fighter
from app.models.report import Report
from app.schemas.report import ReportCreate, ReportOut, SanctionApply

router = APIRouter(prefix="/reports", tags=["Reports & Moderation"])


def _to_report_out(r: Report, db: Session) -> ReportOut:
    reporter = db.get(Fighter, r.reporter_id)
    target = db.get(Fighter, r.target_id)
    return ReportOut(
        id=r.id,
        reporter_id=r.reporter_id,
        target_id=r.target_id,
        reporter_name=reporter.full_name if reporter else f"Fighter #{r.reporter_id}",
        target_name=target.full_name if target else f"Fighter #{r.target_id}",
        reason=r.reason,
        details=r.details,
        severity=r.severity,
        status=r.status,
        action_taken=r.action_taken,
        admin_note=r.admin_note,
        created_at=r.created_at,
        resolved_at=r.resolved_at,
    )


@router.post("/", response_model=ReportOut, status_code=status.HTTP_201_CREATED)
def create_report(
    payload: ReportCreate,
    current_user: Fighter = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    """Fighter reports another fighter for unsportsmanlike behavior, harassment, or no-show (FR-015)."""
    if current_user.id == payload.target_id:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Tidak dapat melaporkan diri sendiri",
        )

    target = db.get(Fighter, payload.target_id)
    if not target:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Petarung terlapor tidak ditemukan",
        )

    report = Report(
        reporter_id=current_user.id,
        target_id=payload.target_id,
        reason=payload.reason,
        details=payload.details,
        severity=payload.severity or "Medium",
        status="Pending",
    )
    db.add(report)
    db.commit()
    db.refresh(report)
    return _to_report_out(report, db)


@router.get("/", response_model=List[ReportOut])
def list_reports(
    status_filter: str | None = None,
    current_user: Fighter = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    """Admin views all submitted reports (FR-020)."""
    stmt = select(Report).order_by(desc(Report.created_at))
    if status_filter:
        stmt = stmt.where(Report.status == status_filter)

    reports = db.execute(stmt).scalars().all()
    return [_to_report_out(r, db) for r in reports]


@router.post("/{report_id}/sanction", response_model=ReportOut)
def apply_sanction(
    report_id: int,
    payload: SanctionApply,
    current_user: Fighter = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    """Admin applies sanction (warning, suspend, ban) to reported fighter (FR-021)."""
    report = db.get(Report, report_id)
    if not report:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Laporan tidak ditemukan",
        )

    report.action_taken = payload.action
    report.admin_note = payload.note
    report.status = "Resolved"
    report.resolved_at = datetime.utcnow()

    db.commit()
    db.refresh(report)
    return _to_report_out(report, db)
