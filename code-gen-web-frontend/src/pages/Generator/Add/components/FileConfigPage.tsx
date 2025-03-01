import React, { useState, useEffect } from 'react';
import { Button, Form, Input, Radio, Space, Tree, Popconfirm } from 'antd';
import { FolderOutlined, FileOutlined, DeleteOutlined } from '@ant-design/icons';
import type { DataNode } from 'antd/es/tree';
import { v4 as uuidv4 } from 'uuid';
import type { Key } from 'react';



// 文件配置信息
interface FileInfo {
  id: string;
  type: 'file' | 'group';  // 节点类型：文件或分组
  groupKey?: string;       // 分组的唯一标识
  groupName?: string;      // 分组显示名称
  condition?: string;      // 分组条件
  files?: FileInfo[];      // 子文件列表
  inputPath?: string;      // 文件输入路径
  outputPath?: string;     // 文件输出路径
  generateType?: 'static' | 'dynamic';  // 生成类型：静态或动态
}

// FileConfigPageProps
interface FileConfigPageProps {
  value?: FileInfo[];
  onChange?: (files: FileInfo[]) => void;
}

// 添加深度比较函数
const deepEqual = (obj1: any, obj2: any): boolean => {
  if (obj1 === obj2) return true;
  if (!obj1 || !obj2) return false;
  if (typeof obj1 !== typeof obj2) return false;
  
  if (Array.isArray(obj1)) {
    if (!Array.isArray(obj2) || obj1.length !== obj2.length) return false;
    return obj1.every((item, index) => deepEqual(item, obj2[index]));
  }
  
  if (typeof obj1 === 'object') {
    const keys1 = Object.keys(obj1);
    const keys2 = Object.keys(obj2);
    if (keys1.length !== keys2.length) return false;
    return keys1.every(key => {
      // 跳过比较可能导致循环引用的属性
      if (key === 'parent' || key === 'children') return true;
      return deepEqual(obj1[key], obj2[key]);
    });
  }
  
  return obj1 === obj2;
};


