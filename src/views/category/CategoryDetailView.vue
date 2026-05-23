<script setup lang="ts">
import { ref, toRef, watch } from 'vue'
import ContentPanel from '@/components/common/ContentPanel.vue'
import { categoryService } from '@/services'
import type { Category } from '@/types/category'

const props = defineProps<{
  id: string
}>()

const idRef = toRef(props, 'id')
const category = ref<Category | null>(null)
const error = ref<string | null>(null)
const loading = ref(true)

async function load() {
  loading.value = true
  error.value = null
  category.value = null
  const numericId = Number(idRef.value)
  if (!Number.isFinite(numericId)) {
    error.value = 'Invalid id'
    loading.value = false
    return
  }
  try {
    category.value = await categoryService.findById(numericId)
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
  <ContentPanel title="Category detail" col-class="col-lg-8">
    <p v-if="loading" class="text-muted">Loading…</p>
    <p v-else-if="error" class="text-danger">{{ error }}</p>
    <dl v-else-if="category" class="row mb-0">
      <dt class="col-sm-3">Id</dt>
      <dd class="col-sm-9">{{ category.id }}</dd>
      <dt class="col-sm-3">Name</dt>
      <dd class="col-sm-9">{{ category.name }}</dd>
      <dt class="col-sm-3">Deleted</dt>
      <dd class="col-sm-9">{{ category.isDeleted ? 'Yes' : 'No' }}</dd>
    </dl>
    <RouterLink to="/categories" class="btn btn-light mt-3">Back to list</RouterLink>
  </ContentPanel>
</template>
