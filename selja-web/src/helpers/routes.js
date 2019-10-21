const serverUrl = 'http://localhost:8080'
const placeholder = "/static/placeholder.jpg"

export const baseApiUrl = serverUrl + "/items"

export const singleAdItemApiUrl = (id, lat, long) => (lat && long) ? `${baseApiUrl}/${id}?lat=${lat}&long=${long}` : `${baseApiUrl}/${id}`

export const allItemsApiUrl = (lat, long) =>
    (lat && long) ? `${baseApiUrl}?lat=${lat}&long=${long}` : baseApiUrl

export const photoUrl = (url) =>
    url ? `${serverUrl}/${url}` : placeholder

export const previewPhotoUrl = (file) =>
    file ? URL.createObjectURL(file) : placeholder