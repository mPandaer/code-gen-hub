import { userLogoutUsingPost } from '@/services/backend/userController';
import { LogoutOutlined, SettingOutlined, UserOutlined } from '@ant-design/icons';
import { history, useModel } from '@umijs/max';
import { Avatar, Button, Space } from 'antd';
import { stringify } from 'querystring';
import type { MenuInfo } from 'rc-menu/lib/interface';
import React, { useCallback } from 'react';
import { flushSync } from 'react-dom';
import { Link } from 'umi';
import HeaderDropdown from '../HeaderDropdown';
import { Progress } from 'antd';

export type GlobalHeaderRightProps = {
  menu?: boolean;
};

// 添加等级边框颜色映射
const levelBorderColors: Record<number, string> = {
  1: '#8B939A', // 新手灰
  2: '#4CAF50', // 初级绿
  3: '#2196F3', // 中级蓝
  4: '#9C27B0', // 高级紫
  5: '#FF9800', // 专家橙
};

export const AvatarDropdown: React.FC<GlobalHeaderRightProps> = ({ menu }) => {
  /**
   * 退出登录，并且将当前的 url 保存
   */
  const loginOut = async () => {
    await userLogoutUsingPost();
    const { search, pathname } = window.location;
    const urlParams = new URL(window.location.href).searchParams;
    /** 此方法会跳转到 redirect 参数所在的位置 */
    const redirect = urlParams.get('redirect');
    // Note: There may be security issues, please note
    if (window.location.pathname !== '/user/login' && !redirect) {
      history.replace({
        pathname: '/user/login',
        search: stringify({
          redirect: pathname + search,
        }),
      });
    }
  };

  const { initialState, setInitialState } = useModel('@@initialState');

  const onMenuClick = useCallback(
    (event: MenuInfo) => {
      const { key } = event;
      if (key === 'logout') {
        flushSync(() => {
          setInitialState((s) => ({ ...s, currentUser: undefined }));
        });
        loginOut();
        return;
      }
      // 检查 key 是否已经是完整路径（以 '/' 开头）
      if (key.startsWith('/')) {
        history.push(key);
      } else {
        history.push(`/account/${key}`);
      }
    },
    [setInitialState],
  );

  const { currentUser } = initialState || {};

  if (!currentUser) {
    return (
      <Link to="/user/login">
        <Button type="primary" shape="round">
          登录
        </Button>
      </Link>
    );
  }

  const renderUserLevel = () => {
    if (!currentUser?.privilege) return null;
    
    const { experience, privilege } = currentUser;
    const progressPercent = ((experience - privilege.minExp) / (privilege.maxExp - privilege.minExp)) * 100;
    
    return (
      <div style={{ padding: '0 16px', marginBottom: 8 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
          <span>Lv.{privilege.level} {privilege.levelName}</span>
          <span>{experience}/{privilege.maxExp}</span>
        </div>
        <Progress 
          percent={progressPercent} 
          showInfo={false} 
          strokeColor="#1DA57A"
          size="small"
        />
      </div>
    );
  };

  const menuItems = [
    ...(menu
      ? [
          {
            key: 'center',
            icon: <UserOutlined />,
            label: '个人中心',
          },
          {
            key: 'settings',
            icon: <SettingOutlined />,
            label: '个人设置',
          },
          {
            type: 'divider' as const,
          },
        ]
      : []),
    {
      key: 'userLevel',
      label: renderUserLevel(),
      disabled: true,
    },
    {
      type: 'divider' as const,
    },
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: '退出登录',
    },
    {
      key: '/user/profile',
      icon: <UserOutlined />,
      label: '用户中心',
    },
  ];

  return (
    <HeaderDropdown
      menu={{
        selectedKeys: [],
        onClick: onMenuClick,
        items: menuItems,
      }}
    >
      <Space>
        {currentUser?.userAvatar ? (
          <div style={{
            display: 'inline-flex',
            justifyContent: 'center',
            alignItems: 'center',
            borderRadius: '50%',
            border: `1.5px solid ${currentUser?.userLevel ? levelBorderColors[currentUser.userLevel] || '#8B939A' : '#8B939A'}`,
            lineHeight: 0,
          }}>
            <Avatar size="small" src={currentUser.userAvatar} />
          </div>
        ) : (
          <div style={{
            display: 'inline-flex',
            justifyContent: 'center',
            alignItems: 'center',
            borderRadius: '50%',
            border: `1.5px solid ${currentUser?.userLevel ? levelBorderColors[currentUser.userLevel] || '#8B939A' : '#8B939A'}`,
            lineHeight: 0,
          }}>
            <Avatar size="small" icon={<UserOutlined />} />
          </div>
        )}
        <span className="anticon">{currentUser?.userName ?? '无名'}</span>
      </Space>
    </HeaderDropdown>
  );
};

export const AvatarName = () => {};
