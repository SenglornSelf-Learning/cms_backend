/**
 * Re-initializes Sleek sidebar behavior after Vue mounts.
 * sleek.js binds on DOMContentLoaded before #sidebar-toggler exists.
 */
export function initSleekSidebar(): () => void {
  const body = document.getElementById('body')!
  const toggler = document.getElementById('sidebar-toggler')!
  if (!body || !toggler) {
    return () => {}
  }

  let isMinified = false
  let isCollapsed = false
  const cleanups: (() => void)[] = []

  const isMobile = () => window.innerWidth < 768
  const isTablet = () => window.innerWidth >= 768 && window.innerWidth < 992

  if (
    isTablet() &&
    (body.classList.contains('sidebar-fixed') || body.classList.contains('sidebar-static'))
  ) {
    body.classList.remove('sidebar-collapse', 'sidebar-minified-out')
    body.classList.add('sidebar-minified')
    isMinified = true
  }

  function removeMobileOverlay() {
    document.querySelector('.mobile-sticky-body-overlay')?.remove()
    document.body.style.overflow = 'auto'
  }

  function onOverlayClick() {
    removeMobileOverlay()
    body.classList.remove('sidebar-mobile-in')
    body.classList.add('sidebar-mobile-out')
  }

  function ensureMobileOverlay() {
    if (document.querySelector('.mobile-sticky-body-overlay')) {
      return
    }
    document.body.style.overflow = 'hidden'
    const overlay = document.createElement('div')
    overlay.className = 'mobile-sticky-body-overlay'
    document.body.prepend(overlay)
    overlay.addEventListener('click', onOverlayClick)
    cleanups.push(() => {
      overlay.removeEventListener('click', onOverlayClick)
      overlay.remove()
    })
  }

  function toggleMobileSidebar(event: Event) {
    event.preventDefault()
    const min = 'sidebar-mobile-in'
    const minOut = 'sidebar-mobile-out'
    if (body.classList.contains(min)) {
      body.classList.remove(min)
      body.classList.add(minOut)
      removeMobileOverlay()
    } else {
      body.classList.add(min)
      body.classList.remove(minOut)
      ensureMobileOverlay()
    }
  }

  function toggleDesktopSidebar(event: Event) {
    event.preventDefault()

    if (
      body.classList.contains('sidebar-fixed-offcanvas') ||
      body.classList.contains('sidebar-static-offcanvas')
    ) {
      toggler.classList.add('sidebar-offcanvas-toggle')
      toggler.classList.remove('sidebar-toggle')
      if (!isCollapsed) {
        body.classList.add('sidebar-collapse')
        isCollapsed = true
        isMinified = false
      } else {
        body.classList.remove('sidebar-collapse')
        body.classList.add('sidebar-collapse-out')
        window.setTimeout(() => body.classList.remove('sidebar-collapse-out'), 300)
        isCollapsed = false
      }
      return
    }

    if (body.classList.contains('sidebar-fixed') || body.classList.contains('sidebar-static')) {
      toggler.classList.add('sidebar-toggle')
      toggler.classList.remove('sidebar-offcanvas-toggle')
      if (!isMinified) {
        body.classList.remove('sidebar-collapse', 'sidebar-minified-out')
        body.classList.add('sidebar-minified')
        isMinified = true
        isCollapsed = false
      } else {
        body.classList.remove('sidebar-minified')
        body.classList.add('sidebar-minified-out')
        isMinified = false
      }
    }
  }

  function onTogglerClick(event: Event) {
    if (isMobile()) {
      toggleMobileSidebar(event)
    } else {
      toggleDesktopSidebar(event)
    }
  }

  toggler.addEventListener('click', onTogglerClick)
  cleanups.push(() => toggler.removeEventListener('click', onTogglerClick))

  document.querySelectorAll<HTMLAnchorElement>('.sidebar .nav > .has-sub > a').forEach((link) => {
    const onSubmenuClick = (event: Event) => {
      event.preventDefault()
      const parent = link.parentElement
      if (!parent) return
      parent.parentElement
        ?.querySelectorAll(':scope > .has-sub')
        .forEach((sibling) => {
          if (sibling !== parent) sibling.classList.remove('expand')
        })
      parent.classList.toggle('expand')
    }
    link.addEventListener('click', onSubmenuClick)
    cleanups.push(() => link.removeEventListener('click', onSubmenuClick))
  })

  return () => {
    cleanups.forEach((fn) => fn())
    removeMobileOverlay()
  }
}
