import React, { useEffect, useState } from 'react';
import { useParams } from '@@/exports';
import { useModel } from '@umijs/max';
import { Button, List, Input, message, Popconfirm } from 'antd';
import {
  addCommentUsingPost,
  deleteCommentUsingDelete,
  pageListCommentsUsingGet,
  replyCommentUsingPost,
} from '@/services/backend/generatorCommentController';
import dayjs from 'dayjs';
import relativeTime from 'dayjs/plugin/relativeTime';

dayjs.extend(relativeTime);

const { TextArea } = Input;

const CommentInfoTab: React.FC = () => {
  const { id } = useParams();
  const { initialState } = useModel('@@initialState');
  const [comments, setComments] = useState<API.GeneratorComment[]>([]);
  const [newComment, setNewComment] = useState('');
  const [replyText, setReplyText] = useState<{ [key: number]: string }>({});
  const [showReply, setShowReply] = useState<{ [key: number]: boolean }>({});
  const [commentTree, setCommentTree] = useState<{
    [key: string]: API.GeneratorComment[];
  }>({});
  const [showReplies, setShowReplies] = useState<{ [key: number]: boolean }>({});
  const [submitting, setSubmitting] = useState(false);

  const buildCommentTree = (comments: API.GeneratorComment[]) => {
    const tree: { [key: string]: API.GeneratorComment[] } = { root: [] };
    
    comments.forEach((comment) => {
      if (!comment.parentId) {
        tree.root.push(comment);
      } else {
        if (!tree[comment.parentId]) {
          tree[comment.parentId] = [];
        }
        tree[comment.parentId].push(comment);
      }
    });
    
    setCommentTree(tree);
  };

  const loadComments = async () => {
    try {
      const res = await pageListCommentsUsingGet({
        generatorId: Number(id),
        pageSize: 100,
        current: 1,
      });
      if (res?.data?.records) {
        setComments(res.data.records);
        buildCommentTree(res.data.records);
      }
    } catch (e: any) {
      message.error('加载评论失败：' + e.message);
    }
  };

  const handleAddComment = async () => {
    if (!newComment.trim()) {
      message.error('评论内容不能为空');
      return;
    }
    try {
      setSubmitting(true);
      await addCommentUsingPost({
        generatorId: Number(id),
        content: newComment,
        userId: initialState?.currentUser?.id,
      });
      message.success('评论成功');
      setNewComment('');
      await loadComments();
    } catch (e: any) {
      message.error('评论失败：' + e.message);
    } finally {
      setSubmitting(false);
    }
  };

  const handleReply = async (commentId: number) => {
    if (!replyText[commentId]?.trim()) {
      message.error('回复内容不能为空');
      return;
    }
    try {
      setSubmitting(true);
      await replyCommentUsingPost({
        generatorId: Number(id),
        content: replyText[commentId],
        parentId: commentId,
        userId: initialState?.currentUser?.id,
      });
      message.success('回复成功');
      setReplyText({ ...replyText, [commentId]: '' });
      setShowReply({ ...showReply, [commentId]: false });
      await loadComments();
    } catch (e: any) {
      message.error('回复失败：' + e.message);
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (commentId: number) => {
    try {
      await deleteCommentUsingDelete({ id: commentId });
      message.success('删除成功');
      await loadComments();
    } catch (e: any) {
      message.error('删除失败：' + e.message);
    }
  };

  useEffect(() => {
    loadComments();
  }, [id]);

  const renderComment = (comment: API.GeneratorCommentVO, level = 0) => {
    const hasReplies = commentTree[comment.id]?.length > 0;
    
    return (
      <List.Item
        key={comment.id}
        style={{ 
          marginBottom: 16,
          borderBottom: '1px solid #f0f0f0',
          paddingBottom: 16 
        }}
        actions={[
          initialState?.currentUser?.id === comment.userId && (
            <Popconfirm
              key="delete"
              title="确定要删除这条评论吗？"
              onConfirm={() => handleDelete(comment.id)}
            >
              <Button type="link" danger>
                删除
              </Button>
            </Popconfirm>
          ),
        ]}
      >
        <List.Item.Meta
          avatar={
            <img 
              src={comment.userVO?.userAvatar || 'https://gw.alipayobjects.com/zos/rmsportal/BiazfanxmamNRoxxVxka.png'} 
              alt="avatar" 
              style={{ 
                width: 40, 
                height: 40, 
                borderRadius: '50%',
                boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
                border: '2px solid #fff'
              }}
            />
          }
          title={
            <div style={{ display: 'flex', alignItems: 'center' }}>
              <span style={{ fontWeight: 600, fontSize: 14 }}>
                {comment.userVO?.userName || comment.userAccount || '匿名用户'}
              </span>
              <span style={{ 
                marginLeft: 12,
                color: '#8c8c8c',
                fontSize: 12,
                fontWeight: 400
              }}>
                {dayjs(comment.createTime).fromNow()}
              </span>
            </div>
          }
          description={
            <div>
              <div style={{ 
                fontSize: 14,
                lineHeight: 1.6,
                margin: '8px 0'
              }}>
                {comment.content}
              </div>
              <div style={{ 
                display: 'flex',
                alignItems: 'center',
                gap: 16,
                fontSize: 12 
              }}>
                <Button 
                  type="text" 
                  size="small"
                  style={{ 
                    padding: 0,
                    color: '#8c8c8c',
                    '&:hover': { color: '#1890ff' }
                  }}
                >
                  👍 {comment.likeCount || 0}
                </Button>
                <Button
                  type="link"
                  onClick={() => setShowReply({ ...showReply, [comment.id]: !showReply[comment.id] })}
                  style={{ padding: 0, marginLeft: 8 }}
                >
                  回复
                </Button>
                {hasReplies && (
                  <Button
                    type="link"
                    onClick={() => setShowReplies(prev => ({ ...prev, [comment.id]: !prev[comment.id] }))}
                    style={{ padding: 0, marginLeft: 8 }}
                  >
                    {showReplies[comment.id] ? '收起' : `查看回复（${commentTree[comment.id]?.length}）`}
                  </Button>
                )}
              </div>
            </div>
          }
        />
        
        {showReply[comment.id] && (
          <div style={{ 
            marginTop: 16,
            backgroundColor: '#fafafa',
            borderRadius: 8,
            padding: 12
          }}>
            <TextArea
              autoSize={{ minRows: 2, maxRows: 4 }}
              value={replyText[comment.id]}
              onChange={(e) =>
                setReplyText({ ...replyText, [comment.id]: e.target.value })
              }
              placeholder="请输入回复内容"
            />
            <div style={{ 
              marginTop: 12,
              display: 'flex',
              justifyContent: 'flex-end',
              gap: 8
            }}>
              <Button
                loading={submitting}
                type="primary"
                size="small"
                onClick={() => handleReply(comment.id)}
              >
                提交
              </Button>
              <Button
                size="small"
                onClick={() => {
                  setShowReply({ ...showReply, [comment.id]: false });
                  setReplyText({ ...replyText, [comment.id]: '' });
                }}
              >
                取消
              </Button>
            </div>
          </div>
        )}
        
        {showReplies[comment.id] && (
          <div style={{ 
            marginLeft: level * 32,
            borderLeft: '2px solid #f0f0f0',
            paddingLeft: 16,
            marginTop: 16
          }}>
            {commentTree[comment.id]?.map(reply => renderComment(reply, level + 1))}
          </div>
        )}
      </List.Item>
    );
  };

  return (
    <div>
      {initialState?.currentUser && (
        <div style={{ marginBottom: 24 }}>
          <TextArea
            rows={4}
            value={newComment}
            onChange={(e) => setNewComment(e.target.value)}
            placeholder="请输入评论"
          />
          <Button
            type="primary"
            style={{ marginTop: 16 }}
            onClick={handleAddComment}
          >
            发表评论
          </Button>
        </div>
      )}

      <List
        style={{ maxHeight: 600, overflowY: 'auto' }}
        locale={{ emptyText: '暂无评论，快来第一个留言吧～' }}
        itemLayout="vertical"
        dataSource={commentTree.root || []}
        renderItem={(item) => renderComment(item)}
      />
    </div>
  );
};

export default CommentInfoTab;
