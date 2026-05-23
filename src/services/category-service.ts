import type { Category } from '@/types/category'
import { getHttpClient } from './http-client'

/**
 * Category API — replaces direct `fetch` / old `api/categories` module.
 */
export class CategoryService {
  async findAll(): Promise<Category[]> {
    const { data } = await getHttpClient().get<Category[]>('/api/categories')
    return data
  }

  async findById(id: number): Promise<Category> {
    const { data } = await getHttpClient().get<Category>(`/api/categories/${id}`)
    return data
  }

  async create(payload: Pick<Category, 'name'>): Promise<Category> {
    const { data } = await getHttpClient().post<Category>('/api/categories', payload)
    return data
  }
}

export const categoryService = new CategoryService()
