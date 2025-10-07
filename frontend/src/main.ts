import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from './router'
import './style.css'
import App from './App.vue'
import { persistPlugin } from './plugins/persist'

const app = createApp(App)
const pinia = createPinia()
pinia.use(persistPlugin)
app.use(pinia)
app.use(router)
app.mount('#app')
