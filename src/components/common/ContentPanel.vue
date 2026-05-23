<script setup lang="ts">
/**
 * Page shell matching Thymeleaf `adminLayout` + `card` blocks:
 * row → col → card → card-body.
 */
withDefaults(
  defineProps<{
    title?: string
    /** Bootstrap column class, e.g. col-lg-8 */
    colClass?: string
  }>(),
  { colClass: 'col-12' },
)
</script>

<template>
  <div class="row">
    <div :class="colClass">
      <div class="card">
        <div class="card-body">
          <div
            v-if="title || $slots.actions"
            class="d-flex justify-content-between align-items-center flex-wrap mb-3"
          >
            <h1 v-if="title" class="h4 mb-0">{{ title }}</h1>
            <div v-if="$slots.actions" class="d-flex align-items-center flex-wrap">
              <slot name="actions" />
            </div>
          </div>
          <slot />
        </div>
      </div>
    </div>
  </div>
</template>
