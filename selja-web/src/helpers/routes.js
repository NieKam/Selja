const serverUrl = 'http://localhost:8080'

export const singleAdItemApiUrl = (id) => `${serverUrl}/items/${id}`

export const allItemsApiUrl = (lat, long) => `${serverUrl}/items?lat=${lat}&long=${long}`

export const photoUrl = (url) => 
url ? `${serverUrl}/${url}` : "/static/placeholder.png"