// 文件配置组件
const FileConfigPage: React.FC<FileConfigPageProps> = ({ value, onChange }) => {
  const [selectedKey, setSelectedKey] = useState<string | undefined>();
  const [selectedNode, setSelectedNode] = useState<FileInfo | undefined>();

  const fileInfoToTreeData = (fileInfo: FileInfo): DataNode => {
    // 创建清理后的文件信息对象，避免循环引用
    const cleanFileInfo = {
      id: fileInfo.id ?? uuidv4(),
      type: fileInfo.type,
      groupKey: fileInfo.groupKey,
      groupName: fileInfo.groupName,
      condition: fileInfo.condition,
      inputPath: fileInfo.inputPath,
      outputPath: fileInfo.outputPath,
      generateType: fileInfo.generateType
    };

    // 根据节点类型返回不同的树节点结构
    if (fileInfo.type === 'group') {
      return {
        key: fileInfo.id,
        title: `📁 ${fileInfo.groupName || '未命名分组'} (${fileInfo.groupKey || '未命名'})`,
        icon: <FolderOutlined />,
        children: fileInfo.files?.map(file => fileInfoToTreeData(file)) || [],
        data: cleanFileInfo,
        selectable: true
      };
    }

    return {
      key: fileInfo.id,
      title: `📄 ${fileInfo.inputPath || '未命名文件'}`,
      icon: <FileOutlined />,
      isLeaf: true,
      data: cleanFileInfo,
      selectable: true
    };
  };
  
  // 将 treeData 改为受控模式
  const [treeData, setTreeData] = useState<DataNode[]>(() => {
    if (!value) return [];
    const processFileInfo = (fileInfo: FileInfo): FileInfo => ({
      ...fileInfo,
      id: fileInfo.id || uuidv4(),
      files: fileInfo.files?.map(processFileInfo)
    });
    return value.map(fileInfo => fileInfoToTreeData(processFileInfo(fileInfo)));
  });

  // 修改 effect，使用新的比较方法
  useEffect(() => {
    if (!value) return;
    
    const processFileInfo = (fileInfo: FileInfo): FileInfo => ({
      ...fileInfo,
      id: fileInfo.id || uuidv4(),
      files: fileInfo.files?.map(processFileInfo)
    });

    const newTreeData = value.map(fileInfo => fileInfoToTreeData(processFileInfo(fileInfo)));
    
    // 使用深度比较，避免不必要的更新
    const isEqual = deepEqual(newTreeData, treeData);
    if (!isEqual) {
      setTreeData(newTreeData);
    }
  }, []);

  // 修改 onChange 触发逻辑
  useEffect(() => {
    const extractFileInfo = (nodes: DataNode[]): FileInfo[] => {
      return nodes.map(node => ({
        ...node.data,
        files: node.children ? extractFileInfo(node.children) : undefined
      }));
    };
    
    const fileInfo = extractFileInfo(treeData);
    // 使用深度比较，避免不必要的 onChange 调用
    const isEqual = deepEqual(fileInfo, value);
    if (!isEqual && onChange) {
      onChange(fileInfo);
    }
  }, [treeData, onChange, value]);

  const [form] = Form.useForm();

  const handleAddGroup = () => {
    const newGroup: FileInfo = {
      id: uuidv4(),
      type: 'group',
      groupKey: 'newGroup',
      groupName: '新分组',
      files: []
    };
    setTreeData([...treeData, fileInfoToTreeData(newGroup)]);
  };

  const handleAddFile = (parentNode?: DataNode) => {
    const newFile: FileInfo = {
      id: uuidv4(),
      type: 'file',
      inputPath: 'newFile.txt',
      outputPath: 'newFile.txt',
      generateType: 'static'
    };

    if (parentNode?.data.type === 'group') {
      // Add file to selected group
      const newTreeData = [...treeData];
      const findAndAddFile = (nodes: DataNode[]): boolean => {
        for (let i = 0; i < nodes.length; i++) {
          if (nodes[i].key === parentNode.key) {
            nodes[i].children = nodes[i].children || [];
            nodes[i].children.push(fileInfoToTreeData(newFile));
            return true;
          }
          if (nodes[i].children) {
            if (findAndAddFile(nodes[i].children)) {
              return true;
            }
          }
        }
        return false;
      };
      findAndAddFile(newTreeData);
      setTreeData(newTreeData);
    } else {
      // Add file to root level
      setTreeData([...treeData, fileInfoToTreeData(newFile)]);
    }
  };

  const onSelect = (selectedKeys: Key[], info: any) => {
    console.log("Tree onSelect triggered");
    console.log("selectedKeys:", selectedKeys);
    console.log("info:", info);
    
    if (selectedKeys.length > 0) {
      const key = selectedKeys[0].toString();
      console.log("Selected key:", key);
      setSelectedKey(key);
      setSelectedNode(info.node.data);
      form.setFieldsValue(info.node.data);
    } else {
      setSelectedKey(undefined);
      setSelectedNode(undefined);
      form.resetFields();
    }
  };

  const onDrop = (info: any) => {
    // 添加类型验证，防止无效的拖拽操作
    if (!info.dragNode || !info.node) return;
    
    const dropKey = info.node.key;
    const dragKey = info.dragNode.key;
    const dropPos = info.node.pos.split('-');
    const dropPosition = info.dropPosition - Number(dropPos[dropPos.length - 1]);

    // 防止文件节点包含子节点
    if (info.node.data.type === 'file') return;

    // 防止分组嵌套
    if (info.dragNode.data.type === 'group' && info.node.data.type === 'group') return;

    const loop = (
      data: DataNode[],
      key: string,
      callback: (node: DataNode, i: number, data: DataNode[]) => void,
    ) => {
      for (let i = 0; i < data.length; i++) {
        if (data[i].key === key) {
          callback(data[i], i, data);
          return;
        }
        if (data[i].children) {
          loop(data[i].children!, key, callback);
        }
      }
    };

    const data = [...treeData];

    // Find dragObject
    let dragObj: DataNode;
    loop(data, dragKey, (item, index, arr) => {
      arr.splice(index, 1);
      dragObj = item;
    });

    if (!info.dropToGap) {
      // Drop on the content
      loop(data, dropKey, (item) => {
        item.children = item.children || [];
        item.children.unshift(dragObj);
      });
    } else if (
      (info.node.children || []).length > 0 && 
      info.node.expanded && 
      dropPosition === 1
    ) {
      // Drop on the bottom gap
      loop(data, dropKey, (item) => {
        item.children = item.children || [];
        item.children.unshift(dragObj);
      });
    } else {
      let ar: DataNode[] = [];
      let i: number;
      loop(data, dropKey, (_item, index, arr) => {
        ar = arr;
        i = index;
      });
      if (dropPosition === -1) {
        ar.splice(i!, 0, dragObj!);
      } else {
        ar.splice(i! + 1, 0, dragObj!);
      }
    }

    setTreeData(data);
  };

  const handleDelete = (nodeKey: string) => {
    const newTreeData = [...treeData];
    
    const removeNode = (nodes: DataNode[]): boolean => {
      const index = nodes.findIndex(node => node.key === nodeKey);
      if (index > -1) {
        nodes.splice(index, 1);
        return true;
      }
      
      return nodes.some((node, i) => {
        if (node.children?.length) {
          const removed = removeNode(node.children);
          if (removed && node.children.length === 0) {
            nodes[i].children = undefined;
          }
          return removed;
        }
        return false;
      });
    };

    removeNode(newTreeData);
    setTreeData(newTreeData);
    
    // 如果删除的是当前选中节点，清空选择状态
    if (selectedKey === nodeKey) {
      setSelectedKey(undefined);
      setSelectedNode(undefined);
      form.resetFields();
    }
  };

  return (
    <div style={{ 
      display: 'flex', 
      gap: '24px',
      height: '600px',
      border: '1px solid #f0f0f0',
      borderRadius: '8px',
      padding: '16px',
      background: '#fff',
      minWidth: '800px'
    }}>
      {/* Left Panel - File Tree */}
      <div style={{ 
        width: '300px', 
        borderRight: '1px solid #f0f0f0',
        display: 'flex',
        flexDirection: 'column',
        height: '100%'
      }}>
        <Space style={{ 
          marginBottom: '16px',
          padding: '0 8px'
        }}>
          <Button onClick={handleAddGroup}>添加分组</Button>
          <Button onClick={() => {
            const selectedParent = selectedNode?.type === 'group' 
              ? {key: selectedKey, data: selectedNode} as DataNode 
              : undefined;
            handleAddFile(selectedParent);
          }}>
            添加文件
          </Button>
        </Space>
        <div style={{ 
          flex: 1, 
          overflow: 'auto',
          padding: '0 8px'
        }}>
          <Tree
            treeData={treeData}
            selectedKeys={selectedKey ? [selectedKey] : []}
            onSelect={onSelect}
            draggable
            onDrop={onDrop}
            defaultExpandAll
            defaultExpandParent
            selectable
          />
        </div>
      </div>

      {/* Right Panel - Configuration Form */}
      <div style={{ 
        flex: 1,
        overflow: 'auto',
        padding: '0 16px'
      }}>
        <Form
          form={form}
          layout="vertical"
          onValuesChange={(changedValues) => {
            if (selectedNode && selectedKey) {
              // Update the tree data when form values change
              const newTreeData = [...treeData];
              const updateNode = (nodes: DataNode[]): boolean => {
                for (let i = 0; i < nodes.length; i++) {
                  if (nodes[i].key === selectedKey) {
                    nodes[i].data = { ...nodes[i].data, ...changedValues };
                    if (nodes[i].data.type === 'group') {
                      nodes[i].title = `📁 ${nodes[i].data.groupName} (${nodes[i].data.groupKey})`;
                    } else {
                      nodes[i].title = `📄 ${nodes[i].data.inputPath}`;
                    }
                    return true;
                  }
                  if (nodes[i].children) {
                    if (updateNode(nodes[i].children)) {
                      return true;
                    }
                  }
                }
                return false;
              };
              updateNode(newTreeData);
              setTreeData(newTreeData);
            }
          }}
        >
          {selectedNode ? (
            <>
              <div style={{ 
                marginBottom: '16px', 
                textAlign: 'right',
                position: 'sticky',
                top: 0,
                background: '#fff',
                padding: '8px 0',
                zIndex: 1
              }}>
                <Popconfirm
                  title="确定要删除吗？"
                  description={selectedNode.type === 'group' ? 
                    "删除分组将同时删除其下所有文件" : 
                    "确定要删除这个文件吗？"}
                  onConfirm={() => selectedKey && handleDelete(selectedKey)}
                  okText="确定"
                  cancelText="取消"
                >
                  <Button 
                    danger 
                    icon={<DeleteOutlined />}
                  >
                    删除{selectedNode.type === 'group' ? '分组' : '文件'}
                  </Button>
                </Popconfirm>
              </div>

              {selectedNode.type === 'group' ? (
                // Group Config
                <>
                  <Form.Item label="分组名称" name="groupName">
                    <Input />
                  </Form.Item>
                  <Form.Item label="分组KEY" name="groupKey">
                    <Input />
                  </Form.Item>
                  <Form.Item label="条件" name="condition">
                    <Input />
                  </Form.Item>
                </>
              ) : (
                // File Config
                <>
                  <Form.Item label="输入路径" name="inputPath">
                    <Input />
                  </Form.Item>
                  <Form.Item label="输出路径" name="outputPath">
                    <Input />
                  </Form.Item>
                  <Form.Item label="生成类型" name="generateType">
                    <Radio.Group>
                      <Radio value="static">静态</Radio>
                      <Radio value="dynamic">动态</Radio>
                    </Radio.Group>
                  </Form.Item>
                </>
              )}
            </>
          ) : (
            <div style={{ 
              textAlign: 'center', 
              color: '#999', 
              padding: '24px',
              fontSize: '14px'
            }}>
              请选择一个文件或分组进行配置
            </div>
          )}
        </Form>
      </div>
    </div>
  );
};

export default FileConfigPage;