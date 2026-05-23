<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import ContentPanel from '@/components/common/ContentPanel.vue'
import { categoryService, contentService } from '@/services'
import type { Category } from '@/types/category'
import type { CreateContentPayload } from '@/types/content'

const router = useRouter()
const categories = ref<Category[]>([])
const title = ref('')
const slug = ref('')
const keyword = ref('')
const description = ref('')
const thumbnail = ref('')
const editor = ref('')
const categoryId = ref<number | null>(null)
const loadingCategories = ref(true)
const saving = ref(false)
const error = ref<string | null>(null)

onMounted(async () => {
  try {
    categories.value = await categoryService.findAll()
  } catch (e) {
    error.value = e instanceof Error ? e.message : 'Failed to load categories'
  } finally {
    loadingCategories.value = false
  }
})

function optionalText(value: string): string | undefined {
  const trimmed = value.trim()
  return trimmed || undefined
}

async function submit() {
  error.value = null
  const trimmedTitle = title.value.trim()
  if (!trimmedTitle) {
    error.value = 'Title is required'
    return
  }

  const payload: CreateContentPayload = {
    title: trimmedTitle,
    slug: optionalText(slug.value),
    keyword: optionalText(keyword.value),
    description: optionalText(description.value),
    thumbnail: optionalText(thumbnail.value),
    editor: optionalText(editor.value),
    categoryId: categoryId.value,
  }

  saving.value = true
  try {
    await contentService.create(payload)
    await router.push('/contents')
  } catch (e) {
    error.value = e instanceof Error ? e.message : 'Save failed'
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <ContentPanel title="Create content" col-class="col-lg-8">
    <p v-if="error" class="text-danger">{{ error }}</p>
    <form @submit.prevent="submit">
      <div class="form-group">
        <label for="title">Title</label>
        <input id="title" v-model="title" type="text" class="form-control" autocomplete="off" />
      </div>

      <div class="form-group">
        <label for="slug">Slug</label>
        <input id="slug" v-model="slug" type="text" class="form-control" autocomplete="off" />
        <small class="form-text text-muted">Leave blank to generate from the title.</small>
      </div>

      <div class="form-group">
        <label for="category">Category</label>
        <select id="category" v-model="categoryId" class="form-control" :disabled="loadingCategories">
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

      <button type="submit" class="btn btn-primary" :disabled="saving">
        {{ saving ? 'Saving...' : 'Save' }}
      </button>
      <RouterLink to="/contents" class="btn btn-light ml-2">Cancel</RouterLink>
    </form>
  </ContentPanel>
</template>
