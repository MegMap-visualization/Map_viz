// 开启initWorker
var initWorker
function createInitWorker() {
    initWorker = new Worker('../static/initWorker.js')
}

// 开启saveWorker
var saveWorker
function createSaveWorker() {
    saveWorker = new Worker('../static/saveWorker.js')
}