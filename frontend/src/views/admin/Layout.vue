<template>
  <div class="admin-wrapper">
    <!-- Decorative background elements -->
    <div class="glow-orb glow-1"></div>
    <div class="glow-orb glow-2"></div>
    
    <el-container class="admin-container">
      <!-- Premium Sidebar -->
      <el-aside width="260px" class="admin-sidebar flex flex-col">
        <!-- Brand -->
        <div class="sidebar-brand">
          <div class="brand-icon">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
              <path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2V9z" fill="rgba(255,255,255,0.9)"/>
              <path d="M9 22V12h6v10" fill="rgba(255,255,255,0.5)"/>
            </svg>
          </div>
          <div style="flex:1; min-width:0;">
            <p style="font-weight:900; color:white; font-size:15px; line-height:1.2; margin:0; white-space:nowrap;">社区智联</p>
            <p style="font-size:10px; color:rgba(255,255,255,0.4); margin:2px 0 0; white-space:nowrap;">管理控制台</p>
          </div>
          <div class="version-badge" style="flex-shrink:0;">v1.0</div>
        </div>
  
        <!-- Nav -->
        <nav class="flex-1 px-3 py-4 space-y-1 overflow-y-auto">
          <router-link v-for="item in navItems" :key="item.path" :to="item.path"
            class="nav-link" :class="{ 'nav-link-active': route.path === item.path }">
            <span class="nav-icon">{{ item.icon }}</span>
            <span class="font-medium text-sm">{{ item.label }}</span>
            <span v-if="item.badge" class="ml-auto badge-dot">{{ item.badge }}</span>
          </router-link>
        </nav>
  
        <!-- User Card -->
        <div class="sidebar-footer">
          <div class="user-card">
            <div class="user-avatar" style="flex-shrink:0;">A</div>
            <div style="flex:1; min-width:0;">
              <p style="color:white; font-weight:700; font-size:13px; margin:0; white-space:nowrap; overflow:hidden; text-overflow:ellipsis;">超级管理员</p>
              <p style="color:rgba(255,255,255,0.4); font-size:11px; margin:2px 0 0; white-space:nowrap; overflow:hidden; text-overflow:ellipsis;">物业总部</p>
            </div>
            <button @click="handleLogout" title="退出登录" class="logout-btn" style="flex-shrink:0;">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4M16 17l5-5-5-5M21 12H9"/>
              </svg>
            </button>
          </div>
        </div>
      </el-aside>
  
      <!-- Main Area -->
      <el-container class="flex flex-col overflow-hidden" style="background: transparent;">
        <!-- Top Bar -->
        <el-header class="admin-header">
          <div style="display: flex; align-items: center; gap: 12px;">
            <div class="breadcrumb-wrap">
              <span class="breadcrumb-root">控制台</span>
              <span class="breadcrumb-sep">/</span>
              <span class="breadcrumb-current">{{ currentRouteName }}</span>
            </div>
          </div>
          <div style="display: flex; align-items: center; gap: 12px;">
            <div class="header-badge">
              <div class="status-dot"></div>
              <span class="text-xs font-medium" style="color: #4ade80;">系统正常运行</span>
            </div>
            <div class="notification-btn" @click="handleNotice">
              <el-badge :value="reportCount + bindingCount" :hidden="reportCount + bindingCount === 0" class="item">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="rgba(255,255,255,0.7)" stroke-width="2">
                  <path d="M18 8A6 6 0 006 8c0 7-3 9-3 9h18s-3-2-3-9M13.73 21a2 2 0 01-3.46 0"/>
                </svg>
              </el-badge>
            </div>
          </div>
        </el-header>
  
        <!-- Content Area -->
        <el-main class="overflow-y-auto main-content">
          <router-view v-slot="{ Component }">
            <transition name="fade-slide" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const route = useRoute()

const reportCount = ref(0)
const bindingCount = ref(0)

const navItems = computed(() => [
  { path: '/admin/dashboard', icon: '📊', label: '数据看板', badge: null },
  { path: '/admin/users', icon: '👥', label: '居民管理', badge: null },
  { path: '/admin/reports', icon: '🚨', label: '违规举报审核', badge: reportCount.value > 0 ? reportCount.value : null },
  { path: '/admin/map', icon: '🗺️', label: '虚拟设备大屏', badge: null },
  { path: '/admin/point-config', icon: '⚙️', label: '积分规则配置', badge: null },
  { path: '/admin/ai-manage', icon: '🤖', label: 'AI 问答管理', badge: null },
  { path: '/admin/binding', icon: '🆔', label: '社区绑定审核', badge: bindingCount.value > 0 ? bindingCount.value : null },
  { path: '/admin/advisories', icon: '📝', label: '劝导工单管理', badge: null },
])

