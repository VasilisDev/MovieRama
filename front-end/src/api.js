import axios from 'axios'
import {isEmpty,isNull} from 'lodash';

const DEFAULT_HEADERS = {
  'Accept': 'application/json',
  'Content-Type': 'application/json',
}
const doApiCall = (method, endpoint, headers = {}, params = {},body = null) => {
  const config = {
    method: method,
    url: `${endpoint}`,
    params: params,
    headers: Object.assign(DEFAULT_HEADERS, headers || {}),
  };

  if (!isEmpty(body) || !isNull(body)) {
    config.data = body;
  }

  return axios(config);
}


export const doGet = ({endpoint, headers ,params}) =>
  doApiCall('GET', endpoint, headers,params)

export const doPost = ({endpoint, body, headers,params}) =>
  doApiCall('POST', endpoint, headers, params,body)

export const doPut = ({endpoint, body, headers, params}) =>
  doApiCall('PUT', endpoint, headers, params,body)

export const doDelete = ({endpoint, params, headers}) =>
  doApiCall('DELETE', endpoint, params, headers)
