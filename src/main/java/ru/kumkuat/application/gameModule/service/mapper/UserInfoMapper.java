package ru.kumkuat.application.gameModule.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.kumkuat.application.gameModule.api.controller.UserInfo;
import ru.kumkuat.application.gameModule.models.User;

@Mapper(componentModel = "spring")
public interface UserInfoMapper {

    @Mapping(source = "name", target = "username")
    @Mapping(source = "hasPay", target = "hasPayed")
    UserInfo toUserInfo(User user);

    @Mapping(source = "telegramChatLink", target = "telegramChatLink")
    UserInfo setLink(@MappingTarget UserInfo userInfo, String telegramChatLink);
}
