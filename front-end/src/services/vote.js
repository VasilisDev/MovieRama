import {doDelete, doPut} from "@/api";
import errorParser from '@/utils/error-parser'

export default  {

  vote(addVoteRequest) {
    return new Promise((resolve, reject) => {
      doPut({endpoint: 'vote', body: addVoteRequest}).then(({data}) => {
        resolve(data)
      }).catch((error) =>{
        reject(errorParser.parse(error))
      })
    });
  },
  retract (movieId) {
    return new Promise((resolve, reject) => {
      doDelete({endpoint: `vote?movieId=${movieId}`}).then(({data}) => {
        resolve(data)
      }).catch((error) =>{
        console.log(JSON.stringify(data))
        reject(errorParser.parse(error))
      })
    });
  }
}
