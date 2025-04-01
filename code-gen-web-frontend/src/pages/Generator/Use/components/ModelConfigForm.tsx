import React, { useEffect, useState } from 'react';
import { Form, Input, Switch, Card, Typography, Space, Button } from 'antd';
import { QuestionCircleOutlined } from '@ant-design/icons';

interface ModelConfigFormProps {
  onSubmit: (values: Record<string, any>) => void;
  modelConfig: any;
  submitting?: boolean;
}

interface FieldModel {
  fieldName: string;
  type: string;
  description: string;
  defaultValue: any;
  abbr: string | null;
  groupKey: string | null;
  groupName: string | null;
  condition: string | null;
  models: FieldModel[] | null;
  groupArgsStr: string | null;
}

const { Text } = Typography;

const ModelConfigForm: React.FC<ModelConfigFormProps> = ({
  onSubmit,
  modelConfig,
  submitting,
}) => {
  const [form] = Form.useForm();
  const [formValues, setFormValues] = useState<Record<string, any>>({});

  useEffect(() => {
    // 设置默认值
    const defaultValues: Record<string, any> = {};
    const setDefaultValuesRecursive = (models: FieldModel[]) => {
      models.forEach((model) => {
        if (model.fieldName) {
          defaultValues[model.fieldName] = model.defaultValue;
        }
        if (model.models) {
          setDefaultValuesRecursive(model.models);
        }
      });
    };

    if (modelConfig?.models) {
      setDefaultValuesRecursive(modelConfig.models);
      form.setFieldsValue(defaultValues);
      setFormValues(defaultValues);
    }
  }, [modelConfig]);

  const renderField = (field: FieldModel) => {
    // 如果有condition字段，检查条件是否满足
    if (field.condition && !formValues[field.condition]) {
      return null;
    }

    // 处理分组和嵌套结构
    if (field.models) {
      return (
        <Card 
          key={field.groupKey || field.fieldName} 
          title={field.groupName || field.fieldName}
          size="small"
          style={{ marginBottom: 16 }}
        >
          {field.models.map((model) => renderField(model))}
        </Card>
      );
    }

    if (!field.fieldName) {
      return null;
    }

    const label = (
      <Space>
        {field.fieldName}
        {field.description && (
          <Text type="secondary">
            <QuestionCircleOutlined /> {field.description}
          </Text>
        )}
      </Space>
    );

    switch (field.type) {
      case 'boolean':
        return (
          <Form.Item
            key={field.fieldName}
            name={field.fieldName}
            label={label}
            valuePropName="checked"
          >
            <Switch />
          </Form.Item>
        );
      case 'String':
      default:
        return (
          <Form.Item
            key={field.fieldName}
            name={field.fieldName}
            label={label}
            rules={[{ required: true, message: `请输入${field.fieldName}` }]}
          >
            <Input placeholder={`请输入${field.fieldName}`} />
          </Form.Item>
        );
    }
  };

  const handleValuesChange = (changedValues: any, allValues: any) => {
    setFormValues(allValues);
  };

  return (
    <Form
      form={form}
      layout="vertical"
      onFinish={onSubmit}
      onValuesChange={handleValuesChange}
    >
      {modelConfig?.models?.map((model: FieldModel) => renderField(model))}
      <Form.Item>
        <Button type="primary" htmlType="submit" loading={submitting}>
          生成代码
        </Button>
      </Form.Item>
    </Form>
  );
};

export default ModelConfigForm; 