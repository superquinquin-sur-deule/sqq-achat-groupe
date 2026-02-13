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
          redirect: '/admin/dashboard',
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
          path: 'campaign',
          name: 'admin-campaign',
          component: () => import('@/views/admin/AdminCampaignView.vue'),
        },
      ],
    },
    {
      path: '/backoffice',
      component: () => import('@/views/backoffice/BackofficeLayout.vue'),
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          redirect: '/backoffice/orders',
        },
        {
          path: 'orders',
          name: 'backoffice-orders',
          component: () => import('@/views/backoffice/BackofficeOrdersView.vue'),
        },
        {
          path: 'orders/:id',
          name: 'backoffice-order-detail',
          component: () => import('@/views/backoffice/BackofficeOrderDetailView.vue'),
        },
        {
          path: 'supplier-order',
          name: 'backoffice-supplier-order',
          component: () => import('@/views/backoffice/BackofficeSupplierOrderView.vue'),
        },
        {
          path: 'preparation',
          name: 'backoffice-preparation',
          component: () => import('@/views/backoffice/BackofficePreparationView.vue'),
        },
        {
          path: 'distribution',
          name: 'backoffice-distribution',
          component: () => import('@/views/backoffice/BackofficeDistributionView.vue'),
        },
      ],
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
