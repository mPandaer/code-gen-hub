import { getGeneratorVoByIdUsingGet } from '@/services/backend/generatorController';
import { payOrderUsingPost } from '@/services/backend/orderController';
import { useModel } from '@@/exports';
import { PageContainer } from '@ant-design/pro-components';
import { Button, Card, Descriptions, message, Result, Spin } from 'antd';
import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'umi';

const OrderConfirmPage: React.FC = () => {
  const { generatorId, orderId } = useParams();
  const [loading, setLoading] = useState(false);
  const [generatorInfo, setGeneratorInfo] = useState<API.GeneratorVO>();
  const { initialState } = useModel('@@initialState');
  const navigate = useNavigate();

  const loadGeneratorInfo = async () => {
    if (!generatorId) {
      return;
    }
    setLoading(true);
    try {
      const res = await getGeneratorVoByIdUsingGet({ id: Number(generatorId) });
      if (res.data) {
        setGeneratorInfo(res.data);
      }
    } catch (error: any) {
      message.error('获取生成器信息失败：' + error.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadGeneratorInfo();
  }, [generatorId]);

  const handlePayment = async () => {
    if (!generatorId || !initialState?.currentUser?.id) {
      message.error('订单信息不完整');
      return;
    }

    setLoading(true);
    try {
      const res = await payOrderUsingPost({
        orderId: orderId,
        subject: "购买" + generatorInfo?.name,
      });

      if (res.code === 0 && res.data?.htmlPage) {
        // 创建一个新窗口
        const newWindow = window.open('', '_blank');
        if (newWindow) {
          // 写入HTML内容
          newWindow.document.write(res.data.htmlPage);
          newWindow.document.close();
          message.success('正在跳转到支付页面');
        } else {
          message.error('无法打开支付窗口，请检查是否被浏览器拦截');
        }
      } else {
        message.error('支付失败：' + res.message);
      }
    } catch (error: any) {
      message.error('支付失败：' + error.message);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '50px' }}>
        <Spin size="large" />
      </div>
    );
  }

  if (!generatorInfo) {
    return (
      <Result
        status="error"
        title="获取订单信息失败"
        subTitle="未能找到相关生成器信息"
        extra={[
          <Button type="primary" key="console" onClick={() => navigate('/')}>
            返回首页
          </Button>,
        ]}
      />
    );
  }

  return (
    <PageContainer title="订单确认">
      <Card>
        <Descriptions title="生成器信息" bordered>
          <Descriptions.Item label="名称" span={3}>
            {generatorInfo.name}
          </Descriptions.Item>
          <Descriptions.Item label="作者" span={3}>
            {generatorInfo.author}
          </Descriptions.Item>
          <Descriptions.Item label="版本" span={3}>
            {generatorInfo.version}
          </Descriptions.Item>
          <Descriptions.Item label="描述" span={3}>
            {generatorInfo.description}
          </Descriptions.Item>
        </Descriptions>

        <Descriptions title="订单信息" bordered style={{ marginTop: '24px' }}>
          <Descriptions.Item label="订单编号" span={3}>
            {orderId}
          </Descriptions.Item>
          <Descriptions.Item label="支付金额" span={3}>
            ¥{generatorInfo.generatorFee?.price || 0}
          </Descriptions.Item>
          <Descriptions.Item label="有效期" span={3}>
            {generatorInfo.generatorFee?.validity || '永久'}
          </Descriptions.Item>
        </Descriptions>

        <div style={{ textAlign: 'center', marginTop: '24px' }}>
          <Button type="primary" size="large" onClick={handlePayment}>
            立即支付
          </Button>
        </div>
      </Card>
    </PageContainer>
  );
};

export default OrderConfirmPage; 