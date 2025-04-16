import request from '@/utils/request'

/**
 * 通用API服务
 */

// 获取列表数据
export function fetchList(resource, params) {
  return request({
    url: `/${resource}`,
    method: 'get',
    params
  })
}

// 获取详情
export function fetchDetail(resource, id) {
  return request({
    url: `/${resource}/${id}`,
    method: 'get'
  })
}

// 创建数据
export function createItem(resource, data) {
  return request({
    url: `/${resource}`,
    method: 'post',
    data
  })
}

// 更新数据
export function updateItem(resource, id, data) {
  return request({
    url: `/${resource}/${id}`,
    method: 'put',
    data
  })
}

// 删除数据
export function deleteItem(resource, id) {
  return request({
    url: `/${resource}/${id}`,
    method: 'delete'
  })
} 