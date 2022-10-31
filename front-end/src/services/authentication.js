import {doPost} from "@/api";

export default {
  authenticate(detail) {
    return new Promise((resolve, reject) => {
      doPost({endpoint: '/authentication', body: detail}).then(({data}) => {
        resolve(data)
      }).catch((error) => {
        reject(error)
      })
    })
  }
}
