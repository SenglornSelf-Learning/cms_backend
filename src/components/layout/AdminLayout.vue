<script setup lang="ts">
import { onMounted, onUnmounted } from 'vue'
import { storeToRefs } from 'pinia'
import { CMS } from '@/constants/cms'
import { useCmsConfigStore } from '@/stores'
import { initSleekSidebar } from '@/theme/use-sleek-sidebar'

const cmsConfig = useCmsConfigStore()
const { apiBaseUrl } = storeToRefs(cmsConfig)

let teardownSidebar: (() => void) | undefined

onMounted(() => {
  teardownSidebar = initSleekSidebar()
})

onUnmounted(() => {
  teardownSidebar?.()
})
</script>

<template>
  <div class="wrapper">
    <aside class="left-sidebar bg-sidebar">
      <div id="sidebar" class="sidebar sidebar-with-footer">
        <div class="app-brand">
          <RouterLink to="/" class="brand-link" :title="CMS.APP_TITLE">
            <svg
              class="brand-icon"
              xmlns="http://www.w3.org/2000/svg"
              preserveAspectRatio="xMidYMid"
              width="30"
              height="33"
              viewBox="0 0 30 33"
            >
              <g fill="none" fill-rule="evenodd">
                <path class="logo-fill-blue" fill="#7DBCFF" d="M0 4v25l8 4V0zM22 4v25l8 4V0z" />
                <path class="logo-fill-white" fill="#FFF" d="M11 4v25l8 4V0z" />
              </g>
            </svg>
            <span class="brand-name text-truncate">{{ CMS.APP_TITLE }}</span>
          </RouterLink>
        </div>

        <div data-simplebar style="height: 100%">
          <ul class="nav sidebar-inner" id="sidebar-menu">
            <li>
              <RouterLink class="sidenav-item-link" to="/">
                <i class="mdi mdi-view-dashboard-outline"></i>
                <span class="nav-text">Dashboard</span>
              </RouterLink>
            </li>
            <li class="has-sub">
              <a
                class="sidenav-item-link"
                href="javascript:void(0)"
                data-toggle="collapse"
                data-target="#category-menu"
                aria-expanded="false"
                aria-controls="category-menu"
              >
                <i class="mdi mdi-pencil-box-multiple"></i>
                <span class="nav-text">Category</span>
                <b class="caret"></b>
              </a>
              <ul class="collapse show" id="category-menu" data-parent="#sidebar-menu">
                <div class="sub-menu">
                  <li>
                    <RouterLink class="sidenav-item-link" to="/categories/new">
                      <span class="nav-text">New Create</span>
                    </RouterLink>
                  </li>
                  <li>
                    <RouterLink class="sidenav-item-link" to="/categories">
                      <span class="nav-text">Data Table</span>
                    </RouterLink>
                  </li>
                </div>
              </ul>
            </li>
            <li class="has-sub">
              <a
                class="sidenav-item-link"
                href="javascript:void(0)"
                data-toggle="collapse"
                data-target="#content-menu"
                aria-expanded="false"
                aria-controls="content-menu"
              >
                <i class="mdi mdi-file-document-box-outline"></i>
                <span class="nav-text">Content</span>
                <b class="caret"></b>
              </a>
              <ul class="collapse show" id="content-menu" data-parent="#sidebar-menu">
                <div class="sub-menu">
                  <li>
                    <RouterLink class="sidenav-item-link" to="/contents/new">
                      <span class="nav-text">New Create</span>
                    </RouterLink>
                  </li>
                  <li>
                    <RouterLink class="sidenav-item-link" to="/contents">
                      <span class="nav-text">Data Table</span>
                    </RouterLink>
                  </li>
                </div>
              </ul>
            </li>
          </ul>
        </div>

        <div class="sidebar-footer">
          <hr class="separator mb-0" />
          <div class="sidebar-footer-content">
            <h6 class="text-uppercase">Admin</h6>
            <p class="text-muted small mb-0">API: {{ apiBaseUrl }}</p>
          </div>
        </div>
      </div>
    </aside>

    <div class="page-wrapper">
      <header class="main-header" id="header">
        <nav class="navbar navbar-static-top navbar-expand-lg">
          <button id="sidebar-toggler" class="sidebar-toggle" type="button">
            <span class="sr-only">Toggle navigation</span>
          </button>
          <div class="search-form d-none d-lg-inline-block">
            <div class="input-group">
              <button type="button" name="search" id="search-btn" class="btn btn-flat">
                <i class="mdi mdi-magnify"></i>
              </button>
              <input
                type="text"
                name="query"
                id="search-input"
                class="form-control"
                placeholder="Search…"
                autocomplete="off"
              />
            </div>
          </div>
        </nav>
      </header>

      <div class="content-wrapper">
        <div class="content">
          <RouterView />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.brand-link {
  display: flex;
  align-items: center;
  text-decoration: none;
  color: inherit;
}
</style>
