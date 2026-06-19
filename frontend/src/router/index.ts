import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
    history: createWebHistory('/'),
    routes: [
        {
            path: '/login',
            name: 'Login',
            component: () => import('@/views/Login.vue')
        },
        {
            path: '/',
            name: 'Home',
            component: () => import('@/views/Home.vue'),
            meta: { requiresAuth: true }
        },
        {
            path: '/point-mall',
            name: 'PointMall',
            component: () => import('@/views/PointMall.vue'),
            meta: { requiresAuth: true }
        },
        {
            path: '/ai-chat',
            name: 'AiChat',
            component: () => import('@/views/AiChat.vue'),
            meta: { requiresAuth: true }
        },
        {
            path: '/orders',
            name: 'MyOrders',
            component: () => import('@/views/MyOrders.vue'),
            meta: { requiresAuth: true }
        },
        {
            path: '/orders/:id',
            name: 'OrderDetail',
            component: () => import('@/views/OrderDetail.vue'),
            meta: { requiresAuth: true }
        },
        {
            path: '/admin',
            name: 'AdminLayout',
            component: () => import('@/views/admin/Layout.vue'),
            meta: { requiresAuth: true, role: 'ADMIN' },
            children: [
                { path: 'dashboard', component: () => import('@/views/admin/Dashboard.vue') },
                { path: 'users', component: () => import('@/views/admin/UserManage.vue') },
                { path: 'reports', component: () => import('@/views/admin/ReportManage.vue') },
                { path: 'map', component: () => import('@/views/admin/MapManage.vue') },
                { path: 'point-config', component: () => import('@/views/admin/PointConfig.vue') },
                { path: 'ai-manage', component: () => import('@/views/admin/AiManage.vue') },
                { path: 'binding', component: () => import('@/views/admin/BindingAudit.vue') },
                { path: 'advisories', component: () => import('@/views/admin/AdvisoryOrderManage.vue') }
            ]
        }
    ]
})

router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token')
    const userRole = localStorage.getItem('role') || 'USER'

    if (to.meta.requiresAuth && !token) {
        next('/login')
    } else if (to.meta.role && to.meta.role !== userRole) {
        next('/') // 无权限访问后台则跳前台
    } else {
        next()
    }
})

export default router
