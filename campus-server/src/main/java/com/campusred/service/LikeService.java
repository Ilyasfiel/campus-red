package com.campusred.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.campusred.entity.LikeRecord;
import com.campusred.entity.Note;
import com.campusred.entity.User;
import com.campusred.mapper.LikeRecordMapper;
import com.campusred.mapper.NoteMapper;
import com.campusred.mapper.UserMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeService {
    private final LikeRecordMapper likeRecordMapper;
    private final NoteMapper noteMapper;
    private final UserMapper userMapper;
    private final MessageService messageService;

    public LikeService(LikeRecordMapper likeRecordMapper, NoteMapper noteMapper,
                       UserMapper userMapper, MessageService messageService) {
        this.likeRecordMapper = likeRecordMapper;
        this.noteMapper = noteMapper;
        this.userMapper = userMapper;
        this.messageService = messageService;
    }

    @Transactional
    public boolean toggleLike(Long userId, Long noteId) {
        Note note = noteMapper.selectById(noteId);
        if (note == null) return false;

        LikeRecord exist = likeRecordMapper.selectOne(
                new LambdaQueryWrapper<LikeRecord>()
                        .eq(LikeRecord::getUserId, userId)
                        .eq(LikeRecord::getNoteId, noteId));

        if (exist != null) {
            likeRecordMapper.deleteById(exist.getId());
            noteMapper.update(null, new LambdaUpdateWrapper<Note>()
                    .eq(Note::getId, noteId)
                    .setSql("like_count = GREATEST(0, like_count - 1)"));
            return false;
        }

        LikeRecord record = new LikeRecord();
        record.setUserId(userId);
        record.setNoteId(noteId);
        try {
            likeRecordMapper.insert(record);
        } catch (DuplicateKeyException e) {
            return false;
        }

        noteMapper.update(null, new LambdaUpdateWrapper<Note>()
                .eq(Note::getId, noteId)
                .setSql("like_count = like_count + 1"));

        if (!userId.equals(note.getUserId())) {
            User fromUser = userMapper.selectById(userId);
            String fromName = fromUser != null ? fromUser.getNickname() : "有人";
            messageService.createNotification(note.getUserId(), "like",
                    fromName + " 赞了你的笔记", note.getTitle(), noteId);
        }
        return true;
    }
}
