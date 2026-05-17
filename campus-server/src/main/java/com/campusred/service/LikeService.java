package com.campusred.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campusred.entity.LikeRecord;
import com.campusred.entity.Note;
import com.campusred.entity.User;
import com.campusred.mapper.LikeRecordMapper;
import com.campusred.mapper.NoteMapper;
import com.campusred.mapper.UserMapper;
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
        LikeRecord exist = likeRecordMapper.selectOne(
                new LambdaQueryWrapper<LikeRecord>()
                        .eq(LikeRecord::getUserId, userId)
                        .eq(LikeRecord::getNoteId, noteId));

        Note note = noteMapper.selectById(noteId);
        if (note == null) return false;

        if (exist != null) {
            likeRecordMapper.deleteById(exist.getId());
            note.setLikeCount(Math.max(0, note.getLikeCount() - 1));
            noteMapper.updateById(note);
            return false;
        } else {
            LikeRecord record = new LikeRecord();
            record.setUserId(userId);
            record.setNoteId(noteId);
            likeRecordMapper.insert(record);
            note.setLikeCount(note.getLikeCount() + 1);
            noteMapper.updateById(note);

            // 给笔记作者发通知
            if (!userId.equals(note.getUserId())) {
                User fromUser = userMapper.selectById(userId);
                String fromName = fromUser != null ? fromUser.getNickname() : "有人";
                messageService.createNotification(note.getUserId(), "like",
                        fromName + " 赞了你的笔记", note.getTitle(), noteId);
            }
            return true;
        }
    }
}
