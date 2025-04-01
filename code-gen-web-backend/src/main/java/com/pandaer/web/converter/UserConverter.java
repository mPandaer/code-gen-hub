package com.pandaer.web.converter;

import com.pandaer.web.model.entity.User;
import com.pandaer.web.model.vo.LoginUserVO;
import com.pandaer.web.model.vo.UserVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface UserConverter {


    UserVO entityMapToVO(User user);

    LoginUserVO entityMapToLoginVO(User user);
}
