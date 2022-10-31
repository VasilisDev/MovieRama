<template>
  <div>
  <div class="container public">
    <div class="row justify-content-center">
      <div class="form">
        <form @submit.prevent="submitForm">
          <div v-show="errorMessage" class="alert alert-danger failed">{{ errorMessage }}</div>
          <div class="form-group">
            <label for="username">{{ $t("loginPage.form.username.label") }}</label>
            <input type="text" class="form-control" id="username" v-model="form.username">
            <div class="field-error" v-if="$v.form.username.$dirty">
              <div class="error" v-if="!$v.form.username.required">{{ $t("loginPage.form.username.required") }}</div>
            </div>
          </div>
          <div class="form-group">
            <label for="password">{{ $t("loginPage.form.password.label") }}</label>
            <input type="password" class="form-control" id="password" v-model="form.password">
            <div class="field-error" v-if="$v.form.password.$dirty">
              <div class="error" v-if="!$v.form.password.required">{{ $t("loginPage.form.password.required") }}</div>
            </div>
          </div>
          <button type="submit" class="btn btn-primary btn-block">{{ $t("loginPage.form.submit") }}</button>
          <div class="links">
            <p class="sign-up text-muted">{{ $t("loginPage.form.noAccountYet") }} <router-link to="register" class="link-sign-up">{{ $t("loginPage.form.signUpHere") }}</router-link></p>
          </div>
        </form>
      </div>
    </div>
  </div>
  </div>
</template>

<script>
import { required } from 'vuelidate/lib/validators'
import authenticationService from '@/services/authentication'
import PageHeader from "@/components/PageHeader";

export default {
  name: 'LoginPage',
  components: {PageHeader},
  data: function () {
    return {
      form: {
        username: '',
        password: ''
      },
      errorMessage: ''
    }
  },
  validations: {
    form: {
      username: {
        required
      },
      password: {
        required
      }
    }
  },
  methods: {
    submitForm () {
      this.$v.$touch()
      if (this.$v.$invalid) {
        return
      }

      authenticationService.authenticate(this.form).then(() => {
        this.$bus.$emit('authenticated')
        this.$store.dispatch('getMyData')
        this.$router.push({name: 'home'})
      }).catch((error) => {
        this.errorMessage = error.message
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.links {
  margin: 30px 0 50px 0;
  text-align: center;
}
</style>
