export default {
  updateMyData (state, data) {
    state.user.fullName = data.firstName + ' ' + data.lastName
    state.user.username = data.username
    state.user.id = data.id
    state.user.authenticated = true
  },
  updateMovies(state, data) {
    state.movies = data
  },
  addMovie(state, data) {
    state.movies.push(data)
  },
  updateVote(state, data) {
    const movieIndex = state.movies.findIndex(
      (movie) =>  {
        return data.movieId === movie.movieId
      })

    if (movieIndex !== -1) {
      let movie = state.movies[movieIndex]
        movie.likes = data.likes
        movie.dislikes = data.dislikes
      }
  },
  logout (state) {
    state.user.fullName = ''
    state.user.username = ''
    state.user.id = null
    state.user.authenticated = false
  }
}
