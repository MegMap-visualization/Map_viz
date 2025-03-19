function paramsData(data) {
    var str = ''
    const keys = Object.keys(data)
    for(let key of keys) {
      str += `&${key}=${data[key]}`
    }
    return str.slice(1)
  }
  
  function request(url, data, isMain = true) {  
    var URL = url
    if(data) URL = url + '?' + paramsData(data)
    return fetch(URL).then(res => {
      if (!res.ok) {
        throw Error('接口请求异常')

      } 
      return res.json()
    }).catch(e => {
      console.log(e)
      if(isMain) removeTitleCircle()
    }) 
  }