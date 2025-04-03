import { useEffect, useState } from 'react';
import { Card, Avatar, Form, Input, Button, message, Modal, Tag, Typography, Progress, Tooltip, Row, Col, Divider, Tabs, Empty } from 'antd';
import { UserOutlined, LockOutlined, CrownOutlined, EditOutlined, TrophyOutlined, GiftOutlined, LikeOutlined, WalletOutlined, DollarOutlined, CodeOutlined } from '@ant-design/icons';
import { changePasswordUsingPut, updateMyUserUsingPost } from '@/services/backend/userController';
import { useModel, history } from 'umi';
import { uploadFileUsingPost } from '@/services/backend/fileController';

const { Paragraph, Title } = Typography;
const { TabPane } = Tabs;

const Profile = () => {
  const [form] = Form.useForm();
  const [passwordForm] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [isPasswordModalVisible, setIsPasswordModalVisible] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const { initialState, setInitialState } = useModel('@@initialState');

  const { currentUser } = initialState || {};
  
  // 添加用户等级相关状态
  const [userLevel, setUserLevel] = useState({
    level: 1,
    title: '新手开发者',
    currentExp: 0,
    nextLevelExp: 200,
    totalGenerators: 0,
    totalLikes: 0
  });

  // 获取用户等级称号
  const getLevelTitle = (level: number) => {
    return currentUser?.privilege?.levelName || '新手开发者';
  };

  // 获取用户等级数据
  useEffect(() => {
    if (currentUser?.id) {
      // 使用实际的用户数据
      setUserLevel({
        level: currentUser.userLevel || 1,
        title: currentUser.privilege?.levelName || '新手开发者',
        currentExp: currentUser.experience || 0,
        nextLevelExp: currentUser.privilege?.maxExp || 200,
        totalGenerators: 0, // 这个数据可能需要从API获取
        totalLikes: 0 // 这个数据可能需要从API获取
      });
    }
  }, [currentUser?.id, currentUser?.userLevel, currentUser?.experience, currentUser?.privilege]);

  console.log("当前用户：",currentUser);

  // 添加Tab状态
  const [activeTab, setActiveTab] = useState('1');

  const onFinish = async (values: any) => {
    try {
      setLoading(true);
      const res = await updateMyUserUsingPost({
        userName: values.username,
        userProfile: values.description,
      });
      
      if (res.code === 0) {
        message.success('更新成功');
        // Update the global user state - using the already destructured variables from the top level
        if (initialState?.currentUser) {
          setInitialState({
            ...initialState,
            currentUser: {
              ...initialState.currentUser,
              userName: values.username,
              userProfile: values.description,
            },
          });
        }
        setIsEditing(false);
      } else {
        message.error('更新失败：' + res.message);
      }
    } catch (error: any) {
      message.error('更新失败：' + error.message);
    } finally {
      setLoading(false);
    }
  };

  const handlePasswordChange = async (values: any) => {
    try {
      setLoading(true);
      const res = await changePasswordUsingPut({
        oldPassword: values.oldPassword,
        newPassword: values.newPassword,
      });
      
      if (res.code === 0) {
        message.success('密码修改成功，请重新登录');
        setIsPasswordModalVisible(false);
        passwordForm.resetFields();
        // Redirect to login page
        history.push('/user/login?redirect=' + window.location.pathname); 
      } else {
        message.error('密码修改失败：' + res.message);
      }
    } catch (error: any) {
      message.error('密码修改失败：' + error.message);
    } finally {
      setLoading(false);
    }
  };

  const getRoleTag = (role: string) => {
    switch (role) {
      case 'admin':
        return <Tag color="gold" icon={<CrownOutlined />}>管理员</Tag>;
      case 'user':
        return <Tag color="blue" icon={<UserOutlined />}>普通用户</Tag>;
      default:
        return <Tag>未知角色</Tag>;
    }
  };

  const handleAvatarUpload = async (file: File) => {
    try {
      const hide = message.loading('正在上传...');
      const res = await uploadFileUsingPost(
        { biz: 'user_avatar' },
        {},
        file
      );
      hide();
      
      if (res.code === 0) {
        const avatarUrl = res.data;
        // Update user profile with new avatar
        const updateRes = await updateMyUserUsingPost({
          userAvatar: avatarUrl,
        });
        
        if (updateRes.code === 0) {
          message.success('头像更新成功');
          // Update global user state
          const { initialState, setInitialState } = useModel('@@initialState');
          if (initialState?.currentUser) {
            setInitialState({
              ...initialState,
              currentUser: {
                ...initialState.currentUser,
                userAvatar: avatarUrl,
              },
            });
          }
        }
      } else {
        message.error('上传失败：' + res.message);
      }
    } catch (error: any) {
      message.error('上传失败：' + error.message);
    }
  };

  return (
    <div style={{ 
      padding: '0',
      maxWidth: '100%', 
      margin: '0',
      background: '#f5f5f5', 
      minHeight: '100vh',
      width: '100vw',
      height: '100vh',
      display: 'flex',
      flexDirection: 'column'
    }}>
      {/* 上部分：用户基本信息 */}
      <div style={{ flex: 'none' }}>
        <Card 
          bordered={false}
          style={{ 
            marginBottom: 16,
            borderRadius: 0,
            boxShadow: 'none',
            width: '100%'
          }}
        >
          <Row gutter={[32, 32]} align="middle">
            <Col xs={24} sm={7} md={6} lg={5} style={{ textAlign: 'center' }}>
              <div 
                style={{ 
                  position: 'relative',
                  cursor: 'pointer',
                  display: 'inline-block'
                }}
                onClick={() => {
                  const input = document.createElement('input');
                  input.type = 'file';
                  input.accept = 'image/*';
                  input.onchange = (e) => {
                    const file = (e.target as HTMLInputElement).files?.[0];
                    if (file) {
                      handleAvatarUpload(file);
                    }
                  };
                  input.click();
                }}
              >
                <Avatar 
                  size={140} 
                  icon={<UserOutlined />} 
                  src={currentUser?.userAvatar} 
                  style={{ border: '5px solid #f0f0f0' }}
                />
                <div style={{
                  position: 'absolute',
                  top: 0,
                  left: 0,
                  width: '100%',
                  height: '100%',
                  background: 'rgba(0, 0, 0, 0.5)',
                  borderRadius: '50%',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  opacity: 0,
                  transition: 'opacity 0.3s',
                  '&:hover': {
                    opacity: 1
                  }
                }}>
                  <EditOutlined style={{ color: '#fff', fontSize: 28 }} />
                </div>
              </div>
              
              {/* 添加用户ID显示 */}
              <div style={{ marginTop: 12, color: '#666' }}>
                ID: {currentUser?.id || 'N/A'}
              </div>
            </Col>
            
            <Col xs={24} sm={17} md={18} lg={19}>
              <div style={{ display: 'flex', alignItems: 'center', marginBottom: 16, flexWrap: 'wrap' }}>
                <Title level={2} style={{ margin: 0, marginRight: 16 }}>{currentUser?.userName || '未设置用户名'}</Title>
                {getRoleTag(currentUser?.userRole)}
                
                {/* 等级标签 */}
                <Tooltip title={`等级 ${userLevel.level}：${userLevel.title}`}>
                  <Tag color="purple" icon={<TrophyOutlined />} style={{ marginLeft: 8, fontSize: '14px', padding: '4px 8px' }}>
                    Lv.{userLevel.level} {userLevel.title}
                  </Tag>
                </Tooltip>
              </div>
              
              <Paragraph style={{ marginBottom: 24, fontSize: '16px', color: '#555' }}>
                {currentUser?.userProfile || '这个人很懒，还没有填写个人简介'}
              </Paragraph>
              
              {/* 等级进度条 */}
              <div style={{ marginBottom: 28 }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 8 }}>
                  <span style={{ fontWeight: 'bold', fontSize: '15px' }}>经验值</span>
                  <span>{userLevel.currentExp}/{userLevel.nextLevelExp}</span>
                </div>
                <Progress 
                  percent={Math.floor((userLevel.currentExp / userLevel.nextLevelExp) * 100)} 
                  showInfo={false}
                  strokeColor={{
                    '0%': '#108ee9',
                    '100%': '#87d068',
                  }}
                  strokeWidth={12}
                  style={{ height: '12px' }}
                />
              </div>
              
              <Divider style={{ margin: '20px 0' }} />
              
              {/* 用户统计数据 */}
              <Row gutter={[48, 24]}>
                <Col xs={12} sm={6}>
                  <StatisticCard 
                    icon={<CodeOutlined style={{ color: '#1890ff' }} />}
                    title="发布的代码生成器" 
                    value={userLevel.totalGenerators} 
                  />
                </Col>
                <Col xs={12} sm={6}>
                  <StatisticCard 
                    icon={<LikeOutlined style={{ color: '#52c41a' }} />}
                    title="获得点赞" 
                    value={userLevel.totalLikes} 
                  />
                </Col>
                <Col xs={12} sm={6}>
                  <StatisticCard 
                    icon={<WalletOutlined style={{ color: '#faad14' }} />}
                    title="金币余额" 
                    value={parseFloat(currentUser?.goldCoins || '0')} 
                  />
                </Col>
                <Col xs={12} sm={6}>
                  <StatisticCard 
                    icon={<DollarOutlined style={{ color: '#722ed1' }} />}
                    title="月度配额" 
                    value={parseFloat(currentUser?.monthlyQuota || '0')} 
                  />
                </Col>
              </Row>
              
              {/* 操作按钮 */}
              <div style={{ marginTop: 28 }}>
                <Button 
                  icon={<EditOutlined />} 
                  type="primary" 
                  size="large"
                  onClick={() => setIsEditing(true)}
                  style={{ marginRight: 16, borderRadius: '6px', height: '42px', paddingLeft: '20px', paddingRight: '20px' }}
                >
                  编辑资料
                </Button>
                <Button 
                  icon={<LockOutlined />} 
                  size="large"
                  onClick={() => setIsPasswordModalVisible(true)}
                  style={{ borderRadius: '6px', height: '42px', paddingLeft: '20px', paddingRight: '20px' }}
                >
                  修改密码
                </Button>
              </div>
            </Col>
          </Row>
        </Card>
      </div>
      
      {/* 下部分：Tab栏 */}
      <div style={{ flex: '1', overflow: 'auto', padding: '0 16px 16px' }}>
        <Card bordered={false} style={{ height: '100%' }}>
          <Tabs 
            activeKey={activeTab} 
            onChange={setActiveTab}
            size="large"
            style={{ marginBottom: 16 }}
          >
            <TabPane 
              tab={
                <span>
                  <CodeOutlined />
                  我的生成器
                </span>
              } 
              key="1"
            >
              <div style={{ minHeight: 300 }}>
                {/* 我的生成器内容 */}
                <Empty 
                  description="暂无生成器数据" 
                  image={Empty.PRESENTED_IMAGE_SIMPLE} 
                />
              </div>
            </TabPane>
            <TabPane 
              tab={
                <span>
                  <LikeOutlined />
                  获得的点赞
                </span>
              } 
              key="2"
            >
              <div style={{ minHeight: 300 }}>
                {/* 获得的点赞内容 */}
                <Empty 
                  description="暂无点赞数据" 
                  image={Empty.PRESENTED_IMAGE_SIMPLE} 
                />
              </div>
            </TabPane>
          </Tabs>
        </Card>
      </div>

      {/* 编辑个人资料表单 */}
      {isEditing && (
      <Modal
        title="编辑个人资料"
        open={isEditing}
        onCancel={() => {
          setIsEditing(false);
          form.resetFields();
        }}
        footer={[
          <Button 
            key="cancel"
            onClick={() => {
              setIsEditing(false);
              form.resetFields();
            }}
            style={{ borderRadius: '6px' }}
          >
            取消
          </Button>,
          <Button
            key="submit"
            type="primary"
            loading={loading}
            onClick={() => form.submit()}
            style={{ borderRadius: '6px' }}
          >
            保存
          </Button>
        ]}
        width={600}
        style={{ borderRadius: '12px' }}
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={onFinish}
          initialValues={{
            username: currentUser?.userName,
            description: currentUser?.userProfile
          }}
        >
          {/* 保持原有表单字段不变 */}
          <Form.Item 
            label="用户名" 
            name="username"
            rules={[{ required: true, message: '请输入用户名' }]}
          >
            <Input size="large" style={{ borderRadius: '6px' }} />
          </Form.Item>
          
          <Form.Item 
            label="个人简介" 
            name="description"
          >
            <Input.TextArea rows={6} placeholder="介绍一下自己吧" style={{ borderRadius: '6px' }} />
          </Form.Item>
        </Form>
      </Modal>
      )}

      {/* 密码修改模态框 */}
      <Modal
        title={<span style={{ fontSize: '18px', fontWeight: 'bold' }}>修改密码</span>}
        open={isPasswordModalVisible}
        onCancel={() => {
          setIsPasswordModalVisible(false);
          passwordForm.resetFields();
        }}
        footer={null}
        width={450}
        style={{ borderRadius: '12px', overflow: 'hidden' }}
      >
        <Form
          form={passwordForm}
          layout="vertical"
          onFinish={handlePasswordChange}
        >
          <Form.Item
            name="oldPassword"
            label="当前密码"
            rules={[{ required: true, message: '请输入当前密码' }]}
          >
            <Input.Password size="large" style={{ borderRadius: '6px' }} />
          </Form.Item>
          <Form.Item
            name="newPassword"
            label="新密码"
            rules={[
              { required: true, message: '请输入新密码' },
              { min: 6, message: '密码长度不能小于6位' }
            ]}
          >
            <Input.Password size="large" style={{ borderRadius: '6px' }} />
          </Form.Item>
          <Form.Item
            name="confirmPassword"
            label="确认新密码"
            rules={[
              { required: true, message: '请确认新密码' },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue('newPassword') === value) {
                    return Promise.resolve();
                  }
                  return Promise.reject(new Error('两次输入的密码不一致'));
                },
              }),
            ]}
          >
            <Input.Password size="large" style={{ borderRadius: '6px' }} />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" loading={loading} size="large" block style={{ height: '42px', borderRadius: '6px' }}>
              确认修改
            </Button>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

// 统计数据卡片组件
const StatisticCard = ({ icon, title, value }: { icon: React.ReactNode, title: string, value: number }) => {
  return (
    <div style={{ display: 'flex', alignItems: 'center' }}>
      <div style={{ 
        fontSize: 28, 
        marginRight: 14,
        width: 48,
        height: 48,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        borderRadius: '50%',
        background: '#f0f5ff',
        boxShadow: '0 2px 6px rgba(0,0,0,0.08)'
      }}>
        {icon}
      </div>
      <div>
        <div style={{ fontSize: 24, fontWeight: 'bold', color: '#1890ff', lineHeight: '1.2' }}>{value}</div>
        <div style={{ fontSize: 14, color: '#666' }}>{title}</div>
      </div>
    </div>
  );
};

export default Profile;


