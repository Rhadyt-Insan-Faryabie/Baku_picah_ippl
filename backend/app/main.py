"""FIGHT Platform - Backend API entry point."""
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.api.routes import auth, bout, chat, fighter, master, match, payment, report
from app.core.database import Base, engine

# Ensure tables are created
try:
    Base.metadata.create_all(bind=engine)
except Exception:
    pass

app = FastAPI(
    title="FIGHT API",
    description="Fighter Matchmaking & Bout Scheduling System",
    version="0.1.0",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=False,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(auth.router, prefix="/api/auth", tags=["auth"])
app.include_router(fighter.router, prefix="/api/fighters", tags=["fighters"])
app.include_router(match.router, prefix="/api/match", tags=["match"])
app.include_router(bout.router, prefix="/api/bout", tags=["bout"])
app.include_router(chat.router, prefix="/api/chat", tags=["chat"])
app.include_router(report.router, prefix="/api/reports", tags=["reports"])
app.include_router(master.router, prefix="/api/master", tags=["master"])
app.include_router(payment.router, prefix="/api/payment", tags=["payment"])


@app.get("/health", tags=["health"])
def health_check():
    """Simple liveness probe."""
    return {"status": "ok", "service": "fight-api"}
