
<template>
  <form @submit.prevent="addMovie">
    <div class="modal" tabindex="-1" role="dialog" backdrop="static" id="addMovieModal">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ $t('addMovieModal.title') }}</h5>
            <button type="button" class="close" @click="close" aria-label="Close">
              <span aria-hidden="true">&times;</span>
            </button>
          </div>
          <div class="modal-body">
            <div v-show="errorMessage" class="alert alert-danger failed">{{ errorMessage }}</div>
            <div class="form-group">
              <input type="text" class="form-control" id="title" v-model="title" placeholder="Title" maxlength="128">
              <div class="field-error" v-if="$v.title.$dirty">
                <div class="error" v-if="!$v.title.required">{{ $t('addMovieModal.required') }}</div>
              </div>
            </div>
            <div class="form-group">
              <input type="text" class="form-control" id="description" v-model="description" placeholder="Description" maxlength="256">
              <div class="field-error" v-if="$v.description.$dirty">
                <div class="error" v-if="!$v.description.required">{{ $t('addMovieModal.required') }}</div>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button type="submit" class="btn btn-primary">{{ $t('addMovieModal.add') }}</button>
            <button type="button" class="btn btn-default btn-cancel" @click="close">{{ $t('addMovieModal.cancel') }}</button>
          </div>
        </div>
      </div>
    </div>
  </form>
</template>

<script>
import $ from "jquery";
import { required } from 'vuelidate/lib/validators'
import movieService from "@/services/movie";

export default {
  name: 'AddMovieModal',
  data () {
    return {
      title: '',
      description: '',
      errorMessage: ''
    }
  },
  validations: {
    title: {
      required
    },
    description: {
      required
    }
  },
  mounted () {
    $('#addMovieModal').on('shown.bs.modal', () => {
      $('#title').trigger('focus')
    })
  },
  methods: {
    addMovie () {
      this.$v.$touch()
      if (this.$v.$invalid) {
        return
      }

      movieService.addMovie(this.$data).then((member) => {
        this.$emit('added', member)
        this.close()
      }).catch(error => {
        this.errorMessage = error.message
      })
    },
    close () {
      this.$v.$reset()
      this.text = ''
      this.description = ''
      this.errorMessage = ''
      $('#addMovieModal').modal('hide')
    }
  }
}
</script>

<style lang="scss" scoped>
.modal {
  .modal-dialog {
    width: 500px;
  }
}
</style>
