<script setup lang="ts">
import { ref, toRef, watch } from 'vue'
import ContentPanel from '@/components/common/ContentPanel.vue'
import { contentService } from '@/services'
import type { CmsContent } from '@/types/content'

const props = defineProps<{
  id: string
}>()

const idRef = toRef(props, 'id')
const content = ref<CmsContent | null>(null)
const error = ref<string | null>(null)
const loading = ref(true)

async function load() {
  loading.value = true
  error.value = null
  content.value = null
  const numericId = Number(idRef.value)
  if (!Number.isFinite(numericId)) {
    error.value = 'Invalid id'
    loading.value = false
    return
  }
  try {
    content.value = await contentService.findById(numericId)
  } catch (e) {
    error.value = e instanceof Error ? e.message : 'Not found'
  } finally {
    loading.value = false
  }
}

watch(idRef, () => {
  void load()
}, { immediate: true })
</script>

<template>
  <ContentPanel title="Content detail" col-class="col-lg-8">
    <p v-if="loading" class="text-muted">Loading...</p>
    <p v-else-if="error" class="text-danger">{{ error }}</p>
    <dl v-else-if="content" class="row mb-0">
      <dt class="col-sm-3">Id</dt>
      <dd class="col-sm-9">{{ content.id }}</dd>
      <dt class="col-sm-3">Title</dt>
      <dd class="col-sm-9">{{ content.title }}</dd>
      <dt class="col-sm-3">Slug</dt>
      <dd class="col-sm-9">{{ content.slug }}</dd>
      <dt class="col-sm-3">Keyword</dt>
      <dd class="col-sm-9">{{ content.keyword }}</dd>
      <dt class="col-sm-3">Category</dt>
      <dd class="col-sm-9">{{ content.categoryId ?? 'None' }}</dd>
      <dt class="col-sm-3">Deleted</dt>
      <dd class="col-sm-9">{{ content.isDeleted ? 'Yes' : 'No' }}</dd>
      <dt class="col-sm-3">Description</dt>
      <dd class="col-sm-9">{{ content.description }}</dd>
      <dt class="col-sm-3">Content</dt>
      <dd class="col-sm-9">
        <pre class="mb-0">{{ content.editor }}</pre>
      </dd>
    </dl>
    <RouterLink to="/contents" class="btn btn-light mt-3">Back to list</RouterLink>
  </ContentPanel>
</template>
