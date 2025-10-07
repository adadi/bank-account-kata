import axios from 'axios'

const baseURL = import.meta.env.VITE_API_BASE_URL as string | undefined

if (!baseURL) {
  // Enforce required env var for correctness
  throw new Error('VITE_API_BASE_URL must be defined')
}

export const http = axios.create({
  baseURL,
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  },
  timeout: 10000,
})

export function getApiBaseUrl(): string {
  return baseURL
}

