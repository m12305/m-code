import request from './request'

export function login(data: { username: string; password: string }) {
  return request.post('/user/login', data)
}

export function register(data: {
  username: string
  password: string
  nickname?: string
  email?: string
}) {
  return request.post('/user/register', data)
}

export function getProfile() {
  return request.get('/user/profile')
}

export function updateProfile(data: Record<string, unknown>) {
  return request.put('/user/profile', data)
}

// 管理员 - 用户管理
export function getUserList(params: {
  pageNum?: number
  pageSize?: number
  keyword?: string
  status?: number
  role?: number
}) {
  return request.get('/user/admin/users', { params })
}

export function getUserDetail(id: number) {
  return request.get(`/user/admin/users/${id}`)
}

export function updateUser(id: number, data: Record<string, unknown>) {
  return request.put(`/user/admin/users/${id}`, data)
}

export function deleteUser(id: number) {
  return request.delete(`/user/admin/users/${id}`)
}
