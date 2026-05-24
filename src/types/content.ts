export interface CmsContent {
  id?: number
  uuid?: string
  slug?: string
  keyword?: string
  title: string
  description?: string
  thumbnail?: string
  editor?: string
  isDeleted?: boolean | null
  createAt?: string
  categoryId?: number | null
}

export type CreateContentPayload = Pick<
  CmsContent,
  'title' | 'slug' | 'keyword' | 'description' | 'thumbnail' | 'editor' | 'categoryId'
>

export type UpdateContentPayload = Pick<
  CmsContent,
  'title' | 'slug' | 'keyword' | 'description' | 'thumbnail' | 'editor' | 'categoryId' | 'isDeleted'
>
