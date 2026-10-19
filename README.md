# FIGHT — Fighter Matchmaking & Bout Scheduling System

**FIGHT** (brand **BAKU PICAH**) merupakan platform digital terintegrasi yang dirancang untuk menghubungkan para petarung dalam menyepakati pertandingan tanding (*bout*) **MMA** yang seimbang dan aman melalui algoritma pencocokan berbobot (*weighted matchmaking* — machine learning terencana di roadmap).

Platform ini mengintegrasikan aplikasi mobile berbasis **Android Native (Java)** dan aplikasi desktop/web (**Vue 3 + Electron**) untuk para petarung, serta **Web Dashboard** untuk administrator pengelola operasional pertandingan.

---

## Daftar Isi

- [Fitur Utama](#fitur-utama)
- [Tech Stack & Arsitektur](#tech-stack--arsitektur)
- [Struktur Project](#struktur-project)
- [Menjalankan Secara Lokal](#menjalankan-secara-lokal)
- [Status Fitur terhadap Kebutuhan Fungsional](#status-fitur-terhadap-kebutuhan-fungsional)
- [Rencana Pengembangan](#rencana-pengembangan)

---

## Fitur Utama

Keterangan status: **Live** = berfungsi end-to-end; **Dalam pengembangan** = prototipe UI / belum tersambung API.

### Aplikasi Petarung (Android + Desktop)

- **Autentikasi & Setup Identitas Petarung** — **Live** di kedua aplikasi: registrasi akun, login (JWT), kelola profil fisik (berat badan, tinggi badan, **kelas berat otomatis**), **kota**, dan **background bela diri** (Boxing, Muay Thai, BJJ, Pencak Silat, Wrestling, dll). **Aturan tanding selalu MMA.**
  - **Dalam pengembangan:** input riwayat tanding (Win/Loss/Draw) dan unggah foto profil.
- **Discovery & Matchmaking** — **Live** di aplikasi desktop (`baku-picah`): rekomendasi lawan dengan **skor kompatibilitas** dan **alasan kecocokan** (kelas berat, kota, kesamaan bela diri, kedekatan pengalaman), Like/Pass tersambung ke API.
  - **Dalam pengembangan:** gesture kartu swipe dan penyempurnaan feed di aplikasi Android.
- **Match Inbox** — **Live** di desktop: daftar lawan yang saling suka. **Dalam pengembangan** di Android.
- **Chat 1-on-1** — **Dalam pengembangan**: ruang komunikasi privat antar petarung (baru tersedia kerangka endpoint; penyimpanan pesan belum).
- **Pengajuan & Manajemen Pertandingan** — **Dalam pengembangan**: back-end pembuatan dan verifikasi bout sudah berjalan, UI pengajuan di aplikasi petarung belum.
- **Jadwal & Deposit Pertandingan** — **Dalam pengembangan**: kesepakatan jadwal tanggal/jam dan alur deposit jaminan.
- **Rekam Jejak & Sportivitas** — **Dalam pengembangan**: laporan hasil tanding, riwayat laga tervalidasi, dan rating pasca-tanding.

### Web Dashboard Admin

- **Login Admin** — **Live**: terhubung backend asli dengan validasi role admin.
- **Verifikasi Pertandingan** — **Dalam pengembangan**: back-end `approve`/`reject` bout sudah berjalan (guard role admin); panel admin masih memakai data contoh.
- **Manajemen Data Pertandingan, Kelas Tanding, dan Lokasi** — **Dalam pengembangan**.
- **Moderasi & Tindak Pelanggaran** — **Dalam pengembangan**.

---

## Tech Stack & Arsitektur

| Lapisan | Teknologi | Keterangan |
|---|---|---|
| Mobile App | Android Native (Java) | Aplikasi petarung untuk perangkat Android |
| Desktop/Web App | Vue 3 + Electron | Aplikasi petarung desktop & web (`baku-picah/`) |
| Web Dashboard | Vue 3 (Vite) | Panel admin back-office (`web-admin/`) |
| Backend API | Python (FastAPI) | RESTful API, dokumentasi Swagger di `/docs` |
| Matchmaking Core | Python (rule-based weighted scoring) | Heuristik kompatibilitas: kelas berat **0.5**, satu kota **0.2**, kemiripan bela diri **0.2** (Jaccard), kedekatan pengalaman **0.1** — machine learning (scikit-learn) terencana di roadmap |
| Database | MySQL (3NF Normalized) | Tabel `fighters`, `swipes`, `bouts` |
| Autentikasi | JWT (python-jose) + bcrypt | Token Bearer dengan guard role admin |
| Desain Antarmuka | Figma | UI/UX Dark-Athletic & Design System |
| Alat Bantu Dev | VS Code, Android Studio, DBeaver | Lingkungan pengembangan & database management |

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
│   │   ├── api/routes/         # Endpoint router (auth, fighter, match, bout, chat)
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
| FR-003 | Menambahkan Data Diri Petarung | Sebagian | 60% | Data fisik (bobot, tinggi, kota, bela diri) sudah bisa diisi; rekor pertarungan ada pada skema namun belum ada input; unggah foto profil belum |
| FR-004 | Menghapus Data Diri Petarung | Belum | 0% | Belum ada endpoint/UI hapus profil |
| FR-005 | Mencari Lawan | Sebagian | 60% | Feed rekomendasi + filter kelas tanding sudah berjalan; filter lokasi belum |
| FR-006 | Melihat Profil Lawan | Sebagian | 60% | Kartu Discover menampilkan bobot, kelas, bela diri, dan rekor; halaman detail khusus baru ada di aplikasi Android |
| FR-007 | Menentukan Hasil Pertandingan | Belum | 0% | Belum ada input skor/status hasil |
| FR-008 | Like/Pass Lawan | Selesai | 100% | `POST /api/match/{id}/like` dan `/pass` + tombol di Discover |
| FR-009 | Melihat Match | Selesai | 100% | `GET /api/match/matches` + halaman Matches |
| FR-010 | Chat dengan Lawan | Belum | 0% | Baru kerangka endpoint; penyimpanan pesan dan UI belum |
| FR-011 | Mengajukan Pertandingan | Sebagian | 80% | `POST /api/bout` berjalan (teruji); UI pengajuan belum |
| FR-012 | Mengatur Jadwal Pertandingan | Belum | 0% | Belum ada kesepakatan tanggal/jam |
| FR-013 | Melakukan Deposit Pertandingan | Belum | 0% | Belum ada alur pembayaran/deposit |
| FR-014 | Konfirmasi Pertandingan | Belum | 0% | Belum ada konfirmasi syarat oleh kedua pihak |
| FR-015 | Melaporkan Petarung | Belum | 0% | Belum ada formulir pelaporan |
| FR-016 | Melihat Riwayat Pertandingan | Belum | 0% | Belum ada endpoint/UI riwayat laga |
| FR-017 | Memberikan Rating & Review | Belum | 0% | Belum ada rating/review pasca-tanding |
| FR-018 | Mengelola Data Pertandingan (Admin) | Belum | 0% | Panel admin masih memakai data contoh |
| FR-019 | Memverifikasi Pertandingan (Admin) | Sebagian | 80% | `approve`/`reject` bout berjalan dengan guard admin; tombol di panel admin belum tersambung |
| FR-020 | Mengelola Laporan | Belum | 0% | Belum ada modul laporan |
| FR-021 | Menindak Pelanggaran | Belum | 0% | Belum ada sanksi/blokir akun |
| FR-022 | Mengelola Data Kelas Pertandingan | Belum | 0% | Kelas berat masih ditentukan otomatis, belum ada CRUD |
| FR-023 | Mengelola Data Lokasi | Belum | 0% | Belum ada master data arena/gym |
| FR-024 | Menganalisis Profil Petarung (AI) | Selesai | 100% | Mesin skoring berbobot menganalisis kelas berat, bela diri, kota, dan pengalaman (berbasis aturan; ML scikit-learn di roadmap) |
| FR-025 | Mencocokkan Petarung (AI) | Selesai | 100% | Komputasi match score berjalan (berbasis aturan) |
| FR-026 | Memberikan Rekomendasi Lawan (AI) | Selesai | 100% | `GET /api/match/recommendations` + tampilan Discover |
| **TOTAL** | **26 Kebutuhan Fungsional** | **7 Selesai, 5 Sebagian, 14 Belum** | **40,0%** | **Rata-rata capaian = 10,40 dari 26 FR** |

### Ringkasan Progres

| Status | Jumlah FR | Rentang Capaian |
|---|---:|---|
| Selesai | 7 | 100% |
| Sebagian | 5 | 60% - 80% |
| Belum | 14 | 0% |
| **Total** | **26** | **Progres keseluruhan: 40,0%** |

Progres keseluruhan: **40,0%** — dihitung sebagai rata-rata capaian seluruh FR (10,40 dari 26). FR berstatus "Sebagian" dinilai sesuai porsi yang sudah berjalan; misalnya FR-011 dan FR-019 bagian back-end-nya sudah selesai dan teruji (tertinggal UI), sedangkan FR-003/005/006 baru sebagian tercakup.

---

## Rencana Pengembangan

Prioritas berikutnya:

1. **Chat 1-on-1** — tabel `conversations`/`messages` + pengiriman pesan.
2. **Pengajuan bout lengkap** — jadwal tanggal/jam, respons lawan, alur deposit.
3. **Halaman Bouts & Chat di aplikasi petarung** serta penyambungan data panel admin.
4. **Rekam jejak & sportivitas** — laporan hasil tanding, riwayat tervalidasi, rating pasca-tanding.
5. **Master data & moderasi** — CRUD kelas berat dan lokasi, laporan pelanggaran, sanksi akun.
6. **Penyempurnaan aplikasi Android** — feed discovery & match inbox berbasis API.
7. **Machine learning matchmaking** — pengganti heuristik dengan scikit-learn.
8. **Automated tests** — pytest (backend) dan unit test klien.
