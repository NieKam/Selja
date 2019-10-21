import { singleAdItemApiUrl, allItemsApiUrl, baseApiUrl } from './routes'
import * as api from './api'

export const getAll = (lat, long) =>
    api.get(allItemsApiUrl(lat, long))

export const getOne = (id, lat, long) =>
    api.get(singleAdItemApiUrl(id, lat, long))

export const createNewAd = (newItem, photoFile) => {
    var data = new FormData();
    data.append('ad', new Blob([JSON.stringify(newItem)], {
        type: "application/json"
    }));

    data.append("photo", photoFile);
    return api.uploadForm(baseApiUrl, data)
}