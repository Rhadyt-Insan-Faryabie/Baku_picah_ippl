export function statusTone(status) {
  const map = {
    'Pending Opponent': 'muted',
    'Opponent Accepted': 'info',
    'Awaiting Verification': 'warning',
    Confirmed: 'success',
    Completed: 'success',
    Open: 'danger',
    'In Review': 'warning',
    Resolved: 'success',
    Tinggi: 'danger',
    Sedang: 'warning',
    Rendah: 'muted',
  };
  return map[status] || 'muted';
}
