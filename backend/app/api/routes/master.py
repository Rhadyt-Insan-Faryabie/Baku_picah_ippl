"""Master data routes for weight classes and neutral locations (FR-022, FR-023)."""
from typing import List
from fastapi import APIRouter
from pydantic import BaseModel

router = APIRouter(prefix="/master", tags=["Master Data"])


class WeightClassItem(BaseModel):
    id: int
    name: str
    maxKg: float


class LocationItem(BaseModel):
    id: int
    name: str
    city: str
    address: str
    is_neutral: bool = True


# In-memory store with full initial seeding
_WEIGHT_CLASSES: List[dict] = [
    {"id": 1, "name": "Strawweight", "maxKg": 52.2},
    {"id": 2, "name": "Flyweight", "maxKg": 56.7},
    {"id": 3, "name": "Bantamweight", "maxKg": 61.2},
    {"id": 4, "name": "Featherweight", "maxKg": 65.8},
    {"id": 5, "name": "Lightweight", "maxKg": 70.3},
    {"id": 6, "name": "Welterweight", "maxKg": 77.1},
    {"id": 7, "name": "Middleweight", "maxKg": 83.9},
    {"id": 8, "name": "Light Heavyweight", "maxKg": 93.0},
    {"id": 9, "name": "Heavyweight", "maxKg": 120.2},
]

_LOCATIONS: List[dict] = [
    {
        "id": 1,
        "name": "Baku Picah Neutral Octagon",
        "city": "Bandung",
        "address": "Jl. LLRE Martadinata No. 88, Citarum, Bandung",
        "is_neutral": True,
    },
    {
        "id": 2,
        "name": "Garuda Boxing & Combat Arena",
        "city": "Bandung",
        "address": "Jl. Asia Afrika No. 120, Bandung",
        "is_neutral": True,
    },
    {
        "id": 3,
        "name": "Tiger MMA Center",
        "city": "Jakarta",
        "address": "Jl. Senopati No. 45, Kebayoran Baru, Jakarta Selatan",
        "is_neutral": True,
    },
    {
        "id": 4,
        "name": "Surabaya Fight Dome",
        "city": "Surabaya",
        "address": "Jl. Mayjen Sungkono No. 100, Surabaya",
        "is_neutral": True,
    },
]


@router.get("/weight-classes", response_model=List[WeightClassItem])
def get_weight_classes():
    """List standard MMA weight classes (FR-022)."""
    return _WEIGHT_CLASSES


@router.post("/weight-classes", response_model=WeightClassItem)
def add_weight_class(item: WeightClassItem):
    """Add a new custom weight class (FR-022)."""
    new_item = item.model_dump()
    if not new_item.get("id"):
        new_item["id"] = len(_WEIGHT_CLASSES) + 1
    _WEIGHT_CLASSES.append(new_item)
    return new_item


@router.get("/locations", response_model=List[LocationItem])
def get_locations():
    """List sanctioned neutral arenas and venues (FR-023)."""
    return _LOCATIONS


@router.post("/locations", response_model=LocationItem)
def add_location(item: LocationItem):
    """Register a new neutral arena (FR-023)."""
    new_item = item.model_dump()
    if not new_item.get("id"):
        new_item["id"] = len(_LOCATIONS) + 1
    _LOCATIONS.append(new_item)
    return new_item
