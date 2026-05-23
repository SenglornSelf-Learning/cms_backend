import { createApp } from 'vue'
import { createPinia } from 'pinia'

import '@sleek-theme/assets/plugins/simplebar/simplebar.css'
import '@sleek-theme/assets/plugins/nprogress/nprogress.css'
import '@sleek-theme/assets/css/sleek.css'

import App from './App.vue'
import router from './router'
import { initializeServices } from '@/services'
import { loadThemeScripts } from '@/theme/load-theme-scripts'

async function bootstrap() {
  await loadThemeScripts()

  const app = createApp(App)

  app.use(createPinia())
  app.use(router)

  initializeServices()

  app.mount('#app')
}

bootstrap().catch((err) => {
  console.error('Failed to start CMS frontend:', err)
})
