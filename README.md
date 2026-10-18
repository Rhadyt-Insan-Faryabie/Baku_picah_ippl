# FIGHT — Fighter Matchmaking & Bout Scheduling System

**FIGHT** (brand **BAKU PICAH**) merupakan platform digital terintegrasi yang dirancang untuk menghubungkan para petarung dalam menyepakati pertandingan tanding (*bout*) **MMA** yang seimbang dan aman melalui algoritma pencocokan berbobot (*weighted matchmaking* — machine learning terencana di roadmap).

Platform ini mengintegrasikan aplikasi mobile berbasis **Android Native (Java)** dan aplikasi desktop/web (**Vue 3 + Electron**) untuk para petarung, serta **Web Dashboard** untuk administrator pengelola operasional pertandingan.

---

## Daftar Isi

- [Fitur Utama](#fitur-utama)
- [Tech Stack & Arsitektur](#tech-stack--arsitektur)
- [Struktur Project](#struktur-project)
- [Menjalankan Secara Lokal](#menjalankan-secara-lokal)
- [Status Pengembangan & Roadmap](#status-pengembangan--roadmap)

---

## Fitur Utama

Status: ✅ **Live** = berfungsi end-to-end · 🚧 **Dalam pengembangan** = prototipe UI / belum tersambung API

### 📱 Aplikasi Petarung (Android + Desktop)

- **Autentikasi & Setup Identitas Petarung** — ✅ *Live* di kedua aplikasi: registrasi akun, login (JWT), kelola profil fisik (berat badan, tinggi badan, **kelas berat otomatis**), **kota**, dan **background bela diri** (Boxing, Muay Thai, BJJ, Pencak Silat, Wrestling, dll). **Aturan tanding selalu MMA.**
  - 🚧 Input riwayat tanding (Win/Loss/Draw) dan unggah foto profil
- **Discovery & Matchmaking** — ✅ *Live* di aplikasi desktop (`baku-picah`): rekomendasi lawan dengan **skor kompatibilitas** dan **alasan kecocokan** (kelas berat, kota, kesamaan bela diri, kedekatan pengalaman), Like/Pass tersambung ke API.
  - 🚧 Gesture kartu swipe dan penyempurnaan feed di aplikasi Android
- **Match Inbox** — ✅ *Live* di desktop: daftar lawan yang saling suka. 🚧 di Android
- **Match Inbox & Chat 1-on-1** — 🚧 Ruang komunikasi privat antar petarung (struktur endpoint tersedia, implementasi penyimpanan pesan belum)
- **Pengajuan & Manajemen Pertandingan** — 🚧 Form kesepakatan jadwal tanding, **lokasi tanding bebas (jalanan / tempat acak)**, sistem komitmen deposit, dan pelacak status verifikasi admin (endpoint pengajuan bout di backend sudah tersedia)
- **Rekam Jejak & Sportivitas** — 🚧 Laporan hasil pertandingan, log riwayat laga tervalidasi, serta penilaian rating ulasan sportivitas lawan pasca-tanding

### 💻 Web Dashboard Admin

- **Login Admin** — ✅ *Live*: autentikasi dengan validasi role `admin`
- **Verifikasi Pertandingan** — 🚧 Halaman peninjauan dan persetujuan (*Approve/Reject*) jadwal laga; endpoint approve/reject ber-guard role admin sudah ada di backend, sambungan API dari dashboard belum
- **Kelola Master Data** — 🚧 Manajemen data kategori kelas tanding (*weight classes*) standar MMA
- **Pusat Moderasi & Keamanan** — 🚧 Pengawasan laporan pelanggaran etika tanding serta penindakan sanksi (peringatan hingga penangguhan akun)
- **Arbitrase Hasil** — 🚧 Penyelesaian sengketa hasil laga (fitur tambahan di luar spesifikasi awal)

---

## Tech Stack & Arsitektur

| Lapisan | Teknologi | Deskripsi |
|---|---|---|
| **Mobile Client** | Java (Android SDK) | Aplikasi native Android untuk Petarung (fase awal: auth & profil) |
| **Desktop Fighter App** | Vue 3 + Electron | Aplikasi petarung desktop/web (`baku-picah/`) — matchmaking, match inbox, profil |
| **Web Dashboard** | Vue 3 (Vite) | Dashboard admin dan operasional (`web-admin/`) |
| **Backend API** | Python (FastAPI) | RESTful API & Service Layer, dokumentasi otomatis di `/docs` |
| **Matchmaking Core** | Python (rule-based weighted scoring) | Heuristik kompatibilitas: kelas berat **0.5**, satu kota **0.2**, kemiripan bela diri **0.2** (Jaccard), kedekatan pengalaman **0.1** — machine learning (scikit-learn) terencana di roadmap |
| **Database** | MySQL (3NF Normalized) | Penyimpanan data relasional terstruktur (tabel `fighters`, `swipes`, `bouts`) |
| **Autentikasi** | JWT (python-jose) + bcrypt | Token Bearer dengan guard role admin |
| **Desain Antarmuka** | Figma | UI/UX Dark-Athletic & Design System |
| **Alat Bantu Dev** | VS Code, Android Studio, DBeaver | Lingkungan pengembangan & database management |

---

## Struktur Project

```text
Baku_picah_ippl/
│
├── mobile/                     # Aplikasi Android Native (Java)
│   ├── app/
│   │   ├── src/main/java/      # Activity, fragment, client API
│   │   └── src/main/res/       # Layout XML, drawable, values
│   └── build.gradle
│
├── backend/                    # Core REST API (Python FastAPI)
│   ├── app/
│   │   ├── api/routes/         # Endpoint router (auth, fighters, match, bout, chat)
│   │   ├── core/               # Konfigurasi & security token
│   │   ├── models/             # Skema model ORM database
│   │   └── services/           # Logika bisnis & algoritma weighted matchmaking
│   ├── alembic/                # Migrasi database
│   ├── database/seeders/       # Seeder awal (admin + petarung contoh)
│   ├── requirements.txt        # Daftar pustaka dependensi Python
│   └── .env.example
│
├── web-admin/                  # Panel admin back-office (Vue 3 + Vite)
│   ├── src/
│   └── package.json
│
├── baku-picah/                 # Aplikasi petarung desktop/web (Vue 3 + Electron)
│   ├── src/
│   └── package.json
│
└── README.md
```

---

## Menjalankan Secara Lokal

### 1. Backend (FastAPI) — port 8000

```bash
cd backend
python -m venv .venv
.venv\Scripts\activate          # Windows; macOS/Linux: source .venv/bin/activate
pip install -r requirements.txt
copy .env.example .env          # lalu isi DB_PASSWORD MySQL kamu
alembic upgrade head            # buat tabel via migrasi
python database/seeders/seed.py # data awal: admin + 6 petarung contoh
uvicorn app.main:app --reload
```

API berjalan di `http://localhost:8000` — dokumentasi Swagger di `http://localhost:8000/docs`.

Akun hasil seeder:
- Admin: `admin@bakupicah.id` / `admin123`
- Petarung contoh: `rambo@example.com` dkk. — password `fighter123`

### 2. Aplikasi Petarung Desktop (`baku-picah/`) — port 5174

```bash
cd baku-picah
npm install
npm run dev                 # mode web di http://localhost:5174
npm run electron:dev        # mode desktop (butuh `npm run dev` berjalan di terminal lain)
npm run electron:build      # build .exe Windows
```

### 3. Web Dashboard Admin (`web-admin/`) — port 5173

```bash
cd web-admin
npm install
npm run dev                 # http://localhost:5173
```

### 4. Aplikasi Android (`mobile/`)

Buka folder `mobile/` di Android Studio lalu jalankan di emulator/perangkat. Aplikasi menghubungi backend di `http://10.0.2.2:8000/api` (alamat loopback host untuk emulator).

---

## Status Pengembangan & Roadmap

Proyek berada di **fase awal pengembangan**. Fitur yang berfungsi end-to-end saat ini: autentikasi JWT, profil petarung dengan kelas berat otomatis, discovery/matchmaking dengan skor kompatibilitas, match inbox, dan endpoint persetujuan laga untuk admin.

Prioritas pengembangan berikutnya:

1. **Chat 1-on-1** — tabel `conversations`/`messages` + delivery real-time
2. **Pengajuan bout lengkap** — jadwal tanggal/jam, respon lawan, alur deposit
3. **Rekam jejak & sportivitas** — laporan hasil tanding, riwayat tervalidasi, rating pasca-tanding
4. **Backend master data & moderasi** — CRUD kelas berat, laporan pelanggaran, sanksi akun + sambungkan dashboard admin
5. **Penyempurnaan aplikasi Android** — feed discovery & match inbox berbasis API
6. **Machine learning matchmaking** — pengganti heuristik dengan scikit-learn
7. **Automated tests** — pytest (backend) dan unit test klien
