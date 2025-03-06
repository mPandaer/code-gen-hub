// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** getDownloadTrend GET /api/statistic/download-trend */
export async function getDownloadTrendUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getDownloadTrendUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseMapStringObject_>('/api/statistic/download-trend', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** getGeneratorRanking GET /api/statistic/generator-ranking */
export async function getGeneratorRankingUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getGeneratorRankingUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseMapStringObject_>('/api/statistic/generator-ranking', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** getNewUserTrend GET /api/statistic/user-trend */
export async function getNewUserTrendUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getNewUserTrendUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseMapStringObject_>('/api/statistic/user-trend', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}
