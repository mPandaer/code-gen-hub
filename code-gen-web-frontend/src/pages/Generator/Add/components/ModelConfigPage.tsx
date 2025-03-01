import React, { useState, useEffect, useCallback } from 'react';
import { Button, Form, Input, Radio, Space, Tree, Popconfirm } from 'antd';
import { FolderOutlined, CodeOutlined, DeleteOutlined } from '@ant-design/icons';
import type { DataNode } from 'antd/es/tree';
import { v4 as uuidv4 } from 'uuid';

interface ModelConfigFormData {
  modelConfig: {
    models: ModelInfo[];
  };
}

interface ModelInfo {
  id: string;
  groupKey?: string;
  groupName?: string;
  condition?: string;
  models?: ModelInfo[];
  fieldName?: string;
  type?: string;
  description?: string;
  defaultValue?: any;
  abbr?: string;
}

interface ModelConfigPageProps {
  value?: ModelInfo[];
  onChange?: (models: ModelInfo[]) => void;
}

interface CustomDataNode extends DataNode {
  data: ModelInfo;
}

const ModelConfigPage: React.FC<ModelConfigPageProps> = ({ value, onChange }) => {
  const [selectedKey, setSelectedKey] = useState<string>();
  const [selectedNode, setSelectedNode] = useState<ModelInfo>();

  console.log("ModelConfigPage value", value);

  const modelInfoToTreeData = (modelInfo: ModelInfo): CustomDataNode => {
    if (modelInfo.type === 'group') {
      return {
        key: modelInfo.id,
        title: `📁 ${modelInfo.groupName} (${modelInfo.groupKey})`,
        icon: <FolderOutlined />,
        children: modelInfo.models?.map(model => modelInfoToTreeData(model)) || [],
        data: modelInfo,
        selectable: true
      };
    }

    return {
      key: modelInfo.id,
      title: `📄 ${modelInfo.fieldName}: ${modelInfo.type}`,
      icon: <CodeOutlined />,
      isLeaf: true,
      data: modelInfo,
      selectable: true
    };
  };


  const [treeData, setTreeData] = useState<CustomDataNode[]>(() => {
    if (!value) return [];
    const processModelInfo = (modelInfo: ModelInfo): ModelInfo => ({
      ...modelInfo,
      id: modelInfo.id || uuidv4(),
      models: modelInfo.models?.map(processModelInfo)
    });
    return value.map(modelInfo => modelInfoToTreeData(processModelInfo(modelInfo)));
  });
  const [form] = Form.useForm();

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
        if (key === 'parent' || key === 'children') return true;
        return deepEqual(obj1[key], obj2[key]);
      });
    }
    
    return obj1 === obj2;
  };

  useEffect(() => {
    if (!value) return;
    console.log("ModelConfigPage value", value);
    
    const processModelInfo = (modelInfo: ModelInfo): ModelInfo => ({
      ...modelInfo,
      id: modelInfo.id || uuidv4(),
      models: modelInfo.models?.map(processModelInfo)
    });

    const newTreeData = value.map(modelInfo => modelInfoToTreeData(processModelInfo(modelInfo)));
    
    const isEqual = deepEqual(newTreeData, treeData);
    if (!isEqual) {
      setTreeData(newTreeData);
    }
  }, []);

  useEffect(() => {
    const extractModelInfo = (nodes: CustomDataNode[]): ModelInfo[] => {
      return nodes.map(node => ({
        ...node.data,
        models: node.children ? extractModelInfo(node.children) : undefined
      }));
    };
    
    const modelInfo = extractModelInfo(treeData);
    const isEqual = deepEqual(modelInfo, value);
    if (!isEqual && onChange) {
      onChange(modelInfo);
    }
  }, [treeData]);



  const updateParentForm = useCallback((newTreeData: CustomDataNode[]) => {
    const convertToModelInfo = (node: CustomDataNode): ModelInfo => {
      const modelInfo = { ...node.data };
      if (node.children) {
        modelInfo.models = node.children.map(child => convertToModelInfo(child));
      }
      return modelInfo;
    };

    const models = newTreeData.map(node => convertToModelInfo(node));
    onChange?.(models);
  }, [onChange]);

  const handleAddGroup = () => {
    const newGroup: ModelInfo = {
      id: uuidv4(),
      type: 'group',
      groupKey: 'newGroup',
      groupName: '新分组',
      models: []
    };
    const newTreeData = [...treeData, modelInfoToTreeData(newGroup)];
    setTreeData(newTreeData);
    updateParentForm(newTreeData);
  };

  const handleAddModel = (parentNode?: CustomDataNode) => {
    const newModel: ModelInfo = {
      id: uuidv4(),
      type: 'model',
      fieldName: 'newField',
      type: 'String',
      description: '请输入描述',
      defaultValue: '',
    };

    if (parentNode?.data.type === 'group') {
      // Add model to selected group
      const newTreeData = [...treeData];
      const findAndAddModel = (nodes: CustomDataNode[]): boolean => {
        for (let i = 0; i < nodes.length; i++) {
          if (nodes[i].key === parentNode.key) {
            nodes[i].children = nodes[i].children || [];
            nodes[i].children.push(modelInfoToTreeData(newModel));
            return true;
          }
          if (nodes[i].children) {
            if (findAndAddModel(nodes[i].children)) {
              return true;
            }
          }
        }
        return false;
      };
      findAndAddModel(newTreeData);
      setTreeData(newTreeData);
      updateParentForm(newTreeData);
    } else {
      // Add model to root level
      setTreeData([...treeData, modelInfoToTreeData(newModel)]);
      updateParentForm([...treeData, modelInfoToTreeData(newModel)]);
    }
  };

  const onSelect = (selectedKeys: Key[], info: any) => {
    if (selectedKeys.length > 0) {
      const key = selectedKeys[0] as string;
      setSelectedKey(key);
      setSelectedNode(info.node.data);
      form.setFieldsValue(info.node.data);
    }
  };

  const onDrop = (info: any) => {
    const dropKey = info.node.key;
    const dragKey = info.dragNode.key;
    const dropPos = info.node.pos.split('-');
    const dropPosition = info.dropPosition - Number(dropPos[dropPos.length - 1]);

    const loop = (
      data: CustomDataNode[],
      key: string,
      callback: (node: CustomDataNode, i: number, data: CustomDataNode[]) => void,
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
    let dragObj: CustomDataNode;
    loop(data, dragKey, (item, index, arr) => {
      arr.splice(index, 1);
      dragObj = item;
    });

    // Prevent model from having children
    if (info.node.data.type === 'model') {
      return;
    }

    // Prevent group from being nested in another group
    if (dragObj.data.type === 'group' && info.node.data.type === 'group') {
      return;
    }

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
      let ar: CustomDataNode[] = [];
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
    updateParentForm(data);
  };

  const handleDelete = (nodeKey: string) => {
    const newTreeData = [...treeData];
    
    const removeNode = (nodes: CustomDataNode[]): boolean => {
      for (let i = 0; i < nodes.length; i++) {
        if (nodes[i].key === nodeKey) {
          nodes.splice(i, 1);
          return true;
        }
        if (nodes[i].children) {
          if (removeNode(nodes[i].children!)) {
            // If the node was found and removed from children
            // Check if the parent group is now empty
            if (nodes[i].children!.length === 0) {
              nodes[i].children = undefined;
            }
            return true;
          }
        }
      }
      return false;
    };

    removeNode(newTreeData);
    setTreeData(newTreeData);
    // Clear form and selection if the deleted node was selected
    if (selectedKey === nodeKey) {
      setSelectedKey(undefined);
      setSelectedNode(undefined);
      form.resetFields();
    }
    updateParentForm(newTreeData);
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
      {/* Left Panel - Model Tree */}
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
              ? {key: selectedKey, data: selectedNode} as CustomDataNode 
              : undefined;
            handleAddModel(selectedParent);
          }}>
            添加模型
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
              const updateNode = (nodes: CustomDataNode[]): boolean => {
                for (let i = 0; i < nodes.length; i++) {
                  if (nodes[i].key === selectedKey) {
                    nodes[i].data = { ...nodes[i].data, ...changedValues };
                    if (nodes[i].data.type === 'group') {
                      nodes[i].title = `📁 ${nodes[i].data.groupName} (${nodes[i].data.groupKey})`;
                    } else {
                      nodes[i].title = `📄 ${nodes[i].data.fieldName}: ${nodes[i].data.type}`;
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
              updateParentForm(newTreeData);
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
                    "删除分组将同时删除其下所有模型" : 
                    "确定要删除这个模型吗？"}
                  onConfirm={() => selectedKey && handleDelete(selectedKey)}
                  okText="确定"
                  cancelText="取消"
                >
                  <Button 
                    danger 
                    icon={<DeleteOutlined />}
                  >
                    删除{selectedNode.type === 'group' ? '分组' : '模型'}
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
                // Model Config
                <>
                  <Form.Item label="字段名" name="fieldName">
                    <Input />
                  </Form.Item>
                  <Form.Item label="字段类型" name="fieldType" initialValue={selectedNode?.type}>
                    <Radio.Group>
                      <Radio value="String">String</Radio>
                      <Radio value="boolean">boolean</Radio>
                      <Radio value="number">number</Radio>
                    </Radio.Group>
                  </Form.Item>
                  <Form.Item label="描述" name="description">
                    <Input />
                  </Form.Item>
                  <Form.Item label="默认值" name="defaultValue">
                    <Input />
                  </Form.Item>
                  <Form.Item label="简称" name="abbr">
                    <Input />
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
              请选择一个模型或分组进行配置
            </div>
          )}
        </Form>
      </div>
    </div>
  );
};

export default ModelConfigPage;