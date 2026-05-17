package com.campusred.service;

import com.campusred.entity.Follow;
import com.campusred.mapper.FollowMapper;
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
class FollowServiceTest {

    @Mock
    private FollowMapper followMapper;
    @Mock
    private UserMapper userMapper;
    @Mock
    private MessageService messageService;

    @InjectMocks
    private FollowService followService;

    @Test
    void cannotFollowSelf() {
        boolean result = followService.toggleFollow(1L, 1L);
        assertFalse(result);
        verify(followMapper, never()).selectOne(any());
    }

    @Test
    void followNewUser() {
        when(followMapper.selectOne(any())).thenReturn(null);

        boolean result = followService.toggleFollow(1L, 2L);

        assertTrue(result);
    }

    @Test
    void unfollowExisting() {
        Follow exist = new Follow();
        exist.setId(10L);
        exist.setFollowerId(1L);
        exist.setFolloweeId(2L);

        when(followMapper.selectOne(any())).thenReturn(exist);

        boolean result = followService.toggleFollow(1L, 2L);

        assertFalse(result);
        verify(followMapper).deleteById(10L);
    }

    @Test
    void isFollowing() {
        when(followMapper.exists(any())).thenReturn(true);

        assertTrue(followService.isFollowing(1L, 2L));
    }
}
