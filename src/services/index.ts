export { createHttpClient, getHttpClient } from './http-client'
export { CategoryService, categoryService } from './category-service'
export { ContentService, contentService } from './content-service'
export { DashboardService, dashboardService } from './dashboard-service'

import { createHttpClient } from './http-client'

/** Call once at app bootstrap — same pattern as coreit.planfit `initializeServices`. */
export function initializeServices(): void {
  createHttpClient()
}
