import request from './request'

export function getExamList(params: { pageNum?: number; pageSize?: number }) {
  return request.get('/exam/list', { params })
}

export function getExamDetail(id: string) {
  return request.get(`/exam/${id}`)
}

export function getExamQuestions(id: string) {
  return request.get(`/exam/${id}/questions`)
}

export function createExam(data: Record<string, unknown>) {
  return request.post('/exam/add', data)
}

export function updateExam(data: Record<string, unknown>) {
  return request.put('/exam/update', data)
}

export function startExam(examId: string) {
  return request.post(`/exam/${examId}/start`)
}

export function submitExam(examId: string, data: {
  answers: Array<{ questionId: number; answer: string; language: number }>
}) {
  return request.post(`/exam/${examId}/submit`, data)
}

export function getMyExamRecords() {
  return request.get('/exam/my-records')
}

export function getMyExamRecord(examId: string) {
  return request.get(`/exam/${examId}/my-record`)
}

export function deleteExam(id: string) {
  return request.delete(`/exam/${id}`)
}

export function getExamRank(examId: string) {
  return request.get(`/exam/${examId}/rank`)
}
