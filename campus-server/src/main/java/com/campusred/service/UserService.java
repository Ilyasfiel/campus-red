package com.campusred.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campusred.dto.UserVO;
import com.campusred.entity.Follow;
import com.campusred.entity.Note;
import com.campusred.entity.User;
import com.campusred.mapper.FollowMapper;
import com.campusred.mapper.NoteMapper;
import com.campusred.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final NoteMapper noteMapper;
    private final FollowMapper followMapper;

    public UserService(UserMapper userMapper, NoteMapper noteMapper, FollowMapper followMapper) {
        this.userMapper = userMapper;
        this.noteMapper = noteMapper;
        this.followMapper = followMapper;
    }

    public User login(String code, String nickname, String avatarUrl, String campus) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getOpenid, code));
        if (user == null) {
            user = new User();
            user.setOpenid(code);
            user.setNickname(nickname != null ? nickname : "校园用户" + UUID.randomUUID().toString().substring(0, 6));
            user.setAvatarUrl(avatarUrl != null ? avatarUrl : "");
            user.setCampus(campus != null ? campus : "");
            userMapper.insert(user);
        }
        return user;
    }

    public UserVO getProfile(Long userId, Long currentUserId) {
        User user = userMapper.selectById(userId);
        if (user == null) return null;

        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setNickname(user.getNickname());
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setBio(user.getBio());
        vo.setCampus(user.getCampus());

        vo.setNoteCount(noteMapper.selectCount(
                new LambdaQueryWrapper<Note>().eq(Note::getUserId, userId)).intValue());
        vo.setFollowerCount(followMapper.selectCount(
                new LambdaQueryWrapper<Follow>().eq(Follow::getFolloweeId, userId)).intValue());
        vo.setFollowingCount(followMapper.selectCount(
                new LambdaQueryWrapper<Follow>().eq(Follow::getFollowerId, userId)).intValue());

        if (currentUserId != null && !currentUserId.equals(userId)) {
            vo.setIsFollowed(followMapper.exists(
                    new LambdaQueryWrapper<Follow>()
                            .eq(Follow::getFollowerId, currentUserId)
                            .eq(Follow::getFolloweeId, userId)));
        }

        return vo;
    }

    public void updateProfile(Long userId, String nickname, String avatarUrl, String bio, String campus) {
        User user = new User();
        user.setId(userId);
        user.setNickname(nickname);
        user.setAvatarUrl(avatarUrl);
        user.setBio(bio);
        user.setCampus(campus);
        userMapper.updateById(user);
    }

    public java.util.List<String> getSchoolList() {
        return userMapper.selectList(null).stream()
                .map(User::getCampus)
                .filter(c -> c != null && !c.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(java.util.stream.Collectors.toList());
    }
}
