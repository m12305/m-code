const BASE = '/api/ai'

interface Message {
  role: string
  content: string
}

export function chatStream(
  messages: Message[],
  onToken: (token: string) => void,
  onDone: () => void,
  onError: (err: Error) => void,
): AbortController {
  const controller = new AbortController()
  const token = localStorage.getItem('token')

  fetch(`${BASE}/chat/stream`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    body: JSON.stringify({ messages }),
    signal: controller.signal,
  })
    .then(async (response) => {
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`)
      }
      const reader = response.body?.getReader()
      if (!reader) throw new Error('No response body')

      const decoder = new TextDecoder()
      let buffer = ''

      while (true) {
        const { done, value } = await reader.read()
        if (done) break

        buffer += decoder.decode(value, { stream: true })
        const lines = buffer.split('\n')
        buffer = lines.pop() || ''

        for (const line of lines) {
          const trimmed = line.trim()
          if (trimmed.startsWith('data:')) {
            const data = trimmed.slice(5).trim()
            if (data && data !== '[DONE]') {
              onToken(data)
            }
          }
        }
      }

      // flush remaining buffer
      if (buffer.trim().startsWith('data:')) {
        const data = buffer.trim().slice(5).trim()
        if (data && data !== '[DONE]') {
          onToken(data)
        }
      }

      onDone()
    })
    .catch((err) => {
      if (err.name !== 'AbortError') {
        onError(err)
      }
    })

  return controller
}
