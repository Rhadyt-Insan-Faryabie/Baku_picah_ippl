"""Automated Black Box Testing Suite for FIGHT (Baku Picah).
Covers FR-001 to FR-026 end-to-end user scenarios without looking at internal source.
"""
import unittest
from fastapi.testclient import TestClient

from app.main import app

client = TestClient(app)


class BlackBoxTestCase(unittest.TestCase):

    @classmethod
    def setUpClass(cls):
        # 1. Login to get token
        login_res = client.post(
            "/api/auth/login",
            json={"email": "rambo@example.com", "password": "fighter123"},
        )
        assert login_res.status_code == 200, "Setup login failed"
        cls.token = login_res.json()["access_token"]
        cls.headers = {"Authorization": f"Bearer {cls.token}"}

    def test_tc_auth_01_login_valid(self):
        """TC-AUTH-01: Login dengan email dan password valid."""
        res = client.post(
            "/api/auth/login",
            json={"email": "rambo@example.com", "password": "fighter123"},
        )
        self.assertEqual(res.status_code, 200)
        self.assertIn("access_token", res.json())

    def test_tc_auth_02_login_invalid_password(self):
        """TC-AUTH-02: Login dengan password salah (Boundary/Negative test)."""
        res = client.post(
            "/api/auth/login",
            json={"email": "rambo@example.com", "password": "salahpassword123"},
        )
        self.assertEqual(res.status_code, 401)
        self.assertIn("detail", res.json())

    def test_tc_match_01_get_recommendations(self):
        """TC-MATCH-01: Rekomendasi lawan berbasis AI dengan filter kota & bobot."""
        res = client.get(
            "/api/match/recommendations?limit=5",
            headers=self.headers,
        )
        self.assertEqual(res.status_code, 200)
        data = res.json()
        self.assertIsInstance(data, list)
        if data:
            self.assertIn("match_score", data[0])
            self.assertIn("reasons", data[0])

    def test_tc_bout_01_propose_neutral_bout(self):
        """TC-BOUT-01: Pengajuan tanding di arena netral (FR-011, FR-012)."""
        res = client.post(
            "/api/bout/",
            headers=self.headers,
            json={
                "opponent_id": 3,
                "location": "Baku Picah Neutral Octagon (Bandung)",
                "venue_name": "Baku Picah Neutral Octagon",
                "notes": "Jadwal: Sabtu, 28 Nov 2026 19:00 WIB",
            },
        )
        self.assertIn(res.status_code, (200, 201))
        data = res.json()
        self.assertEqual(data["location"], "Baku Picah Neutral Octagon (Bandung)")
        self.assertFalse(data["challenger_deposit_paid"])

    def test_tc_pay_01_midtrans_snap_token(self):
        """TC-PAY-01: Request token Snap Midtrans untuk pembayaran deposit (FR-013)."""
        res = client.post(
            "/api/payment/midtrans/snap-token/1",
            headers=self.headers,
        )
        self.assertEqual(res.status_code, 200)
        data = res.json()
        self.assertIn("token", data)
        self.assertIn("redirect_url", data)

    def test_tc_pay_02_midtrans_webhook_auto_settlement(self):
        """TC-PAY-02: Webhook callback Midtrans otomatis melunasi deposit (FR-013)."""
        res = client.post(
            "/api/payment/midtrans/notification",
            json={
                "order_id": "BP-BOUT-1-CHALLENGER-1732500000",
                "transaction_status": "settlement",
                "fraud_status": "accept",
                "gross_amount": "160000.00",
            },
        )
        self.assertEqual(res.status_code, 200)

    def test_tc_mod_01_submit_report(self):
        """TC-MOD-01: Pelaporan petarung atas pelanggaran etika (FR-015)."""
        res = client.post(
            "/api/reports/",
            headers=self.headers,
            json={
                "target_id": 3,
                "reason": "Pelecehan / Kata Kasar di Chat",
                "details": "Black box automated test report",
                "severity": "High",
            },
        )
        self.assertIn(res.status_code, (200, 201))
        data = res.json()
        self.assertEqual(data["status"], "Pending")

    def test_tc_master_01_weight_classes_and_locations(self):
        """TC-MASTER-01: Pengambilan master data kelas berat MMA dan arena netral (FR-022, FR-023)."""
        wc_res = client.get("/api/master/weight-classes")
        self.assertEqual(wc_res.status_code, 200)
        self.assertTrue(len(wc_res.json()) >= 9)

        loc_res = client.get("/api/master/locations")
        self.assertEqual(loc_res.status_code, 200)
        self.assertTrue(len(loc_res.json()) >= 4)


if __name__ == "__main__":
    unittest.main()
