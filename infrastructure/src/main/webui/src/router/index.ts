import { createRouter, createWebHistory } from 'vue-router'
import { useAuth } from '@/composables/useAuth'

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
          redirect: { name: 'admin-dashboard' },
        },
        {
          path: 'dashboard',
          name: 'admin-dashboard',
          component: () => import('@/views/admin/AdminDashboardView.vue'),
        },
        {
          path: 'products',
          name: 'admin-products',
          component: () => import('@/views/admin/AdminProductsView.vue'),
        },
        {
          path: 'timeslots',
          name: 'admin-timeslots',
          component: () => import('@/views/admin/AdminTimeSlotsView.vue'),
        },
        {
          path: 'ventes',
          name: 'admin-ventes',
          component: () => import('@/views/admin/AdminVentesView.vue'),
        },
        {
          path: 'orders',
          name: 'admin-orders',
          component: () => import('@/views/admin/AdminOrdersView.vue'),
        },
        {
          path: 'orders/:id',
          name: 'admin-order-detail',
          component: () => import('@/views/admin/AdminOrderDetailView.vue'),
        },
        {
          path: 'supplier-order',
          name: 'admin-supplier-order',
          component: () => import('@/views/admin/AdminSupplierOrderView.vue'),
        },
        {
          path: 'preparation',
          name: 'admin-preparation',
          component: () => import('@/views/admin/AdminPreparationView.vue'),
        },
        {
          path: 'distribution',
          name: 'admin-distribution',
          component: () => import('@/views/admin/AdminDistributionView.vue'),
        },
        {
          path: 'reception',
          name: 'admin-reception',
          component: () => import('@/views/admin/AdminReceptionView.vue'),
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
    const { checkAuth, redirectToLogin } = useAuth()
    const isAuthenticated = await checkAuth()
    if (!isAuthenticated) {
      redirectToLogin()
      return false
    }
  }
})

export default router
