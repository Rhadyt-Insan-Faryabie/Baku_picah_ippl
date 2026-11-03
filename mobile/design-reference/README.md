# Mobile authentication — Figma references

Source: [Fighter · Masuk, node 3:68](https://www.figma.com/design/DTkMS42Nu9QdSu28MIbSzh/baku-_picah_fogma?node-id=3-68).

Reference PNGs are exported directly from the Figma file:

| Screen | Figma node | Reference |
| --- | --- | --- |
| Login | `3:68` | `figma-login.png` (402 × 924) |
| Welcome | `3:30` | `figma-welcome.png` (402 × 900) |
| Register | `3:117` | `figma-register.png` (402 × 964) |
| Forgot password | `3:169` | `figma-forgot-password.png` (402 × 900) |

The Welcome training portrait is exported separately from node `3:32` at 2×, bundled as `bp_welcome_training.png`, and overlaid with native text and a gradient. No screen is implemented as a screenshot.

## Implementation

- Native Android Java/XML; existing login API and destinations retained.
- Bundled static Inter (400/500/600/700) and Barlow Condensed (700/800) fonts. No runtime font downloads. Licenses are included here.
- Figma values: 24px content margins, 20px section gaps, 52px controls, 6px corners. Converted to dp; text uses sp so user font-size preferences are respected.
- Auth screens reuse the Login-specific resources without changing the global theme of other screens.
- Status/navigation bars remain real Android system UI, not the decorative iOS elements in the frame.
- Content scrolls on shorter screens and when the keyboard opens. Smaller widths or increased font size may wrap text. Secondary actions have 48dp touch targets.
- Text heights are allowed to expand for Android font metrics rather than clipping to Figma's fixed boxes. This can introduce small vertical differences, especially in the two-line hero heading; tune against an actual device screenshot before calling the result pixel-perfect.
- Figma sample credentials are hints, not prefilled account data.
- Google sign-in retains the existing “not available” message; OAuth is not implemented by this UI change.
- Header overflow offers the existing forgot-password and registration destinations.

## Phase 1 behavior and integration boundaries

- Welcome → **Mulai perjalananmu** now opens registration instead of skipping directly to profile setup. **Masuk** opens Login. Existing authenticated-session routing is unchanged.
- Registration still uses `AuthApi.register`, saves the session, and opens profile setup on success. New registration validates name, email, password confirmation, and an explicit unchecked 18+/terms/privacy/fair-play declaration.
- Passwords on this registration screen require 8–128 characters with ASCII letters and digits to match the design guidance. The backend currently permits 6–128 characters; it was not changed. Existing accounts can still log in with their existing passwords.
- Consent is a client-side declaration, not verified age or server-recorded legal acceptance. Legal copy has no invented document URLs; actual policy pages and versioned server consent remain integration work.
- Password visibility, input state, and registration loading survive rotation. An in-flight request is retained without retaining the Activity. Back navigation is temporarily blocked during submission so a successful registration is not silently discarded while the server may be creating the account.
- Google registration/sign-in remains unavailable and says so explicitly.
- Password recovery has **no backend endpoint**. The old fake “email sent” toast was removed. Valid submission now stays on screen and displays a persistent accessible message saying the feature is unavailable and the email was **not sent**. No email request is sent.
- Recovery's subtitle discloses the unavailable delivery service up front. Its guidance intentionally omits Figma's unimplemented 30-minute expiry guarantee. Delivery requires backend/reset-token/email-service implementation, outside this UI phase.
- Splash, profile setup, discovery, desktop, and admin screens are not redesigned in this phase.

## Validation

From the repository root with Python available:

```sh
python -m unittest discover -s mobile/tests -v
```

These are static XML/resource checks, not an Android build or screenshot test.
For runtime verification, open `mobile` in Android Studio with JDK 17 and Android SDK 34 installed, sync Gradle, and run on an emulator/device.
Compare each screen at approximately 402dp width and default font scale against its reference. Check smaller screens, large font size, keyboard scrolling, password visibility, header actions, registration confirmation/consent, login/register loading/error/success, rotation during registration, and recovery's unavailable status. A pixel-perfect match is not verified until a runtime screenshot comparison has been performed.
