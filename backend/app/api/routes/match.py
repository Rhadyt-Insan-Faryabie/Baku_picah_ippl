"""Matchmaking / discovery routes."""
from fastapi import APIRouter

router = APIRouter()


@router.get("/recommendations")
def recommendations():
    """Return AI-scored opponent recommendations. (placeholder)"""
    return {"message": "recommendations endpoint - not implemented yet"}


@router.post("/{target_id}/like")
def like(target_id: int):
    """Like an opponent profile. (placeholder)"""
    return {"message": f"like {target_id} - not implemented yet"}


@router.post("/{target_id}/pass")
def pass_profile(target_id: int):
    """Pass an opponent profile. (placeholder)"""
    return {"message": f"pass {target_id} - not implemented yet"}
