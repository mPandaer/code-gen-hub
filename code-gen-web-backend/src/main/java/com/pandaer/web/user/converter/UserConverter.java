package com.pandaer.web.user.converter;

import cn.hutool.core.bean.BeanUtil;
import com.pandaer.web.user.dto.resp.LoginUserVO;
import com.pandaer.web.user.dto.resp.UserVO;
import com.pandaer.web.user.entity.User;
import com.pandaer.web.user.entity.UserLevelPrivilege;
import com.pandaer.web.user.service.UserLevelPrivilegeService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserConverter {


    private final UserLevelPrivilegeService privilegeService;

    public UserVO entityMapToVO(User user) {
        UserVO vo = BeanUtil.toBean(user, UserVO.class);
        UserLevelPrivilege privilege = privilegeService.getById(user.getUserLevel());
        vo.setPrivilege(privilege);
        return vo;
    }

    public LoginUserVO entityMapToLoginVO(User user) {
        LoginUserVO loginUserVO = BeanUtil.toBean(user, LoginUserVO.class);
        UserLevelPrivilege privilege = privilegeService.getById(user.getUserLevel());
        loginUserVO.setPrivilege(privilege);
        return loginUserVO;
    }
}
