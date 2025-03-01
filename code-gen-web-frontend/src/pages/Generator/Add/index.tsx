import FileConfigPage from '@/pages/Generator/Add/components/FileConfigPage';
import FileUploader from '@/pages/Generator/components/FileUploader';
import PictureUploader from '@/pages/Generator/components/PictureUploader';
import {
  addGeneratorUsingPost,
  editGeneratorUsingPost,
  getGeneratorVoByIdUsingGet,
  makeGeneratorOnlineUsingPost,
} from '@/services/backend/generatorController';
import { useParams } from '@@/exports';
import {
  PageContainer,
  ProCard,
  ProFormInstance,
  ProFormItem,
  ProFormSelect,
  ProFormText,
  ProFormTextArea,
  StepsForm,
} from '@ant-design/pro-components';
import '@umijs/max';
import React, { useEffect, useRef, useState } from 'react';
import ModelConfigPage from './components/ModelConfigPage';
import { Button, message } from 'antd';
import saveAs from 'file-saver';

const AddGeneratorPage: React.FC = () => {
  const formRef = useRef<ProFormInstance>();
  const { id } = useParams();
  const [originInfo, setOriginInfo] = useState<API.GeneratorVO>();
  const [loading, setLoading] = useState<boolean>(false);
  const [formData, setFormData] = useState<{
    baseInfo?: any;
    fileConfig?: any;
    modelConfig?: any;
    distInfo?: any;
  }>({});

  const loadOriginInfo = async () => {
    if (!id) {
      return;
    }
    setLoading(true);
    try {
      const resp = await getGeneratorVoByIdUsingGet({ id: Number(id) });
      if (resp.data) {
        const data = resp.data;
        formRef.current?.setFieldsValue(data);
        setOriginInfo(data);
      }
    } catch (error) {
      message.error('加载数据失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (id) {
      loadOriginInfo();
    }
  }, [id]);

  return (
    <PageContainer>
      <ProCard style={{ marginTop: '8vh' }}>
        <StepsForm<API.GeneratorAddRequest>
          formRef={formRef}
          onFinish={async (values) => {
            // console.log(values);
            // 创建一个代码生成器
            try {
              if (id) {
                const resp = await editGeneratorUsingPost({ ...values, id: Number(id) });
                if (resp.data) {
                  message.success('修改成功');
                  // TODO 跳转到详情页
                }
              } else {
                const resp = await addGeneratorUsingPost(values);
                if (resp.data) {
                  message.success('创建成功');
                  // TODO 跳转到详情页
                }
              }
            } catch (e) {
              message.success('创建失败');
            }
          }}
          formProps={{
            validateMessages: {
              required: '此项为必填项',
            },
          }}
        >
          {/*第一步 */}
          <StepsForm.StepForm
            name="base"
            title="生成器基本信息"
            onFinish={async (values) => {
              // 保存第一步的数据
              setFormData((prev) => ({
                ...prev,
                baseInfo: {
                  picture: values.picture,
                  name: values.name,
                  description: values.description,
                  author: values.author,
                  version: values.version,
                  basePackage: values.basePackage,
                  tags: values.tags,
                },
              }));
              return true;
            }}
          >
            <ProFormItem name="picture" label={'生成器代码封面'}>
              <PictureUploader />
            </ProFormItem>
            <ProFormText
              name="name"
              label="名称"
              width="md"
              placeholder="请输入名称"
              rules={[{ required: true }]}
            />

            <ProFormTextArea
              name="description"
              label="描述"
              width="md"
              placeholder="请输入描述"
              rules={[{ required: true }]}
            />

            <ProFormText name="author" label="作者" width="md" placeholder="请输入作者" />

            <ProFormText name="version" label="版本" width="md" placeholder="版本" />

            <ProFormText
              name="basePackage"
              label="基础包"
              width="md"
              placeholder="请输入基础包路径"
            />

            <ProFormSelect mode="tags" label="标签" name="tags" />
          </StepsForm.StepForm>

          {/* TODO 第二步 文件信息 */}

          <StepsForm.StepForm
            name="fileConfig"
            title="文件配置信息"
            initialValues={{
              fileConfig: {
                files: originInfo?.fileConfig?.files || [],
              },
            }}
            onFinish={async (values) => {
              // 保存第二步的数据
              setFormData((prev) => ({
                ...prev,
                fileConfig: values.fileConfig,
              }));
              return true;
            }}
          >
            {(!id || originInfo?.fileConfig?.files) && (
              <ProFormItem name={['fileConfig', 'files']}>
                <FileConfigPage
                  value={formRef.current?.getFieldValue(['fileConfig', 'files'])}
                  onChange={(files) => {
                    formRef.current?.setFieldValue(['fileConfig', 'files'], files);
                  }}
                />
              </ProFormItem>
            )}
          </StepsForm.StepForm>

          <StepsForm.StepForm
            name="modelConfig"
            title="模型配置信息"
            initialValues={{
              modelConfig: {
                models: originInfo?.modelConfig?.models || [],
              },
            }}
            onFinish={async (values) => {
              // 保存第三步的数据
              setFormData((prev) => ({
                ...prev,
                modelConfig: values.modelConfig,
              }));
              return true;
            }}
          >
            {(!id || originInfo?.modelConfig?.models) && (
              <ProFormItem name={['modelConfig', 'models']}>
                <ModelConfigPage
                  value={formRef.current?.getFieldValue(['modelConfig', 'models'])}
                  onChange={(models) => {
                    formRef.current?.setFieldValue(['modelConfig', 'models'], models);
                  }}
                />
              </ProFormItem>
            )}
          </StepsForm.StepForm>

          <StepsForm.StepForm name="dist" title="生成器压缩文件">
            <ProFormSelect
              name="generatorMode"
              label="生成器制作方式"
              initialValue="upload"
              options={[
                {
                  value: 'upload',
                  label: '上传生成器压缩包',
                },
                {
                  value: 'online',
                  label: '在线制作生成器',
                },
              ]}
            />
            <ProFormItem
              noStyle
              shouldUpdate={(prevValues, currentValues) =>
                prevValues.generatorMode !== currentValues.generatorMode
              }
            >
              {({ getFieldValue }) =>
                getFieldValue('generatorMode') === 'upload' ? (
                  <ProFormItem name="distPath">
                    {(!id || originInfo) && <FileUploader value={originInfo?.distPath} biz="generator_dist" />}
                  </ProFormItem>
                ) : (
                  <>
                    <ProFormItem name="templateFiles" label="模板文件">
                      <FileUploader
                        biz="generator_template"
                        accept=".zip,.rar,.7z"
                        description="请上传模板文件压缩包"
                        maxCount={1}
                      />
                    </ProFormItem>
                    <ProFormItem>
                      <Button
                        type="primary"
                        onClick={async () => {
                          // 使用已保存的数据
                          const { baseInfo, fileConfig, modelConfig } = formData;
                          const templateFilesUrl = formRef.current?.getFieldValue('templateFiles');

                          console.log('基础信息：', baseInfo);
                          console.log('文件配置：', fileConfig);
                          console.log('模型配置：', modelConfig);
                          console.log('打包信息：', templateFilesUrl);

                          message.success('开始制作生成器');
                          // 后续的生成器制作逻辑...
                          try {
                            const resp = await makeGeneratorOnlineUsingPost({
                              meta: {
                                ...baseInfo,
                                fileConfig: fileConfig,
                                modelConfig: modelConfig,
                              },
                              zipTemplateFilesUrl: templateFilesUrl,
                            },{ responseType: 'blob' });
                            if (resp) {
                              const blob = new Blob([resp], { type: 'application/octet-stream' });
                              saveAs(blob, 'generator.zip');
                              message.success('制作成功，已自动下载生成器代码');
                            }
                          } catch (error) {
                            message.error('制作失败');
                          }
                        }}
                      >
                        制作生成器
                      </Button>
                    </ProFormItem>
                  </>
                )
              }
            </ProFormItem>
          </StepsForm.StepForm>
        </StepsForm>
      </ProCard>
    </PageContainer>
  );
};

export default AddGeneratorPage;
