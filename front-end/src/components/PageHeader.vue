<template>
  <nav class="navbar navbar-light bg-light ">
    <h1 class="logo" @click="goHome()">{{ $t('header.title') }}</h1>
    <div v-if="!isUserAuthenticated">
      <ul class="nav navbar-nav flex-row float-right" >
        <li class="nav-item">
          <router-link class="nav-link pr-3" to="login">{{ $t('header.login') }} </router-link>
        </li>
        <li class="nav-item">
          <router-link class="btn btn-outline-primary" to="register">{{ $t('header.signUp') }} </router-link>
        </li>
      </ul>
    </div>
    <div class="welcome-back" v-else>
      <p>{{ $t('header.welcomeMessage') }}<a href="#" @click="signOut()">{{this.user.fullName}}</a></p>
    </div>
  </nav>
</template>

<script>
import 'bootstrap/dist/js/bootstrap.min'
import {mapGetters} from 'vuex'
import meService from '@/services/account'
import notify from '@/utils/notify'

export default {
  name: 'PageHeader',
  computed: {
    ...mapGetters([
      'user'
    ]),
    isUserAuthenticated () {
      console.log(this.user.authenticated)
      return this.user.authenticated
    }
  },
  methods: {
    goHome() {
      this.$store.dispatch('getAllMovies')
      this.$router.push({name: 'home'}).catch(()=>{})
    },
    signOut() {
      meService.signOut().then(() => {
        this.$store.dispatch('logout')
      }).catch(error => {
        notify.error(error.message)
      })
    }
  }
}
</script>
<style>
.logo {
  cursor: pointer;
}
</style>
