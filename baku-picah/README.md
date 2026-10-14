# BAKU PICAH — Fighter App (Desktop / Web)

Aplikasi sisi petarung untuk platform BAKU PICAH (matchmaking MMA), dibuat dengan
**Vue 3 + Vite** dan dibungkus menjadi aplikasi desktop dengan **Electron**.

> Aplikasi ini **hanya klien**. Backend FastAPI + MySQL harus berjalan terpisah di
> `http://localhost:8000` (lihat `../backend`).

## Prasyarat

- Node.js 18+ dan npm
- Backend BAKU PICAH berjalan di `http://localhost:8000`

## Konfigurasi

API base URL diambil dari `VITE_API_BASE_URL` (lihat `.env.example`). Jika tidak
diset, default-nya `http://localhost:8000/api`.

```bash
copy .env.example .env
```

## Menjalankan

```bash
npm install
```

### Sebagai web app (browser)

```bash
npm run dev          # http://localhost:5174
```

### Sebagai aplikasi desktop (Electron, mode dev)

Jalankan dev server dulu di satu terminal, lalu Electron di terminal lain:

```bash
npm run dev          # terminal 1
npm run electron:dev # terminal 2
```

## Membuat `.exe`

```bash
npm run electron:build
```

Hasil ada di `dist-electron/`:

- `BAKU-PICAH-0.1.0-portable.exe` — portable, langsung klik tanpa instalasi
- `BAKU-PICAH-0.1.0-setup.exe` — installer (NSIS)

Icon aplikasi diambil dari `build/icon.png`.

## Catatan

- Jalankan build dari **folder lokal, bukan folder yang tersinkron OneDrive**.
  Sinkronisasi OneDrive sering menyebabkan error `EPERM` saat Electron mengemas
  (rename `win-unpacked.tmp`). Jika terjadi, arahkan output ke folder lain:
  `npx electron-builder --win --config.directories.output=<folder-luar-oneDrive>`.
- Exe tidak ditandatangani (unsigned), jadi Windows SmartScreen bisa menampilkan
  peringatan "Unknown publisher".
- Struktur: `electron/` (proses utama Electron), `src/` (aplikasi Vue), `build/` (ikon).
