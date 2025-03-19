var db = new Dexie("lineDatabase");
db.version(1).stores({
  Lines: '&remark, xml, signals, objects, lanes, junctions, driveLanes, searchTime'
});
let canStorage = true
if(!navigator.storage || !navigator.storage.estimate) {
    canStorage = false
    window.alert('当前浏览器不支持本地存储')
}

// 查询 1 中心参考线  2 车道线
async function searchByDB(remark, type) {
    const data = await db.Lines.get(remark)
    if(data !== undefined && (type === 1 || data.driveLanes) && data.xml[0].ossPath) {
        db.Lines.update(remark, { searchTime: new Date().getTime() })
        return type === 1 ? data : data.driveLanes
    }
    if(type === 1) {
        const res = await request('/api/getAllCenterLaneByRemark',{remark})
        if(!saveWorker && canStorage) {
            createSaveWorker()
            saveWorker.postMessage({data: res.data, type, remark})
        }
        return res.data
    }

    if(type === 2) {
        const res = await request('/api/getInfoByXml', {remark})
        if(!saveWorker && canStorage) {
            createSaveWorker()
            saveWorker.postMessage({data: res.data.lanes, type, remark})
        }
        return res.data.lanes
    }
}