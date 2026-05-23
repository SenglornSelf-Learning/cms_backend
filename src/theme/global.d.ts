export {}

declare global {
  interface Window {
    NProgress?: {
      configure: (options: { showSpinner?: boolean }) => void
      start: () => void
      done: () => void
    }
  }
}
