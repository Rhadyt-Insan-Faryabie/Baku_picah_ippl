"""1-on-1 chat routes."""
from fastapi import APIRouter

router = APIRouter()


@router.get("/{conversation_id}/messages")
def list_messages(conversation_id: int):
    """List messages in a conversation. (placeholder)"""
    return {"message": f"messages for conversation {conversation_id} - not implemented yet"}


@router.post("/{conversation_id}/messages")
def send_message(conversation_id: int):
    """Send a message in a conversation. (placeholder)"""
    return {"message": f"send message to conversation {conversation_id} - not implemented yet"}
