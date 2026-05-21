import request from './request'

export function submitCode(data: {
  questionId: number
  answer: string
  language: number
}) {
  return request.post('/judge/submit', data)
}

export function getSubmissionList(params: { pageNum?: number; pageSize?: number }) {
  return request.get('/judge/submission', { params })
}

export function getSubmissionDetail(id: string) {
  return request.get(`/judge/submission/${id}`)
}

export function getJudgeResults(submissionId: string) {
  return request.get(`/judge/result/${submissionId}`)
}
