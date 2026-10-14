// Electron main process for the BAKU PICAH fighter desktop app.
// CommonJS (.cjs) so it works regardless of the package "type": "module".
const { app, BrowserWindow, shell } = require('electron');
const path = require('node:path');
const fs = require('node:fs');

// Vite dev server (see vite.config.js). Only used when not packaged.
const DEV_SERVER_URL = 'http://localhost:5174';

function createWindow() {
  const winOptions = {
    width: 1200,
    height: 800,
    minWidth: 900,
    minHeight: 600,
    backgroundColor: '#121212',
    title: 'BAKU PICAH',
    autoHideMenuBar: true,
    webPreferences: {
      preload: path.join(__dirname, 'preload.cjs'),
      contextIsolation: true,
      nodeIntegration: false,
      sandbox: true,
    },
  };

  // The window icon is only present in dev; the packaged exe embeds its own icon.
  const devIcon = path.join(__dirname, '..', 'build', 'icon.png');
  if (fs.existsSync(devIcon)) {
    winOptions.icon = devIcon;
  }

  const win = new BrowserWindow(winOptions);

  if (!app.isPackaged) {
    win.loadURL(DEV_SERVER_URL);
    win.webContents.openDevTools({ mode: 'detach' });
  } else {
    win.loadFile(path.join(__dirname, '..', 'dist', 'index.html'));
  }

  // External links open in the system browser instead of a new Electron window.
  win.webContents.setWindowOpenHandler(({ url }) => {
    shell.openExternal(url);
    return { action: 'deny' };
  });
}

app.whenReady().then(() => {
  createWindow();

  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      createWindow();
    }
  });
});

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit();
  }
});
