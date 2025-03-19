importScripts('https://unpkg.com/dexie@latest/dist/dexie.js',  '../static/request.js')

var db = new Dexie("lineDatabase");
db.version(1).stores({
  Lines: '&remark, xml, signals, objects, lanes, junctions, driveLanes, searchTime'
});

const limitSize = 0.5 * 1024 * 1024 * 1024
var sortData

onmessage = async (msg) => {
    const {data, type, remark} = msg.data
    let size = await navigator.storage.estimate()
    let usage = size.usage
    sortData = await db.Lines.orderBy('searchTime').toArray()
    try {
      while(usage >= limitSize && sortData.length) {
        await db.Lines.delete(sortData[0].remark)
        sortData.shift()
        size = await navigator.storage.estimate()
        usage = size.usage
      }
    } catch(e) {
      console.log(e)
      return
    }
    if(type === 1) {
        const bool = await save(data, 1, remark)
        if(bool) {
            const res = await request('/api/getInfoByXml', {remark}, false)
            save(res.data.lanes, 2, remark)
        }
    }
    if(type === 2) save(data, type, remark)
}

async function save(data, type, remark) {
    let count = 0
    while(count < 3) {
        if(type === 1) {
            // console.log(data, count)
            const res = await db.Lines.put(Object.assign(data, { searchTime: new Date().getTime(), remark }))
            if(res === remark) return true
            count++
            await db.Lines.delete(sortData[0].remark).catch(() => {})
            sortData.shift()
        }
        if(type === 2) {
            const res = await db.Lines.update(remark, {driveLanes: data})
            if(res === 1) return true
            count++
            await db.Lines.delete(sortData[0].remark).catch(() => {})
            sortData.shift()
        }
    }
    return false
}

