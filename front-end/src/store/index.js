import Vue from 'vue'
import Vuex from 'vuex'
import * as getters from './getters'
import * as actions from './actions'
import mutations from './mutations'
import createLogger from "vuex/dist/logger";
import createPersistedState from 'vuex-persistedstate'


Vue.use(Vuex)

const state = {
  user: {
    id: null,
    username: null,
    fullName: null,
    authenticated: false
  },
  movies: []
}

export default new Vuex.Store({
  state,
  getters,
  actions,
  mutations,
  plugins: process.env.NODE_ENV !== 'production'
    ? [createLogger()]
    : [
      createPersistedState({
        storage: window.sessionStorage,
        reducer(val) {
          return {
            user: val.user
          }
        }
      })
    ],
})
