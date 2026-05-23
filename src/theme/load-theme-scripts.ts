const THEME_SCRIPT_PATHS = [
  '/theme/assets/plugins/nprogress/nprogress.js',
  '/theme/assets/plugins/jquery/jquery.min.js',
  '/theme/assets/plugins/bootstrap/js/bootstrap.bundle.min.js',
  '/theme/assets/plugins/simplebar/simplebar.min.js',
  '/theme/assets/js/sleek.js',
] as const

function loadScript(src: string): Promise<void> {
  return new Promise((resolve, reject) => {
    const script = document.createElement('script')
    script.src = src
    script.async = false
    script.onload = () => resolve()
    script.onerror = () => reject(new Error(`Failed to load script: ${src}`))
    document.body.appendChild(script)
  })
}

export async function loadThemeScripts(): Promise<void> {
  for (const src of THEME_SCRIPT_PATHS) {
    await loadScript(src)
  }
  if (window.NProgress) {
    window.NProgress.configure({ showSpinner: false })
  }
}
