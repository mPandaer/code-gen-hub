import { listGeneratorVoByPageUsingPost } from '@/services/backend/generatorController';
import { PageContainer, ProFormSelect, ProFormText, QueryFilter } from '@ant-design/pro-components';
import { Avatar, Card, Flex, List, Tabs, TabsProps, Tag, Typography } from 'antd';
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
      style={{ margin: '0 auto', width: '60%' ,minWidth:"500px"}}
    >
      <div>
        <Search
          placeholder="请输入关键字"
          allowClear
          enterButton="搜索"
          size="large"
          style={{ width: '50%', margin: '0 auto', display: 'block' }}
          onSearch={(searchText) => {
            setSearchParams({ ...searchParams, searchText: searchText });
          }}
        />
      </div>

      <Tabs
        defaultActiveKey="latest"
        items={items}
        onChange={(value) => {
          console.log(value);
        }}
      />

      <QueryFilter
        layout="vertical"
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

      <List<API.GeneratorVO>
        rowKey="id"
        loading={loading}
        grid={{
          gutter: 16,
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
                style={{ height: '100%' }}
                cover={<img alt={item.name} src={item.picture} />}
              >
                <Card.Meta
                  title={<a>{item.name}</a>}
                  description={
                    <Typography.Paragraph
                      ellipsis={{
                        rows: 2,
                      }}
                    >
                      {item.description}
                    </Typography.Paragraph>
                  }
                />
                <div>{item.tags && item.tags.map((tag: any) => <Tag key={tag}>{tag}</Tag>)}</div>

                <Flex justify={'space-between'} align={'center'}>
                  <Typography.Text type={"secondary"}>{dayjs(item.updateTime).fromNow()}</Typography.Text>
                  <Avatar src={item.user?.userAvatar} />
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
