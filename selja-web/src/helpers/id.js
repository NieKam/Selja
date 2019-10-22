export const generateUuid = () => {
    var S4 = function () {
        return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
    };
    var uuid = (S4() + S4() + "-" + S4() + "-" + S4() + "-" + S4() + "-" + S4() + S4() + S4());
    window.localStorage.setItem("id", uuid)
}

export const getUuid = () => window.localStorage.getItem("id")