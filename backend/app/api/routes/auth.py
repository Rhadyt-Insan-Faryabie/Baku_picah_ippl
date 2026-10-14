"""Authentication routes."""
from fastapi import APIRouter

router = APIRouter()


@router.post("/register")
def register():
    """Register a new fighter account. (placeholder)"""
    return {"message": "register endpoint - not implemented yet"}


@router.post("/login")
def login():
    """Authenticate and issue an access token. (placeholder)"""
    return {"message": "login endpoint - not implemented yet"}


@router.get("/me")
def me():
    """Return the current authenticated fighter profile. (placeholder)"""
    return {"message": "me endpoint - not implemented yet"}
