"""Unit tests for FIGHT (Baku Picah) backend services and endpoints."""
import unittest

from app.api.routes.master import get_weight_classes, get_locations
from app.services.matchmaking import score_match, match_reasons
from app.services.weight_class import weight_class_for


class BackendSmokeTestCase(unittest.TestCase):

    def test_master_weight_classes(self):
        classes = get_weight_classes()
        self.assertIsInstance(classes, list)
        self.assertTrue(any(c["name"] == "Lightweight" for c in classes))
        self.assertTrue(any(c["name"] == "Welterweight" for c in classes))

    def test_master_locations(self):
        locations = get_locations()
        self.assertIsInstance(locations, list)
        self.assertTrue(len(locations) >= 4)
        self.assertTrue(any("Bandung" in loc["city"] for loc in locations))

    def test_weight_class_mapping(self):
        self.assertEqual(weight_class_for(65.0), "Featherweight")
        self.assertEqual(weight_class_for(70.0), "Lightweight")
        self.assertEqual(weight_class_for(55.0), "Flyweight")
        self.assertEqual(weight_class_for(80.0), "Middleweight")
        self.assertEqual(weight_class_for(110.0), "Heavyweight")

    def test_matchmaking_scoring_range(self):
        fa = {
            "weight_kg": 66.0,
            "city": "Bandung",
            "martial_arts": ["Boxing", "BJJ"],
            "wins": 5,
            "losses": 1,
        }
        fb = {
            "weight_kg": 67.0,
            "city": "Bandung",
            "martial_arts": ["Boxing", "Muay Thai"],
            "wins": 5,
            "losses": 2,
        }
        score = score_match(fa, fb)
        reasons = match_reasons(fa, fb)

        self.assertTrue(0.0 <= score <= 1.0)
        self.assertIsInstance(reasons, list)
        self.assertTrue(len(reasons) > 0)


if __name__ == "__main__":
    unittest.main()
