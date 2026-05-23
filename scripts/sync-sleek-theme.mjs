import fs from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'

const __dirname = path.dirname(fileURLToPath(import.meta.url))
const projectRoot = path.resolve(__dirname, '..')
const defaultSleekRoot = path.resolve(projectRoot, '../../sleek-bootstrap-main')
const sleekRoot = process.env.SLEEK_THEME_ROOT ?? defaultSleekRoot
const themeSrc = path.join(sleekRoot, 'theme')
const simplebarSrc = path.join(sleekRoot, 'source/assets/plugins/simplebar')
const dest = path.join(projectRoot, 'public/theme')

if (!fs.existsSync(themeSrc)) {
  console.error(
    `Sleek theme not found at "${themeSrc}".\n` +
      'Set SLEEK_THEME_ROOT to your sleek-bootstrap-main folder, then run: npm run sync:theme',
  )
  process.exit(1)
}

fs.cpSync(themeSrc, dest, { recursive: true, force: true })

if (fs.existsSync(simplebarSrc)) {
  fs.cpSync(simplebarSrc, path.join(dest, 'assets/plugins/simplebar'), {
    recursive: true,
    force: true,
  })
} else {
  console.warn(`Warning: simplebar assets missing at "${simplebarSrc}"`)
}

console.log(`Synced Sleek theme → ${dest}`)
