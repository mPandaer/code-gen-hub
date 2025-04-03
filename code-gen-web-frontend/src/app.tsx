import Footer from '@/components/Footer';
import { getLoginUserUsingGet } from '@/services/backend/userController';
import type { RunTimeLayoutConfig } from '@umijs/max';
import { history } from '@umijs/max';
import defaultSettings from '../config/defaultSettings';
import { AvatarDropdown } from './components/RightContent/AvatarDropdown';
import { requestConfig } from './requestConfig';
import logo from "@/assets/logo.png"
const loginPath = '/user/login';
const whiteList = [
  '/user/login',
  '/user/register',
  '/user/password/email',
  '/user/password/reset',
  '/generator/detail'
];

export async function getInitialState(): Promise<InitialState> {
  const initialState: InitialState = {
    currentUser: undefined,
  };
  
  try {
    const res = await getLoginUserUsingGet();
    initialState.currentUser = res.data;
  } catch (error: any) {
    // 如果未登录且当前不在白名单路由，则跳转到登录页
    const { location } = history;
    if (!whiteList.includes(location.pathname)) {
      history.push(loginPath + "?redirect=" + location.pathname);
    }
  }

  return initialState;
}

// ProLayout 支持的api https://procomponents.ant.design/components/layout
// @ts-ignore
export const layout: RunTimeLayoutConfig = ({ initialState }) => {
  return {
    logo,
    avatarProps: {
      render: () => {
        return <AvatarDropdown />;
      },
    },
    waterMarkProps: {
      content: initialState?.currentUser?.userName,
    },
    footerRender: () => <Footer />,
    menuHeaderRender: undefined,
    // 自定义 403 页面
    // unAccessible: <div>unAccessible</div>,
    ...defaultSettings,
  };
};

/**
 * @name request 配置，可以配置错误处理
 * 它基于 axios 和 ahooks 的 useRequest 提供了一套统一的网络请求和错误处理方案。
 * @doc https://umijs.org/docs/max/request#配置
 */
export const request = requestConfig;
