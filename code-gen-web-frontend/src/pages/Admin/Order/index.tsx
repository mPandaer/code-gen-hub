import { pageOrdersUsingGet, editOrderRemarkUsingPost } from '@/services/backend/orderController';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import { PageContainer, ProTable } from '@ant-design/pro-components';
import '@umijs/max';
import { Typography, message, Modal, Form, Input } from 'antd';
import React, { useRef, useState } from 'react';

/**
 * 订单管理页面
 */
const OrderAdminPage: React.FC = () => {
  const actionRef = useRef<ActionType>();
  const [editableRow, setEditableRow] = useState<string | null>(null);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [currentOrder, setCurrentOrder] = useState<API.OrderVO | null>(null);
  const [form] = Form.useForm();

  /**
   * 表格列配置
   */
  const columns: ProColumns<API.OrderVO>[] = [
    {
      title: '订单号',
      dataIndex: 'orderId',
      valueType: 'text',
      copyable: true,
    },
    {
      title: '生成器',
      dataIndex: ['generatorId'],
      valueType: 'text',
      render: (_, record) => (
        <div>
          <Typography.Text copyable={{ text: record.generator?.id?.toString() }}>
            {record.generator?.name}
          </Typography.Text>
        </div>
      ),
    },
    {
      title: '用户',
      dataIndex: ['userId'],
      valueType: 'text',
      render: (_, record) => (
        <div>
          <Typography.Text copyable={{ text: record.user?.id?.toString() }}>
            {record.user?.userName}
          </Typography.Text>
        </div>
      ),
    },
    {
      title: '支付金额',
      dataIndex: ['generator', 'generatorFee', 'price'],
      valueType: 'money',
    },
    {
      title: '有效期',
      dataIndex: ['generator', 'generatorFee', 'validity'],
      valueType: 'text',
      renderText: (text) => text || '永久',
    },
    {
      title: '订单状态',
      dataIndex: 'orderStatus',
      valueEnum: {
        0: {
          text: '待支付',
          status: 'warning',
        },
        1: {
          text: '已支付',
          status: 'success',
        },
        2: {
          text: '已取消',
          status: 'default',
        },
        3: {
          text: '支付失败',
          status: 'error',
        },
        4: {
          text: '支付中',
          status: 'processing',
        },
        9999: {
          text: '非法状态',
          status: 'error',
        },
      },
    },
    {
      title: '支付方式',
      dataIndex: 'paymentMethod',
      valueType: 'text',
    },
    {
      title: '支付时间',
      dataIndex: 'payTime',
      valueType: 'dateTime',
      hideInSearch: true,
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      valueType: 'dateTime',
      hideInSearch: true,
      sorter: true,
    },
    {
      title: '备注',
      dataIndex: 'remark',
      valueType: 'text',
      ellipsis: true,
      render: (_, record) => (
        <Typography.Text ellipsis>{record.remark}</Typography.Text>
      ),
    },
    {
      title: '操作',
      dataIndex: 'operation',
      render: (_, record) => (
        <a
          onClick={() => handleEditClick(record)}
        >
          修改备注
        </a>
      ),
    },
  ];

  const handleEditClick = (record: API.OrderVO) => {
    setCurrentOrder(record);
    form.setFieldsValue({ remark: record.remark });
    setIsModalVisible(true);
  };

  const handleOk = async () => {
    try {
      const values = await form.validateFields();
      const hide = message.loading('正在更新');
      await editOrderRemarkUsingPost({
        orderId: currentOrder!.orderId,
        remark: values.remark,
      });
      hide();
      message.success('更新成功');
      setIsModalVisible(false);
      actionRef.current?.reload();
    } catch (error) {
      message.error('更新失败，' + error.message);
    }
  };

  const handleCancel = () => {
    setIsModalVisible(false);
  };

  return (
    <>
      <PageContainer>
        <ProTable<API.OrderVO>
          headerTitle="订单列表"
          actionRef={actionRef}
          rowKey="orderId"
          search={{
            labelWidth: 120,
          }}
          request={async (params) => {
            const { data, code } = await pageOrdersUsingGet({
              pageNum: params.current,
              pageSize: params.pageSize,
              orderId: params.orderId,
              userId: params.userId ? params.userId : undefined,
              generatorId: params.generatorId ? params.generatorId : undefined,
            });

            return {
              success: code === 0,
              data: data?.data || [],
              total: data?.total || 0,
            };
          }}
          columns={columns}
        />
      </PageContainer>
      <Modal
        title="修改备注"
        visible={isModalVisible}
        onOk={handleOk}
        onCancel={handleCancel}
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="remark"
            label="备注"
            rules={[{ required: true, message: '请输入备注' }, { max: 100, message: '备注最长为 100 字符' }]}
          >
            <Input.TextArea rows={4} />
          </Form.Item>
        </Form>
      </Modal>
    </>
  );
};

export default OrderAdminPage;