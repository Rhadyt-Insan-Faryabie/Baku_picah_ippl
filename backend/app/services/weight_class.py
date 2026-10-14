"""MMA weight class calculation.

The bout rules are always MMA, so every fighter's weight class is derived
from their body weight using standard MMA divisions.
"""
from __future__ import annotations

# (name, upper bound in kg). The last entry is the catch-all division.
MMA_WEIGHT_CLASSES: list[tuple[str, float | None]] = [
    ("Flyweight", 56.7),
    ("Bantamweight", 61.2),
    ("Featherweight", 65.8),
    ("Lightweight", 70.3),
    ("Welterweight", 77.1),
    ("Middleweight", 83.9),
    ("Light Heavyweight", 92.9),
    ("Heavyweight", None),
]

MMA_WEIGHT_CLASS_NAMES = [name for name, _ in MMA_WEIGHT_CLASSES]


def weight_class_for(weight_kg: float | None) -> str | None:
    """Return the MMA weight class name for a given body weight."""
    if weight_kg is None:
        return None
    for name, limit in MMA_WEIGHT_CLASSES:
        if limit is None or weight_kg <= limit:
            return name
    return "Heavyweight"
