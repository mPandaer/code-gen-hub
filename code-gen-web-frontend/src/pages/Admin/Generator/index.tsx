import CreateModal from '@/pages/Admin/Generator/components/CreateModal';
import UpdateModal from '@/pages/Admin/Generator/components/UpdateModal';
import { deleteGeneratorUsingPost, listGeneratorByPageUsingPost } from '@/services/backend/generatorController';
import { PlusOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import { PageContainer, ProTable } from '@ant-design/pro-components';
import '@umijs/max';
import { Button, message, Select, Space, Tag, Typography, Drawer, Tabs, Descriptions, Image } from 'antd';
import React, { useRef, useState } from 'react';
import FileInfoTab from '@/pages/Generator/Detail/components/FileInfo';
import ModelInfoTab from '@/pages/Generator/Detail/components/ModelInfo';

/**
 * 代码生成器管理页面
 *
 * @constructor
 */
const GeneratorAdminPage: React.FC = () => {
  // 是否显示新建窗口
  const [createModalVisible, setCreateModalVisible] = useState<boolean>(false);
  // 是否显示更新窗口
  const [updateModalVisible, setUpdateModalVisible] = useState<boolean>(false);
  // 是否显示详情抽屉
  const [detailDrawerVisible, setDetailDrawerVisible] = useState<boolean>(false);
  const actionRef = useRef<ActionType>();
  // 当前用户点击的数据
  const [currentRow, setCurrentRow] = useState<API.Generator>();

  /**
   * 删除节点
   *
   * @param row
   */
  const handleDelete = async (row: API.Generator) => {
    const hide = message.loading('正在删除');
    if (!row) return true;
    try {
      await deleteGeneratorUsingPost({
        id: row.id as any,
      });
      hide();
      message.success('删除成功');
      actionRef?.current?.reload();
      return true;
    } catch (error: any) {
      hide();
      message.error('删除失败，' + error.message);
      return false;
    }
  };

  /**
   * 查看详情
   * 
   * @param record 
   */
  const handleViewDetail = (record: API.Generator) => {
    setCurrentRow(record);
    setDetailDrawerVisible(true);
  };

  /**
   * 表格列配置
   */
  const columns: ProColumns<API.Generator>[] = [
    {
      title: 'id',
      dataIndex: 'id',
      valueType: 'text',
      hideInForm: true,
    },
    {
      title: '名称',
      dataIndex: 'name',
      valueType: 'text',
    },
    {
      title: '标签',
      dataIndex: 'tags',
      valueType: 'text',
      renderFormItem: (schema) => {
        const {fieldProps} = schema;
        // @ts-ignore
        return <Select {...fieldProps} mode="tags"/>
      },
      renderText: (row: string) => {
        if (!row) return null;
        try {
          const tags = JSON.parse(row);
          return (
            <>
              {tags.map((tag:any) => (<Tag key={tag}>{tag}</Tag>))}
            </>
          );
        } catch (e) {
          return row;
        }
      },
    },
    {
      title: '描述',
      dataIndex: 'description',
      valueType: 'textarea',
      ellipsis: true,
    },
    {
      title: '版本',
      dataIndex: 'version',
      valueType: 'text',
    },
    {
      title: '作者',
      dataIndex: 'author',
      valueType: 'text',
    },
    {
      title: '状态',
      dataIndex: 'status',
      valueEnum: {
        0: {
          text: '待审核',
          status: 'warning',
        },
        1: {
          text: '审核通过',
          status: 'success',
        },
        2: {
          text: '审核失败',
          status: 'error',
        },
      },
      render: (_, record) => {
        return (
          <Select
            value={record.status}
            style={{ width: 120 }}
            onChange={(value) => handleUpdateStatus(record, value)}
            options={[
              { value: 0, label: '待审核' },
              { value: 1, label: '审核通过' },
              { value: 2, label: '审核失败' },
            ]}
          />
        );
      },
    },
    {
      title: '创建时间',
      sorter: true,
      dataIndex: 'createTime',
      valueType: 'dateTime',
      hideInSearch: true,
      hideInForm: true,
    },
    {
      title: '更新时间',
      sorter: true,
      dataIndex: 'updateTime',
      valueType: 'dateTime',
      hideInSearch: true,
      hideInForm: true,
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => (
        <Space size="middle">
          <Typography.Link
            onClick={() => handleViewDetail(record)}
          >
            详情
          </Typography.Link>
          <Typography.Link
            onClick={() => {
              setCurrentRow(record);
              setUpdateModalVisible(true);
            }}
          >
            修改
          </Typography.Link>
          <Typography.Link type="danger" onClick={() => handleDelete(record)}>
            删除
          </Typography.Link>
        </Space>
      ),
    },
  ];

  // 表单中需要使用的完整列配置
  const formColumns: ProColumns<API.Generator>[] = [
    ...columns,
    {
      title: '基础包名',
      dataIndex: 'basePackage',
      valueType: 'text',
    },
    {
      title: '产物包地址',
      dataIndex: 'distPath',
      valueType: 'text',
    },
    {
      title: '文件配置',
      dataIndex: 'fileConfig',
      valueType: 'jsonCode',
    },
    {
      title: '数据模型配置',
      dataIndex: 'modelConfig',
      valueType: 'jsonCode',
    },
    {
      title: '头像',
      dataIndex: 'generatorAvatar',
      valueType: 'image',
      fieldProps: {
        width: 64,
      },
      hideInSearch: true,
    },
    {
      title: '用户ID',
      dataIndex: 'userId',
      valueType: 'text',
    },
  ];

  // 详情抽屉的标签页
  const detailTabs = [
    {
      key: 'basic',
      label: '基本信息',
      children: currentRow && (
        <Descriptions column={1} bordered>
          <Descriptions.Item label="ID">{currentRow.id}</Descriptions.Item>
          <Descriptions.Item label="名称">{currentRow.name}</Descriptions.Item>
          <Descriptions.Item label="描述">{currentRow.description}</Descriptions.Item>
          <Descriptions.Item label="标签">
            {currentRow.tags && (
              <>
                {JSON.parse(currentRow.tags as string).map((tag: any) => (
                  <Tag key={tag}>{tag}</Tag>
                ))}
              </>
            )}
          </Descriptions.Item>
          <Descriptions.Item label="基础包名">{currentRow.basePackage}</Descriptions.Item>
          <Descriptions.Item label="版本">{currentRow.version}</Descriptions.Item>
          <Descriptions.Item label="作者">{currentRow.author}</Descriptions.Item>
          <Descriptions.Item label="产物包地址">{currentRow.distPath}</Descriptions.Item>
          <Descriptions.Item label="用户ID">{currentRow.userId}</Descriptions.Item>
          <Descriptions.Item label="状态">
            {currentRow.status === 0 ? '待审核' : 
             currentRow.status === 1 ? '审核通过' : 
             currentRow.status === 2 ? '审核失败' : currentRow.status}
          </Descriptions.Item>
          <Descriptions.Item label="创建时间">{currentRow.createTime}</Descriptions.Item>
          <Descriptions.Item label="更新时间">{currentRow.updateTime}</Descriptions.Item>
          <Descriptions.Item label="头像">
            {currentRow.generatorAvatar && (
              <Image src={currentRow.generatorAvatar} width={200} />
            )}
          </Descriptions.Item>
        </Descriptions>
      ),
    },
    {
      key: 'fileConfig',
      label: '文件配置',
      children: currentRow?.fileConfig && (
        <div style={{ padding: '16px 0' }}>
          <FileInfoTab {...(typeof currentRow.fileConfig === 'string' 
            ? JSON.parse(currentRow.fileConfig) 
            : currentRow.fileConfig)} />
        </div>
      ),
    },
    {
      key: 'modelConfig',
      label: '数据模型配置',
      children: currentRow?.modelConfig && (
        <div style={{ padding: '16px 0' }}>
          <ModelInfoTab {...(typeof currentRow.modelConfig === 'string' 
            ? JSON.parse(currentRow.modelConfig) 
            : currentRow.modelConfig)} />
        </div>
      ),
    },
  ];

  /**
   * 更新生成器状态
   * 
   * @param row 当前行数据
   * @param status 要更新的状态值
   */
  const handleUpdateStatus = async (row: API.Generator, status: number) => {
    const hide = message.loading('正在更新状态');
    if (!row) return true;
    try {
      // 复用现有的更新接口，只更新状态字段
      const { updateGeneratorUsingPost } = await import('@/services/backend/generatorController');
      await updateGeneratorUsingPost({
        id: row.id,
        status,
      });
      hide();
      message.success('状态更新成功');
      actionRef?.current?.reload();
      return true;
    } catch (error: any) {
      hide();
      message.error('状态更新失败，' + error.message);
      return false;
    }
  };

  return (
    <PageContainer>
      <ProTable<API.Generator>
        headerTitle={'代码生成器管理'}
        actionRef={actionRef}
        rowKey="id"
        search={{
          labelWidth: 120,
        }}
        toolBarRender={() => [
          <Button
            type="primary"
            key="primary"
            onClick={() => {
              setCreateModalVisible(true);
            }}
          >
            <PlusOutlined /> 新建
          </Button>,
        ]}
        request={async (params, sort, filter) => {
          const sortField = Object.keys(sort)?.[0];
          const sortOrder = sort?.[sortField] ?? undefined;

          const { data, code } = await listGeneratorByPageUsingPost({
            ...params,
            sortField,
            sortOrder,
            ...filter,
          } as API.GeneratorQueryRequest);

          return {
            success: code === 0,
            data: data?.records || [],
            total: Number(data?.total) || 0,
          };
        }}
        columns={columns}
      />
      <CreateModal
        visible={createModalVisible}
        columns={formColumns}
        onSubmit={() => {
          setCreateModalVisible(false);
          actionRef.current?.reload();
        }}
        onCancel={() => {
          setCreateModalVisible(false);
        }}
      />
      <UpdateModal
        visible={updateModalVisible}
        columns={formColumns}
        oldData={currentRow}
        onSubmit={() => {
          setUpdateModalVisible(false);
          setCurrentRow(undefined);
          actionRef.current?.reload();
        }}
        onCancel={() => {
          setUpdateModalVisible(false);
        }}
      />
      <Drawer
        title="代码生成器详情"
        width={800}
        open={detailDrawerVisible}
        onClose={() => {
          setDetailDrawerVisible(false);
          setCurrentRow(undefined);
        }}
        extra={
          <Space>
            <Button 
              type="primary"
              onClick={() => {
                setDetailDrawerVisible(false);
                setUpdateModalVisible(true);
              }}
            >
              修改
            </Button>
          </Space>
        }
      >
        {currentRow ? (
          <Tabs defaultActiveKey="basic" items={detailTabs} />
        ) : (
          <div>加载中...</div>
        )}
      </Drawer>
    </PageContainer>
  );
};

export default GeneratorAdminPage;
