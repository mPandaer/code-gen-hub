import { useRequest } from '@umijs/max';
import ReactECharts from 'echarts-for-react';
import { getDownloadTrendUsingGet, getGeneratorRankingUsingGet, getNewUserTrendUsingGet } from '@/services/backend/statisticController';
import { Spin, DatePicker, Menu, Select } from 'antd';
import { useEffect, useState } from 'react';

const Statistic = () => {
  const [dayDownloadData, setDayDownloadData] = useState<any>();
  const [loading, setLoading] = useState(false);
  const [startTime, setStartTime] = useState<string>();
  const [endTime, setEndTime] = useState<string>();
  const [currentStat, setCurrentStat] = useState('download');
  const [generatorData, setGeneratorData] = useState<any>();
  const [count, setCount] = useState<number>(10);
  const [newUserData, setNewUserData] = useState<any>();

  const loadData = async () => {
    setLoading(true);
    const res = await getDownloadTrendUsingGet({
      start: startTime,
      end: endTime
    });
    console.log(res);
    setDayDownloadData(res.data);
    setLoading(false);
  }

  const loadGeneratorData = async () => {
    setLoading(true);
    console.log("count generator", count);
    const res = await getGeneratorRankingUsingGet({
      count: count
    });
    setGeneratorData(res.data);
    setLoading(false);
  }

  const loadNewUserData = async () => {
    setLoading(true);
    const res = await getNewUserTrendUsingGet({
      start: startTime,
      end: endTime
    });
    setNewUserData(res.data);
    setLoading(false);
  }

  useEffect(() => {
    if (currentStat === 'download') {
      loadData();
    } else if (currentStat === 'user') {
      loadNewUserData();
    }
  }, [startTime, endTime, currentStat]);

  useEffect(() => {
    if(currentStat === 'generator') {
      loadGeneratorData();
    }
  }, [currentStat, count]);

  const chartOption = {
    title: {
      text: '下载趋势',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      formatter: (params: any[]) => {
        const date = params[0].name;
        const value = params[0].value;
        return `${date}<br/>下载量: ${value}`;
      }
    },
    xAxis: {
      type: 'time',
      name: '日期',
      boundaryGap: false,
      axisLabel: {
        formatter: '{yyyy}-{MM}-{dd}'
      }
    },
    yAxis: {
      type: 'value',
      name: '下载量'
    },
    series: [{
      name: '下载量',
      type: 'line',
      smooth: true,
      areaStyle: {
        color: '#1890ff22'
      },
      lineStyle: {
        color: '#1890ff'
      },
      data: dayDownloadData ? dayDownloadData.dates.map((date, index) => [
        date,
        dayDownloadData.downloads[index]
      ]) : []
    }]
  };

  const generatorChartOption = {
    title: {
      text: '生成器排行榜',
      left: 'center'
    },
    tooltip: {
      trigger: 'item',
      formatter: (params: any) => {
        return `生成器: ${params.name}<br/>
                ID: ${params.data.id}<br/>
                下载量: ${params.value}`;
      }
    },
    xAxis: {
      type: 'value',
      name: '下载量'
    },
    yAxis: {
      type: 'category',
      name: '生成器',
      axisLabel: {
        formatter: (value: string) => value.length > 4 ? value.substring(0,4)+'...' : value
      },
      inverse: true,
      data: generatorData?.generatorName || []
    },
    series: [{
      type: 'bar',
      data: generatorData ? generatorData.generatorName.map((name: string, index: number) => ({
        name: name,
        value: parseInt(generatorData.downloadCount[index]),
        id: generatorData.generatorIds[index]
      })) : [],
      itemStyle: {
        color: '#1890ff'
      },
      label: {
        show: true,
        position: 'right'
      }
    }]
  };

  const newUserChartOption = {
    title: {
      text: '新增用户趋势',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      formatter: (params: any[]) => {
        const date = params[0].name;
        const value = params[0].value;
        return `${date}<br/>新增用户: ${value}`;
      }
    },
    xAxis: {
      type: 'time',
      name: '日期',
      boundaryGap: false,
      axisLabel: {
        formatter: '{yyyy}-{MM}-{dd}'
      }
    },
    yAxis: {
      type: 'value',
      name: '新增用户'
    },
    series: [{
      name: '新增用户',
      type: 'line',
      smooth: true,
      areaStyle: {
        color: '#1890ff22'
      },
      lineStyle: {
        color: '#1890ff'
      },
      data: newUserData ? newUserData.dates.map((date, index) => [
        date,
        newUserData.newUsers[index]
      ]) : []
    }]
  };

  // 添加菜单项
  const items = [
    { label: '每日下载量', key: 'download' },
    { label: '生成器排行榜', key: 'generator' },
    { label: '每日新增用户', key: 'user' },
  ];

  return (
    <div style={{ 
      padding: 24, 
      background: '#fff',
      display: 'flex',
      gap: 24
    }}>
      {/* 左侧导航 */}
      <Menu
        style={{ width: 200 }}
        selectedKeys={[currentStat]}
        mode="inline"
        items={items}
        onSelect={({ key }) => setCurrentStat(key)}
      />
      
      {/* 右侧内容 */}
      <div style={{ flex: 1 }}>
        {currentStat === 'download' && (
          <>
            <DatePicker.RangePicker
              style={{ marginBottom: 16 }}
              onChange={(_, dateStrings) => {
                setStartTime(dateStrings[0]);
                setEndTime(dateStrings[1]);
              }}
            />
            <Spin spinning={loading}>
              <ReactECharts
                option={chartOption}
                style={{ height: 500 }}
                opts={{ renderer: 'svg' }}
              />
            </Spin>
          </>
        )}
        
        {currentStat === 'generator' && (
          <Spin spinning={loading}>
            <div style={{ marginBottom: 16 }}>
              <Select 
                defaultValue={10}
                style={{ width: 120 }}
                onChange={(value) => setCount(value)}
                options={[
                  { value: 10, label: '显示前10项' },
                  { value: 20, label: '显示前20项' },
                  { value: 50, label: '显示前50项' }
                ]}
              />
            </div>
            <ReactECharts
              option={generatorChartOption}
              style={{ height: 500 }}
              opts={{ renderer: 'svg' }}
            />
          </Spin>
        )}
        
        {currentStat === 'user' && (
          <>
            <DatePicker.RangePicker
              style={{ marginBottom: 16 }}
              onChange={(_, dateStrings) => {
                setStartTime(dateStrings[0]);
                setEndTime(dateStrings[1]);
              }}
            />
            <Spin spinning={loading}>
              <ReactECharts
                option={newUserChartOption}
                style={{ height: 500 }}
                opts={{ renderer: 'svg' }}
              />
            </Spin>
          </>
        )}
      </div>
    </div>
  );
};

export default Statistic;

