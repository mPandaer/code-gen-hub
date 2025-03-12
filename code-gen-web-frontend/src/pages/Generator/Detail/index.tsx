import { fileDownloadUsingGet } from '@/services/backend/fileController';
import { getGeneratorForUserPurchaseUsingGet, getGeneratorVoByIdUsingGet, isFreeByIdUsingGet } from '@/services/backend/generatorController';
import { useParams } from '@@/exports';
import { DownloadOutlined, EditOutlined } from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-components';
import '@umijs/max';
import { useModel } from '@umijs/max';
import {
  Button,
  Card,
  Col,
  Divider,
  Flex,
  Image,
  message,
  Modal,
  Row,
  Space,
  Tabs, TabsProps,
  Tag,
  Typography,
} from 'antd';
import { saveAs } from 'file-saver';
import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'umi';
import UserInfoTab from "@/pages/Generator/Detail/components/UserInfo";
import FileInfoTab from "@/pages/Generator/Detail/components/FileInfo";
import ModelInfoTab from "@/pages/Generator/Detail/components/ModelInfo";
import CommentInfoTab from "@/pages/Generator/Detail/components/CommentInfo";
import { addOrderUsingPost } from '@/services/backend/orderController';

const DetailGeneratorPage: React.FC = () => {
  const { id } = useParams();
  const [generatorInfo, setGeneratorInfo] = useState<API.GeneratorVO>();
  const { initialState, setInitialState } = useModel('@@initialState');
  const navigate = useNavigate();

  const loadGeneratorInfo = async () => {
    try {
      const resp = await getGeneratorVoByIdUsingGet({ id: Number(id) });
      if (resp.data) {
        setGeneratorInfo(resp.data);
      }
    } catch (e: any) {
      message.error(e);
    }
  };

  const doDownload = async (url: string) => {
    const urlObj = new URL(url);
    const pathName = decodeURI(urlObj.pathname); // 会得到 '/hello/1'
    // console.log(pathName);
    const blob = await fileDownloadUsingGet({ filePath: pathName }, { responseType: 'blob' });
    console.log(blob);
    // 从路径中获取文件名
    const fileName = pathName.split('/').pop() || 'download';
    saveAs(blob, fileName);
  };

  const handleUseGenerator = async () => {
    if (!initialState?.currentUser?.id) {
      message.error('请先登录');
      return;
    }
    
    try {
      // 检查用户是否已购买该生成器
      const res = await getGeneratorForUserPurchaseUsingGet({
        generatorId: generatorInfo?.id,
        userId: initialState.currentUser.id
      });
      
      // 如果已购买或免费，直接使用
      if (res.data || generatorInfo?.generatorFee?.isFree === 1) {
        // 跳转到使用页面
        navigate(`/generator/use/${generatorInfo?.id}`);
      } else {
        // 显示购买确认弹窗
        Modal.confirm({
          title: '购买确认',
          content: (
            <div>
              <p>该代码生成器需要购买才能使用</p>
              <p>价格: ¥{generatorInfo?.generatorFee?.price || 0}</p>
              <p>有效期: {generatorInfo?.generatorFee?.validity || '永久'}</p>
              <p>是否确认购买？</p>
            </div>
          ),
          onOk: async () => {
            // 创建订单
            const orderRes = await addOrderUsingPost({
              generatorId: generatorInfo?.id,
              userId: initialState.currentUser.id,
              amount: generatorInfo?.generatorFee?.price
            });
            
            if (orderRes.code === 0 && orderRes.data) {
              message.success('订单创建成功，即将跳转到支付页面');
              // 跳转到订单详情页面进行支付
              navigate(`/order/${generatorInfo?.id}/${orderRes.data.orderId}`);
            } else {
              message.error('订单创建失败');
            }
          },
          okText: '确认购买',
          cancelText: '取消'
        });
      }
    } catch (error) {
      message.error('操作失败，请重试');
      console.error(error);
    }
  };

  useEffect(() => {
    loadGeneratorInfo();
  }, [id]);

  const items: TabsProps['items'] = [
    {
      key: '1',
      label: '作者信息',
      children: <UserInfoTab {...generatorInfo?.user}/>,
    },
    {
      key: '2',
      label: '文件配置',
      children: <FileInfoTab {...generatorInfo?.fileConfig}/>,
    },
    {
      key: '3',
      label: '模型配置',
      children: <ModelInfoTab {...generatorInfo?.modelConfig}/>,
    },
    {
      key: '4',
      label: '评论列表',
      children: <CommentInfoTab/>,
    },
  ];
  const { Title, Paragraph } = Typography;

  return (
    <PageContainer
      contentWidth={'Fixed'}
      style={{ margin: '0 auto', width: '80%', minWidth: '500px' }}
    >
      <Card>
        <Row justify={'space-around'}>
          <Col flex="auto">
            <Title level={3}>{generatorInfo?.name}</Title>
            <Flex gap="4px 0" wrap>
              {generatorInfo?.tags?.map((tag) => (
                <Tag key={tag}>{tag}</Tag>
              ))}
            </Flex>
            <Divider />
            <Paragraph type={'secondary'}>描述：{generatorInfo?.description}</Paragraph>
            <Paragraph type={'secondary'}>作者：{generatorInfo?.author}</Paragraph>
            <Paragraph type={'secondary'}>版本：{generatorInfo?.version}</Paragraph>
            <Paragraph type={'secondary'}>基础包路径：{generatorInfo?.basePackage}</Paragraph>
            <Paragraph type={'secondary'}>状态：{generatorInfo?.status}</Paragraph>
            <Paragraph type={'secondary'}>创建时间：{generatorInfo?.createTime}</Paragraph>
            <Paragraph type={'secondary'}>更新时间：{generatorInfo?.updateTime}</Paragraph>
            <Divider />

            <Space>
              <Button 
                type={'primary'} 
                disabled={!initialState?.currentUser?.id}
                onClick={handleUseGenerator}
              >
                立即使用
              </Button>
              {initialState?.currentUser?.id === generatorInfo?.userId && (
                <Link to={`/generator/edit/${generatorInfo?.id}`}>
                  <Button icon={<EditOutlined />} type={'primary'}>
                    编辑
                  </Button>
                </Link>
              )}

              {initialState?.currentUser?.id && (
                <Button
                  icon={<DownloadOutlined />}
                  onClick={async () => {
                    doDownload(generatorInfo?.distPath ?? '');
                  }}
                >
                  点击下载
                </Button>
              )}
            </Space>
          </Col>
          <Col flex="320px">
            <Image src={generatorInfo?.picture} />
          </Col>
        </Row>
      </Card>

      <Card style={{ marginTop: '16px' }}>
        <Tabs defaultActiveKey="1" items={items} onChange={() => {}} />
      </Card>
    </PageContainer>
  );
};

export default DetailGeneratorPage;
