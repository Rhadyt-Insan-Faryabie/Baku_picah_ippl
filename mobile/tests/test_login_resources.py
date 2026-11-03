"""Static resource checks; these do not replace Android build or device tests."""
from pathlib import Path
import re
import unittest
import xml.etree.ElementTree as ET


MOBILE = Path(__file__).resolve().parents[1]
RES = MOBILE / "app/src/main/res"
ANDROID = "{http://schemas.android.com/apk/res/android}"


class LoginResourcesTest(unittest.TestCase):
    def setUp(self):
        self.layout = ET.parse(RES / "layout/activity_login.xml").getroot()
        self.controls = {
            node.get(ANDROID + "id").split("/")[-1]: node
            for node in self.layout.iter()
            if node.get(ANDROID + "id")
        }

    def test_xml_is_well_formed(self):
        for path in RES.rglob("*.xml"):
            with self.subTest(path=path.name):
                ET.parse(path)
        ET.parse(MOBILE / "app/src/main/AndroidManifest.xml")

    def test_activity_controls_and_editable_credentials(self):
        for name in ("btnBack", "btnLoginMore", "btnTogglePassword", "etEmail",
                     "etPassword", "btnLogin", "btnGoogle", "tvForgot", "tvRegister"):
            self.assertIn(name, self.controls)
        for name in ("etEmail", "etPassword"):
            node = self.controls[name]
            self.assertTrue(node.tag.endswith("TextInputEditText"))
            self.assertIsNone(node.get(ANDROID + "text"), "Do not prefill credentials")
        self.assertEqual(self.controls["etPassword"].get(ANDROID + "inputType"),
                         "textPassword")

    def test_local_login_resource_references_resolve(self):
        symbols = set()
        for path in RES.iterdir():
            if not path.is_dir():
                continue
            kind = path.name.split("-")[0]
            for file in path.iterdir():
                if kind == "values" and file.suffix == ".xml":
                    for node in ET.parse(file).getroot():
                        symbols.add((node.tag, node.get("name")))
                else:
                    symbols.add((kind, file.stem))
        files = [RES / "layout/activity_login.xml"]
        files += list((RES / "values").glob("login_*.xml"))
        files += list((RES / "drawable").glob("bp_login_*.xml"))
        files += list((RES / "color").glob("bp_login_*.xml"))
        for path in files:
            for kind, name in re.findall(r"@(color|string|drawable|font|style)/([\w.]+)",
                                         path.read_text(encoding="utf-8")):
                if name.startswith(("bp_", "login_", "TextAppearance.BP.", "Widget.BP.")) or kind != "style":
                    with self.subTest(path=path.name, reference=name):
                        self.assertIn((kind, name), symbols)

    def test_fonts_are_bundled_true_type_with_licenses(self):
        for name in ("inter_regular", "inter_medium", "inter_semibold", "inter_bold",
                     "barlow_condensed_bold", "barlow_condensed_extrabold"):
            with self.subTest(font=name):
                data = (RES / "font" / (name + ".ttf")).read_bytes()
                self.assertEqual(data[:4], b"\x00\x01\x00\x00")
                self.assertGreater(len(data), 1000)
        for name in ("Inter-OFL.txt", "BarlowCondensed-OFL.txt"):
            self.assertIn("SIL OPEN FONT LICENSE",
                          (MOBILE / "design-reference" / name).read_text(encoding="utf-8"))

    def test_secondary_actions_have_touch_targets(self):
        for name in ("btnBack", "btnLoginMore", "btnTogglePassword"):
            self.assertEqual(self.controls[name].get(ANDROID + "layout_width"), "48dp")
            self.assertEqual(self.controls[name].get(ANDROID + "layout_height"), "48dp")
            self.assertIsNotNone(self.controls[name].get(ANDROID + "contentDescription"))
        for name in ("tvForgot", "tvRegister"):
            self.assertEqual(self.controls[name].get(ANDROID + "minHeight"), "48dp")


if __name__ == "__main__":
    unittest.main()
