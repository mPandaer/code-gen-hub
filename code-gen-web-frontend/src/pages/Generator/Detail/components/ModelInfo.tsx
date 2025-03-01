import React, { useState, useEffect } from 'react';
import { Tree, Table, Typography, Spin, Empty } from 'antd';
import type { DataNode } from 'antd/es/tree';
import type { ColumnsType } from 'antd/es/table';

const { Text } = Typography;

interface ModelItem {
  fieldName: string;
  type: string;
  description: string;
  defaultValue: any;
  abbr?: string;
}

interface GroupItem {
  groupKey: string;
  groupName: string;
  type: string;
  condition?: string;
  models: ModelItem[];
}

interface ModelItemWithGroup extends ModelItem {
  groupKey?: string;
  groupName?: string;
}

const ModelInfoTab: React.FC<API.ModelConfig> = (modelConfig) => {
  const [selectedModels, setSelectedModels] = useState<ModelItemWithGroup[]>([]);
  const [isGroupView, setIsGroupView] = useState(false);
  const [loading, setLoading] = useState(true);

  // 分组视图的列定义
  const groupColumns: ColumnsType<GroupItem> = [
    {
      title: '分组名称',
      dataIndex: 'groupName',
      key: 'groupName',
      width: 200,
    },
    {
      title: '分组标识',
      dataIndex: 'groupKey',
      key: 'groupKey',
      width: 200,
    },
    {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      width: 120,
    },
    {
      title: '条件',
      dataIndex: 'condition',
      key: 'condition',
      width: 120,
      render: (text) => text || '-',
    },
  ];

  // 模型视图的列定义
  const modelColumns: ColumnsType<ModelItemWithGroup> = [
    {
      title: '字段名',
      dataIndex: 'fieldName',
      key: 'fieldName',
    },
    {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      width: 100,
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
    },
    {
      title: '默认值',
      dataIndex: 'defaultValue',
      key: 'defaultValue',
      render: (value) => String(value),
    },
    {
      title: '简写',
      dataIndex: 'abbr',
      key: 'abbr',
      width: 80,
      render: (text) => text || '-',
    },
  ];

  // 修改获取所有模型的方法，添加分组信息
  const getAllModels = (models: (GroupItem | ModelItem)[], parentGroup?: GroupItem): ModelItemWithGroup[] => {
    const result: ModelItemWithGroup[] = [];
    models.forEach((item) => {
      if (item.type === 'group') {
        const groupItem = item as GroupItem;
        result.push(...getAllModels(groupItem.models, groupItem));
      } else {
        const modelItem = item as ModelItem;
        result.push({
          ...modelItem,
          groupKey: parentGroup?.groupKey,
          groupName: parentGroup?.groupName,
        });
      }
    });
    return result;
  };

  // 修改树形数据生成方法，传递分组信息
  const getTreeData = (models: (GroupItem | ModelItem)[]): DataNode[] => {
    return models.map((item) => {
      if (item.type === 'group' || ('groupKey' in item && item.groupKey)) {
        const groupItem = item as GroupItem;
        return {
          key: groupItem.groupKey,
          title: (
            <>
              <Text strong>{groupItem.groupName}</Text>
              {groupItem.condition && <Text type="secondary"> (条件: {groupItem.condition})</Text>}
            </>
          ),
          children: groupItem.models ? getTreeData(groupItem.models) : [],
          data: getAllModels([groupItem]),
        };
      }
      const modelItem = item as ModelItem;
      return {
        key: modelItem.fieldName,
        title: (
          <>
            <Text>{modelItem.fieldName}</Text>
            <Text type="secondary"> ({modelItem.type})</Text>
          </>
        ),
        data: [modelItem],
      };
    });
  };

  // Update the onSelect function to correctly handle group data
  const onSelect = (selectedKeys: React.Key[], info: any) => {
    if (info.node.data) {
      const isGroup = info.node.children !== undefined;
      setIsGroupView(isGroup);
      if (isGroup) {
        // For group nodes, find the original group data
        const groupData = modelConfig.models.find(
          (item): item is GroupItem => 
            'groupKey' in item && item.groupKey === info.node.key
        );
        if (groupData) {
          setSelectedModels([groupData]);
        }
      } else {
        setSelectedModels(info.node.data);
      }
    }
  };

  // Update the renderGroupInfo function to correctly handle the group data
  const renderGroupInfo = (groupData: GroupItem) => (
    <>
      <Table
        columns={groupColumns}
        dataSource={[{
          groupName: groupData.groupName,
          groupKey: groupData.groupKey,
          type: groupData.type,
          condition: groupData.condition
        }]}
        size="small"
        pagination={false}
      />
      <Typography.Title level={5} style={{ marginTop: '24px' }}>
        分组模型列表
      </Typography.Title>
      <Table
        columns={modelColumns.filter(col => col.dataIndex !== 'groupName')}
        dataSource={groupData.models}
        size="small"
        pagination={false}
      />
    </>
  );

  // Update useEffect to handle loading state
  useEffect(() => {
    setLoading(true);
    if (modelConfig.models) {
      setSelectedModels(getAllModels(modelConfig.models));
      setIsGroupView(false);
    }
    setLoading(false);
  }, [modelConfig.models]);

  return (
    <div style={{ 
      display: 'flex', 
      gap: '24px',
      height: '600px',
      width: '100%',
      overflow: 'hidden'
    }}>
      {loading ? (
        <div style={{ 
          width: '100%', 
          height: '100%', 
          display: 'flex', 
          justifyContent: 'center', 
          alignItems: 'center' 
        }}>
          <Spin tip="加载中..." />
        </div>
      ) : !modelConfig.models || modelConfig.models.length === 0 ? (
        <div style={{ 
          width: '100%', 
          height: '100%', 
          display: 'flex', 
          flexDirection: 'column',
          justifyContent: 'center', 
          alignItems: 'center',
          gap: '16px'
        }}>
          <Empty description="暂无模型配置信息" />
          <Typography.Text type="secondary">请先配置模型信息</Typography.Text>
        </div>
      ) : (
        <>
          <div style={{ 
            flex: '0 0 300px',
            overflow: 'auto',
            padding: '0 8px' 
          }}>
            <Typography.Title level={5}>模型结构</Typography.Title>
            <Tree
              treeData={getTreeData(modelConfig.models)}
              defaultExpandAll
              onSelect={onSelect}
              height={520}
            />
          </div>
          <div style={{ 
            flex: 1,
            overflow: 'auto',
            padding: '0 8px'
          }}>
            <Typography.Title level={5}>
              {isGroupView ? '分组信息' : '详细信息'}
            </Typography.Title>
            {isGroupView 
              ? renderGroupInfo(selectedModels[0] as GroupItem)
              : (
                <Table
                  columns={modelColumns}
                  dataSource={selectedModels}
                  size="small"
                  pagination={false}
                  scroll={{ y: 520 }}
                />
              )}
          </div>
        </>
      )}
    </div>
  );
};

export default ModelInfoTab;
