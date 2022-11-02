<template>
  <div>
    <div class="movies-container">
      <MovieSort @selected="sortedMovies"/>
      <div class="movies-section">
        <div class="row">
          <div class="col-10">
            <div v-show="errorMessage" class="alert alert-danger failed">{{ errorMessage }}</div>
            <div v-for="movie in this.movies" v-bind:key="movie.movieId" class="card">
              <div class="card-body">
                <h2 class="card-title">{{ movie.title }}</h2>
                <div class="card-text">Posted by <a href="#" @click="findMoviesBy(movie.publisherUsername)">{{
                    movie.publisherFullname
                  }}</a>
                  {{ daysAgo(movie.createdDate) }}
                </div>
                <p class="card-text">{{ movie.description }}</p>
                <div class="row">
                  <div class="col-6">
                    <div v-if="hasVotes(movie)">
                      <a :class="disableVoteLinksClass(movie.publisherUsername ,movie.likes)" href="#"
                         @click.prevent="vote(movie, true)">{{ movie.likes.length }} likes</a>
                      |
                      <a :class="disableVoteLinksClass(movie.publisherUsername, movie.dislikes)" href="#"
                         @click.prevent="vote(movie, false)">{{ movie.dislikes.length }} hates</a>
                    </div>
                    <div v-else>
                      <div v-if="isAuthenticated">
                          <span v-if="!hasPublishedTheMovie(movie.publisherUsername)">
                              {{ $t("homePage.firstTimeVote") }}
                          </span>
                        <a :class="hasPublishedTheMovie(movie.publisherUsername) ? 'disabled' : ''" href="#"
                           @click.prevent="vote(movie, true)">like</a>
                        |
                        <a :class="hasPublishedTheMovie(movie.publisherUsername) ? 'disabled' : ''" href="#"
                           @click.prevent="vote(movie, false)">hate</a>
                      </div>
                    </div>
                  </div>
                  <div v-if="isAuthenticated" class="col-6 text-right">
                    <div v-if="hasVotes(movie)">
                      <div>{{ prepareVoteMessage(movie) }} |
                        <a v-if="hasLike(movie)" href="#" @click.prevent="retract(movie.movieId)">
                          Unlike
                        </a>
                        <a v-if="hasDislike(movie)" href="#" @click.prevent="retract(movie.movieId)">
                          Unhate
                        </a>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div v-if="isAuthenticated" class="col-2">
            <button class="btn btn-dark" @click="openAddMovie">{{ $t("homePage.createNewMovie") }}</button>
          </div>
        </div>
      </div>
    </div>
    <AddMovieModal
      @added="onMovieAdded"
    />
  </div>
</template>

<script>
import $ from 'jquery'
import PageHeader from "@/components/PageHeader";
import moment from 'moment';
import {mapGetters} from 'vuex'
import AddMovieModal from "@/components/AddMovieModal";
import voteService from "@/services/vote";
import MovieSort from "@/components/MovieSort";

export default {
  components: {MovieSort, AddMovieModal, PageHeader},
  data() {
    return {
      errorMessage:'',
      voteResultMessage: '',
      hatesInAscending: false,
      createdDateAscending: false,
      likesInAscending: false

    }
  },
  computed: {
    ...mapGetters([
      'user',
      'movies'
    ]),
    isAuthenticated() {
      return this.user.authenticated
    }
  },
  methods: {
    sortedMovies(index) {
      switch (index) {
        case 0:
          this.likesInAscending = !this.likesInAscending
          let ascendingOrderLikesFn = (a, b) => {
            let l = a.likes.length
            let r = b.likes.length
            return l - r
          }
          if(this.likesInAscending) {
            return this.movies.sort(ascendingOrderLikesFn)
          }
          return this.movies.reverse(ascendingOrderLikesFn)
        case 1:
          this.hatesInAscending = !this.hatesInAscending
          let ascendingOrderDislikesLikesFn = (a, b) => {
            let l = a.dislikes.length
            let r = b.dislikes.length
            return l - r
          }
          if(this.hatesInAscending) {
            return this.movies.sort(ascendingOrderDislikesLikesFn)
          }
          return this.movies.reverse(ascendingOrderDislikesLikesFn)
        case 2:
          this.createdDateAscending = !this.createdDateAscending
          let createdDateAscendingFn = (a, b) => {
            return new Date(b.createdDate) - new Date(a.createdDate);
          }
          if(this.createdDateAscending) {
            return this.movies.sort(createdDateAscendingFn)
          }
          return this.movies.reverse(createdDateAscendingFn)
      }
    },
    hasLike(movie) {
      let authenticatedUser = this.user
      return movie.likes.some(l => l.username === authenticatedUser.username)
    },
    hasDislike(movie) {
      let authenticatedUser = this.user
      return movie.dislikes.some(d => d.username === authenticatedUser.username)
    },
    prepareVoteMessage(movie) {
      let isLike = this.hasLike(movie)
      let isDislike = this.hasDislike(movie)
      if (isLike) {
        return 'You like this movie '
      } else if (isDislike) {
        return 'You hate this movie '
      }
      return ''
    },
    hasPublishedTheMovie(publisherUsername) {
      return this.user.username === publisherUsername
    },
    disableVoteLinksClass(publisherUsername, votes) {
      let authenticatedUser = this.user
      let authenticated = authenticatedUser.authenticated
      let hasAlreadyUserVote = votes.some(vote => vote.username === authenticatedUser.username)
      return !authenticated || hasAlreadyUserVote || this.hasPublishedTheMovie(publisherUsername) ? 'disabled' : ''
    },
    vote(movie, isLike) {
      voteService.vote({
        publisherId: movie.publisherId,
        movieId: movie.movieId,
        isLike: isLike
      }).then(data => {
        this.$store.dispatch('updateVote', data)
      }).catch(error => {
        this.errorMessage = error.message
      })
    },
    retract(movieId) {
      voteService.retract(movieId).then(data => {
        this.$store.dispatch('updateVote', data)
      }).error(error => {
        this.errorMessage = error.message
      })
    },
    daysAgo(date) {
      return moment(date).fromNow();
    },
    hasVotes(movie) {
        let likes = movie.likes.length;
        let dislikes = movie.dislikes.length;
        return likes > 0 || dislikes > 0
    },
    openAddMovie() {
      $('#addMovieModal').modal('show')
    },
    onMovieAdded(movie) {
      this.$store.dispatch("addMovie", movie)
    },
    findMoviesBy(username) {
      this.$store.dispatch('getAllMoviesBy', username)
    }
  }
}
</script>

<style lang="scss" scoped>

.movies-container {
  padding: 0 35px;

  .disabled {
    pointer-events: none;
    color: gray
  }

  h2 {
    font-size: 18px;
    margin-bottom: 15px;
    font-weight: 400;
  }

  .movies-section {
    margin: 30px 10px;

    .card {
      margin-bottom: 20px;
    }
  }
}
</style>
