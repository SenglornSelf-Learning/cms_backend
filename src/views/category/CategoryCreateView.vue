<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import ContentPanel from '@/components/common/ContentPanel.vue'
import { categoryService } from '@/services'

const router = useRouter()
const name = ref('')
const saving = ref(false)
const error = ref<string | null>(null)

async function submit() {
  error.value = null
  const trimmed = name.value.trim()
  if (!trimmed) {
    error.value = 'Name is required'
    return
  }
  saving.value = true
  try {
    await categoryService.create({ name: trimmed })
    await router.push('/categories')
  } catch (e) {
    error.value = e instanceof Error ? e.message : 'Save failed'
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <ContentPanel title="Create category" col-class="col-lg-8">
    <p v-if="error" class="text-danger">{{ error }}</p>
    <form @submit.prevent="submit">
      <div class="form-group">
        <label for="name">Name</label>
        <input id="name" v-model="name" type="text" class="form-control" autocomplete="off" />
      </div>
      <button type="submit" class="btn btn-primary" :disabled="saving">
        {{ saving ? 'Saving…' : 'Save' }}
      </button>
      <RouterLink to="/categories" class="btn btn-light ml-2">Cancel</RouterLink>
    </form>
  </ContentPanel>
</template>
