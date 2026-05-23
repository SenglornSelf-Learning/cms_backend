/** App-wide constants (same role as planfit-domain in coreit.planfit). */
export const CMS = {
  APP_TITLE: 'CMS Admin',
  /** Named routes — values must stay in sync with `src/router/index.ts` `name` fields. */
  ROUTES: {
    DASHBOARD: 'dashboard',
    CATEGORIES: 'categories',
    CATEGORY_NEW: 'category-new',
    CATEGORY_DETAIL: 'category-detail',
  },
} as const
