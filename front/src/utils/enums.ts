// 题目类型
export enum QuestionType {
  PROGRAMMING = 1,
  MULTIPLE_CHOICE = 2,
  SHORT_ANSWER = 3,
  TRUE_FALSE = 4,
}

export const QuestionTypeMap: Record<number, string> = {
  [QuestionType.PROGRAMMING]: '编程题',
  [QuestionType.MULTIPLE_CHOICE]: '选择题',
  [QuestionType.SHORT_ANSWER]: '简答题',
  [QuestionType.TRUE_FALSE]: '判断题',
}

// 难度
export enum Difficulty {
  EASY = 1,
  MEDIUM = 2,
  HARD = 3,
}

export const DifficultyMap: Record<number, string> = {
  [Difficulty.EASY]: '简单',
  [Difficulty.MEDIUM]: '中等',
  [Difficulty.HARD]: '困难',
}

export const DifficultyColor: Record<number, string> = {
  [Difficulty.EASY]: 'success',
  [Difficulty.MEDIUM]: 'warning',
  [Difficulty.HARD]: 'danger',
}

// 编程语言
export enum Language {
  JAVA = 1,
  PYTHON = 2,
  CPP = 3,
  C = 4,
  JAVASCRIPT = 5,
  GO = 6,
}

export const LanguageMap: Record<number, string> = {
  [Language.JAVA]: 'Java',
  [Language.PYTHON]: 'Python',
  [Language.CPP]: 'C++',
  [Language.C]: 'C',
  [Language.JAVASCRIPT]: 'JavaScript',
  [Language.GO]: 'Go',
}

export const LanguageMonacoMap: Record<number, string> = {
  [Language.JAVA]: 'java',
  [Language.PYTHON]: 'python',
  [Language.CPP]: 'cpp',
  [Language.C]: 'c',
  [Language.JAVASCRIPT]: 'javascript',
  [Language.GO]: 'go',
}

// 判题状态
export enum JudgeStatus {
  PENDING = 0,
  RUNNING = 1,
  ACCEPTED = 2,
  WRONG_ANSWER = 3,
  COMPILE_ERROR = 4,
  RUNTIME_ERROR = 5,
  TIME_LIMIT_EXCEEDED = 6,
  MEMORY_LIMIT_EXCEEDED = 7,
}

export const JudgeStatusMap: Record<number, string> = {
  [JudgeStatus.PENDING]: '等待判题',
  [JudgeStatus.RUNNING]: '判题中',
  [JudgeStatus.ACCEPTED]: '通过',
  [JudgeStatus.WRONG_ANSWER]: '答案错误',
  [JudgeStatus.COMPILE_ERROR]: '编译错误',
  [JudgeStatus.RUNTIME_ERROR]: '运行错误',
  [JudgeStatus.TIME_LIMIT_EXCEEDED]: '运行超时',
  [JudgeStatus.MEMORY_LIMIT_EXCEEDED]: '内存溢出',
}

export const JudgeStatusColor: Record<number, string> = {
  [JudgeStatus.PENDING]: 'info',
  [JudgeStatus.RUNNING]: 'warning',
  [JudgeStatus.ACCEPTED]: 'success',
  [JudgeStatus.WRONG_ANSWER]: 'danger',
  [JudgeStatus.COMPILE_ERROR]: 'danger',
  [JudgeStatus.RUNTIME_ERROR]: 'danger',
  [JudgeStatus.TIME_LIMIT_EXCEEDED]: 'warning',
  [JudgeStatus.MEMORY_LIMIT_EXCEEDED]: 'danger',
}

// 考试记录状态
export enum ExamRecordStatus {
  STARTED = 1,
  COMPLETED = 2,
  PARTIAL_JUDGING = 3,
  SUBMITTED = 4,
}

export const ExamRecordStatusMap: Record<number, string> = {
  [ExamRecordStatus.STARTED]: '已开始',
  [ExamRecordStatus.COMPLETED]: '已完成',
  [ExamRecordStatus.PARTIAL_JUDGING]: '部分判题中',
  [ExamRecordStatus.SUBMITTED]: '已提交',
}

// 用户角色
export enum UserRole {
  USER = 0,
  ADMIN = 1,
}
