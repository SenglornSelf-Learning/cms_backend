import type { Category } from '@/types/category'
import { getHttpClient } from './http-client'

/**
 * Category API — paths match CategoryRestController under context-path `/admin`.
 */
export class CategoryService {
  async findAll(): Promise<Category[]> {
    const { data } = await getHttpClient().get<Category[]>('/api/categories/list')
    return data
  }

  async findById(id: number): Promise<Category> {
    const { data } = await getHttpClient().get<Category>(`/api/categories/getById/${id}`)
    return data
  }

  async create(payload: Pick<Category, 'name'>): Promise<Category> {
    const { data } = await getHttpClient().post<Category>('/api/categories/create', payload)
    return data
  }

  async update(id: number, payload: Pick<Category, 'name' | 'isDeleted'>): Promise<Category> {
    const { data } = await getHttpClient().put<Category>(`/api/categories/update/${id}`, payload)
    return data
  }

  async delete(id: number): Promise<void> {
    await getHttpClient().delete(`/api/categories/delete/${id}`)
  }
}

export const categoryService = new CategoryService()
