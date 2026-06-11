import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue')
  },
  {
    path: '/user',
    name: 'User',
    component: () => import('@/views/user/Index.vue'),
    redirect: '/user/home',
    children: [
      {
        path: 'home',
        name: 'UserHome',
        component: () => import('@/views/user/Home.vue')
      },
      {
        path: 'points',
        name: 'UserPoints',
        component: () => import('@/views/user/Points.vue')
      },
      {
        path: 'exchange',
        name: 'UserExchange',
        component: () => import('@/views/user/Exchange.vue')
      },
      {
        path: 'orders',
        name: 'UserOrders',
        component: () => import('@/views/user/Orders.vue')
      },
      {
        path: 'benefits',
        name: 'UserBenefits',
        component: () => import('@/views/user/Benefits.vue')
      },
      {
        path: 'activities',
        name: 'UserActivities',
        component: () => import('@/views/user/Activities.vue')
      },
      {
        path: 'cart',
        name: 'UserCart',
        component: () => import('@/views/user/Cart.vue')
      },
      {
        path: 'notices',
        name: 'UserNotices',
        component: () => import('@/views/user/Notices.vue')
      },
      {
        path: 'messages',
        name: 'UserMessages',
        component: () => import('@/views/user/Messages.vue')
      },
      {
        path: 'profile',
        name: 'UserProfile',
        component: () => import('@/views/user/Profile.vue')
      }
    ]
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/views/admin/Index.vue'),
    redirect: '/admin/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue')
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/Users.vue')
      },
      {
        path: 'products',
        name: 'AdminProducts',
        component: () => import('@/views/admin/Products.vue')
      },
      {
        path: 'rules',
        name: 'AdminRules',
        component: () => import('@/views/admin/Rules.vue')
      },
      {
        path: 'orders',
        name: 'AdminOrders',
        component: () => import('@/views/admin/Orders.vue')
      },
      {
        path: 'benefits',
        name: 'AdminBenefits',
        component: () => import('@/views/admin/Benefits.vue')
      },
      {
        path: 'rewards',
        name: 'AdminRewards',
        component: () => import('@/views/admin/Rewards.vue')
      },
      {
        path: 'activities',
        name: 'AdminActivities',
        component: () => import('@/views/admin/Activities.vue')
      },
      {
        path: 'messages',
        name: 'AdminMessages',
        component: () => import('@/views/admin/Messages.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  let token = localStorage.getItem('user')
  
  // 不需要登录的路径
  const noNeedLogin = ['/login', '/register']
  
  // 如果访问不需要登录的路径，直接放行
  if (noNeedLogin.includes(to.path)) {
    next()
  } else {
    // 如果没有登录，重定向到登录页面
    if (!token) {
      next('/login')
    } else {
      // 已经登录，放行
      next()
    }
  }
})

export default router
