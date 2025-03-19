// 删除不存在remark对应数据
importScripts('https://unpkg.com/dexie@latest/dist/dexie.js', '../static/request.js')

var dbRemarkList = []
var remarkList = []
var deleteList = []
var addList = []
const limitSize = 0.5 * 1024 * 1024 * 1024
var db = new Dexie("lineDatabase");
db.version(1).stores({
  Lines: '&remark, xml, signals, objects, lanes, junctions, driveLanes, searchTime'
});


initDB()
async function initDB() {
  const res = await request('/api/getXmlList')
  remarkList = res.data
  const all = await db.Lines.toArray()
  const updateList = all.filter(item => !item.xml[0].ossPath).map(item => item.remark)
  dbRemarkList = all.map(item => item.remark)
  // 删除废弃remark
  dbRemarkList.forEach(remark => {
    const index = remarkList.indexOf(remark)
    if( index < 0) {
        db.Lines.delete(remark)
    } else {
        remarkList.splice(index, 1)
    }
  });
  try { 
      // 添加新的remark
    for (let i = 0; i < remarkList.length; i++) {
      const {usage} = await navigator.storage.estimate()
      if(usage >= limitSize) break
      const info = await db.Lines.get(remarkList[i])
      if(info !== undefined) continue 
      const res1 = await request('/api/getAllCenterLaneByRemark', {remark: remarkList[i]}, false)
      const saveData = Object.assign(res1.data, {remark: remarkList[i], searchTime: 0})
      await db.Lines.put(saveData)
      const res2 = await request('/api/getInfoByXml', {remark: remarkList[i]}, false)
      await db.Lines.update(remarkList[i], {driveLanes: res2.data.lanes})
    }
    for (let i = 0; i < updateList.length; i++) {
      console.log(updateList[i])
      const res1 = await request('/api/getAllCenterLaneByRemark', {remark: updateList[i]}, false)
      // console.log(res1)
      await db.Lines.update(updateList[i], res1.data)
    }
  } catch(e) {
    console.log(e)
  } finally {
    console.log('initworker closed')
    self.close()
  }
}
