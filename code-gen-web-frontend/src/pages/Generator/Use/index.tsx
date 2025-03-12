import React, { useEffect, useState } from 'react';
import { PageContainer } from '@ant-design/pro-components';
import { Card, Button, message } from 'antd';
import { useParams } from '@@/exports';
import { getGeneratorVoByIdUsingGet, useGeneratorByIdOnlineUsingPost } from '@/services/backend/generatorController';
import ModelConfigForm from './components/ModelConfigForm';
import { saveAs } from 'file-saver';

const GeneratorUsePage: React.FC = () => {
  const { id } = useParams();
  const [loading, setLoading] = useState(false);
  const [modelConfig, setModelConfig] = useState<any>(null);

  const loadData = async () => {
    if (!id) {
      return;
    }
    setLoading(true);
    try {
      const res = await getGeneratorVoByIdUsingGet({
        id: Number(id),
      });
      if (res.data) {
        setModelConfig(res.data.modelConfig);
        console.log('加载的配置数据：', res.data.modelConfig);
      }
    } catch (error: any) {
      message.error('加载失败，' + error.message);
    }
    setLoading(false);
  };

  useEffect(() => {
    loadData();
  }, [id]);

  const handleSubmit = async (values: Record<string, any>) => {
    // 处理嵌套结构
    const processNestedValues = (models: any[], formValues: Record<string, any>) => {
      const result: Record<string, any> = {};
      
      models.forEach(model => {
        if (model.groupName) {
          // 如果有 groupName，创建一个新的嵌套对象
          result[model.groupKey] = {};
          // 递归处理嵌套的 models
          if (model.models) {
            const nestedValues = processNestedValues(model.models, formValues);
            Object.assign(result[model.groupKey], nestedValues);
          }
        } else if (model.fieldName) {
          // 如果是普通字段，直接添加到结果中
          result[model.fieldName] = formValues[model.fieldName];
        }
      });
      
      return result;
    };

    try {
      const processedValues = processNestedValues(modelConfig.models, values);
      const blob = await useGeneratorByIdOnlineUsingPost({
        id: Number(id),
        dataModel: processedValues
      }, { responseType: 'blob' });
      saveAs(blob, 'generator.zip');
      message.success('生成成功');
    } catch (error: any) {
      message.error('生成失败，' + error.message);
    }
  };

  return (
    <PageContainer title="使用生成器">
      <Card loading={loading}>
        {modelConfig ? (
          <ModelConfigForm
            onSubmit={handleSubmit}
            modelConfig={modelConfig}
          />
        ) : (
          <div>暂无配置数据</div>
        )}
      </Card>
    </PageContainer>
  );
};

export default GeneratorUsePage; 