package com.campusred.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campusred.entity.Follow;
import com.campusred.entity.User;
import com.campusred.mapper.FollowMapper;
import com.campusred.mapper.UserMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FollowService {
    private final FollowMapper followMapper;
    private final UserMapper userMapper;
    private final MessageService messageService;

    public FollowService(FollowMapper followMapper, UserMapper userMapper, MessageService messageService) {
        this.followMapper = followMapper;
        this.userMapper = userMapper;
        this.messageService = messageService;
    }

    @Transactional
    public boolean toggleFollow(Long followerId, Long followeeId) {
        if (followerId.equals(followeeId)) return false;

        Follow exist = followMapper.selectOne(
                new LambdaQueryWrapper<Follow>()
                        .eq(Follow::getFollowerId, followerId)
                        .eq(Follow::getFolloweeId, followeeId));

        if (exist != null) {
            followMapper.deleteById(exist.getId());
            return false;
        }

        Follow follow = new Follow();
        follow.setFollowerId(followerId);
        follow.setFolloweeId(followeeId);
        try {
            followMapper.insert(follow);
        } catch (DuplicateKeyException e) {
            return false;
        }

        User fromUser = userMapper.selectById(followerId);
        String fromName = fromUser != null ? fromUser.getNickname() : "有人";
        messageService.createNotification(followeeId, "follow",
                fromName + " 关注了你", "", null);

        return true;
    }

    public boolean isFollowing(Long followerId, Long followeeId) {
        return followMapper.exists(new LambdaQueryWrapper<Follow>()
                .eq(Follow::getFollowerId, followerId)
                .eq(Follow::getFolloweeId, followeeId));
    }

    public java.util.List<com.campusred.dto.UserVO> getFollowers(Long userId) {
        java.util.List<Follow> follows = followMapper.selectList(
                new LambdaQueryWrapper<Follow>()
                        .eq(Follow::getFolloweeId, userId)
                        .orderByDesc(Follow::getCreateTime));
        return follows.stream().map(f -> {
            User u = userMapper.selectById(f.getFollowerId());
            return u != null ? toUserVO(u) : null;
        }).filter(v -> v != null).collect(java.util.stream.Collectors.toList());
    }

    public java.util.List<com.campusred.dto.UserVO> getFollowing(Long userId) {
        java.util.List<Follow> follows = followMapper.selectList(
                new LambdaQueryWrapper<Follow>()
                        .eq(Follow::getFollowerId, userId)
                        .orderByDesc(Follow::getCreateTime));
        return follows.stream().map(f -> {
            User u = userMapper.selectById(f.getFolloweeId());
            return u != null ? toUserVO(u) : null;
        }).filter(v -> v != null).collect(java.util.stream.Collectors.toList());
    }

    private com.campusred.dto.UserVO toUserVO(User user) {
        com.campusred.dto.UserVO vo = new com.campusred.dto.UserVO();
        vo.setId(user.getId());
        vo.setNickname(user.getNickname());
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setCampus(user.getCampus());
        return vo;
    }
}
