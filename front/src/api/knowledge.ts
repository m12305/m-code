import request from './request'

// 知识分类
export function getKnowledgeCategoryList() {
  return request.get('/knowledge/category')
}

export function addKnowledgeCategory(data: Record<string, unknown>) {
  return request.post('/knowledge/category', data)
}

// 文章
export function getArticleList(params: {
  pageNum?: number
  pageSize?: number
  categoryId?: number
}) {
  return request.get('/knowledge/article', { params })
}

export function searchArticles(params: {
  keyword: string
  pageNum?: number
  pageSize?: number
}) {
  return request.get('/knowledge/article/search', { params })
}

export function getArticleDetail(id: string) {
  return request.get(`/knowledge/article/${id}`)
}

export function addArticle(data: Record<string, unknown>) {
  return request.post('/knowledge/article', data)
}

export function updateArticle(data: Record<string, unknown>) {
  return request.put('/knowledge/article', data)
}

export function deleteArticle(id: string) {
  return request.delete(`/knowledge/article/${id}`)
}

// 学习路线
export function getLearningPathList() {
  return request.get('/knowledge/path')
}

export function getLearningPathDetail(id: string) {
  return request.get(`/knowledge/path/${id}`)
}

export function addLearningPath(data: Record<string, unknown>) {
  return request.post('/knowledge/path', data)
}
