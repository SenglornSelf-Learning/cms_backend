import type { DashboardSummary } from '@/types/dashboard'
import { getHttpClient } from './http-client'

export class DashboardService {
  async getSummary(): Promise<DashboardSummary> {
    const { data } = await getHttpClient().get<DashboardSummary>('/api/dashboard')
    return data
  }
}

export const dashboardService = new DashboardService()
