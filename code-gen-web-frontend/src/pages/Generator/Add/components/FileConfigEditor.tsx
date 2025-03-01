import { Button, Card, Col, Divider, Form, Input, Row, Select, Tree } from 'antd';
import {useEffect, useState} from "react";
import {PlusOutlined} from "@ant-design/icons";


interface FileConfigFormProps {
  value?: any;
  onChange?: (value: any) => void;
}


interface FileConfigItem {
  id: string;
  parentId: string | null;
  type: 'file' | 'group' | 'dir';  // 添加 'dir' 类型作为根目录
  inputPath?: string;
  outputPath?: string;
  generateType?: 'static' | 'dynamic';
  condition?: string;
  groupKey?: string;
  groupName?: string;
}


// 将内部数据结构转换为API所需的格式
const convertToApiFormat = (items: FileConfigItem[]) => {
  const rootDir = items.find(item => item.type === 'dir');

  if (!rootDir) {
    return { files: [] };
  }

  const convertItem = (item: FileConfigItem): any => {
    if (item.type === 'file') {
      const fileObj: any = {
        inputPath: item.inputPath,
        outputPath: item.outputPath,
        type: 'file',
        generateType: item.generateType,
      };

      if (item.condition) {
        fileObj.condition = item.condition;
      }

      return fileObj;
    }

    if (item.type === 'group') {
      const children = items
        .filter(child => child.parentId === item.id)
        .map(convertItem);

      const groupObj: any = {
        groupKey: item.groupKey,
        groupName: item.groupName,
        type: 'group',
        files: children,
      };

      if (item.condition) {
        groupObj.condition = item.condition;
      }

      return groupObj;
    }

    return null;
  };

  const rootFiles = items
    .filter(item => item.parentId === 'root')
    .map(convertItem)
    .filter(Boolean);

  return {
    originProjectPath: '', // 这个可能需要从其他地方获取
    type: 'dir',
    files: rootFiles,
  };
};