const routeNameMap: Record<string, string> = {
  '/admin/dashboard': '数据中心看板',
  '/admin/users': '居民账户管理',
  '/admin/reports': '违规事件工单',
  '/admin/map': '社区设备分布',
  '/admin/point-config': '积分规则配置',
  '/admin/ai-manage': 'AI 问答管理',
  '/admin/binding': '社区绑定审核',
  '/admin/advisories': '劝导工单跟进'
}
const currentRouteName = computed(() => routeNameMap[route.path] || '页面')

const handleLogout = async () => {
  await ElMessageBox.confirm('确定要退出管理系统？', '退出确认', { 
    confirmButtonText: '确认退出', 
    cancelButtonText: '取消',
    type: 'warning' 
  })
  localStorage.removeItem('token')
  localStorage.removeItem('role')
  router.push('/login')
}

const fetchNoticeCounts = async () => {
  try {
    // 获取待审核举报数
    const reportRes = await request.get('/report/list', { params: { pageSize: 1, status: 0 } })
    if (reportRes.data.code === 200) {
      reportCount.value = reportRes.data.data.total
    }
    // 获取待审核绑定数
    const bindingRes = await request.get('/binding/admin/pending')
    if (bindingRes.data.code === 200) {
      bindingCount.value = bindingRes.data.data.length
    }
  } catch (error) {
    console.error('获取通知数量失败:', error)
  }
}

const handleNotice = () => {
  const total = reportCount.value + bindingCount.value
  if (total === 0) {
    ElMessage({ message: '暂无待处理的新申请。', type: 'success', offset: 70 })
    return
  }
  ElMessage({
    message: `您有 ${total} 条待处理的新申请，请在对应的审核菜单（告警红点）中查看。`,
    type: 'info',
    duration: 5000,
    offset: 70
  })
}

onMounted(() => {
  fetchNoticeCounts()
  // 每 30 秒轮询一次
  const timer = setInterval(fetchNoticeCounts, 30000)
  onUnmounted(() => clearInterval(timer))
})
</script>

<style scoped>
.admin-wrapper {
  position: fixed;
  inset: 0;
  overflow: hidden;
  background: #0f172a; /* Deep base color */
  z-index: 100;
}

.admin-container {
  height: 100%;
  width: 100%;
}

.glow-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(120px);
  pointer-events: none;
  opacity: 0.15;
  z-index: 0;
}
.glow-1 {
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, #6366f1, transparent);
  top: -100px;
  right: -100px;
}
.glow-2 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, #ec4899, transparent);
  bottom: -50px;
  left: 20%;
}

.admin-sidebar {
  display: flex;
  flex-direction: column;
  background: rgba(30, 41, 59, 0.7);
  backdrop-filter: blur(20px);
  border-right: 1px solid rgba(255, 255, 255, 0.08);
  position: relative;
  z-index: 10;
}

.sidebar-brand {
  display: flex;
  flex-direction: column;
  background: linear-gradient(180deg, #1e293b 0%, #0f172a 100%);
  border-right: 1px solid rgba(255,255,255,0.06);
}

.sidebar-brand {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 24px 20px;
  border-bottom: 1px solid rgba(255,255,255,0.06);
}

.brand-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(102,126,234,0.3);
}

.version-badge {
  font-size: 10px;
  padding: 2px 8px;
  background: rgba(102,126,234,0.2);
  color: #a5b4fc;
  border-radius: 20px;
  border: 1px solid rgba(102,126,234,0.3);
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  border-radius: 14px;
  text-decoration: none;
  color: rgba(255,255,255,0.45);
  transition: all 0.25s ease;
  margin-bottom: 4px;
}
.nav-link:hover {
  color: rgba(255,255,255,0.9);
  background: rgba(255,255,255,0.05);
}
.nav-link-active {
  color: white !important;
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.4), rgba(168, 85, 247, 0.2)) !important;
  box-shadow: inset 0 1px 0 rgba(255,255,255,0.1);
  border: 1px solid rgba(99, 102, 241, 0.2);
}

.nav-icon { font-size: 18px; }

.badge-dot {
  background: linear-gradient(135deg, #f87171, #ef4444);
  color: white;
  font-size: 10px;
  font-weight: 700;
  padding: 1px 7px;
  border-radius: 10px;
}

.sidebar-footer {
  padding: 16px;
  border-top: 1px solid rgba(255,255,255,0.06);
}

.user-card {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px;
  border-radius: 16px;
  background: rgba(255,255,255,0.03);
  border: 1px solid rgba(255,255,255,0.06);
}

.user-avatar {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 900;
  color: white;
  font-size: 15px;
}

.logout-btn {
  width: 32px; height: 32px;
  display: flex; align-items: center; justify-content: center;
  border-radius: 10px;
  background: rgba(239,68,68,0.1);
  color: #f87171;
  border: 1px solid rgba(239,68,68,0.2);
  cursor: pointer;
  transition: all 0.2s;
}
.logout-btn:hover {
  background: rgba(239,68,68,0.25);
  color: white;
}

.admin-header {
  height: 64px !important;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  background: rgba(15, 23, 42, 0.4);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255,255,255,0.06);
  z-index: 20;
}

