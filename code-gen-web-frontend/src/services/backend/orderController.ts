// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** addOrder POST /api/orders */
export async function addOrderUsingPost(
  body: API.AddOrderRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseOrderVO_>('/api/orders', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** getOrderById GET /api/orders/${param0} */
export async function getOrderByIdUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getOrderByIdUsingGETParams,
  options?: { [key: string]: any },
) {
  const { orderId: param0, ...queryParams } = params;
  return request<API.BaseResponseOrderVO_>(`/api/orders/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** payOrder POST /api/orders/pay */
export async function payOrderUsingPost(body: API.PayRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponsePayResponse_>('/api/orders/pay', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
