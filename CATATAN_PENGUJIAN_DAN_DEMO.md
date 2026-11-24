# RANGKUMAN PANDUAN PENGUJIAN, DEMO & SISTEM BAKU PICAH (IPPL)

Dokumen ini merangkum seluruh hasil pengerjaan, panduan pengujian manual (Black Box Testing), cara demo di depan dosen, sistem pembayaran Midtrans QRIS, serta panduan instalasi APK ke HP fisik.

---

## 1. Status Penyelesaian Proyek (100% Selesai)

Seluruh **26 Kebutuhan Fungsional (FR-001 s.d. FR-026)** telah selesai diimplementasikan end-to-end:
* **Back-end:** FastAPI (Python 3.11) + Database MySQL di Laragon.
* **Web Admin Control Room:** Vue 3 + Vite + SPA Router.
* **Mobile App:** Android Java (Native) di Android Studio.

---

## 2. Cara Menjalankan Sistem Sebelum Demo / Uji Coba

Nyalakan 3 komponen ini di laptop:

1. **MySQL (Laragon):** Buka aplikasi Laragon, klik **Start All**.
2. **Backend API:** Buka terminal VS Code di folder `backend`, jalankan:
   ```powershell
   cd backend
   uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
   ```
3. **Web Admin:** Buka terminal kedua di folder `web-admin`, jalankan:
   ```powershell
   cd web-admin
   npm run dev
   ```
   *(Buka di browser: `http://localhost:5173`)*
4. **Mobile App:** Buka project `mobile` di Android Studio, lalu tekan tombol hijau **Run 'app'** ($\blacktriangleright$) ke emulator atau HP.

---

## 3. Akun Pengujian (Testing Credentials)

| Role | Email / Username | Password | Keterangan |
|---|---|---|---|
| **Admin Control Room** | `admin@bakupicah.id` (atau `admin`) | `admin123` | Login di Web Admin (`localhost:5173`) |
| **Petarung Utama (Demo)**| `rambo@example.com` (atau `rambo`) | `fighter123` | Login di Mobile Android (Rambo Sitorus) |
| **Petarung Lawan** | `dedi@example.com` (atau `dedi`) | `fighter123` | Lawan tanding (Dedi "Si Petir") |

---

## 4. Tabel Hasil Black Box Testing (Siap Copy ke Makalah/Laporan IPPL)

| No | ID Pengujian | Skenario Uji | Data Masukan (Input) | Hasil yang Diharapkan | Hasil Pengamatan | Kesimpulan |
|:--:|---|---|---|---|---|:--:|
| **1** | `TC-AUTH-01` | Login data valid | `rambo@example.com` / `fighter123` | Berhasil login, menerima JWT, masuk ke beranda | Masuk ke beranda Discover | **VALID (PASS)** |
| **2** | `TC-AUTH-02` | Login password salah | `rambo@example.com` / `salah123` | Ditolak dengan pesan *"Email atau password salah"* | Muncul notifikasi error di layar | **VALID (PASS)** |
| **3** | `TC-MATCH-01`| Rekomendasi lawan (AI) | Menu Discover / Rekomendasi | Kartu lawan menampilkan persentase kecocokan (*Match Score %*) | Kartu lawan & detail spesifikasi muncul | **VALID (PASS)** |
| **4** | `TC-CHAT-01` | Obrolan 1-on-1 | Kirim pesan chat antar petarung | Pesan terkirim dan tersimpan di database | Bubble chat muncul live di layar | **VALID (PASS)** |
| **5** | `TC-BOUT-01` | Ajukan laga titik tengah | Klik tombol *"Ajukan Pertandingan"* | Form otomatis terisi arena netral & aturan MMA 3x2 menit | Data terisi otomatis tanpa perlu mengetik | **VALID (PASS)** |
| **6** | `TC-PAY-01`  | Bayar deposit Midtrans | Klik *"Bayar Otomatis Sekarang (Midtrans QRIS)"* | Debit otomatis Rp 160.000, dana terkunci di rekening Escrow | Muncul popup *"PEMBAYARAN MIDTRANS BERHASIL"* | **VALID (PASS)** |
| **7** | `TC-ADM-01`  | Verifikasi deposit admin | Buka Web Admin menu *Deposit & Escrow Ledger* | Transaksi pembayaran otomatis berstatus *Lunas (Escrow Secured)* | Transaksi tercatat aman di rekening bersama | **VALID (PASS)** |
| **8** | `TC-ADM-02`  | Setujui laga admin | Buka Web Admin menu *Bout Verification* | Admin menyetujui jadwal tanding netral | Status laga berubah menjadi *Confirmed/Approved* | **VALID (PASS)** |
| **9** | `TC-MOD-01`  | Laporkan petarung | Menu titik tiga chat $\rightarrow$ *"Laporkan Petarung"* | Laporan pelanggaran masuk ke antrean moderasi admin | Status laporan *Pending* di panel admin | **VALID (PASS)** |
| **10**| `TC-MOD-02`  | Aksi sanksi admin | Menu *Moderation* Web Admin $\rightarrow$ Terapkan Sanksi | Sanksi peringatan/suspend tersimpan | Status laporan menjadi *Resolved* | **VALID (PASS)** |

---

## 5. Konsep Sistem Pembayaran (Midtrans QRIS Escrow)

* **Nominal:** Fixed Rp 160.000 (Rp 150.000 jaminan komitmen tanding + Rp 10.000 biaya platform).
* **Alur Uang:**
  1. Uang masuk ke **Rekening Penampung Escrow Baku Picah di Midtrans** dan terkunci sementara.
  2. **Jika kedua petarung hadir:** Uang jaminan Rp 150.000 dikembalikan 100% (*refund/payout*) ke masing-masing petarung pasca tanding.
  3. **Jika salah satu petarung mangkir (*no-show*):** Uang jaminan petarung yang kabur hangus dan dijadikan uang kompensasi ganti rugi bagi petarung yang hadir.

---

## 6. Panduan Export File APK untuk Dipasang di HP Fisik

1. Di **Android Studio**, klik menu:
   **Build** $\rightarrow$ **Build Bundle(s) / APK(s)** $\rightarrow$ **Build APK(s)**.
2. File output `app-debug.apk` tersimpan di:
   ```
   mobile\app\build\outputs\apk\debug\app-debug.apk
   ```
3. Kirim file tersebut ke HP Android kamu lewat WhatsApp Web / Google Drive / Kabel Data USB.
4. Klik file di HP, izinkan instalasi dari sumber tidak dikenal, lalu tekan **Install**.
