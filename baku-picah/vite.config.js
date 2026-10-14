import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';

export default defineConfig({
  // Relative asset paths so the built app also works under Electron's file:// protocol.
  base: './',
  plugins: [vue()],
  server: {
    port: 5174,
    watch: {
      // Don't watch build output (exe files get locked during packaging).
      ignored: ['**/dist/**', '**/dist-electron/**'],
    },
  },
});
