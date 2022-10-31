<template>
  <div id="app">
    <PageHeader/>
    <transition mode="out-in" name="fade">
      <router-view/>
    </transition>
  </div>
</template>
<script>

import PageHeader from "@/components/PageHeader";
import {mapGetters} from "vuex";
export default {
  name: 'App',
  components: {PageHeader},
  computed: {
    ...mapGetters([
      'movies'
    ])
  },
  created () {
    this.$store.dispatch('getAllMovies')
  }
}
</script>

<style lang="scss">
html, body {
  height: 100%;
  font-size: 14px;
  font-family: "Helvetica Neue", Arial, Helvetica, sans-serif !important;
}
#app, .page {
  height: 100%;
  position: relative;
}
.page {
  display: flex;
  flex-direction: column;
}
.public.container {
  max-width: 900px;
}
input.form-control:focus,
textarea.form-control:focus {
  border: 1px solid #377EF6 !important;
}

.public {
  .form {
    margin-top: 50px;
    width: 320px;
    .form-group {
      label {
        font-weight: bold;
        color: #555;
      }
    }
  }
}
.field-error {
  .error {
    display: block;
    color: #ff0000;
  }
}
.modal {
  .modal-dialog {
    top: 25%;
    margin: 0 auto;
    .modal-header {
      border-bottom: none;
      padding: 1rem 1rem .5rem;
      .modal-title {
        font-size: 1rem;
      }
      .close {
        outline: none !important;
      }
    }
    .modal-body {
      padding-bottom: 0;
      textarea {
        resize: none;
        height: 100px;
      }
    }
    .modal-footer {
      justify-content: start;
      border-top: none;
      padding-top: 0;
      padding-bottom: 1.5rem;
      .btn-cancel {
        color: #666;
      }
    }
  }
}
.modal-open .modal-backdrop.show {
  opacity: .7;
}
</style>
