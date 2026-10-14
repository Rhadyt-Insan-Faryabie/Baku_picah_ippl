"""Bout agreement & scheduling routes."""
from fastapi import APIRouter

router = APIRouter()


@router.post("/")
def create_bout():
    """Submit a new bout agreement. (placeholder)"""
    return {"message": "create bout - not implemented yet"}


@router.get("/")
def list_bouts():
    """List bouts for the current fighter. (placeholder)"""
    return {"message": "list bouts - not implemented yet"}


@router.post("/{bout_id}/approve")
def approve_bout(bout_id: int):
    """Admin approves a bout. (placeholder)"""
    return {"message": f"approve bout {bout_id} - not implemented yet"}


@router.post("/{bout_id}/reject")
def reject_bout(bout_id: int):
    """Admin rejects a bout. (placeholder)"""
    return {"message": f"reject bout {bout_id} - not implemented yet"}
