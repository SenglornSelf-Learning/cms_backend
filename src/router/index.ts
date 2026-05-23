import { createRouter, createWebHistory } from 'vue-router'
import AdminLayout from '@/components/layout/AdminLayout.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: AdminLayout,
      children: [
        {
          path: '',
          name: 'dashboard',
          component: () => import('@/views/dashboard/DashboardView.vue'),
        },
        {
          path: 'categories',
          name: 'categories',
          component: () => import('@/views/category/CategoryListView.vue'),
        },
        {
          path: 'categories/new',
          name: 'category-new',
          component: () => import('@/views/category/CategoryCreateView.vue'),
        },
        {
          path: 'categories/:id',
          name: 'category-detail',
          component: () => import('@/views/category/CategoryDetailView.vue'),
          props: true,
        },
        {
          path: 'contents',
          name: 'contents',
          component: () => import('@/views/content/ContentListView.vue'),
        },
        {
          path: 'contents/new',
          name: 'content-new',
          component: () => import('@/views/content/ContentCreateView.vue'),
        },
        {
          path: 'contents/:id',
          name: 'content-detail',
          component: () => import('@/views/content/ContentDetailView.vue'),
          props: true,
        },
      ],
    },
  ],
})

router.beforeEach(() => {
  window.NProgress?.start()
})

router.afterEach(() => {
  window.NProgress?.done()
})

export default router
