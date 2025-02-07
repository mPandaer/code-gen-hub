
import {
  PageContainer,
  ProCard,
  ProFormInstance, ProFormItem,
  ProFormSelect,
  ProFormText,
  ProFormTextArea,
  StepsForm,
} from '@ant-design/pro-components';
import '@umijs/max';
import { message } from 'antd';
import React, {useEffect, useRef, useState} from 'react';
import PictureUploader from "@/pages/Generator/components/PictureUploader";
import FileUploader from "@/pages/Generator/components/FileUploader";
import {
  addGeneratorUsingPost,
  editGeneratorUsingPost,
  getGeneratorVoByIdUsingGet
} from "@/services/backend/generatorController";
import {useModel, useParams} from "@@/exports";
import ModelConfigFrom from "@/pages/Generator/components/ModelConfigFrom";







const AddGeneratorPage: React.FC = () => {

  const formRef = useRef<ProFormInstance>();
  const {id} = useParams();
  const [originInfo, setOriginInfo] = useState<API.GeneratorVO>();


  const loadOriginInfo = async () => {
    if (id) {
      const resp = await getGeneratorVoByIdUsingGet({id:Number(id)})
      formRef.current?.setFieldsValue(resp.data)
      setOriginInfo(resp.data)
    }

  }

  useEffect(() => {
    loadOriginInfo();
  },[id])


  return (
    <PageContainer >
      <ProCard>
        <StepsForm<API.GeneratorAddRequest>
          formRef={formRef}
          onFinish={async (values) => {
            // console.log(values);
            // 创建一个代码生成器
            try {
              if (id) {
                const resp = await editGeneratorUsingPost({...values,id:Number(id)})
                if (resp.data) {
                  message.success('修改成功');
                  // TODO 跳转到详情页
                }
              } else {
                const resp = await addGeneratorUsingPost(values)
                if (resp.data) {
                  message.success('创建成功');
                  // TODO 跳转到详情页
                }
              }
            }catch (e) {
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
            onFinish={async () => {
              console.log(formRef.current?.getFieldsValue());
              return true;
            }}
          >

            <ProFormItem name="picture" label={"生成器代码封面"}>
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

            <ProFormText
              name="author"
              label="作者"
              width="md"
              placeholder="请输入作者"
            />

            <ProFormText
              name="version"
              label="版本"
              width="md"
              placeholder="版本"
            />

            <ProFormText
              name="basePackage"
              label="基础包"
              width="md"
              placeholder="请输入基础包路径"
            />

            <ProFormSelect mode="tags" label="标签" name="tags"/>


          </StepsForm.StepForm>


          {/* TODO 第二步 文件信息 */}
          <StepsForm.StepForm
            name="fileConfig"
            title="文件配置信息"
            onFinish={async () => {
              console.log(formRef.current?.getFieldsValue());
              return true;
            }}
          >
            {/*文件配置信息*/}
          </StepsForm.StepForm>


          <StepsForm.StepForm
            name="modelConfig"
            title="模型配置信息"
          >
            <ProFormItem>
              <ModelConfigFrom
                formRef={formRef}
                oldData={originInfo}
              />
            </ProFormItem>
          </StepsForm.StepForm>

          <StepsForm.StepForm
            name="dist"
            title="生成器压缩文件"
          >
            <ProFormItem name="distPath">
              {
                (!id || originInfo) && <FileUploader value={originInfo?.distPath}/>
              }

            </ProFormItem>
          </StepsForm.StepForm>


        </StepsForm>
      </ProCard>

    </PageContainer>
    );
};








export default AddGeneratorPage;
