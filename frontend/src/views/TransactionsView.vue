<template>
  <h1>Transactions</h1>
  <form class="form" @submit.prevent="onSearch">
    <div class="row">
      <div class="field">
        <label for="from">From</label>
        <input id="from" v-model="fromInput" type="datetime-local" />
      </div>
      <div class="field">
        <label for="to">To</label>
        <input id="to" v-model="toInput" type="datetime-local" />
      </div>
      <div class="field small">
        <label>&nbsp;</label>
        <button type="submit" :disabled="loading">Search</button>
      </div>
    </div>
    <ul class="hints">
      <li>If empty, defaults to last 30 days.</li>
      <li>from must be earlier than or equal to to.</li>
    </ul>
    <p v-if="error" class="error">{{ error }}</p>
    <p v-if="periodText" class="info">Applied period: {{ periodText }}</p>
  </form>

  <section v-if="transactions.length" class="table-section">
    <div class="row gap">
      <div>
        <label for="page">Page</label>
        <input id="page" type="number" v-model.number="page" :min="0" :max="maxPage" @change="coercePage" />
      </div>
      <div>
        <label for="size">Size</label>
        <input id="size" type="number" v-model.number="size" :min="1" :max="100" @change="coerceSize" />
      </div>
      <div class="controls">
        <button type="button" @click="firstPage" :disabled="page <= 0">« First</button>
        <button type="button" @click="prevPage" :disabled="page <= 0">‹ Prev</button>
        <button type="button" @click="nextPage" :disabled="!hasNext">Next ›</button>
        <button type="button" @click="lastPage" :disabled="!hasNext">Last »</button>
      </div>
      <div class="info-inline">
        <span>Total: {{ total }}</span>
        <span>• Page {{ page }} / {{ maxPage }}</span>
        <span>• hasNext: {{ hasNext }}</span>
      </div>
    </div>

    <table class="table">
      <thead>
        <tr>
          <th>Timestamp</th>
          <th>Type</th>
          <th>Amount</th>
          <th>Balance</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(t, idx) in paged" :key="idx">
          <td>{{ formatTimestamp(t.timestamp) }}</td>
          <td>{{ t.type }}</td>
          <td>{{ t.amount }}</td>
          <td>{{ t.resultingBalance }}</td>
        </tr>
      </tbody>
    </table>
  </section>

  <p v-else-if="!loading" class="muted">No transactions.</p>
</template>

<script setup lang="ts">
import { onMounted, ref, computed } from 'vue'
import { listTransactions, type TransactionResponse } from '../services/accounts'

const fromInput = ref('')
const toInput = ref('')
const loading = ref(false)
const error = ref('')

const transactions = ref<TransactionResponse[]>([])
const periodFrom = ref<Date | null>(null)
const periodTo = ref<Date | null>(null)

const size = ref(50)
const page = ref(0) // 0-based

const total = computed(() => transactions.value.length)
const maxPage = computed(() => Math.max(0, Math.ceil(total.value / size.value) - 1))
const hasNext = computed(() => page.value < maxPage.value)
const periodText = computed(() => periodFrom.value && periodTo.value ? `${formatLocal(periodFrom.value)} → ${formatLocal(periodTo.value)}` : '')

const sorted = computed(() => {
  return [...transactions.value].sort((a, b) => b.timestamp.localeCompare(a.timestamp))
})

const paged = computed(() => {
  const start = page.value * size.value
  return sorted.value.slice(start, start + size.value)
})

function parseLocalDateTime(value: string): Date | null {
  if (!value) return null
  // value like "2025-10-07T12:00"
  const d = new Date(value)
  return isNaN(d.getTime()) ? null : d
}

function toIsoUTC(d: Date): string {
  return d.toISOString()
}

function formatLocal(d: Date): string {
  const pad = (n: number) => String(n).padStart(2, '0')
  const yyyy = d.getFullYear()
  const mm = pad(d.getMonth() + 1)
  const dd = pad(d.getDate())
  const hh = pad(d.getHours())
  const mi = pad(d.getMinutes())
  const ss = pad(d.getSeconds())
  return `${yyyy}-${mm}-${dd} ${hh}:${mi}:${ss}`
}

function formatTimestamp(ts: string): string {
  const d = new Date(ts)
  if (isNaN(d.getTime())) return ts
  return formatLocal(d)
}

function coerceSize() {
  if (!Number.isFinite(size.value as any)) size.value = 50
  size.value = Math.max(1, Math.min(100, Math.floor(size.value)))
  if (page.value > maxPage.value) page.value = maxPage.value
}

function coercePage() {
  if (!Number.isFinite(page.value as any)) page.value = 0
  page.value = Math.max(0, Math.min(maxPage.value, Math.floor(page.value)))
}

function firstPage() { page.value = 0 }
function lastPage() { page.value = maxPage.value }
function prevPage() { if (page.value > 0) page.value -= 1 }
function nextPage() { if (hasNext.value) page.value += 1 }

async function onSearch() {
  error.value = ''
  loading.value = true
  try {
    // Determine effective period
    const now = new Date()
    const to = parseLocalDateTime(toInput.value) ?? now
    const from = parseLocalDateTime(fromInput.value) ?? new Date(to.getTime() - 30 * 24 * 60 * 60 * 1000)

    if (from.getTime() > to.getTime()) {
      error.value = 'Invalid range: from must be <= to'
      return
    }

    const params = { from: toIsoUTC(from), to: toIsoUTC(to) }
    const data = await listTransactions(params)
    transactions.value = Array.isArray(data) ? data : []
    periodFrom.value = from
    periodTo.value = to
    page.value = 0
    coerceSize()
  } catch (e: any) {
    const status = e?.response?.status
    const msg = e?.response?.data?.message
    if (status === 400) {
      error.value = msg || 'Invalid range'
    } else if (status === 404) {
      error.value = 'Account not found'
    } else {
      error.value = e?.message ?? 'Failed to load transactions'
    }
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  // Initial load with defaults (last 30 days)
  onSearch()
})
</script>

<style scoped>
.form {
  margin: 1rem auto;
  max-width: 960px;
  text-align: left;
}
.row {
  display: flex;
  gap: 0.75rem;
  align-items: end;
}
.row.gap { align-items: center; }
.field { flex: 1; }
.field.small { flex: 0 0 auto; }
label { display: block; margin-bottom: 0.25rem; }
input[type="datetime-local"],
input[type="number"] {
  width: 100%;
  padding: 0.5rem 0.75rem;
  border-radius: 6px;
  border: 1px solid #ccc;
  font-size: 1rem;
}
.hints {
  margin: 0.25rem 0 0.5rem;
  padding-left: 1rem;
  color: #666;
}
.error { color: #f60733; margin-top: 0.5rem; }
.info { color: #ffffff; margin-top: 0.25rem; }
.muted { color: #666; }

.table-section { margin-top: 1rem; text-align: left; }
.controls { display: flex; gap: 0.5rem; align-items: center; }
.info-inline { display: flex; gap: 0.75rem; align-items: center; }

table.table { width: 100%; border-collapse: collapse; margin-top: 0.75rem; }
table.table th, table.table td { border-bottom: 1px solid #ddd; padding: 0.5rem; text-align: left; }
table.table thead th { background: rgba(0,0,0,0.05); }
</style>
