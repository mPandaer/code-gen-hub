export default [
  {
    path: '/user',
    layout: false,
    routes: [
      { path: '/user/login', component: './User/Login' },
      { path: '/user/register', component: './User/Register' },
    ],
  },
  { path: '/', icon: 'smile', component: './Index', name: '主页' },
  {
    path: '/admin',
    icon: 'crown',
    name: '管理页',
    access: 'canAdmin',
    routes: [
      { path: '/admin', redirect: '/admin/user' },
      { icon: 'table', path: '/admin/user', component: './Admin/User', name: '用户管理' },
      { icon: 'table', path: '/admin/generator', component: './Admin/Generator', name: '生成器管理' },
    ],
  },
  { path: '/', redirect: '/welcome' },
  { path: '/generator/add',icon: 'plus', component: "./Generator/Add",name: "新建生成器"},
  { path: '/generator/edit/:id', component: "./Generator/Add",name: "编辑生成器",hideInMenu: true},
  { path: '/generator/detail/:id', component: "./Generator/Detail",name: "生成器详情",hideInMenu: true},
  { path: '*', layout: false, component: './404' },
];
