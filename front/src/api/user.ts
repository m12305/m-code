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
