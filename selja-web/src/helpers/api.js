export const get = url =>
    new Promise(
        (resolve, reject) => {
            fetch(url)
                .then(response => {
                    if (response.ok) {
                        return response.json();
                    } else {
                        reject(response)
                    }
                })
                .then((responseJson) => {
                   resolve(responseJson)
                })
        }
    )