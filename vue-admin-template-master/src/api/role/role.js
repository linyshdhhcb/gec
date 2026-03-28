import request from '@/utils/request'
// 定义基本的url
const BASE_URL = '/admin/system/role';

export default {
  // 列表
  getPageList(page,limit,searchObj){
    return request({
      //  url: '/admin/system/role/'+page+"/"+limit,
      url: `${BASE_URL}/${page}/${limit}`,
      method: 'get',
      params: searchObj
    })
  }
}
