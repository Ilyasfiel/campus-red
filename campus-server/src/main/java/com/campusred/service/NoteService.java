package com.campusred.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campusred.dto.NoteVO;
import com.campusred.dto.UserVO;
import com.campusred.entity.*;
import com.campusred.mapper.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class NoteService {
    private final NoteMapper noteMapper;
    private final UserMapper userMapper;
    private final LikeRecordMapper likeRecordMapper;
    private final CollectRecordMapper collectRecordMapper;
    private final FollowMapper followMapper;
    private final ViewRecordMapper viewRecordMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public NoteService(NoteMapper noteMapper, UserMapper userMapper,
                       LikeRecordMapper likeRecordMapper, CollectRecordMapper collectRecordMapper,
                       FollowMapper followMapper, ViewRecordMapper viewRecordMapper) {
        this.noteMapper = noteMapper;
        this.userMapper = userMapper;
        this.likeRecordMapper = likeRecordMapper;
        this.collectRecordMapper = collectRecordMapper;
        this.followMapper = followMapper;
        this.viewRecordMapper = viewRecordMapper;
    }

    public NoteVO createNote(Long userId, String title, String content,
                             List<String> images, List<String> tags, String location) {
        User author = userMapper.selectById(userId);
        Note note = new Note();
        note.setUserId(userId);
        note.setTitle(title);
        note.setContent(content != null ? content : "");
        note.setLocation(location != null ? location : "");
        note.setCampus(author != null && author.getCampus() != null ? author.getCampus() : "");
        note.setLikeCount(0);
        note.setCommentCount(0);
        note.setCollectCount(0);
        note.setViewCount(0);

        try {
            if (images != null) note.setImages(objectMapper.writeValueAsString(images));
            if (tags != null) note.setTags(objectMapper.writeValueAsString(tags));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON序列化失败", e);
        }

        noteMapper.insert(note);
        return toNoteVO(note, userId);
    }

    public Page<NoteVO> getNoteList(int page, int size, Long currentUserId, String campus, String sort, String tag, String keyword) {
        Page<Note> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Note> wrapper = new LambdaQueryWrapper<Note>();
        if (campus != null && !campus.isEmpty()) {
            wrapper.eq(Note::getCampus, campus);
        }
        if (tag != null && !tag.isEmpty()) {
            wrapper.like(Note::getTags, tag);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Note::getTitle, keyword).or().like(Note::getContent, keyword));
        }
        if ("hot".equals(sort)) {
            wrapper.orderByDesc(Note::getLikeCount).orderByDesc(Note::getViewCount);
        } else {
            wrapper.orderByDesc(Note::getCreateTime);
        }
        Page<Note> notePage = noteMapper.selectPage(pageParam, wrapper);

        Page<NoteVO> voPage = new Page<>(page, size, notePage.getTotal());
        List<NoteVO> vos = new ArrayList<>();
        for (Note note : notePage.getRecords()) {
            vos.add(toNoteVO(note, currentUserId));
        }
        voPage.setRecords(vos);
        return voPage;
    }

    public NoteVO getNoteDetail(Long noteId, Long currentUserId) {
        Note note = noteMapper.selectById(noteId);
        if (note == null || note.getDeleted() == 1) return null;

        // 浏览去重：同一用户1分钟内重复点击不计数
        boolean shouldCount = true;
        if (currentUserId != null) {
            java.util.List<ViewRecord> recentViews = viewRecordMapper.selectList(
                    new LambdaQueryWrapper<ViewRecord>()
                            .eq(ViewRecord::getUserId, currentUserId)
                            .eq(ViewRecord::getNoteId, noteId)
                            .orderByDesc(ViewRecord::getViewTime)
                            .last("LIMIT 1"));
            if (!recentViews.isEmpty() && recentViews.get(0).getViewTime() != null) {
                long secondsSinceLastView = java.time.Duration.between(
                        recentViews.get(0).getViewTime(), java.time.LocalDateTime.now()).getSeconds();
                if (secondsSinceLastView < 60) {
                    shouldCount = false;
                }
            }
        }

        if (shouldCount) {
            note.setViewCount(note.getViewCount() + 1);
            noteMapper.updateById(note);
            ViewRecord vr = new ViewRecord();
            vr.setUserId(currentUserId != null ? currentUserId : 0L);
            vr.setNoteId(noteId);
            viewRecordMapper.insert(vr);
        }

        return toNoteVO(note, currentUserId);
    }

    public void deleteNote(Long noteId, Long userId) {
        Note note = noteMapper.selectById(noteId);
        if (note != null && note.getUserId().equals(userId)) {
            noteMapper.deleteById(noteId);
        }
    }

    public Page<NoteVO> getUserNotes(Long userId, int page, int size, Long currentUserId) {
        Page<Note> pageParam = new Page<>(page, size);
        Page<Note> notePage = noteMapper.selectPage(pageParam,
                new LambdaQueryWrapper<Note>()
                        .eq(Note::getUserId, userId)
                        .orderByDesc(Note::getCreateTime));

        Page<NoteVO> voPage = new Page<>(page, size, notePage.getTotal());
        List<NoteVO> vos = new ArrayList<>();
        for (Note note : notePage.getRecords()) {
            vos.add(toNoteVO(note, currentUserId));
        }
        voPage.setRecords(vos);
        return voPage;
    }

    private NoteVO toNoteVO(Note note, Long currentUserId) {
        NoteVO vo = new NoteVO();
        vo.setId(note.getId());
        vo.setTitle(note.getTitle());
        vo.setContent(note.getContent());
        vo.setLocation(note.getLocation());
        vo.setCampus(note.getCampus());
        vo.setViewCount(note.getViewCount());
        vo.setLikeCount(note.getLikeCount());
        vo.setCommentCount(note.getCommentCount());
        vo.setCollectCount(note.getCollectCount());
        vo.setCreateTime(note.getCreateTime());

        try {
            if (note.getImages() != null) vo.setImages(objectMapper.readValue(note.getImages(), List.class));
            if (note.getTags() != null) vo.setTags(objectMapper.readValue(note.getTags(), List.class));
        } catch (JsonProcessingException e) {
            vo.setImages(List.of());
            vo.setTags(List.of());
        }

        User user = userMapper.selectById(note.getUserId());
        UserVO userVO = null;
        if (user != null) {
            userVO = new UserVO();
            userVO.setId(user.getId());
            userVO.setNickname(user.getNickname());
            userVO.setAvatarUrl(user.getAvatarUrl());
            userVO.setCampus(user.getCampus());
            vo.setUser(userVO);
        }

        if (currentUserId != null) {
            vo.setIsLiked(likeRecordMapper.exists(
                    new LambdaQueryWrapper<LikeRecord>()
                            .eq(LikeRecord::getUserId, currentUserId)
                            .eq(LikeRecord::getNoteId, note.getId())));
            vo.setIsCollected(collectRecordMapper.exists(
                    new LambdaQueryWrapper<CollectRecord>()
                            .eq(CollectRecord::getUserId, currentUserId)
                            .eq(CollectRecord::getNoteId, note.getId())));
            if (user != null && !Objects.equals(currentUserId, user.getId())) {
                userVO.setIsFollowed(followMapper.exists(
                        new LambdaQueryWrapper<Follow>()
                                .eq(Follow::getFollowerId, currentUserId)
                                .eq(Follow::getFolloweeId, user.getId())));
            }
        }

        return vo;
    }
}
