import { layout } from "@/app";

export default [
  {
    path: '/user',
    layout: false,
    routes: [
      { path: '/user/login', component: './User/Login' ,layout: false},
      { path: '/user/register', component: './User/Register' ,layout: false},
      { path: '/user/password/email', component: './User/FindPassword',layout: false},
      { path: '/user/password/reset', component: './User/ResetPassword',layout: false},
    ],
  },
  { path: '/user/profile', component: './User/Profile'},
  { path: '/', icon: 'smile', component: './Index', name: '主页',hideInMenu: true },
  { path: '/order/:generatorId/:orderId', component: './Order/Confirm', name: '订单确认' ,hideInMenu: true},
  { path: '/generator/use/:id', component: './Generator/Use', name: '使用生成器' ,hideInMenu: true},
  {
    path: '/admin',
    icon: 'crown',
    name: '管理页',
    access: 'canAdmin',
    routes: [
      { path: '/admin', redirect: '/admin/user' },
      { icon: 'table', path: '/admin/user', component: './Admin/User', name: '用户管理' },
      { icon: 'table', path: '/admin/generator', component: './Admin/Generator', name: '生成器管理' },
      { icon: 'table', path: '/admin/statistic', component: './Admin/Statistic', name: '统计信息' },  
      { icon: 'table', path: '/admin/order', component: './Admin/Order', name: '订单管理' },  
    ],
  },
  { path: '/', redirect: '/welcome' },
  { path: '/generator/add',icon: 'plus', component: "./Generator/Add",name: "新建生成器"},
  { path: '/generator/edit/:id', component: "./Generator/Add",name: "编辑生成器",hideInMenu: true},
  { path: '/generator/detail/:id', component: "./Generator/Detail",name: "生成器详情",hideInMenu: true},
  { path: '*', layout: false, component: './404' },
];
