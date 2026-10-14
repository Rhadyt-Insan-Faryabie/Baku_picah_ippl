"""Matchmaking compatibility scoring.

The score blends four signals into a 0.0-1.0 value:

- weight class agreement (a fair bout needs a comparable division)
- same city (easier for both fighters to meet)
- martial-arts background overlap (jaccard similarity)
- experience proximity (closer win/loss totals)

Weights are module constants so the heuristic can later be swapped for a
scikit-learn based model without touching the callers.
"""
from __future__ import annotations

from typing import Any

from app.services.weight_class import weight_class_for

WEIGHT_CLASS_WEIGHT = 0.5
CITY_WEIGHT = 0.2
MARTIAL_ARTS_WEIGHT = 0.2
EXPERIENCE_WEIGHT = 0.1


def _get(fighter: Any, key: str, default: Any = None) -> Any:
    """Read an attribute from a dict or an ORM object."""
    if isinstance(fighter, dict):
        return fighter.get(key, default)
    return getattr(fighter, key, default)


def _weight_class_of(fighter: Any) -> str | None:
    weight_class = _get(fighter, "weight_class")
    if weight_class:
        return weight_class
    return weight_class_for(_get(fighter, "weight_kg"))


def _martial_arts_of(fighter: Any) -> set[str]:
    arts = _get(fighter, "martial_arts") or []
    return {str(art).strip().lower() for art in arts if str(art).strip()}


def _experience_of(fighter: Any) -> int:
    return int(_get(fighter, "wins", 0) or 0) + int(_get(fighter, "losses", 0) or 0)


def match_reasons(fighter_a: Any, fighter_b: Any) -> list[str]:
    """Human-readable explanations behind a compatibility score."""
    reasons: list[str] = []

    weight_class_a = _weight_class_of(fighter_a)
    if weight_class_a and weight_class_a == _weight_class_of(fighter_b):
        reasons.append(f"Sama kelas {weight_class_a}")

    city_a = _get(fighter_a, "city")
    if city_a and city_a == _get(fighter_b, "city"):
        reasons.append(f"Satu kota ({city_a})")

    shared = _martial_arts_of(fighter_a) & _martial_arts_of(fighter_b)
    if shared:
        display = [
            art
            for art in (_get(fighter_b, "martial_arts") or [])
            if str(art).strip().lower() in shared
        ]
        reasons.append("Kesamaan: " + ", ".join(display))

    experience_a = _experience_of(fighter_a)
    experience_b = _experience_of(fighter_b)
    if experience_a and experience_b and experience_a == experience_b:
        reasons.append("Pengalaman seimbang")

    return reasons


def score_match(fighter_a: Any, fighter_b: Any) -> float:
    """Return a compatibility score between 0.0 and 1.0."""
    score = 0.0

    weight_class_a = _weight_class_of(fighter_a)
    if weight_class_a and weight_class_a == _weight_class_of(fighter_b):
        score += WEIGHT_CLASS_WEIGHT

    city_a = _get(fighter_a, "city")
    if city_a and city_a == _get(fighter_b, "city"):
        score += CITY_WEIGHT

    arts_a = _martial_arts_of(fighter_a)
    arts_b = _martial_arts_of(fighter_b)
    union = arts_a | arts_b
    if union:
        score += MARTIAL_ARTS_WEIGHT * (len(arts_a & arts_b) / len(union))

    experience_a = _experience_of(fighter_a)
    experience_b = _experience_of(fighter_b)
    denominator = max(experience_a, experience_b, 1)
    score += EXPERIENCE_WEIGHT * (1 - abs(experience_a - experience_b) / denominator)

    return round(min(score, 1.0), 4)
