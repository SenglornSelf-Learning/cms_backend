<script setup lang="ts">
import { onMounted, ref } from 'vue'
import ContentPanel from '@/components/common/ContentPanel.vue'
import { categoryService } from '@/services'
import type { Category } from '@/types/category'

const categories = ref<Category[]>([])
const error = ref<string | null>(null)
const loading = ref(true)

onMounted(async () => {
  try {
    categories.value = await categoryService.findAll()
  } catch (e) {
    error.value = e instanceof Error ? e.message : 'Failed to load categories'
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <ContentPanel title="Category Management">
    <template #actions>
      <RouterLink class="btn btn-primary" to="/categories/new">Create New Category</RouterLink>
    </template>
    <p v-if="error" class="text-danger">{{ error }}</p>
    <p v-else-if="loading" class="text-muted">Loading…</p>
    <table v-else class="table">
      <thead>
        <tr>
          <th scope="col">#</th>
          <th scope="col">Name</th>
          <th scope="col">Status</th>
          <th scope="col">Action</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="row in categories" :key="row.id ?? row.name">
          <td>{{ row.id }}</td>
          <td>{{ row.name }}</td>
          <td>
            <button
              type="button"
              class="btn-square btn-sm"
              :class="row.isDeleted ? 'btn-danger' : 'btn-success'"
            >
              {{ row.isDeleted ? 'Deleted' : 'Active' }}
            </button>
          </td>
          <td>
            <RouterLink v-if="row.id != null" class="text-success mr-2" :to="`/categories/${row.id}`">
              View
            </RouterLink>
            <span class="text-muted">Edit</span>
            <span class="text-muted ml-2">Delete</span>
          </td>
        </tr>
      </tbody>
    </table>
  </ContentPanel>
</template>
