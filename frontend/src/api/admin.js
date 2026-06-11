import request from '@/utils/request'

export const getAllUsers = () => {
  return request({
    url: '/api/admin/users',
    method: 'get'
  })
}

export const getUser = (id) => {
  return request({
    url: `/api/admin/users/${id}`,
    method: 'get'
  })
}

export const updateUserStatus = (data) => {
  return request({
    url: '/api/admin/users/status',
    method: 'put',
    data
  })
}

export const deleteUser = (id) => {
  return request({
    url: `/api/admin/users/${id}`,
    method: 'delete'
  })
}

export const getAllRules = () => {
  return request({
    url: '/api/admin/rules',
    method: 'get'
  })
}

export const getRule = (id) => {
  return request({
    url: `/api/admin/rules/${id}`,
    method: 'get'
  })
}

export const createRule = (data) => {
  return request({
    url: '/api/admin/rules',
    method: 'post',
    data
  })
}

export const updateRule = (data) => {
  return request({
    url: '/api/admin/rules',
    method: 'put',
    data
  })
}

export const deleteRule = (id) => {
  return request({
    url: `/api/admin/rules/${id}`,
    method: 'delete'
  })
}

export const getAllProducts = () => {
  return request({
    url: '/api/admin/products',
    method: 'get'
  })
}

export const getProduct = (id) => {
  return request({
    url: `/api/admin/products/${id}`,
    method: 'get'
  })
}

export const createProduct = (data) => {
  return request({
    url: '/api/admin/products',
    method: 'post',
    data
  })
}

export const updateProduct = (data) => {
  return request({
    url: '/api/admin/products',
    method: 'put',
    data
  })
}

export const deleteProduct = (id) => {
  return request({
    url: `/api/admin/products/${id}`,
    method: 'delete'
  })
}

export const getAllOrders = () => {
  return request({
    url: '/api/admin/orders',
    method: 'get'
  })
}

export const getOrder = (id) => {
  return request({
    url: `/api/admin/orders/${id}`,
    method: 'get'
  })
}

export const updateOrderStatus = (data) => {
  return request({
    url: '/api/admin/orders/status',
    method: 'put',
    data
  })
}

export const shipOrder = (data) => {
  return request({
    url: '/api/admin/orders/ship',
    method: 'post',
    data
  })
}

export const getAllBenefits = () => {
  return request({
    url: '/api/admin/benefits',
    method: 'get'
  })
}

export const getBenefit = (id) => {
  return request({
    url: `/api/admin/benefits/${id}`,
    method: 'get'
  })
}

export const createBenefit = (data) => {
  return request({
    url: '/api/admin/benefits',
    method: 'post',
    data
  })
}

export const updateBenefit = (data) => {
  return request({
    url: '/api/admin/benefits',
    method: 'put',
    data
  })
}

export const deleteBenefit = (id) => {
  return request({
    url: `/api/admin/benefits/${id}`,
    method: 'delete'
  })
}

export const getStatistics = () => {
  return request({
    url: '/api/admin/statistics',
    method: 'get'
  })
}

export const getUserPointsRecords = (userId) => {
  return request({
    url: `/api/admin/users/${userId}/points-records`,
    method: 'get'
  })
}

export const updateUserPoints = (userId, data) => {
  return request({
    url: `/api/admin/users/${userId}/points`,
    method: 'put',
    data
  })
}

export const getAllRewards = () => {
  return request({
    url: '/api/admin/rewards',
    method: 'get'
  })
}

export const createReward = (data) => {
  return request({
    url: '/api/admin/rewards',
    method: 'post',
    data
  })
}

export const updateReward = (id, data) => {
  return request({
    url: `/api/admin/rewards/${id}`,
    method: 'put',
    data
  })
}

export const deleteReward = (id) => {
  return request({
    url: `/api/admin/rewards/${id}`,
    method: 'delete'
  })
}

export const getAllBankActivities = () => {
  return request({
    url: '/api/admin/bank-activities',
    method: 'get'
  })
}

export const getBankActivity = (id) => {
  return request({
    url: `/api/admin/bank-activities/${id}`,
    method: 'get'
  })
}

export const createBankActivity = (data) => {
  return request({
    url: '/api/admin/bank-activities',
    method: 'post',
    data
  })
}

export const updateBankActivity = (data) => {
  return request({
    url: '/api/admin/bank-activities',
    method: 'put',
    data
  })
}

export const deleteBankActivity = (id) => {
  return request({
    url: `/api/admin/bank-activities/${id}`,
    method: 'delete'
  })
}

export const getActivityParticipants = (id) => {
  return request({
    url: `/api/admin/bank-activities/${id}/participants`,
    method: 'get'
  })
}

export const getAllMessages = () => {
  return request({
    url: '/api/admin/messages',
    method: 'get'
  })
}

export const getUnrepliedMessages = () => {
  return request({
    url: '/api/admin/messages/unreplied',
    method: 'get'
  })
}

export const replyMessage = (id, data) => {
  return request({
    url: `/api/admin/messages/${id}/reply`,
    method: 'put',
    data
  })
}

export const deleteMessage = (id) => {
  return request({
    url: `/api/admin/messages/${id}`,
    method: 'delete'
  })
}

export const getAllReviews = () => {
  return request({
    url: '/api/admin/reviews',
    method: 'get'
  })
}

export const updateReviewStatus = (id, data) => {
  return request({
    url: `/api/admin/reviews/${id}/status`,
    method: 'put',
    data
  })
}

export const deleteReview = (id) => {
  return request({
    url: `/api/admin/reviews/${id}`,
    method: 'delete'
  })
}
