import { apiUrl, singleAdItemApiUrl, allItemsApiUrl } from './routes'
import * as api from './api'

export const getAll = (lat, long) =>
    api.get(allItemsApiUrl(lat, long))

export const getOne = (id) =>
    api.get(singleAdItemApiUrl(id))