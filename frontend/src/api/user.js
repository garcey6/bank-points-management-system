import request from '@/utils/request'

export const login = (data) => {
  return request({
    url: '/api/user/login',
    method: 'post',
    data
  })
}

export const register = (data) => {
  return request({
    url: '/api/user/register',
    method: 'post',
    data
  })
}

export const getUserInfo = (id) => {
  return request({
    url: `/api/user/info/${id}`,
    method: 'get'
  })
}

export const updateUser = (data) => {
  return request({
    url: '/api/user/update',
    method: 'put',
    data
  })
}

export const getPointsAccount = (userId) => {
  return request({
    url: `/api/user/points/${userId}`,
    method: 'get'
  })
}

export const getPointsRecords = (userId) => {
  return request({
    url: `/api/user/points/records/${userId}`,
    method: 'get'
  })
}

export const getPointsRecordsByType = (userId, type) => {
  return request({
    url: `/api/user/points/records/${userId}/${type}`,
    method: 'get'
  })
}

export const getProducts = () => {
  return request({
    url: '/api/user/products',
    method: 'get'
  })
}

export const getProduct = (id) => {
  return request({
    url: `/api/user/products/${id}`,
    method: 'get'
  })
}

export const searchProducts = (name) => {
  return request({
    url: '/api/user/products/search',
    method: 'get',
    params: { name }
  })
}

export const exchangeProduct = (userId, data) => {
  return request({
    url: `/api/user/exchange/${userId}`,
    method: 'post',
    data
  })
}

export const getUserOrders = (userId) => {
  return request({
    url: `/api/user/orders/${userId}`,
    method: 'get'
  })
}

export const getBenefits = (memberLevel) => {
  return request({
    url: `/api/user/benefits/${memberLevel}`,
    method: 'get'
  })
}

export const getNextLevel = (userId) => {
  return request({
    url: `/api/user/member/next-level/${userId}`,
    method: 'get'
  })
}

export const getActivities = () => {
  return request({
    url: '/api/user/activities',
    method: 'get'
  })
}

export const getCart = (userId) => {
  return request({
    url: `/api/user/cart/${userId}`,
    method: 'get'
  })
}

export const addToCart = (userId, data) => {
  return request({
    url: `/api/user/cart/${userId}`,
    method: 'post',
    data
  })
}

export const updateCartItem = (cartId, data) => {
  return request({
    url: `/api/user/cart/${cartId}`,
    method: 'put',
    data
  })
}

export const removeFromCart = (cartId) => {
  return request({
    url: `/api/user/cart/${cartId}`,
    method: 'delete'
  })
}

export const clearCart = (userId) => {
  return request({
    url: `/api/user/cart/clear/${userId}`,
    method: 'delete'
  })
}

export const getProductReviews = (productId) => {
  return request({
    url: `/api/user/reviews/product/${productId}`,
    method: 'get'
  })
}

export const getUserReviews = (userId) => {
  return request({
    url: `/api/user/reviews/user/${userId}`,
    method: 'get'
  })
}

export const addReview = (data) => {
  return request({
    url: '/api/user/reviews',
    method: 'post',
    data
  })
}

export const getBankActivities = () => {
  return request({
    url: '/api/user/bank-activities',
    method: 'get'
  })
}

export const getBankActivity = (id) => {
  return request({
    url: `/api/user/bank-activities/${id}`,
    method: 'get'
  })
}

export const getUserActivities = (userId) => {
  return request({
    url: `/api/user/user-activities/${userId}`,
    method: 'get'
  })
}

export const participateActivity = (userId, data) => {
  return request({
    url: `/api/user/user-activities/${userId}`,
    method: 'post',
    data
  })
}

export const cancelParticipation = (userId, activityId) => {
  return request({
    url: `/api/user/user-activities/${userId}/${activityId}`,
    method: 'delete'
  })
}

export const getUserMessages = (userId) => {
  return request({
    url: `/api/user/messages/${userId}`,
    method: 'get'
  })
}

export const addMessage = (data) => {
  return request({
    url: '/api/user/messages',
    method: 'post',
    data
  })
}
