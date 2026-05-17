package com.campusred.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campusred.entity.CollectRecord;
import com.campusred.entity.Note;
import com.campusred.entity.User;
import com.campusred.mapper.CollectRecordMapper;
import com.campusred.mapper.NoteMapper;
import com.campusred.mapper.UserMapper;
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
        CollectRecord exist = collectRecordMapper.selectOne(
                new LambdaQueryWrapper<CollectRecord>()
                        .eq(CollectRecord::getUserId, userId)
                        .eq(CollectRecord::getNoteId, noteId));

        Note note = noteMapper.selectById(noteId);
        if (note == null) return false;

        if (exist != null) {
            collectRecordMapper.deleteById(exist.getId());
            note.setCollectCount(Math.max(0, note.getCollectCount() - 1));
            noteMapper.updateById(note);
            return false;
        } else {
            CollectRecord record = new CollectRecord();
            record.setUserId(userId);
            record.setNoteId(noteId);
            collectRecordMapper.insert(record);
            note.setCollectCount(note.getCollectCount() + 1);
            noteMapper.updateById(note);

            if (!userId.equals(note.getUserId())) {
                User fromUser = userMapper.selectById(userId);
                String fromName = fromUser != null ? fromUser.getNickname() : "有人";
                messageService.createNotification(note.getUserId(), "collect",
                        fromName + " 收藏了你的笔记", note.getTitle(), noteId);
            }
            return true;
        }
    }
}
