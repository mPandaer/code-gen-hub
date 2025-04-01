import { useEffect, useState } from 'react';
import { Card, Avatar, Form, Input, Button, message, Layout, Menu, Modal, Tag, Typography } from 'antd';
import { UserOutlined, CodeOutlined, LockOutlined, CrownOutlined, EditOutlined } from '@ant-design/icons';
import { changePasswordUsingPut, getUserVoByIdUsingGet, updateMyUserUsingPost } from '@/services/backend/userController';
import { useModel, history } from 'umi';
import { uploadFileUsingPost } from '@/services/backend/fileController';

const { Sider, Content } = Layout;
const { Paragraph } = Typography;

const Profile = () => {
  const [form] = Form.useForm();
  const [passwordForm] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [selectedKey, setSelectedKey] = useState('profile');
  const [isPasswordModalVisible, setIsPasswordModalVisible] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const { initialState, setInitialState } = useModel('@@initialState');

  const { currentUser } = initialState || {};


  console.log("当前用户：",currentUser);


  const onFinish = async (values: any) => {
    try {
      setLoading(true);
      const res = await updateMyUserUsingPost({
        userName: values.username,
        userProfile: values.description,
      });
      
      if (res.code === 0) {
        message.success('更新成功');
        // Update the global user state
        const { initialState, setInitialState } = useModel('@@initialState');
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
    <Layout style={{ minHeight: '100vh' }}>
      <Sider width={200} theme="light">
        <Menu
          mode="inline"
          selectedKeys={[selectedKey]}
          style={{ height: '100%', borderRight: 0 }}
          items={[
            {
              key: 'profile',
              icon: <UserOutlined />,
              label: '个人信息'
            },
            {
              key: 'generators',
              icon: <CodeOutlined />,
              label: '代码生成器'
            }
          ]}
          onClick={({ key }) => setSelectedKey(key)}
        />
      </Sider>
      <Content style={{ padding: '24px', minHeight: 280 }}>
        <Card 
          title="个人资料" 
          bordered={false}
          extra={
            isEditing ? (
              <div>
                <Button 
                  type="primary" 
                  onClick={() => form.submit()} 
                  loading={loading}
                  style={{ marginRight: 8 }}
                >
                  保存
                </Button>
                <Button onClick={() => {
                  setIsEditing(false);
                  form.resetFields();
                }}>
                  取消
                </Button>
              </div>
            ) : (
              <div>
                <Button 
                  icon={<LockOutlined />} 
                  type="link"
                  onClick={() => setIsPasswordModalVisible(true)}
                  style={{ marginRight: 8 }}
                >
                  修改密码
                </Button>
                <Button 
                  icon={<EditOutlined />} 
                  type="link" 
                  onClick={() => setIsEditing(true)}
                >
                  编辑资料
                </Button>
              </div>
            )
          }
        >
          <div style={{ display: 'flex', marginBottom: 24 }}>
            <div style={{ marginRight: 24, textAlign: 'center' }}>
              <div 
                style={{ 
                  position: 'relative',
                  display: 'inline-block',
                  cursor: 'pointer'
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
                <Avatar size={100} icon={<UserOutlined />} src={currentUser?.userAvatar} />
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
                  <EditOutlined style={{ color: '#fff', fontSize: 20 }} />
                </div>
              </div>
            </div>
            <div style={{ flex: 1 }}>
              <Form
                form={form}
                layout="vertical"
                onFinish={onFinish}
                initialValues={{
                  username: currentUser?.userName,
                  role: currentUser?.userRole,
                  description: currentUser?.userProfile
                }}
              >
                <Form.Item 
                  label="用户名" 
                  name="username"
                >
                  {isEditing ? (
                    <Input />
                  ) : (
                    <Typography.Text>
                      {currentUser?.userName}
                    </Typography.Text>
                  )}
                </Form.Item>
                
                <Form.Item label="角色" name="role">
                  <div>{getRoleTag(currentUser?.userRole)}</div>
                </Form.Item>
                
                <Form.Item 
                  label="描述" 
                  name="description"
                >
                  {isEditing ? (
                    <Input.TextArea rows={4} />
                  ) : (
                    <Paragraph style={{ marginBottom: 0 }}>
                      {currentUser?.userProfile}
                    </Paragraph>
                  )}
                </Form.Item>
              </Form>
            </div>
          </div>
        </Card>

        <Modal
          title="修改密码"
          open={isPasswordModalVisible}
          onCancel={() => {
            setIsPasswordModalVisible(false);
            passwordForm.resetFields();
          }}
          footer={null}
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
              <Input.Password />
            </Form.Item>
            <Form.Item
              name="newPassword"
              label="新密码"
              rules={[
                { required: true, message: '请输入新密码' },
                { min: 6, message: '密码长度不能小于6位' }
              ]}
            >
              <Input.Password />
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
              <Input.Password />
            </Form.Item>
            <Form.Item>
              <Button type="primary" htmlType="submit" loading={loading}>
                确认修改
              </Button>
            </Form.Item>
          </Form>
        </Modal>
      </Content>
    </Layout>
  );
};

export default Profile;


