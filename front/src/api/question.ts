import request from './request'

export interface QuestionQuery {
  pageNum?: number
  pageSize?: number
  categoryId?: number
  difficulty?: number
  type?: number
  sectionId?: number
}

export function getQuestionList(params: QuestionQuery) {
  return request.get('/question/list', { params })
}

export function getQuestionDetail(id: string) {
  return request.get(`/question/detail/${id}`)
}

export function addQuestion(data: Record<string, unknown>) {
  return request.post('/question/add', data)
}

export function updateQuestion(data: Record<string, unknown>) {
  return request.put('/question/update', data)
}

export function deleteQuestion(id: string) {
  return request.delete(`/question/delete/${id}`)
}

// 分类
export function getCategoryList() {
  return request.get('/question/category')
}

export function addCategory(data: Record<string, unknown>) {
  return request.post('/question/category', data)
}

export function updateCategory(data: Record<string, unknown>) {
  return request.put('/question/category', data)
}

export function deleteCategory(id: string) {
  return request.delete(`/question/category/${id}`)
}

// 标签
export function getTagList() {
  return request.get('/question/tag')
}

export function addTag(data: Record<string, unknown>) {
  return request.post('/question/tag', data)
}

export function updateTag(data: Record<string, unknown>) {
  return request.put('/question/tag', data)
}

export function deleteTag(id: string) {
  return request.delete(`/question/tag/${id}`)
}

// 板块
export function getSectionList() {
  return request.get('/question/section')
}

export function addSection(data: Record<string, unknown>) {
  return request.post('/question/section', data)
}

export function updateSection(data: Record<string, unknown>) {
  return request.put('/question/section', data)
}

export function deleteSection(id: string) {
  return request.delete(`/question/section/${id}`)
}
