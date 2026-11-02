import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

export default defineConfig({
  plugins: [react()],
  // El back solo acepta este origen por CORS: si el puerto está ocupado, falla en vez de cambiarlo.
  server: {
    port: 5173,
    strictPort: true,
  },
})
