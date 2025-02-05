import '@umijs/max';
import {message, Upload, UploadProps} from 'antd';
import React, {useEffect, useState} from 'react';
import {InboxOutlined, StarOutlined} from '@ant-design/icons';
import { uploadFileUsingPost } from '@/services/backend/fileController';
import { PageContainer } from '@ant-design/pro-components';
import {UploadFile} from "antd/es/upload/interface";

interface FileUploaderProps {
  id?: string;
  value?: string;
  onChange?: (value: string) => void;
}


const FileUploader: React.FC<FileUploaderProps> = (props) => {

  const {id,value,onChange} = props;

  const name = value?.substring(value.lastIndexOf('/') + 1)






  const uploadProps: UploadProps = {
    name: 'file',
    multiple: false,
    maxCount:1,
    showUploadList:true,
    defaultFileList: [{uid: id, url: value, status: "done", name: name, fileName: name} as UploadFile],
    customRequest: async (options) => {
      const file = options.file
      // @ts-ignore
      const resp = await uploadFileUsingPost({biz:"generator_dist"},{},file)
      if (onChange) {
        onChange(resp.data as string)
      }
      // @ts-ignore
      options.onSuccess(resp.data,file);
    }
  };

  const { Dragger } = Upload;

  return (
    <Dragger {...uploadProps} id={id}>
      <p className="ant-upload-drag-icon">
        <InboxOutlined />
      </p>
      <p className="ant-upload-text">点击上传生成器压缩文件</p>
      <p className="ant-upload-hint">
        支持拖拽上传压缩文件
      </p>
    </Dragger>
  );
};








export default FileUploader;
