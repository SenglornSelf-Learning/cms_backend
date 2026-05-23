import { defineStore } from 'pinia'
import { computed } from 'vue'
import { getCmsApiBaseUrl } from '@/config'

/**
 * Read-only runtime info for templates (e.g. footer / diagnostics).
 * Extend like coreit.planfit `stores/config.ts` when you add auth or user prefs.
 */
export const useCmsConfigStore = defineStore('cms-config', () => {
  const apiBaseUrl = computed(() => getCmsApiBaseUrl())

  return { apiBaseUrl }
})
