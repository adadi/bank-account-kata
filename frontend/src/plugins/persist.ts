import type { PiniaPluginContext } from 'pinia'

/**
 * Simple Pinia persistence plugin using localStorage.
 * Persists every store under key `pinia:<storeId>`.
 */
export function persistPlugin({ store }: PiniaPluginContext) {
  const key = `pinia:${store.$id}`

  try {
    const raw = localStorage.getItem(key)
    if (raw) {
      const fromStorage = JSON.parse(raw)
      if (fromStorage && typeof fromStorage === 'object') {
        store.$patch(fromStorage)
      }
    }
  } catch {
  }

  store.$subscribe(
    (_mutation, state) => {
      try {
        localStorage.setItem(key, JSON.stringify(state))
      } catch {
      }
    },
    { detached: true }
  )
}