.breadcrumb-wrap .breadcrumb-root {
  color: rgba(255, 255, 255, 0.5);
  font-size: 13px;
}
.breadcrumb-wrap .breadcrumb-sep {
  color: rgba(255, 255, 255, 0.3);
  margin: 0 2px;
}
.breadcrumb-wrap .breadcrumb-current {
  color: #ffffff;
  font-weight: 700;
  font-size: 14px;
}

.header-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 5px 12px;
  background: rgba(74,222,128,0.08);
  border: 1px solid rgba(74,222,128,0.15);
  border-radius: 20px;
  flex-shrink: 0;
}

.status-dot {
  width: 7px; height: 7px;
  border-radius: 50%;
  background: #4ade80;
  animation: pulse 2s infinite;
}

.notification-btn {
  width: 36px; height: 36px;
  display: flex; align-items: center; justify-content: center;
  border-radius: 10px;
  background: rgba(255,255,255,0.03);
  border: 1px solid rgba(255,255,255,0.06);
  cursor: pointer;
  transition: all 0.2s;
  flex-shrink: 0;
}
.notification-btn:hover { background: rgba(255,255,255,0.08); }

/* Global Dark Table Overrides */
:deep(.dark-table) {
  background-color: transparent !important;
  --el-table-border-color: rgba(255, 255, 255, 0.08);
  --el-table-header-bg-color: rgba(255, 255, 255, 0.03);
  --el-table-tr-bg-color: transparent;
  --el-table-text-color: rgba(255, 255, 255, 0.85);
  --el-table-header-text-color: rgba(255, 255, 255, 0.5);
  --el-table-row-hover-bg-color: rgba(255, 255, 255, 0.05);
}

:deep(.dark-table .el-table__inner-wrapper::before),
:deep(.dark-table .el-table__border-left-patch),
:deep(.dark-table .el-table__border-bottom-patch) {
  background-color: rgba(255, 255, 255, 0.08) !important;
}

:deep(.dark-table th.el-table__cell) {
  font-weight: 800;
  text-transform: uppercase;
  font-size: 11px;
  letter-spacing: 0.05em;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08) !important;
}

:deep(.dark-table td.el-table__cell) {
  border-bottom: 1px solid rgba(255, 255, 255, 0.04) !important;
}

/* Fix fixed columns transparency */
:deep(.dark-table .el-table__fixed),
:deep(.dark-table .el-table__fixed-right) {
  background-color: #161e2e !important; /* Opaque dark color to prevent overlap */
  height: 100% !important;
}

:deep(.dark-table .el-table__fixed-right-patch) {
  background-color: #161e2e !important;
}

:deep(.dark-table .el-table__fixed th),
:deep(.dark-table .el-table__fixed-right th) {
  background-color: rgba(255, 255, 255, 0.03) !important;
}

:deep(.dark-table .el-table__fixed td),
:deep(.dark-table .el-table__fixed-right td) {
  background-color: #161e2e !important;
}

/* Global Dark Pagination Overrides */
:deep(.dark-pagination) {
  --el-pagination-bg-color: transparent !important;
  --el-pagination-button-bg-color: rgba(255, 255, 255, 0.05) !important;
  --el-pagination-hover-color: #6366f1 !important;
  --el-pagination-button-color: rgba(255, 255, 255, 0.6) !important;
  --el-pagination-button-disabled-bg-color: transparent !important;
}

:deep(.dark-pagination .el-pager li) {
  background: rgba(255, 255, 255, 0.05) !important;
  color: rgba(255, 255, 255, 0.5) !important;
  border-radius: 8px;
  margin: 0 4px;
  border: 1px solid rgba(255, 255, 255, 0.08);
}

:deep(.dark-pagination .el-pager li.is-active) {
  background: linear-gradient(135deg, #6366f1, #8b5cf6) !important;
  color: white !important;
  border: none !important;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
}

/* Element Plus Global Dark Dialog/Message Box */
:global(.el-message-box), :global(.el-dialog) {
  background: #1e293b !important;
  border: 1px solid rgba(255, 255, 255, 0.1) !important;
  border-radius: 20px !important;
  backdrop-filter: blur(20px);
}
:global(.el-message-box__title), :global(.el-dialog__title) {
  color: white !important;
}
:global(.el-message-box__content), :global(.el-dialog__body) {
  color: rgba(255, 255, 255, 0.7) !important;
}

@keyframes pulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.5; transform: scale(1.2); }
}
</style>
