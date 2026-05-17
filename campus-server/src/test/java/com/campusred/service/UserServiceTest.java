package com.campusred.service;

import com.campusred.entity.User;
import com.campusred.mapper.FollowMapper;
import com.campusred.mapper.NoteMapper;
import com.campusred.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    private NoteMapper noteMapper;
    @Mock
    private FollowMapper followMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void loginNewUser() {
        when(userMapper.selectOne(any())).thenReturn(null);

        User user = userService.login("wx-openid-123", "测试用户", "http://avatar.jpg", "河北大学");

        assertNotNull(user);
        assertEquals("测试用户", user.getNickname());
        assertEquals("wx-openid-123", user.getOpenid());
    }

    @Test
    void loginExistingUser() {
        User existing = new User();
        existing.setId(1L);
        existing.setOpenid("wx-openid-123");
        existing.setNickname("老用户");

        when(userMapper.selectOne(any())).thenReturn(existing);

        User user = userService.login("wx-openid-123", null, null, null);

        assertEquals(1L, user.getId());
        assertEquals("老用户", user.getNickname());
    }

    @Test
    void getProfileUserNotFound() {
        when(userMapper.selectById(999L)).thenReturn(null);

        assertNull(userService.getProfile(999L, null));
    }
}
