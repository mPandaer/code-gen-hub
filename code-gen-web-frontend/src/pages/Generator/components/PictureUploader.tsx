import '@umijs/max';
import { Upload } from 'antd';
import React, { useState } from 'react';
import { LoadingOutlined, PlusOutlined } from '@ant-design/icons';
import { uploadFileUsingPost } from '@/services/backend/fileController';

interface PictureUploaderProps {
  id?: string;
  value?: string;
  onChange?: (value: string) => void;
}


const PictureUploader: React.FC<PictureUploaderProps> = (props) => {

  const {id,value,onChange} = props;

  const [loading, setLoading] = useState<boolean>(false);




  const uploadButton = (
    <button style={{ border: 0, background: 'none' }} type="button">
      {loading ? <LoadingOutlined /> : <PlusOutlined />}
      <div style={{ marginTop: 8 }}>Upload</div>
    </button>
  );




  return (
    <Upload

      id={id}
      name="avatar"
      listType="picture-card"
      className="avatar-uploader"
      showUploadList={false}
      customRequest={async (options) => {
        setLoading(true)
        const file = options.file
        // @ts-ignore
        const resp = await uploadFileUsingPost({biz:"generator_cover"},{},file)
        if (onChange) {
          onChange(resp.data as string)
        }
        setLoading(false)
      }}
    >
      {value ? <img src={value} alt="avatar" style={{ width: '100%' }} /> : uploadButton}
    </Upload>
  );
};








export default PictureUploader;
