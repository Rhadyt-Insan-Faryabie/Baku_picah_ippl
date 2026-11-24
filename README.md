# FIGHT — Fighter Matchmaking & Bout Scheduling System

**FIGHT** (brand **BAKU PICAH**) merupakan platform digital terintegrasi yang dirancang untuk menghubungkan para petarung dalam menyepakati pertandingan tanding (*bout*) **MMA** yang seimbang dan aman melalui algoritma pencocokan berbobot (*weighted matchmaking* — machine learning terencana di roadmap).

Platform ini mengintegrasikan aplikasi mobile berbasis **Android Native (Java)** khusus untuk para petarung (user), serta **Web Dashboard Control Room (Vue 3)** khusus untuk administrator pengelola operasional pertandingan, verifikasi laga, dan rekening bersama (escrow).

---

## Daftar Isi

- [Fitur Utama](#fitur-utama)
- [Tech Stack & Arsitektur](#tech-stack--arsitektur)
- [Struktur Project](#struktur-project)
- [Menjalankan Secara Lokal](#menjalankan-secara-lokal)
- [Status Fitur terhadap Kebutuhan Fungsional](#status-fitur-terhadap-kebutuhan-fungsional)
- [Panduan Integrasi Midtrans & Deployment](#panduan-integrasi-midtrans--deployment)

---

## Fitur Utama (100% Live & Terintegrasi)

### 1. Aplikasi Mobile Petarung (`mobile/` - Android Java)
- **Autentikasi & Setup Identitas Petarung** — Registrasi akun, login (JWT), kelola profil fisik (bobot, tinggi, **kelas berat MMA otomatis**), kota domisili, dan disiplin bela diri (Boxing, Muay Thai, BJJ, Silat, dll).
- **Discovery & Matchmaking (AI)** — Rekomendasi lawan dengan **skor kompatibilitas** dan **alasan kecocokan** (kelas berat, kota, kesamaan bela diri, kedekatan rekor), tombol Like/Pass.
- **Match Inbox & Chat 1-on-1** — Obrolan privat antar petarung secara real-time.
- **Ajukan Laga di Titik Tengah (Auto Neutral Arena)** — Rekomendasi otomatis sasana netral & aturan resmi keselamatan MMA 3x2 menit.
- **Pembayaran Deposit Otomatis (Midtrans QRIS Escrow)** — 1-klik debit otomatis jaminan tanding Rp 160.000 ke rekening bersama.
- **Hasil Laga & Rating Sportivitas** — Pengiriman hasil tanding (menang/kalah/seri) dan penilaian sportivitas bintang 1-5.
- **Pelaporan Pelanggaran** — Opsi laporkan obrolan/petarung ke tim komisi etik.

### 2. Web Dashboard Admin (`web-admin/` - Vue 3 Vite)
- **Login Admin** — Autentikasi guard role admin (`admin@bakupicah.id`).
- **Verifikasi Laga (Bout Verification)** — Menyetujui/menolak ajuan laga sparring antar petarung.
- **Buku Kas Escrow (Deposit & Escrow Ledger)** — Memantau saldo deposit yang diamankan di rekening penampung bersama.
- **Moderasi & Sanksi Pelanggaran** — Menindaklanjuti laporan petarung dengan sanksi resmi (*Peringatan, Suspend 7 Hari, Ban Permanen*).
- **Master Data Manajemen** — Kelola 9 kelas berat standar MMA dan master sasana/arena netral resmi.

---

## Tech Stack & Arsitektur

| Lapisan | Teknologi | Keterangan |
|---|---|---|
| Mobile App | Android Native (Java) | Aplikasi resmi petarung / user (`mobile/`) |
| Web Dashboard | Vue 3 (Vite) | Control Room back-office admin (`web-admin/`) |
| Backend API | Python (FastAPI) | RESTful API, dokumentasi Swagger di `/docs` |
| Payment Gateway | Midtrans (Snap & QRIS Escrow) | Debit otomatis rekening bersama (Escrow) |
| Matchmaking Core | Python (rule-based weighted scoring) | Heuristik kompatibilitas bobot 0.5, kota 0.2, bela diri 0.2, pengalaman 0.1 |
| Database | MySQL (3NF Normalized) | Tabel `fighters`, `swipes`, `bouts`, `messages`, `reports` |
| Autentikasi | JWT (python-jose) + bcrypt | Token Bearer dengan otorisasi role petarung & admin |
| Desain Antarmuka | Figma | UI/UX Dark-Athletic & Design System |

---

## Struktur Project

```text
Baku_picah_ippl/
│
├── mobile/                     # Aplikasi Android Native (Java) untuk Petarung
│   ├── app/
│   │   ├── src/main/java/      # Activity, fragment, client API, session manager
│   │   └── src/main/res/       # Layout XML, drawable, design system
│   └── build.gradle
│
├── backend/                    # Core REST API (Python FastAPI) & Database
│   ├── app/
│   │   ├── api/routes/         # Endpoint router (auth, fighter, match, bout, chat, payment, report, master)
│   │   ├── core/               # Konfigurasi, database pool & security token
│   │   ├── models/             # ORM models (Bout, Fighter, Message, Report, Swipe)
│   │   └── services/           # Logika bisnis & matchmaking AI
│   ├── alembic/                # Migrasi database MySQL
│   ├── database/seeders/       # Seeder awal akun testing
│   ├── tests/                  # Unit & Black Box Automated Tests
│   ├── requirements.txt        # Dependensi pustaka Python
│   └── .env.example
│
├── web-admin/                  # Panel Control Room Admin (Vue 3 + Vite)
│   ├── src/                    # Views, components, router
│   ├── vercel.json             # Konfigurasi deploy cloud Vercel
│   ├── netlify.toml            # Konfigurasi deploy cloud Netlify
│   └── package.json
│
├── CATATAN_PENGUJIAN_DAN_DEMO.md # Dokumen panduan demo & tabel Black Box testing
└── README.md
```

---

## Menjalankan Secara Lokal

### 1. Backend (FastAPI) — port 8000
```bash
cd backend
python -m venv .venv
.venv\Scripts\activate          # Windows
pip install -r requirements.txt
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```
API aktif di `http://localhost:8000` — dokumentasi Swagger di `http://localhost:8000/docs`.

### 2. Web Dashboard Admin (`web-admin/`) — port 5173
```bash
cd web-admin
npm install
npm run dev
```
Buka di browser: `http://localhost:5173`.  
Akun Login Admin: `admin@bakupicah.id` / `admin123`.

### 3. Aplikasi Android Petarung (`mobile/`)
Buka folder `mobile/` di Android Studio lalu jalankan di emulator Pixel atau perangkat fisik.  
Akun Login Petarung: `rambo@example.com` / `fighter123`.

---

## Status Fitur terhadap Kebutuhan Fungsional

Bagian ini memetakan kebutuhan fungsional **FR-001 s.d. FR-026** ke status implementasi.

Keterangan status:
- **Selesai** — berfungsi end-to-end melalui antarmuka pengguna dan API.
- **Sebagian** — sudah terimplementasi sebagian (mis. baru back-end, atau baru salah satu filter).
- **Belum** — belum dikerjakan.

| Kode | Kebutuhan Fungsional | Status | Capaian | Keterangan |
|---|---|---|---|---|
| FR-001 | Registrasi Akun | Selesai | 100% | `POST /api/auth/register` + halaman registrasi aplikasi petarung |
| FR-002 | Login Akun | Selesai | 100% | `POST /api/auth/login` (JWT) + login petarung & admin |
| FR-003 | Menambahkan Data Diri Petarung | Selesai | 100% | `PUT /api/fighters/me` + form setup profil lengkap (bobot, tinggi, kota, bela diri, rekor) |
| FR-004 | Menghapus Data Diri Petarung | Selesai | 100% | `DELETE /api/fighters/me` + konfirmasi hapus data diri |
| FR-005 | Mencari Lawan | Selesai | 100% | Feed rekomendasi lawan + filter kelas tanding & filter kota/lokasi |
| FR-006 | Melihat Profil Lawan | Selesai | 100% | `GET /api/fighters/{id}` + halaman detail lengkap profil, rekor, dan alasan kecocokan AI |
| FR-007 | Menentukan Hasil Pertandingan | Selesai | 100% | `POST /api/bout/{id}/result` + form kirim hasil laga (pemenang, metode, ronde, skor W/L/D otomatis) |
| FR-008 | Like/Pass Lawan | Selesai | 100% | `POST /api/match/{id}/like` dan `/pass` + tombol di Discover |
| FR-009 | Melihat Match | Selesai | 100% | `GET /api/match/matches` + halaman Matches & Inbox |
| FR-010 | Chat dengan Lawan | Selesai | 100% | `GET/POST /api/chat/{id}/messages` + model tabel pesan + UI Chat 1-on-1 realtime bubble |
| FR-011 | Mengajukan Pertandingan | Selesai | 100% | `POST /api/bout` + tombol tantang laga & propose bout bottom sheet |
| FR-012 | Mengatur Jadwal Pertandingan | Selesai | 100% | Kesepakatan tanggal, jam, dan venue tanding (`scheduled_at` + `POST /api/bout/{id}/accept`) |
| FR-013 | Melakukan Deposit Pertandingan | Selesai | 100% | `POST /api/bout/{id}/deposit` + alur checkout deposit escrow petarung & pencatatan admin |
| FR-014 | Konfirmasi Pertandingan | Selesai | 100% | Validasi dua arah (opponent accept, deposit kedua pihak lunas, status `awaiting_admin`) |
| FR-015 | Melaporkan Petarung | Selesai | 100% | Formulir & dialog pelaporan/blokir petarung di chat terhubung ke `POST /api/reports/` |
| FR-016 | Melihat Riwayat Pertandingan | Selesai | 100% | `GET /api/bout/` + tab riwayat laga selesai di aplikasi petarung |
| FR-017 | Memberikan Rating & Review | Selesai | 100% | Rating sportivitas lawan pasca-tanding (skala bintang 1-5) terintegrasi pada submit result |
| FR-018 | Mengelola Data Pertandingan (Admin) | Selesai | 100% | Panel `BoutVerification.vue` admin terhubung ke `GET /api/bout/` |
| FR-019 | Memverifikasi Pertandingan (Admin) | Selesai | 100% | `POST /api/bout/{id}/approve` & `reject` terhubung langsung dari panel admin |
| FR-020 | Mengelola Laporan | Selesai | 100% | Panel `Moderation.vue` admin terhubung ke `GET /api/reports/` untuk daftar pelanggaran |
| FR-021 | Menindak Pelanggaran | Selesai | 100% | `POST /api/reports/{id}/sanction` terhubung ke modal sanksi (warning, suspend, ban) di admin |
| FR-022 | Mengelola Data Kelas Pertandingan | Selesai | 100% | Master data kelas berat MMA terhubung ke `GET/POST /api/master/weight-classes` di admin |
| FR-023 | Mengelola Data Lokasi | Selesai | 100% | Master data arena netral titik tengah terhubung ke `GET/POST /api/master/locations` di admin |
| FR-024 | Menganalisis Profil Petarung (AI) | Selesai | 100% | Mesin skoring berbobot menganalisis kelas berat, bela diri, kota, dan pengalaman |
| FR-025 | Mencocokkan Petarung (AI) | Selesai | 100% | Komputasi match score berjalan (berbasis aturan heuristik berbobot) |
| FR-026 | Memberikan Rekomendasi Lawan (AI) | Selesai | 100% | `GET /api/match/recommendations` + tampilan Discover |
| **TOTAL** | **26 Kebutuhan Fungsional** | **26 Selesai, 0 Sebagian, 0 Belum** | **100%** | **Rata-rata capaian = 26,0 dari 26 FR (100% Selesai)** |

### Ringkasan Progres

| Status | Jumlah FR | Rentang Capaian |
|---|---:|---|
| Selesai | 26 | 100% |
| Sebagian | 0 | 0% |
| Belum | 0 | 0% |
| **Total** | **26** | **Progres keseluruhan: 100%** |

Progres keseluruhan: **100%** — seluruh 26 Kebutuhan Fungsional (FR-001 s.d. FR-026) telah selesai diimplementasikan secara end-to-end melingkupi Back-end FastAPI, Web Admin Control Room Vue 3, dan Aplikasi Mobile Android Java.

---

## Panduan Integrasi Midtrans & Deployment

### 1. Midtrans Payment Gateway
Sistem menggunakan **Midtrans Snap & QRIS Auto-Debit**:
- **Endpoint Token Snap:** `POST /api/payment/midtrans/snap-token/{bout_id}`
- **Endpoint Webhook:** `POST /api/payment/midtrans/notification`
- **Konfigurasi Sandbox (.env):**
  ```env
  MIDTRANS_SERVER_KEY=SB-Mid-server-test-baku-picah
  MIDTRANS_CLIENT_KEY=SB-Mid-client-test-baku-picah
  MIDTRANS_IS_PRODUCTION=false
  ```
- Di aplikasi mobile, tombol *"Bayar Otomatis Sekarang (Midtrans QRIS)"* langsung mengeksekusi debit escrow otomatis dalam 1 klik.

---

### 2. Deployment Web Admin (Vercel / Netlify)
Berkas konfigurasi SPA routing telah tersedia (`vercel.json` dan `netlify.toml`).
- **Via Vercel:**
  ```bash
  cd web-admin
  npm install -g vercel
  vercel
  ```
- **Via Netlify:**
  Drag-and-drop folder `web-admin/dist` langsung ke dashboard Netlify.

---

### 3. Deployment Backend API (Railway / Render / Docker)
Telah disediakan `Dockerfile` dan `Procfile` di direktori `backend/`.
- **Railway:** Hubungkan repository GitHub, Railway otomatis mendeteksi Dockerfile dan menjalankan Uvicorn di port 8000.
- **Environment Variables di Cloud:**
  - `DATABASE_URL=mysql+pymysql://<user>:<pass>@<host>:<port>/<dbname>`
  - `MIDTRANS_SERVER_KEY=<key_anda>`

---

### 4. Build APK Android untuk HP Fisik
Untuk menginstal aplikasi langsung di HP Android tanpa emulator:
1. Di Android Studio: Menu **Build** > **Build Bundle(s) / APK(s)** > **Build APK(s)**.
2. File output `.apk` tersimpan di:
   `mobile/app/build/outputs/apk/debug/app-debug.apk`
3. Kirim file `app-debug.apk` ke HP melalui WhatsApp / kabel data / Google Drive dan install langsung.
