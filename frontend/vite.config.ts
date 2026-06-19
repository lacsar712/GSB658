import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src')
    }
  },
  server: {
    port: 3000,
    host: '0.0.0.0', // 使其可以在 Docker 中被外部访问
    proxy: {
      '/api': {
        target: 'http://backend:8080', // 依据 Docker Compose 网络名
        changeOrigin: true
      }
    }
  }
})
