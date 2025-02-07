// ModelConfigForm.tsx
import { Button, Form, FormListFieldData, Input, Space } from 'antd';
import { useEffect } from 'react';

interface ModelConfigFormProps {
  formRef: any;
  oldData?: any;
}

const ModelConfigForm: React.FC<ModelConfigFormProps> = ({ formRef, oldData }) => {
  // 使用 Form.useWatch 监听表单变化
  const models = Form.useWatch(['modelConfig', 'models'], formRef.current);

  function singleModelConfigItem(field: FormListFieldData) {
    return (
      <Space>
        <Form.Item label="字段名" name={[field.name, 'fieldName']}>
          <Input />
        </Form.Item>
        <Form.Item label="类型" name={[field.name, 'type']}>
          <Input />
        </Form.Item>

        <Form.Item label="描述" name={[field.name, 'description']}>
          <Input />
        </Form.Item>

        <Form.Item label="默认值" name={[field.name, 'defaultValue']}>
          <Input />
        </Form.Item>
      </Space>
    );
  }

  function singleModelConfigGroupItem(field: FormListFieldData, children: (field:FormListFieldData) => React.ReactNode) {
    return (
      <Space>
        <Form.Item label="分组key" name={[field.name, 'groupKey']}>
          <Input />
        </Form.Item>
        <Form.Item label="分组名" name={[field.name, 'groupName']}>
          <Input />
        </Form.Item>

        <Form.Item label="分组类型" name={[field.name, 'type']}>
          <Input />
        </Form.Item>

        <Form.Item label="条件表达式" name={[field.name, 'condition']}>
          <Input />
        </Form.Item>

        <Form.Item label="分组内模型配置">{children(field)}</Form.Item>
      </Space>
    );
  }

  useEffect(() => {
    // 如果有oldData，初始化表单数据
    if (oldData?.modelConfig) {
      formRef.current?.setFieldsValue({
        modelConfig: oldData.modelConfig
      });
    }
  }, [oldData]);

  return (
    <Form.List name={["modelConfig", "models"]}>
      {(fields, { add, remove }) => (
        <div style={{ display: 'flex', rowGap: 16, flexDirection: 'column' }}>
          {fields.map((field, index) => {
            // 获取当前field对应的model数据
            const model = models?.[field.name];

            return (
              <div key={field.key}>
                {model?.groupKey ? (
                  singleModelConfigGroupItem(field, (field) => (
                    <Form.List name={[field.name, 'list']}>
                      {(subFields, subOpt) => (
                        <div style={{display: 'flex', flexDirection: 'column', rowGap: 16}}>
                          {subFields.map((subField,index) => (
                            <div key={index}>
                              {
                                singleModelConfigItem(subField)
                              }
                            </div>

                          ))}
                          <Button
                            type="dashed"
                            onClick={() => subOpt.add()}
                            block
                          >
                            + Add Sub Item
                          </Button>
                        </div>
                      )}
                    </Form.List>
                  ))
                ) : (
                  singleModelConfigItem(field)
                )}
              </div>
            );
          })}

          <Button
            type="dashed"
            onClick={() => {
              // 添加分组时，给一个唯一标识
              const newGroup = {
                groupName: '分组',
                groupKey: `groupKey`,
              };
              add(newGroup);

              // 可以立即更新表单的值
              const currentModels = formRef.current?.getFieldValue(['modelConfig', 'models']) || [];
              formRef.current?.setFieldsValue({
                modelConfig: {
                  models: [...currentModels, newGroup]
                }
              });
            }}
            block
          >
            添加分组
          </Button>
        </div>
      )}
    </Form.List>
  );
};

export default ModelConfigForm;
