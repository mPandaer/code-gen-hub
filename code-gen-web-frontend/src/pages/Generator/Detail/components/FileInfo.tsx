import React, { useState, useEffect } from 'react';
import { Tree, Table, Typography } from 'antd';
import type { DataNode } from 'antd/es/tree';
import type { ColumnsType } from 'antd/es/table';

const { Text } = Typography;

interface FileItem {
  id: string;
  inputPath: string;
  outputPath: string;
  type: string;
  generateType: string;
  condition?: string;
}

interface GroupItem {
  id: string;
  groupKey: string;
  groupName: string;
  type: string;
  condition?: string;
  files: FileItem[];
}

interface FileItemWithGroup extends FileItem {
  groupKey?: string;
  groupName?: string;
}

const FileInfoTab: React.FC<API.FileConfig> = (fileConfig) => {
  const [selectedFiles, setSelectedFiles] = useState<FileItemWithGroup[]>([]);
  // 添加状态来标记当前是否在查看分组
  const [isGroupView, setIsGroupView] = useState(false);

  // Add check for empty file configuration
  if (!fileConfig?.files?.length) {
    return (
      <div style={{ 
        display: 'flex', 
        justifyContent: 'center', 
        alignItems: 'center', 
        height: '600px' 
      }}>
        <Typography.Text type="secondary">暂无文件配置信息</Typography.Text>
      </div>
    );
  }

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
      title: '条件',
      dataIndex: 'condition',
      key: 'condition',
      width: 120,
      render: (text) => text || '-',
    },
  ];

  // 文件视图的列定义
  const fileColumns: ColumnsType<FileItemWithGroup> = [
    {
      title: '输入路径',
      dataIndex: 'inputPath',
      key: 'inputPath',
    },
    {
      title: '输出路径',
      dataIndex: 'outputPath',
      key: 'outputPath',
    },
    {
      title: '生成类型',
      dataIndex: 'generateType',
      key: 'generateType',
      width: 100,
    },
    {
      title: '条件',
      dataIndex: 'condition',
      key: 'condition',
      width: 120,
      render: (text) => text || '-',
    },
  ];

  // 修改获取所有文件的方法，添加分组信息
  const getAllFiles = (files: (GroupItem | FileItem)[], parentGroup?: GroupItem): FileItemWithGroup[] => {
    const result: FileItemWithGroup[] = [];
    files.forEach((item) => {
      if (item.type === 'group') {
        const groupItem = item as GroupItem;
        result.push(...getAllFiles(groupItem.files, groupItem));
      } else {
        const fileItem = item as FileItem;
        result.push({
          ...fileItem,
          groupKey: parentGroup?.groupKey,
          groupName: parentGroup?.groupName,
        });
      }
    });
    return result;
  };

  // 修改树形数据生成方法，传递分组信息
  const getTreeData = (files: (GroupItem | FileItem)[]): DataNode[] => {
    return files.map((item) => {
      if (item.type === 'group') {
        const groupItem = item as GroupItem;
        return {
          key: groupItem.id,
          title: (
            <>
              <Text strong>{groupItem.groupName}</Text>
              {groupItem.condition && <Text type="secondary"> (条件: {groupItem.condition})</Text>}
            </>
          ),
          children: groupItem.files ? getTreeData(groupItem.files) : [],
          data: getAllFiles([groupItem]),
        };
      }
      // For regular files, only show the input path and condition
      const fileItem = item as FileItem;
      return {
        key: fileItem.id,
        title: (
          <>
            <Text>{fileItem.inputPath}</Text>
            {fileItem.condition && <Text type="secondary"> (条件: {fileItem.condition})</Text>}
          </>
        ),
        data: [fileItem],
      };
    });
  };

  // 处理树节点选择事件
  const onSelect = (selectedKeys: React.Key[], info: any) => {
    if (info.node.data) {
      const isGroup = info.node.children !== undefined;
      setIsGroupView(isGroup);
      if (isGroup) {
        // 如果是分组，找到完整的分组数据
        const groupData = fileConfig.files.find(
          (item) => item.type === 'group' && (item as GroupItem).groupName === info.node.title.props.children[0].props.children
        ) as GroupItem;
        // 设置分组数据和分组下的文件
        setSelectedFiles([groupData]);
      } else {
        setSelectedFiles(info.node.data);
      }
    }
  };

  // 渲染分组信息
  const renderGroupInfo = (groupData: GroupItem) => (
    <>
      <Table
        columns={groupColumns}
        dataSource={[groupData]}
        size="small"
        pagination={false}
      />
      <Typography.Title level={5} style={{ marginTop: '24px' }}>
        分组文件列表
      </Typography.Title>
      <Table
        columns={fileColumns.filter(col => col.dataIndex !== 'groupName')} // 移除分组名称列
        dataSource={groupData.files}
        size="small"
        pagination={false}
      />
    </>
  );

  // 初始化时显示所有文件
  useEffect(() => {
    setSelectedFiles(getAllFiles(fileConfig.files));
    setIsGroupView(false);
  }, [fileConfig.files]);

  return (
    <div style={{ 
      display: 'flex', 
      gap: '24px',
      height: '600px',
      width: '100%',
      overflow: 'hidden'
    }}>
      <div style={{ 
        flex: '0 0 300px',
        overflow: 'auto',
        padding: '0 8px' 
      }}>
        <Typography.Title level={5}>文件结构</Typography.Title>
        <Tree
          treeData={getTreeData(fileConfig.files)}
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
          ? renderGroupInfo(selectedFiles[0] as GroupItem)
          : (
            <Table
              columns={fileColumns}
              dataSource={selectedFiles}
              size="small"
              pagination={false}
              scroll={{ y: 520 }}
            />
          )}
      </div>
    </div>
  );
};

export default FileInfoTab;
