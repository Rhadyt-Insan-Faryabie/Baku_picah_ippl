/**
 * Mock data for the admin dashboard.
 * Replace with real API calls once the backend is ready.
 *
 * Concept: BAKU PICAH is MMA-only (street / random locations, no sasana directory).
 */

export const stats = [
  { label: 'Laga Pending', value: 12, delta: '+3 hari ini', tone: 'warning' },
  { label: 'Menunggu Verifikasi', value: 7, delta: '+2 hari ini', tone: 'info' },
  { label: 'Dispute Hasil', value: 2, delta: '-1 hari ini', tone: 'danger' },
  { label: 'Laga Selesai', value: 134, delta: 'bulan ini', tone: 'success' },
];

export const bouts = [
  {
    id: 'BT-1042',
    fighterA: 'Rambo Sitorus',
    fighterB: 'Dedi "Si Petir"',
    weight: 'Welterweight',
    location: 'Jl. Sudirman, Bandung',
    date: '2026-10-20 19:00',
    deposit: 'Rp 500.000',
    status: 'Awaiting Verification',
  },
  {
    id: 'BT-1041',
    fighterA: 'Andi Pratama',
    fighterB: 'Bagas Wijaya',
    weight: 'Lightweight',
    location: 'GOR Senayan, Jakarta',
    date: '2026-10-19 20:30',
    deposit: 'Rp 750.000',
    status: 'Opponent Accepted',
  },
  {
    id: 'BT-1040',
    fighterA: 'Yoga Saputra',
    fighterB: 'Krisna Mahesa',
    weight: 'Featherweight',
    location: 'Lapangan Tugu, Surabaya',
    date: '2026-10-18 18:00',
    deposit: 'Rp 400.000',
    status: 'Pending Opponent',
  },
  {
    id: 'BT-1039',
    fighterA: 'Fajar Nugroho',
    fighterB: 'Reza Hidayat',
    weight: 'Middleweight',
    location: 'Alun-alun, Yogyakarta',
    date: '2026-10-16 17:00',
    deposit: 'Rp 300.000',
    status: 'Confirmed',
  },
  {
    id: 'BT-1038',
    fighterA: 'Taufik Akbar',
    fighterB: 'Ilham Ramadhan',
    weight: 'Heavyweight',
    location: 'Lapangan Merdeka, Medan',
    date: '2026-10-14 19:30',
    deposit: 'Rp 1.000.000',
    status: 'Completed',
  },
];

export const disputes = [
  {
    id: 'DS-203',
    boutId: 'BT-1033',
    fighterA: 'Rizky Ananda',
    fighterB: 'Galih Permana',
    claimed: 'Rizky menang (R2 KO)',
    counter: 'Galih menang (R2 submission)',
    evidence: 'video_r2.mp4',
    reported: '2026-10-13 21:10',
  },
  {
    id: 'DS-202',
    boutId: 'BT-1029',
    fighterA: 'Hendra Kusuma',
    fighterB: 'Panji Setiawan',
    claimed: 'Draw',
    counter: 'Panji menang (Decision)',
    evidence: 'skor_juri.pdf',
    reported: '2026-10-12 16:45',
  },
];

/** Standar kelas berat MMA (kg). Rules tanding selalu MMA. */
export const weightClasses = [
  { id: 1, name: 'Flyweight', maxKg: 56.7 },
  { id: 2, name: 'Bantamweight', maxKg: 61.2 },
  { id: 3, name: 'Featherweight', maxKg: 65.8 },
  { id: 4, name: 'Lightweight', maxKg: 70.3 },
  { id: 5, name: 'Welterweight', maxKg: 77.1 },
  { id: 6, name: 'Middleweight', maxKg: 83.9 },
  { id: 7, name: 'Light Heavyweight', maxKg: 92.9 },
  { id: 8, name: 'Heavyweight', maxKg: 120.2 },
];

export const reports = [
  {
    id: 'RP-88',
    reporter: 'Fajar Nugroho',
    target: 'Bagas Wijaya',
    reason: 'Ujaran kebencian di chat',
    severity: 'Tinggi',
    status: 'Open',
    date: '2026-10-13 10:20',
  },
  {
    id: 'RP-87',
    reporter: 'Yoga Saputra',
    target: 'Krisna Mahesa',
    reason: 'Tidak hadir setelah deposit',
    severity: 'Sedang',
    status: 'In Review',
    date: '2026-10-12 14:05',
  },
  {
    id: 'RP-86',
    reporter: 'Andi Pratama',
    target: 'Reza Hidayat',
    reason: 'Profil/palsu rekor',
    severity: 'Rendah',
    status: 'Resolved',
    date: '2026-10-10 09:30',
  },
];
