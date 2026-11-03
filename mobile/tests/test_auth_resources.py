"""Static checks for phase-one auth resources; no device/API execution."""
from pathlib import Path
import re
import unittest
import xml.etree.ElementTree as ET


MOBILE = Path(__file__).resolve().parents[1]
RES = MOBILE / "app/src/main/res"
ANDROID = "{http://schemas.android.com/apk/res/android}"
APP = "{http://schemas.android.com/apk/res-auto}"
SCREENS = ("welcome", "register", "forgot_password")


def controls(screen):
    return {node.get(ANDROID + "id").split("/")[-1]: node
            for node in ET.parse(RES / ("layout/activity_" + screen + ".xml")).iter()
            if node.get(ANDROID + "id")}


class AuthResourcesTest(unittest.TestCase):
    def test_local_resources_resolve(self):
        symbols = set()
        for folder in RES.iterdir():
            if not folder.is_dir():
                continue
            kind = folder.name.split("-")[0]
            for path in folder.iterdir():
                if kind == "values" and path.suffix == ".xml":
                    for node in ET.parse(path).getroot():
                        symbols.add((node.tag, node.get("name")))
                else:
                    symbols.add((kind, path.stem))
        files = [RES / ("layout/activity_" + name + ".xml") for name in SCREENS]
        for prefix in ("welcome", "register", "recovery"):
            files += list((RES / "values").glob(prefix + "_*.xml"))
            files += list((RES / "drawable").glob("bp_" + prefix + "_*.xml"))
            files += list((RES / "color").glob("bp_" + prefix + "_*.xml"))
        for path in files:
            for kind, name in re.findall(r"@(color|string|drawable|font|style)/([\w.]+)",
                                         path.read_text(encoding="utf-8")):
                if kind == "style" and not name.startswith(("TextAppearance.BP.", "Widget.BP.")):
                    continue
                with self.subTest(path=path.name, reference=name):
                    self.assertIn((kind, name), symbols)

    def test_registration_defaults_are_safe(self):
        nodes = controls("register")
        self.assertEqual(nodes["cbConsent"].get(ANDROID + "checked"), "false")
        self.assertEqual(nodes["cbConsent"].get(ANDROID + "minHeight"), "48dp")
        for name in ("etName", "etEmail", "etPassword", "etConfirmPassword"):
            self.assertIsNone(nodes[name].get(ANDROID + "text"))
            self.assertTrue(nodes[name].tag.endswith("TextInputEditText"))
        for name in ("etPassword", "etConfirmPassword"):
            self.assertEqual(nodes[name].get(ANDROID + "inputType"), "textPassword")
        self.assertEqual(nodes["etConfirmPassword"].get(ANDROID + "imeOptions"), "actionDone")

    def test_header_and_visibility_buttons_are_accessible(self):
        screens = {"register": ("btnBack", "btnRegisterMore", "btnTogglePassword", "btnToggleConfirmPassword"),
                   "forgot_password": ("btnRecoveryBack", "btnRecoveryMore")}
        for screen, names in screens.items():
            nodes = controls(screen)
            for name in names:
                with self.subTest(screen=screen, control=name):
                    self.assertEqual(nodes[name].get(ANDROID + "layout_width"), "48dp")
                    self.assertEqual(nodes[name].get(ANDROID + "layout_height"), "48dp")
                    self.assertIsNotNone(nodes[name].get(ANDROID + "contentDescription"))

    def test_recovery_status_is_announced(self):
        nodes = controls("forgot_password")
        self.assertEqual(nodes["tvRecoveryStatus"].get(ANDROID + "accessibilityLiveRegion"), "polite")
        self.assertIsNone(nodes["etEmail"].get(ANDROID + "text"))
        messages = {n.get("name"): n.text for n in
                    ET.parse(RES / "values/recovery_strings.xml").getroot()}
        self.assertIn("TIDAK dikirim", messages["recovery_unavailable"])
        self.assertNotIn("30 menit", messages["recovery_card_body"])

    def test_forms_resize_for_keyboard_and_can_scroll(self):
        manifest = ET.parse(MOBILE / "app/src/main/AndroidManifest.xml")
        activities = {n.get(ANDROID + "name"): n for n in manifest.iter("activity")}
        for screen, activity in (("register", "RegisterActivity"), ("forgot_password", "ForgotPasswordActivity")):
            self.assertEqual(activities[".ui.auth." + activity].get(ANDROID + "windowSoftInputMode"), "adjustResize")
            self.assertTrue(list(ET.parse(RES / ("layout/activity_" + screen + ".xml")).iter("ScrollView")))

    def test_line_spacing_scales_with_font_preferences(self):
        for screen in SCREENS:
            tree = ET.parse(RES / ("layout/activity_" + screen + ".xml"))
            for node in tree.iter():
                line_height = node.get(APP + "lineHeight")
                if line_height:
                    with self.subTest(screen=screen, line_height=line_height):
                        self.assertTrue(line_height.endswith("sp"))

    def test_form_headers_can_expand_for_large_fonts(self):
        for screen in ("register", "forgot_password"):
            root = ET.parse(RES / ("layout/activity_" + screen + ".xml")).getroot()
            header = root[0]
            self.assertEqual(header.get(ANDROID + "layout_height"), "wrap_content")
            self.assertEqual(header.get(ANDROID + "minHeight"), "72dp")

    def test_welcome_background_is_bundled_and_not_interactive(self):
        png = RES / "drawable-nodpi/bp_welcome_training.png"
        self.assertEqual(png.read_bytes()[:8], b"\x89PNG\r\n\x1a\n")
        tree = ET.parse(RES / "layout/activity_welcome.xml")
        portrait = next(n for n in tree.iter() if n.get(APP + "srcCompat") == "@drawable/bp_welcome_training")
        self.assertEqual(portrait.get(ANDROID + "importantForAccessibility"), "no")
        self.assertIn("btnStart", controls("welcome"))
        self.assertIn("btnToLogin", controls("welcome"))


if __name__ == "__main__":
    unittest.main()
