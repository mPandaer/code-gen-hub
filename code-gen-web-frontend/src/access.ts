/**
 * @see https://umijs.org/zh-CN/plugins/plugin-access
 * */
export default function access(initialState: { currentUser?: API.LoginUserVO } | undefined) {
  const { currentUser } = initialState ?? {};
  return {
    canUser: currentUser,
    canAdmin: currentUser && currentUser.userRole === 'admin',
    routeFilter: (route: any) => {
      const path = route.path;
      // Check if path starts with any whiteList item
      const isWhitelisted = [
        '/user/login',
        '/user/register',
        '/user/password/email',
        '/user/password/reset',
        '/',
        '/generator/detail'
      ].some(whitePath => {
        // Exact match or starts with for nested paths
        return path === whitePath || path.startsWith(`${whitePath}/`);
      });
      
      return isWhitelisted || currentUser;
    }
  };
}