const FileConfigForm: React.FC<FileConfigFormProps> = ({value,onChange}) => {

  const [fileList, setFileList] = useState<FileConfigItem[]>([]);
  const [selectedFileId, setSelectedFileId] = useState<string | null>(null);

  // 从外部接收的value初始化数据
  useEffect(() => {
    if (value && !initialized.current) {
      const convertedData = convertFromApiFormat(value);
      setFileList(convertedData);
      initialized.current = true;
    }
  }, [value]);

// API格式转换为内部数据结构的函数
  const convertFromApiFormat = (apiData: any): FileConfigItem[] => {
    const result: FileConfigItem[] = [];

    // 添加根目录
    const rootDir: FileConfigItem = {
      id: 'root',
      parentId: null,
      type: 'dir',
    };
    result.push(rootDir);

    // 递归处理文件
    const processFiles = (files: any[], parentId: string) => {
      files.forEach(file => {
        if (file.type === 'file') {
          const fileItem: FileConfigItem = {
            id: `file_${Date.now()}_${Math.random()}`,
            parentId,
            type: 'file',
            inputPath: file.inputPath,
            outputPath: file.outputPath,
            generateType: file.generateType,
            condition: file.condition,
          };
          result.push(fileItem);
        } else if (file.type === 'group') {
          const groupItem: FileConfigItem = {
            id: `group_${Date.now()}_${Math.random()}`,
            parentId,
            type: 'group',
            groupKey: file.groupKey,
            groupName: file.groupName,
            condition: file.condition,
          };
          result.push(groupItem);

          if (Array.isArray(file.files)) {
            processFiles(file.files, groupItem.id);
          }
        }
      });
    };

    if (apiData && Array.isArray(apiData.files)) {
      processFiles(apiData.files, 'root');
    }

    return result;
  };



  // 将树形数据转换为Ant Design Tree所需格式
  const convertToTreeData = (items: FileConfigItem[]) => {
    const rootItems = items.filter(item => item.parentId === null);

    const buildTreeNode = (item: FileConfigItem) => {
      const children = items.filter(child => child.parentId === item.id);

      return {
        key: item.id,
        title: item.type === 'group'
          ? item.groupName
          : (item.type === 'dir' ? '根目录' : item.inputPath?.split('/').pop()),
        isLeaf: item.type === 'file',
        children: children.length > 0 ? children.map(buildTreeNode) : undefined,
      };
    };

    return rootItems.map(buildTreeNode);
  };

  // 初始化根目录
  useEffect(() => {
    if (fileList.length === 0) {
      const rootDir: FileConfigItem = {
        id: 'root',
        parentId: null,
        type: 'dir',
      };
      setFileList([rootDir]);
    }
  }, [fileList]);


  // 添加文件处理函数
  const handleAddFile = (parentId: string | null) => {
    const newFile: FileConfigItem = {
      id: `file_${Date.now()}`,
      parentId: parentId,
      type: 'file',
      inputPath: '',
      outputPath: '',
      generateType: 'static',
    };

    setFileList([...fileList, newFile]);
    setSelectedFileId(newFile.id);
  };

// 添加文件组处理函数
  const handleAddGroup = (parentId: string | null) => {
    const newGroup: FileConfigItem = {
      id: `group_${Date.now()}`,
      parentId: parentId,
      type: 'group',
      groupKey: '',
      groupName: '新建文件组',
    };

    setFileList([...fileList, newGroup]);
    setSelectedFileId(newGroup.id);
  };


  // 渲染文件组表单
  const renderGroupForm = (group: FileConfigItem) => (
    <Form layout="vertical">
      <Form.Item label="文件组标识" required>
        <Input
          value={group.groupKey}
          onChange={(e) => updateFileItem(group.id, { groupKey: e.target.value })}
          placeholder="请输入文件组唯一标识"
        />
      </Form.Item>
      <Form.Item label="文件组名称" required>
        <Input
          value={group.groupName}
          onChange={(e) => updateFileItem(group.id, { groupName: e.target.value })}
          placeholder="请输入文件组显示名称"
        />
      </Form.Item>
      <Form.Item label="条件 (可选)">
        <Input
          value={group.condition}
          onChange={(e) => updateFileItem(group.id, { condition: e.target.value })}
          placeholder="请输入条件表达式"
        />
      </Form.Item>
      <Divider />
      <Button type="primary" onClick={() => handleAddFile(group.id)}>
        添加文件到此组
      </Button>
    </Form>
  );

// 渲染单个文件表单
  const renderFileForm = (file: FileConfigItem) => (
    <Form layout="vertical">
      <Form.Item label="输入路径" required>
        <Input
          value={file.inputPath}
          onChange={(e) => updateFileItem(file.id, { inputPath: e.target.value })}
          placeholder="原始文件的路径"
        />
      </Form.Item>
      <Form.Item label="输出路径" required>
        <Input
          value={file.outputPath}
          onChange={(e) => updateFileItem(file.id, { outputPath: e.target.value })}
          placeholder="生成文件的路径"
        />
      </Form.Item>
      <Form.Item label="生成类型" required>
        <Select
          value={file.generateType}
          onChange={(value) => updateFileItem(file.id, { generateType: value })}
        >
          <Select.Option value="static">静态生成 (static)</Select.Option>
          <Select.Option value="dynamic">动态生成 (dynamic)</Select.Option>
        </Select>
      </Form.Item>
      <Form.Item label="条件 (可选)">
        <Input
          value={file.condition}
          onChange={(e) => updateFileItem(file.id, { condition: e.target.value })}
          placeholder="请输入条件表达式"
        />
      </Form.Item>
    </Form>
  );

  // 根据选中项显示不同的表单
  const renderConfigForm = () => {
    const selectedItem = fileList.find(item => item.id === selectedFileId);

    if (!selectedItem) {
      return <div>请选择一个文件或文件组进行配置</div>;
    }

    if (selectedItem.type === 'dir') {
      return <div>根目录配置</div>;
    }

    if (selectedItem.type === 'group') {
      return renderGroupForm(selectedItem);
    }

    return renderFileForm(selectedItem);
  };



// 更新文件项
  const updateFileItem = (id: string, partialItem: Partial<FileConfigItem>) => {
    const newFileList = fileList.map(item =>
      item.id === id ? { ...item, ...partialItem } : item
    );
    setFileList(newFileList);

    // 如果设置了onChange回调，则调用
    if (onChange) {
      onChange(convertToApiFormat(newFileList));
    }
  };


  // 删除文件或文件组
  const handleDelete = (id: string) => {
    // 递归获取所有要删除的ID（包括子项）
    const getIdsToDelete = (itemId: string): string[] => {
      const children = fileList.filter(item => item.parentId === itemId);
      const childrenIds = children.flatMap(child => getIdsToDelete(child.id));
      return [itemId, ...childrenIds];
    };

    const idsToDelete = getIdsToDelete(id);
    const newFileList = fileList.filter(item => !idsToDelete.includes(item.id));

    setFileList(newFileList);
    setSelectedFileId(null);

    if (onChange) {
      onChange(convertToApiFormat(newFileList));
    }
  };










  return (
    <div className="file-config-container">
      <Row gutter={16}>
        <Col span={8}>
          {/* 左侧树形结构 (稍后实现) */}
          <Card title="文件结构" style={{ minHeight: 500 }}>
            {/* 树形控件将在这里 */}
            <Tree
              showLine
              defaultExpandAll
              onSelect={(selectedKeys) => {
                if (selectedKeys.length > 0) {
                  setSelectedFileId(selectedKeys[0] as string);
                }
              }}
              treeData={convertToTreeData(fileList)}
            />
            <Divider />
            <Button
              type="dashed"
              block
              icon={<PlusOutlined />}
              onClick={() => handleAddFile(null)}
            >
              添加根级文件
            </Button>

            <Button
              type="dashed"
              style={{ marginRight: 8 }}
              icon={<PlusOutlined />}
              onClick={() => handleAddFile('root')}
            >
              添加文件
            </Button>
            <Button
              type="dashed"
              icon={<PlusOutlined />}
              onClick={() => handleAddGroup('root')}
            >
              添加文件组
            </Button>

            {/*// 在表单底部添加删除按钮*/}
            {selectedFileId && selectedFileId !== 'root' && (
              <Button danger onClick={() => handleDelete(selectedFileId)}>
                删除此项
              </Button>
            )}
          </Card>
        </Col>



        <Col span={16}>
          {/* 右侧配置表单 (稍后实现) */}
          <Card title="文件配置详情" style={{ minHeight: 500 }}>
            {/* 配置表单将在这里 */}

          </Card>
        </Col>



      </Row>
    </div>
  );

};

export default FileConfigForm;
