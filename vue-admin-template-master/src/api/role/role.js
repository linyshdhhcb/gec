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
  },
  
  // 删除
  removeId(id){
    return request({
      url: `${BASE_URL}/removeRoleById/${id}`,
      method: 'delete',
    })
  },
  
  // 添加角色
  saveRole(role){
    return request({
      url: `${BASE_URL}/addRole`,
      method: 'post',
      data: role
    })
  },
  
  // 根据 id 查询角色
  getRoleById(id){
    return request({
      url: `${BASE_URL}/findRoleById/${id}`,
      method: 'get',
    })
  },
  
  // 修改角色
  updateRole(role){
    return request({
      url: `${BASE_URL}/updateRole`,
      method: 'post',
      data: role
    })
  },
  
  // 批量删除
  batchRemoveIds(ids){
    return request({
      url: `${BASE_URL}/deleteBatchRoleByIds`,
      method: 'delete',
      data: ids
    })
  }
}
