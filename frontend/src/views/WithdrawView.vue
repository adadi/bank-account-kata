<template>
  <h1>Withdraw</h1>
  <form class="form" @submit.prevent="onSubmit">
    <label for="amount">Amount</label>
    <div class="row">
      <input
        id="amount"
        v-model="amountInput"
        type="text"
        inputmode="decimal"
        autocomplete="off"
        placeholder="e.g. 10.00"
      />
      <button type="submit" :disabled="submitting">Withdraw</button>
    </div>
    <ul class="hints">
      <li>amount > 0</li>
      <li>Up to 2 decimal places</li>
    </ul>
    <p v-if="error" class="error">{{ error }}</p>
    <p v-if="successMsg" class="success">{{ successMsg }}</p>
    <p v-if="balanceText" class="info">{{ balanceText }}</p>
  </form>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import { withdraw, type WithdrawRequest } from "../services/accounts";
import { useAccountStore } from "../stores/account";

const amountInput = ref("");
const error = ref("");
const successMsg = ref("");
const submitting = ref(false);

const account = useAccountStore();
const balanceText = computed(() =>
  account.balance !== undefined ? `Balance: ${account.balance}` : ""
);

const AMOUNT_REGEX = /^(?:\d+)(?:\.\d{1,2})?$/;

function genOperationId(): string {
  return crypto.randomUUID();
}

async function onSubmit() {
  error.value = "";
  successMsg.value = "";

  const raw = amountInput.value.trim();
  if (!AMOUNT_REGEX.test(raw)) {
    error.value = "Invalid amount.";
    return;
  }
  const amount = Number(raw);
  if (!isFinite(amount) || amount <= 0) {
    error.value = "Invalid amount.";
    return;
  }

  const req: WithdrawRequest = {
    amount,
    operationId: genOperationId(),
  };

  submitting.value = true;
  try {
    const { data } = await withdraw(req);
    // Update store balance
    account.balance = data.balance;
    successMsg.value = "Withdrawal completed";
    amountInput.value = "";
  } catch (e: any) {
    const status = e?.response?.status;
    if (status === 409) {
      // Insufficient funds. Show message and update balance if provided.
      error.value = "Insufficient funds";
      const current = e?.response?.data?.currentBalance;
      if (typeof current === "number") {
        account.balance = current;
      }
    } else if (status === 400) {
      error.value = "Invalid amount.";
    } else if (status === 404) {
      error.value = "Account not found.";
    } else {
      error.value = e?.message ?? "Unknown error";
    }
  } finally {
    submitting.value = false;
  }
}
</script>

<style scoped>
.form {
  margin: 1rem auto;
  max-width: 640px;
  text-align: left;
}
.row {
  display: flex;
  gap: 0.5rem;
}
input[type="text"] {
  flex: 1;
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
.error {
  color: #f60733;
  margin-top: 0.5rem;
}
.success {
  color: #22ff00;
  margin-top: 0.5rem;
}
.info {
  color: #ffffff;
  margin-top: 0.25rem;
}
</style>
