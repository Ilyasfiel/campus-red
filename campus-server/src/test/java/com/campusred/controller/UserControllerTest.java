package com.campusred.controller;

import com.campusred.auth.JwtUtil;
import com.campusred.dto.UserVO;
import com.campusred.entity.User;
import com.campusred.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;
    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserController userController;

    @Test
    void loginReturnsToken() {
        User user = new User();
        user.setId(1L);
        user.setNickname("测试");
        user.setAvatarUrl("http://avatar.jpg");

        when(userService.login(any(), any(), any(), any())).thenReturn(user);
        when(jwtUtil.generateToken(anyLong())).thenReturn("test-jwt-token");

        var result = userController.login(createLoginRequest());

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals("test-jwt-token", result.getData().get("token"));
        assertEquals(1L, result.getData().get("userId"));
    }

    @Test
    void profileNotFound() {
        when(userService.getProfile(999L, 1L)).thenReturn(null);

        var result = userController.profile(999L, 1L);

        assertEquals(404, result.getCode());
        assertEquals("用户不存在", result.getMsg());
    }

    @Test
    void profileFound() {
        UserVO vo = new UserVO();
        vo.setId(2L);
        vo.setNickname("张三");

        when(userService.getProfile(2L, 1L)).thenReturn(vo);

        var result = userController.profile(2L, 1L);

        assertEquals(200, result.getCode());
        assertEquals("张三", ((UserVO) result.getData()).getNickname());
    }

    @Test
    void getSchools() {
        when(userService.getSchoolList()).thenReturn(List.of("河北大学", "北京大学"));

        var result = userController.schools();

        assertEquals(200, result.getCode());
        assertEquals(2, ((List<?>) result.getData()).size());
    }

    private com.campusred.dto.LoginRequest createLoginRequest() {
        var req = new com.campusred.dto.LoginRequest();
        req.setCode("test-code");
        req.setNickname("测试");
        req.setAvatarUrl("http://avatar.jpg");
        req.setCampus("河北大学");
        return req;
    }
}
