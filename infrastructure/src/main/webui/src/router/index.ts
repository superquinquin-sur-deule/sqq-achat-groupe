import { createRouter, createWebHistory } from 'vue-router'
import { useAuth } from '@/composables/useAuth'
import type { AppRole } from '@/composables/useAuth'

declare module 'vue-router' {
  interface RouteMeta {
    requiresAuth?: boolean
    roles?: AppRole[]
  }
}

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'ventes',
      component: () => import('@/views/VenteListView.vue'),
    },
    {
      path: '/ventes/:venteId',
      name: 'home',
      component: () => import('@/views/HomeView.vue'),
    },
    {
      path: '/ventes/:venteId/cart',
      name: 'cart',
      component: () => import('@/views/CartView.vue'),
    },
    {
      path: '/ventes/:venteId/checkout',
      name: 'checkout',
      component: () => import('@/views/CheckoutView.vue'),
    },
    {
      path: '/confirmation',
      name: 'confirmation',
      component: () => import('@/views/ConfirmationView.vue'),
    },
    {
      path: '/payment-error',
      name: 'payment-error',
      component: () => import('@/views/PaymentErrorView.vue'),
    },
    {
      path: '/admin',
      component: () => import('@/views/admin/AdminLayout.vue'),
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          redirect: (to) => {
            const { isAdmin } = useAuth()
            return { name: isAdmin.value ? 'admin-dashboard' : 'admin-distribution' }
          },
        },
        {
          path: 'dashboard',
          name: 'admin-dashboard',
          component: () => import('@/views/admin/AdminDashboardView.vue'),
          meta: { roles: ['SQQ_ADMIN'] },
        },
        {
          path: 'products',
          name: 'admin-products',
          component: () => import('@/views/admin/AdminProductsView.vue'),
          meta: { roles: ['SQQ_ADMIN'] },
        },
        {
          path: 'timeslots',
          name: 'admin-timeslots',
          component: () => import('@/views/admin/AdminTimeSlotsView.vue'),
          meta: { roles: ['SQQ_ADMIN'] },
        },
        {
          path: 'ventes',
          name: 'admin-ventes',
          component: () => import('@/views/admin/AdminVentesView.vue'),
          meta: { roles: ['SQQ_ADMIN'] },
        },
        {
          path: 'orders',
          name: 'admin-orders',
          component: () => import('@/views/admin/AdminOrdersView.vue'),
          meta: { roles: ['SQQ_ADMIN'] },
        },
        {
          path: 'orders/:id',
          name: 'admin-order-detail',
          component: () => import('@/views/admin/AdminOrderDetailView.vue'),
          meta: { roles: ['SQQ_ADMIN'] },
        },
        {
          path: 'supplier-order',
          name: 'admin-supplier-order',
          component: () => import('@/views/admin/AdminSupplierOrderView.vue'),
          meta: { roles: ['SQQ_ADMIN'] },
        },
        {
          path: 'preparation',
          name: 'admin-preparation',
          component: () => import('@/views/admin/AdminPreparationView.vue'),
          meta: { roles: ['SQQ_ADMIN', 'SQQ_DISTRIB'] },
        },
        {
          path: 'distribution',
          name: 'admin-distribution',
          component: () => import('@/views/admin/AdminDistributionView.vue'),
          meta: { roles: ['SQQ_ADMIN', 'SQQ_DISTRIB'] },
        },
      ],
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('@/views/NotFoundView.vue'),
    },
  ],
})

router.beforeEach(async (to) => {
  if (to.matched.some((record) => record.meta.requiresAuth)) {
    const { checkAuth, redirectToLogin, hasRole } = useAuth()
    const isAuthenticated = await checkAuth()
    if (!isAuthenticated) {
      redirectToLogin()
      return false
    }

    const requiredRoles = to.meta.roles
    if (requiredRoles && requiredRoles.length > 0) {
      const hasRequiredRole = requiredRoles.some((role) => hasRole(role))
      if (!hasRequiredRole) {
        return { name: 'admin-distribution' }
      }
    }
  }
})

export default router
