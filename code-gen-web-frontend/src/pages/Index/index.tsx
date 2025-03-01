import { listGeneratorVoByPageUsingPost } from '@/services/backend/generatorController';
import { PageContainer, ProFormSelect, ProFormText, QueryFilter } from '@ant-design/pro-components';
import { Avatar, Card, Flex, List, Space, Tabs, TabsProps, Tag, Typography } from 'antd';
import Search from 'antd/es/input/Search';
import dayjs from 'dayjs';
import React, { useEffect } from 'react';

import relativeTime from 'dayjs/plugin/relativeTime';
import {Link} from "umi";

dayjs.extend(relativeTime);

/**
 * 每个单独的卡片，为了复用样式抽成了组件
 * @param param0
 * @returnsa
 */

const DEFAULT_SEARCH_PARAMS: API.GeneratorQueryRequest = {
  current: 1,
  pageSize: 10,
  sortField: 'createTime',
  sortOrder: 'descend',
};

// 添加卡片样式常量
const CARD_HEIGHT = 380;
const IMAGE_HEIGHT = 200;

const IndexPage: React.FC<any> = () => {
  const [searchParams, setSearchParams] =
    React.useState<API.GeneratorQueryRequest>(DEFAULT_SEARCH_PARAMS);
  const [loading, setLoading] = React.useState(true);
  const [dataList, setDataList] = React.useState<API.GeneratorVO[]>([]);

  const doSearch = async (params: API.GeneratorQueryRequest) => {
    setLoading(true);
    const data = await listGeneratorVoByPageUsingPost(params);
    setDataList((data?.data?.records as API.GeneratorVO[]) ?? []);
    setLoading(false);
    // console.log(data)
  };

  useEffect(() => {
    // 请求数据
    doSearch(searchParams);
  }, [searchParams]);

  const items: TabsProps['items'] = [
    {
      key: 'latest',
      label: '最新',
    },
    {
      key: 'recommend',
      label: '推荐',
    },
  ];

  // @ts-ignore
  return (
    <PageContainer
      title={<></>}
      loading={loading}
      contentWidth={'Fixed'}
      style={{ margin: '0 auto', width: '80%', minWidth: "800px" }}  // 调整容器宽度
    >
      <Card style={{ marginBottom: 24, borderRadius: 8 }}>
        <Search
          placeholder="搜索你感兴趣的生成器..."  // 更友好的提示文本
          allowClear
          enterButton="搜索"
          size="large"
          style={{ maxWidth: 600, margin: '0 auto', display: 'block' }}
          onSearch={(searchText) => {
            setSearchParams({ ...searchParams, searchText: searchText });
          }}
        />

        <Tabs
          defaultActiveKey="latest"
          items={items}
          onChange={(value) => {
            console.log(value);
          }}
          style={{ marginTop: 16 }}  // 添加间距
        />

        <QueryFilter
          layout="horizontal"  // 改为水平布局
          style={{ marginTop: 16 }}
          onFinish={(record: any) => {
            setSearchParams({
              ...searchParams,
              name: record.name,
              description: record.description,
              author: record.author,
              tags: record.tags,
            });
          }}
        >
          <ProFormText name="name" label={'名称'} />
          <ProFormText name="description" label={'描述'} />
          <ProFormText name="author" label={'作者'} />
          <ProFormSelect name="tags" label={'标签'} mode={'tags'} />
        </QueryFilter>
      </Card>

      <List<API.GeneratorVO>
        rowKey="id"
        loading={loading}
        grid={{
          gutter: 24,  // 增加间距
          xs: 1,
          sm: 2,
          md: 3,
          lg: 3,
          xl: 4,
          xxl: 4,
        }}
        dataSource={dataList}
        renderItem={(item) => (
          <List.Item>
            <Link to={`/generator/detail/${item.id}`}>
              <Card
                hoverable
                style={{ height: CARD_HEIGHT }}
                cover={
                  <div style={{ height: IMAGE_HEIGHT, overflow: 'hidden' }}>
                    <img 
                      alt={item.name} 
                      src={item.picture} 
                      style={{
                        width: '100%',
                        height: '100%',
                        objectFit: 'cover'
                      }}
                    />
                  </div>
                }
              >
                <Card.Meta
                  title={
                    <Typography.Text strong style={{ fontSize: 16 }}>
                      {item.name}
                    </Typography.Text>
                  }
                  description={
                    <Typography.Paragraph
                      ellipsis={{
                        rows: 2,
                      }}
                      style={{ marginBottom: 8, height: 40 }}  // 固定高度
                    >
                      {item.description}
                    </Typography.Paragraph>
                  }
                />
                <div style={{ marginBottom: 12 }}>
                  {item.tags && item.tags.map((tag: any) => (
                    <Tag key={tag} color="blue">{tag}</Tag>
                  ))}
                </div>

                <Flex justify={'space-between'} align={'center'} style={{ paddingBottom: 8 }}>
                  <Flex align="center" gap={8}>
                    <Avatar size="small" src={item.user?.userAvatar} />
                    <Typography.Text type="secondary" style={{ fontSize: 14 }}>
                      {item.user?.userName || '匿名用户'}
                    </Typography.Text>
                  </Flex>


                  <Typography.Text type="secondary" style={{ fontSize: 12 }}>
                    {dayjs(item.updateTime).fromNow()}
                  </Typography.Text>


                </Flex>



              </Card>
            </Link>
          </List.Item>
        )}
      />
    </PageContainer>
  );
};

export default IndexPage;
