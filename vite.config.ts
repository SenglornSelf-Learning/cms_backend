import fs from 'node:fs'
import path from 'node:path'
import { fileURLToPath, URL } from 'node:url'
import type { IncomingMessage, ServerResponse } from 'node:http'
import { defineConfig, type Plugin } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

const __dirname = path.dirname(fileURLToPath(import.meta.url))
const themeRoot = path.resolve(__dirname, 'public/theme')

const mime: Record<string, string> = {
  '.css': 'text/css; charset=utf-8',
  '.js': 'application/javascript; charset=utf-8',
  '.map': 'application/json; charset=utf-8',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.jpeg': 'image/jpeg',
  '.gif': 'image/gif',
  '.svg': 'image/svg+xml',
  '.ico': 'image/x-icon',
  '.woff': 'font/woff',
  '.woff2': 'font/woff2',
  '.ttf': 'font/ttf',
  '.eot': 'application/vnd.ms-fontobject',
  '.json': 'application/json; charset=utf-8',
}

function resolveThemeAsset(rel: string): string | null {
  const themePath = path.resolve(themeRoot, rel)
  if (fs.existsSync(themePath) && fs.statSync(themePath).isFile()) {
    return themePath
  }
  return null
}

function themeAssetsMiddleware(
  req: IncomingMessage,
  res: ServerResponse,
  next: (err?: unknown) => void,
) {
  const rawUrl = req.url?.split('?')[0] ?? ''
  if (!rawUrl.startsWith('/theme/')) {
    next()
    return
  }
  const rel = decodeURIComponent(rawUrl.slice('/theme/'.length))
  if (rel.includes('..')) {
    res.statusCode = 403
    res.end()
    return
  }
  const file = resolveThemeAsset(rel)
  if (!file) {
    next()
    return
  }
  const ext = path.extname(file)
  res.setHeader('Content-Type', mime[ext] ?? 'application/octet-stream')
  fs.createReadStream(file).pipe(res)
}

function sleekThemePlugin(): Plugin {
  return {
    name: 'serve-sleek-theme',
    configureServer(server) {
      server.middlewares.use(themeAssetsMiddleware)
    },
    configurePreviewServer(server) {
      server.middlewares.use(themeAssetsMiddleware)
    },
    // Theme lives in public/theme; Vite copies public/ into the build output.
  }
}

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue(), vueDevTools(), sleekThemePlugin()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
      '@sleek-theme': themeRoot,
    },
  },
  server: {
    proxy: {
      '/admin': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
