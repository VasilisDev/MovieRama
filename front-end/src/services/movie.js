import {doGet, doPost} from "@/api";
import errorParser from '@/utils/error-parser'

export default {

  addMovie(addMovieRequest) {
    return new Promise((resolve, reject) => {
      doPost({endpoint: 'movies', body: addMovieRequest}).then(({data}) => {
        resolve(data)
      }).catch((error) =>{
        reject(errorParser.parse(error))
      })
    });
  },

  findMovieByUsername(username) {
    return new Promise((resolve, reject) => {
        doGet({endpoint: `movies/?username=${username}`}).then(({data}) => {
          resolve(data)
        }).catch((error) => {
          reject(errorParser.parse(error))
        })
    })
  },
  findAll () {
    return new Promise((resolve, reject) => {
      doGet({endpoint: '/movies'}).then(({data}) => {
        resolve(data)
      }).catch((error) => {
        reject(errorParser.parse(error))
      })
    })
  }
}
