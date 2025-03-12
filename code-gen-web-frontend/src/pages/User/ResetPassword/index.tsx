import React, { useState } from 'react';
import { Form, Input, Button, message } from 'antd';
import { useLocation, history } from 'umi';
import { resetPasswordUsingPost } from '@/services/backend/userController';

const ResetPassword: React.FC = () => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);

  const location = useLocation();

  // 从 URL 中获取 token
  const token = new URLSearchParams(location.search).get('token');
  const email = new URLSearchParams(location.search).get('email');

  const onFinish = async (values: { password: string; confirmPassword: string }) => {
    if (!token) {
      message.error('无效的重置链接');
      return;
    }

    if (values.password !== values.confirmPassword) {
      message.error('两次输入的密码不一致');
      return;
    }

    try {
      setLoading(true);
      const res = await resetPasswordUsingPost({
        email,
        token,
        newPassword: values.password,
      })
      if (res.code === 0) {
        message.success('密码重置成功');
        // 使用 history 进行路由跳转
        setTimeout(() => {
          history.push('/user/login');
        }, 1000);
      } else {
        message.error(`密码重置失败：${res.message}`);
      }
    } catch (error) {
      message.error('密码重置失败，请重试');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: 400, margin: '0 auto', padding: '40px 20px' }}>
      <h2 style={{ textAlign: 'center', marginBottom: 24 }}>重置密码</h2>
      <Form
        form={form}
        onFinish={onFinish}
        layout="vertical"
      >
        <Form.Item
          name="password"
          label="新密码"
          rules={[
            { required: true, message: '请输入新密码' },
            { min: 6, message: '密码长度至少为 6 位' },
          ]}
        >
          <Input.Password />
        </Form.Item>

        <Form.Item
          name="confirmPassword"
          label="确认密码"
          rules={[
            { required: true, message: '请确认密码' },
            { min: 6, message: '密码长度至少为 6 位' },
          ]}
        >
          <Input.Password />
        </Form.Item>

        <Form.Item>
          <Button type="primary" htmlType="submit" loading={loading} block>
            确认重置
          </Button>
        </Form.Item>
      </Form>
    </div>
  );
};

export default ResetPassword;
