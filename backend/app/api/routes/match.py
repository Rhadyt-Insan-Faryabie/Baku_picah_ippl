"""Matchmaking / discovery routes.

Implements opponent discovery with rule-based compatibility scoring plus
like/pass swipes. Two reciprocal likes form a match.
"""
from fastapi import APIRouter, Depends, HTTPException, Query, status
from sqlalchemy import select
from sqlalchemy.orm import Session

from app.api.deps import get_current_user
from app.core.database import get_db
from app.models.fighter import Fighter
from app.models.swipe import LIKE, PASS, Swipe
from app.schemas.match import MatchItem, OpponentCard, SwipeResult
from app.services.matchmaking import match_reasons, score_match

router = APIRouter()


def _recommendations_for(
    db: Session,
    current_user: Fighter,
    limit: int,
    same_weight_class_only: bool,
) -> list[OpponentCard]:
    """Score every eligible opponent and return the best `limit` of them."""
    already_swiped = select(Swipe.target_id).where(Swipe.actor_id == current_user.id)

    candidates = db.scalars(
        select(Fighter).where(
            Fighter.id != current_user.id,
            Fighter.role == "fighter",
            ~Fighter.id.in_(already_swiped),
        )
    ).all()

    scored: list[tuple[float, Fighter]] = []
    for candidate in candidates:
        if same_weight_class_only and current_user.weight_class != candidate.weight_class:
            continue
        scored.append((score_match(current_user, candidate), candidate))

    scored.sort(key=lambda pair: pair[0], reverse=True)

    return [
        OpponentCard(
            fighter=candidate,
            score=score,
            reasons=match_reasons(current_user, candidate),
        )
        for score, candidate in scored[:limit]
    ]


def _record_swipe(
    db: Session, current_user: Fighter, target_id: int, action: str
) -> tuple[Fighter, bool]:
    """Upsert a swipe and report whether it completes a mutual like."""
    if target_id == current_user.id:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Tidak bisa memilih dirimu sendiri",
        )

    target = db.get(Fighter, target_id)
    if target is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Petarung tidak ditemukan",
        )

    swipe = db.scalar(
        select(Swipe).where(
            Swipe.actor_id == current_user.id, Swipe.target_id == target_id
        )
    )
    if swipe is None:
        db.add(Swipe(actor_id=current_user.id, target_id=target_id, action=action))
    else:
        swipe.action = action
    db.commit()

    matched = False
    if action == LIKE:
        reciprocal = db.scalar(
            select(Swipe).where(
                Swipe.actor_id == target_id,
                Swipe.target_id == current_user.id,
                Swipe.action == LIKE,
            )
        )
        matched = reciprocal is not None

    return target, matched


@router.get("/recommendations", response_model=list[OpponentCard])
def recommendations(
    limit: int = Query(default=20, ge=1, le=100),
    same_weight_class_only: bool = False,
    current_user: Fighter = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    """Return scored opponent suggestions the current fighter hasn't swiped yet."""
    return _recommendations_for(
        db, current_user, limit=limit, same_weight_class_only=same_weight_class_only
    )


@router.post("/{target_id}/like", response_model=SwipeResult)
def like(
    target_id: int,
    current_user: Fighter = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    """Like an opponent. Reports a match if they already liked you."""
    target, matched = _record_swipe(db, current_user, target_id, LIKE)
    return SwipeResult(
        action=LIKE, target_id=target_id, matched=matched, opponent=target
    )


@router.post("/{target_id}/pass", response_model=SwipeResult)
def pass_profile(
    target_id: int,
    current_user: Fighter = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    """Pass on an opponent so they stop appearing in discovery."""
    target, _ = _record_swipe(db, current_user, target_id, PASS)
    return SwipeResult(
        action=PASS, target_id=target_id, matched=False, opponent=target
    )


@router.get("/matches", response_model=list[MatchItem])
def list_matches(
    current_user: Fighter = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    """List fighters who liked you back (mutual likes)."""
    liked_by_me = set(
        db.scalars(
            select(Swipe.target_id).where(
                Swipe.actor_id == current_user.id, Swipe.action == LIKE
            )
        ).all()
    )
    liked_me = set(
        db.scalars(
            select(Swipe.actor_id).where(
                Swipe.target_id == current_user.id, Swipe.action == LIKE
            )
        ).all()
    )

    mutual_ids = liked_by_me & liked_me
    if not mutual_ids:
        return []

    fighters = db.scalars(select(Fighter).where(Fighter.id.in_(mutual_ids))).all()
    items = [
        MatchItem(
            fighter=fighter,
            score=score_match(current_user, fighter),
            reasons=match_reasons(current_user, fighter),
        )
        for fighter in fighters
    ]
    items.sort(key=lambda item: item.score, reverse=True)
    return items
