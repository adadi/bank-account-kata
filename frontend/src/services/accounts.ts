import { http } from './http'
import { useSettingsStore } from '../stores/settings'

export type Money = number
export type UUID = string

export interface DepositRequest {
  amount: Money
  operationId: UUID
}

export interface DepositResponse {
  accountId: UUID
  balance: Money
  applied: boolean
}

export interface WithdrawRequest {
  amount: Money
  operationId: UUID
}

export interface WithdrawResponse {
  accountId: UUID
  balance: Money
}

export interface TransactionResponse {
  type: 'DEPOSIT' | 'WITHDRAWAL'
  amount: Money
  timestamp: string
  resultingBalance: Money
}

export interface AccountResponse {
  accountId: UUID
  balance: Money
}

function requireAccountId(): string {
  const settings = useSettingsStore()
  const id = settings.accountId?.trim()
  if (!id) throw new Error('Account ID not set')
  return id
}

export async function deposit(req: DepositRequest) {
  const id = requireAccountId()
  const { data, status } = await http.post<DepositResponse>(`/accounts/${id}/deposit`, req)
  return { data, status }
}

export async function withdraw(req: WithdrawRequest) {
  const id = requireAccountId()
  const { data, status } = await http.post<WithdrawResponse>(`/accounts/${id}/withdraw`, req)
  return { data, status }
}

export async function listTransactions(params?: { from?: string; to?: string }) {
  const id = requireAccountId()
  const { data } = await http.get<TransactionResponse[]>(`/accounts/${id}/transactions`, { params })
  return data
}

export async function getAccount(accountId?: string) {
  const id = (accountId ?? requireAccountId()).trim()
  const { data } = await http.get<AccountResponse>(`/accounts/${id}`)
  return { data }
}
