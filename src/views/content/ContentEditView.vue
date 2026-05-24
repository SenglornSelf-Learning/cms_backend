<script setup lang="ts">
import { ref, toRef, watch } from 'vue'
import { useRouter } from 'vue-router'
import ContentPanel from '@/components/common/ContentPanel.vue'
import { categoryService, contentService } from '@/services'
import type { Category } from '@/types/category'
import type { UpdateContentPayload } from '@/types/content'

const props = defineProps<{
  id: string
}>()

const router = useRouter()
const idRef = toRef(props, 'id')
const categories = ref<Category[]>([])
const title = ref('')
const slug = ref('')
const keyword = ref('')
const description = ref('')
const thumbnail = ref('')
const editor = ref('')
const categoryId = ref<number | null>(null)
const isDeleted = ref(false)
const loading = ref(true)
const saving = ref(false)
const error = ref<string | null>(null)

function optionalText(value: string): string | undefined {
  const trimmed = value.trim()
  return trimmed || undefined
}

async function load() {
  loading.value = true
  error.value = null
  const numericId = Number(idRef.value)
  if (!Number.isFinite(numericId)) {
    error.value = 'Invalid id'
    loading.value = false
    return
  }
  try {
    const [content, loadedCategories] = await Promise.all([
      contentService.findById(numericId),
      categoryService.findAll(),
    ])
    categories.value = loadedCategories
    title.value = content.title
    slug.value = content.slug ?? ''
    keyword.value = content.keyword ?? ''
    description.value = content.description ?? ''
    thumbnail.value = content.thumbnail ?? ''
    editor.value = content.editor ?? ''
    categoryId.value = content.categoryId ?? null
    isDeleted.value = Boolean(content.isDeleted)
  } catch (e) {
    error.value = e instanceof Error ? e.message : 'Failed to load content'
  } finally {
    loading.value = false
  }
}

async function submit() {
  error.value = null
  const numericId = Number(idRef.value)
  const trimmedTitle = title.value.trim()
  if (!Number.isFinite(numericId)) {
    error.value = 'Invalid id'
    return
  }
  if (!trimmedTitle) {
    error.value = 'Title is required'
    return
  }

  const payload: UpdateContentPayload = {
    title: trimmedTitle,
    slug: optionalText(slug.value),
    keyword: optionalText(keyword.value),
    description: optionalText(description.value),
    thumbnail: optionalText(thumbnail.value),
    editor: optionalText(editor.value),
    categoryId: categoryId.value,
    isDeleted: isDeleted.value,
  }

  saving.value = true
  try {
    await contentService.update(numericId, payload)
    await router.push('/contents')
  } catch (e) {
    error.value = e instanceof Error ? e.message : 'Save failed'
  } finally {
    saving.value = false
  }
}

watch(idRef, () => {
  void load()
}, { immediate: true })
</script>

<template>
  <ContentPanel title="Edit content" col-class="col-lg-8">
    <p v-if="loading" class="text-muted">Loading...</p>
    <template v-else>
      <p v-if="error" class="text-danger">{{ error }}</p>
      <form @submit.prevent="submit">
        <div class="form-group">
          <label for="title">Title</label>
          <input id="title" v-model="title" type="text" class="form-control" autocomplete="off" />
        </div>

        <div class="form-group">
          <label for="slug">Slug</label>
          <input id="slug" v-model="slug" type="text" class="form-control" autocomplete="off" />
          <small class="form-text text-muted">Leave blank to regenerate from the title.</small>
        </div>

        <div class="form-group">
          <label for="category">Category</label>
          <select id="category" v-model="categoryId" class="form-control">
            <option :value="null">No category</option>
            <option v-for="category in categories" :key="category.id ?? category.name" :value="category.id">
              {{ category.name }}
            </option>
          </select>
        </div>

        <div class="form-group">
          <label for="keyword">Keyword</label>
          <input id="keyword" v-model="keyword" type="text" class="form-control" autocomplete="off" />
        </div>

        <div class="form-group">
          <label for="description">Description</label>
          <textarea id="description" v-model="description" class="form-control" rows="3" />
        </div>

        <div class="form-group">
          <label for="thumbnail">Thumbnail URL</label>
          <input id="thumbnail" v-model="thumbnail" type="text" class="form-control" autocomplete="off" />
        </div>

        <div class="form-group">
          <label for="editor">Content</label>
          <textarea id="editor" v-model="editor" class="form-control" rows="8" />
        </div>

        <div class="form-group form-check">
          <input id="isDeleted" v-model="isDeleted" type="checkbox" class="form-check-input" />
          <label class="form-check-label" for="isDeleted">Deleted</label>
        </div>

        <button type="submit" class="btn btn-primary" :disabled="saving">
          {{ saving ? 'Saving...' : 'Save Changes' }}
        </button>
        <RouterLink to="/contents" class="btn btn-light ml-2">Cancel</RouterLink>
      </form>
    </template>
  </ContentPanel>
</template>
