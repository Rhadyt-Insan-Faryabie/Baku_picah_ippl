# FIGHT — Fighter Matchmaking & Bout Scheduling System

**FIGHT** (brand **BAKU PICAH**) merupakan platform digital terintegrasi yang dirancang untuk menghubungkan para petarung dalam menyepakati pertandingan tanding (*bout*) **MMA** yang seimbang dan aman melalui algoritma pencocokan cerdas (*AI-driven matchmaking*). Laga berlangsung di **lokasi bebas (jalanan / tempat acak)** tanpa terikat sasana.

Platform ini mengintegrasikan aplikasi mobile berbasis **Android Native (Java)** dan aplikasi **desktop/web** untuk para petarung, serta **Web Dashboard** untuk administrator pengelola operasional pertandingan.

---

## Daftar Isi

- [Fitur Utama](#fitur-utama)
- [Tech Stack & Arsitektur](#tech-stack--arsitektur)
- [Struktur Project](#struktur-project)
- [Persiapan Sistem](#persiapan-sistem)
- [Instalasi & Setup](#instalasi--setup)
  - [1. Konfigurasi Database (MySQL)](#1-konfigurasi-database-mysql)
  - [2. Setup Backend (Python FastAPI/Flask)](#2-setup-backend-python-fastapiflask)
  - [3. Setup Mobile App (Android Java)](#3-setup-mobile-app-android-java)
  - [4. Setup Web Admin (Dashboard)](#4-setup-web-admin-dashboard)
- [Alur Algoritma Matchmaking AI](#alur-algoritma-matchmaking-ai)
- [Dokumentasi API](#dokumentasi-api)
- [Troubleshooting](#troubleshooting)
- [Tim Pengembang](#tim-pengembang)
- [Lisensi](#lisensi)

---

## Fitur Utama

### 📱 Aplikasi Petarung (Mobile App)
- **Autentikasi & Setup Identitas Petarung:** Registrasi akun, kelola profil fisik (berat badan, tinggi badan, **kelas berat otomatis**), **kota**, **background bela diri** (Boxing, Muay Thai, BJJ, Pencak Silat, Wrestling, dll), serta riwayat tanding (Win/Loss/Draw). **Aturan tanding selalu MMA.**
- **Discovery & Matchmaking (AI-Powered):** Rekomendasi lawan tanding ideal dengan penilaian kompatibilitas kelas berat dan tingkat pengalaman, dilengkapi kartu profil interaktif (*Swipe/Like/Pass*).
- **Match Inbox & Chat 1-on-1:** Ruang komunikasi langsung antar petarung yang cocok untuk mendiskusikan kesepakatan laga secara privat.
- **Pengajuan & Manajemen Pertandingan:** Form kesepakatan jadwal tanding, **lokasi tanding bebas (jalanan / tempat acak)**, sistem komitmen deposit, dan pelacak status verifikasi admin.
- **Rekam Jejak & Sportivitas:** Laporan hasil pertandingan, log riwayat laga tervalidasi, serta penilaian rating ulasan sportivitas lawan pasca-tanding.

### 💻 Web Dashboard Admin
- **Verifikasi Pertandingan:** Peninjauan dan persetujuan (*Approve/Reject*) jadwal laga dan kesepakatan tanding yang diajukan petarung.
- **Kelola Master Data:** Manajemen data kategori kelas tanding (*weight classes*) standar MMA.
- **Pusat Moderasi & Keamanan:** Pengawasan laporan pelanggaran etika tanding serta penindakan sanksi (peringatan hingga penangguhan akun).

---

## Tech Stack & Arsitektur

| Lapisan | Teknologi | Deskripsi |
|---|---|---|
| **Mobile Client** | Java (Android SDK) | Aplikasi native untuk Petarung |
| **Web Dashboard** | React / Streamlit | Dashboard admin dan operasional |
| **Backend API** | Python (FastAPI / Flask) | RESTful API & Service Layer |
| **Matchmaking Core** | Python (scikit-learn / NumPy) | Algoritma klasifikasi & scoring kecocokan lawan |
| **Database** | MySQL (3NF Normalized) | Penyimpanan data relasional terstruktur |
| **Desain Antarmuka** | Figma | UI/UX Dark-Athletic & Design System |
| **Alat Bantu Dev** | VS Code, Android Studio, DBeaver | Lingkungan pengembangan & database management |

---

## Struktur Project

```text
FIGHT-Platform/
│
├── mobile/                     # Aplikasi Android Native (Java)
│   ├── app/
│   │   ├── src/main/java/     # Controller & Activity views
│   │   └── src/main/res/      # Layout XML, drawable, values
│   └── build.gradle
│
├── backend/                    # Core REST API (Python)
│   ├── app/
│   │   ├── api/routes/        # Endpoint router (auth, match, bout)
│   │   ├── core/              # Konfigurasi & security token
│   │   ├── models/            # Skema model ORM database
│   │   └── services/          # Logika bisnis & algoritma AI Matchmaking
│   ├── database/              # DDL migrasi SQL & seeder awal
│   ├── requirements.txt       # Daftar pustaka dependensi Python
│   └── .env.example
│
├── web-admin/                  # Panel admin back-office
│   ├── src/
│   └── package.json
│
├── baku-picah/                 # Aplikasi petarung (desktop/web, Electron-ready)
│   ├── src/
│   └── package.json
│
└── README.md
