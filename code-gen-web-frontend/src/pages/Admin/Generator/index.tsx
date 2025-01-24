import CreateModal from '@/pages/Admin/Generator/components/CreateModal';
import UpdateModal from '@/pages/Admin/Generator/components/UpdateModal';
import { deleteGeneratorUsingPost, listGeneratorByPageUsingPost } from '@/services/backend/generatorController';
import { PlusOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import { PageContainer, ProTable } from '@ant-design/pro-components';
import '@umijs/max';
import {Button, message, Select, Space, Tag, Typography} from 'antd';
import React, { useRef, useState } from 'react';

/**
 * 用户管理页面
 *
 * @constructor
 */
const GeneratorAdminPage: React.FC = () => {
  // 是否显示新建窗口
  const [createModalVisible, setCreateModalVisible] = useState<boolean>(false);
  // 是否显示更新窗口
  const [updateModalVisible, setUpdateModalVisible] = useState<boolean>(false);
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
        console.log("标签",row)
        const tags = JSON.parse(row)
        return (
          <>
          {tags.map((tag:any) => (<Tag key={tag}>{tag}</Tag>))}
          </>
      )

      },
    },
    {
      title: '描述',
      dataIndex: 'description',
      valueType: 'textarea',
    },
    {
      title: '基础包名',
      dataIndex: 'basePackage',
      valueType: 'text',
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
    {
      title: '状态',
      dataIndex: 'status',
      valueEnum: {
        0: {
          text: '默认状态',
        },
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
  return (
    <PageContainer>
      <ProTable<API.Generator>
        headerTitle={'查询表格'}
        actionRef={actionRef}
        rowKey="key"
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
        columns={columns}
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
        columns={columns}
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
    </PageContainer>
  );
};
export default GeneratorAdminPage;
