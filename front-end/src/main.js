import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import axios from 'axios'
import Vuelidate from 'vuelidate'
import { i18n } from './i18n'
import eventBus from './event-bus'

// Bootstrap axios
axios.defaults.baseURL = '/api'
axios.defaults.headers.common.Accept = 'application/json'
axios.interceptors.response.use(
  response => response,
  (error) => {
    return Promise.reject(error)
  }
)

// Enable Vuelidate
Vue.use(Vuelidate)

Vue.config.productionTip = false

Vue.prototype.$bus = eventBus

new Vue({
  router,
  store,
  i18n,
  render: h => h(App)
}).$mount('#app')
