<script setup lang="ts">
import { ref, toRef, watch } from 'vue'
import { useRouter } from 'vue-router'
import ContentPanel from '@/components/common/ContentPanel.vue'
import { categoryService } from '@/services'

const props = defineProps<{
  id: string
}>()

const router = useRouter()
const idRef = toRef(props, 'id')
const name = ref('')
const isDeleted = ref(false)
const loading = ref(true)
const saving = ref(false)
const error = ref<string | null>(null)

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
    const category = await categoryService.findById(numericId)
    name.value = category.name
    isDeleted.value = Boolean(category.isDeleted)
  } catch (e) {
    error.value = e instanceof Error ? e.message : 'Failed to load category'
  } finally {
    loading.value = false
  }
}

async function submit() {
  error.value = null
  const numericId = Number(idRef.value)
  const trimmed = name.value.trim()
  if (!Number.isFinite(numericId)) {
    error.value = 'Invalid id'
    return
  }
  if (!trimmed) {
    error.value = 'Name is required'
    return
  }
  saving.value = true
  try {
    await categoryService.update(numericId, {
      name: trimmed,
      isDeleted: isDeleted.value,
    })
    await router.push('/categories')
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
  <ContentPanel title="Edit category" col-class="col-lg-8">
    <p v-if="loading" class="text-muted">Loading...</p>
    <template v-else>
      <p v-if="error" class="text-danger">{{ error }}</p>
      <form @submit.prevent="submit">
        <div class="form-group">
          <label for="name">Name</label>
          <input id="name" v-model="name" type="text" class="form-control" autocomplete="off" />
        </div>
        <div class="form-group form-check">
          <input id="isDeleted" v-model="isDeleted" type="checkbox" class="form-check-input" />
          <label class="form-check-label" for="isDeleted">Deleted</label>
        </div>
        <button type="submit" class="btn btn-primary" :disabled="saving">
          {{ saving ? 'Saving...' : 'Save Changes' }}
        </button>
        <RouterLink to="/categories" class="btn btn-light ml-2">Cancel</RouterLink>
      </form>
    </template>
  </ContentPanel>
</template>
