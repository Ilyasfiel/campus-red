package com.campusred.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.campusred.entity.CollectRecord;
import com.campusred.entity.Note;
import com.campusred.entity.User;
import com.campusred.mapper.CollectRecordMapper;
import com.campusred.mapper.NoteMapper;
import com.campusred.mapper.UserMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CollectService {
    private final CollectRecordMapper collectRecordMapper;
    private final NoteMapper noteMapper;
    private final UserMapper userMapper;
    private final MessageService messageService;

    public CollectService(CollectRecordMapper collectRecordMapper, NoteMapper noteMapper,
                          UserMapper userMapper, MessageService messageService) {
        this.collectRecordMapper = collectRecordMapper;
        this.noteMapper = noteMapper;
        this.userMapper = userMapper;
        this.messageService = messageService;
    }

    @Transactional
    public boolean toggleCollect(Long userId, Long noteId) {
        Note note = noteMapper.selectById(noteId);
        if (note == null) return false;

        CollectRecord exist = collectRecordMapper.selectOne(
                new LambdaQueryWrapper<CollectRecord>()
                        .eq(CollectRecord::getUserId, userId)
                        .eq(CollectRecord::getNoteId, noteId));

        if (exist != null) {
            collectRecordMapper.deleteById(exist.getId());
            noteMapper.update(null, new LambdaUpdateWrapper<Note>()
                    .eq(Note::getId, noteId)
                    .setSql("collect_count = GREATEST(0, collect_count - 1)"));
            return false;
        }

        CollectRecord record = new CollectRecord();
        record.setUserId(userId);
        record.setNoteId(noteId);
        try {
            collectRecordMapper.insert(record);
        } catch (DuplicateKeyException e) {
            return false;
        }

        noteMapper.update(null, new LambdaUpdateWrapper<Note>()
                .eq(Note::getId, noteId)
                .setSql("collect_count = collect_count + 1"));

        if (!userId.equals(note.getUserId())) {
            User fromUser = userMapper.selectById(userId);
            String fromName = fromUser != null ? fromUser.getNickname() : "有人";
            messageService.createNotification(note.getUserId(), "collect",
                    fromName + " 收藏了你的笔记", note.getTitle(), noteId);
        }
        return true;
    }
}
