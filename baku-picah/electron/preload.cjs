// Minimal, safe preload bridge for the renderer.
const { contextBridge } = require('electron');

contextBridge.exposeInMainWorld('bakupicah', {
  isDesktop: true,
});
