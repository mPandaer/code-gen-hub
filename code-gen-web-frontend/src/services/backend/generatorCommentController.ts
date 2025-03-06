// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** addComment POST /api/generators/comments */
export async function addCommentUsingPost(
  body: API.AddGeneratorCommentRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseObject_>('/api/generators/comments', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** deleteComment DELETE /api/generators/comments/${param0} */
export async function deleteCommentUsingDelete(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteCommentUsingDELETEParams,
  options?: { [key: string]: any },
) {
  const { id: param0, ...queryParams } = params;
  return request<API.BaseResponseObject_>(`/api/generators/comments/${param0}`, {
    method: 'DELETE',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** pageListComments GET /api/generators/comments/page */
export async function pageListCommentsUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.pageListCommentsUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponsePageGeneratorCommentVO_>('/api/generators/comments/page', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** replyComment POST /api/generators/comments/reply */
export async function replyCommentUsingPost(
  body: API.ReplyGeneratorCommentRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseObject_>('/api/generators/comments/reply', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
