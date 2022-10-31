import accountService from '@/services/account'
import movieService from "@/services/movie";

export const logout = ({ commit }) => {
  window.sessionStorage.clear()
  commit('logout')
}

export const getMyData = ({ commit }) => {
  accountService.getMyData().then(data => {
    commit('updateMyData', data)
  })
}

export const updateVote = ({commit}, data) => {
  commit('updateVote', data)
}

export const addMovie = ({commit}, data) => {
  commit('addMovie', data)
}

export const getAllMovies = ({commit}) => {
  return new Promise((resolve, reject) => {
    movieService.findAll().then(data => {
      commit('updateMovies', data)
    }).catch(e => reject(e))
  });
}

export const getAllMoviesBy = ({commit}, username) => {
  movieService.findMovieByUsername(username).then(data => {
    commit('updateMovies', data)
  })
}
