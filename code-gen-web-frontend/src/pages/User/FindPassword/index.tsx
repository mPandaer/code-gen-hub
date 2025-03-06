import { ProForm, ProFormText } from '@ant-design/pro-components';
import { Card, message } from 'antd';
import { MailOutlined } from '@ant-design/icons';
import { useEmotionCss } from '@ant-design/use-emotion-css';
import { Helmet } from '@umijs/max';
import React from 'react';
import Settings from '../../../../config/defaultSettings';
import { findPasswordUsingGet } from '@/services/backend/userController';

const FindPassword: React.FC = () => {
  const containerClassName = useEmotionCss(() => {
    return {
      display: 'flex',
      flexDirection: 'column',
      height: '100vh',
      overflow: 'auto',
      backgroundImage:
        "url('https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/V-_oS6r-i7wAAAAAAAAAAAAAFl94AQBr')",
      backgroundSize: '100% 100%',
    };
  });

  const handleSubmit = async (values: { email: string }) => {
    try {
      const res = await findPasswordUsingGet({email: values.email});
      if (res.code === 0) {
        message.success('重置密码邮件已发送，请查收邮箱');
      } else {
        message.error(`发送失败：${res.message}`);
      }
    } catch (error: any) {
      message.error(`发送失败：${error.message}`);
    }
  };

  return (
    <div className={containerClassName}>
      <Helmet>
        <title>找回密码 - {Settings.title}</title>
      </Helmet>
      <div style={{ padding: '32px 0' }}>
        <Card
          style={{
            width: '400px',
            margin: '0 auto',
          }}
          title="找回密码"
        >
          <ProForm
            onFinish={handleSubmit}
            submitter={{
              searchConfig: {
                submitText: '发送重置邮件',
              },
            }}
          >
            <ProFormText
              name="email"
              fieldProps={{
                size: 'large',
                prefix: <MailOutlined />,
              }}
              placeholder="请输入注册邮箱"
              rules={[
                {
                  required: true,
                  message: '请输入邮箱地址！',
                },
                {
                  type: 'email',
                  message: '请输入有效的邮箱地址！',
                },
              ]}
            />
          </ProForm>
        </Card>
      </div>
    </div>
  );
};

export default FindPassword;