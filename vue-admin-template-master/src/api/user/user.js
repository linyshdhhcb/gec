import request from '@/utils/request'

// 定义基本的url
const BASE_URL = '/admin/system/sysUser';

export default {
  // 分页查询用户列表
  getPageList(page, limit, username) {
    return request({
      url: `${BASE_URL}/${page}/${limit}`,
      method: 'get',
      params: { username }
    })
  },
  
  // 删除用户
  removeById(id) {
    return request({
      url: `${BASE_URL}/remove/${id}`,
      method: 'delete'
    })
  },
  
  // 添加用户
  saveUser(user) {
    return request({
      url: `${BASE_URL}/save`,
      method: 'post',
      data: user
    })
  },
  
  // 根据id查询用户
  getUserById(id) {
    return request({
      url: `${BASE_URL}/get/${id}`,
      method: 'get'
    })
  },
  
  // 修改用户
  updateUser(user) {
    return request({
      url: `${BASE_URL}/update`,
      method: 'put',
      data: user
    })
  },
  
  // 批量删除用户
  batchRemoveIds(ids) {
    return request({
      url: `${BASE_URL}/batchRemove`,
      method: 'delete',
      data: ids
    })
  }
}
