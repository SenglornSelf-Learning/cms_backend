import type { CmsContent, CreateContentPayload, UpdateContentPayload } from '@/types/content'
import { getHttpClient } from './http-client'

export class ContentService {
  async findAll(): Promise<CmsContent[]> {
    const { data } = await getHttpClient().get<CmsContent[]>('/api/contents/list')
    return data
  }

  async findById(id: number): Promise<CmsContent> {
    const { data } = await getHttpClient().get<CmsContent>(`/api/contents/getById/${id}`)
    return data
  }

  async create(payload: CreateContentPayload): Promise<CmsContent> {
    const { data } = await getHttpClient().post<CmsContent>('/api/contents', payload)
    return data
  }

  async update(id: number, payload: UpdateContentPayload): Promise<CmsContent> {
    const { data } = await getHttpClient().put<CmsContent>(`/api/contents/update/${id}`, payload)
    return data
  }

  async delete(id: number): Promise<void> {
    await getHttpClient().delete(`/api/contents/delete/${id}`)
  }
}

export const contentService = new ContentService()
