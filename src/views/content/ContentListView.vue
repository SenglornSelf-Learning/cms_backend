<script setup lang="ts">
import { onMounted, ref } from 'vue'
import ContentPanel from '@/components/common/ContentPanel.vue'
import { contentService } from '@/services'
import type { CmsContent } from '@/types/content'

const contents = ref<CmsContent[]>([])
const error = ref<string | null>(null)
const loading = ref(true)

onMounted(async () => {
  try {
    contents.value = await contentService.findAll()
  } catch (e) {
    error.value = e instanceof Error ? e.message : 'Failed to load contents'
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <ContentPanel title="Content Management">
    <template #actions>
      <RouterLink class="btn btn-primary" to="/contents/new">Create New Content</RouterLink>
    </template>
    <p v-if="error" class="text-danger">{{ error }}</p>
    <p v-else-if="loading" class="text-muted">Loading...</p>
    <table v-else class="table">
      <thead>
        <tr>
          <th scope="col">#</th>
          <th scope="col">Title</th>
          <th scope="col">Slug</th>
          <th scope="col">Status</th>
          <th scope="col">Action</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="row in contents" :key="row.id ?? row.slug ?? row.title">
          <td>{{ row.id }}</td>
          <td>{{ row.title }}</td>
          <td>{{ row.slug }}</td>
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
            <RouterLink v-if="row.id != null" class="text-success mr-2" :to="`/contents/${row.id}`">
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
