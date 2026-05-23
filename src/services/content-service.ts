import type { CmsContent, CreateContentPayload } from '@/types/content'
import { getHttpClient } from './http-client'

export class ContentService {
  async findAll(): Promise<CmsContent[]> {
    const { data } = await getHttpClient().get<CmsContent[]>('/api/contents')
    return data
  }

  async findById(id: number): Promise<CmsContent> {
    const { data } = await getHttpClient().get<CmsContent>(`/api/contents/${id}`)
    return data
  }

  async create(payload: CreateContentPayload): Promise<CmsContent> {
    const { data } = await getHttpClient().post<CmsContent>('/api/contents', payload)
    return data
  }
}

export const contentService = new ContentService()
