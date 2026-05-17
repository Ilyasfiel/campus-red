package com.campusred.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campusred.dto.CommentVO;
import com.campusred.dto.UserVO;
import com.campusred.entity.Comment;
import com.campusred.entity.Note;
import com.campusred.entity.User;
import com.campusred.mapper.CommentMapper;
import com.campusred.mapper.NoteMapper;
import com.campusred.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentService {
    private final CommentMapper commentMapper;
    private final NoteMapper noteMapper;
    private final UserMapper userMapper;
    private final MessageService messageService;

    public CommentService(CommentMapper commentMapper, NoteMapper noteMapper,
                          UserMapper userMapper, MessageService messageService) {
        this.commentMapper = commentMapper;
        this.noteMapper = noteMapper;
        this.userMapper = userMapper;
        this.messageService = messageService;
    }

    @Transactional
    public CommentVO addComment(Long userId, Long noteId, Long parentId, String content) {
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setNoteId(noteId);
        comment.setParentId(parentId != null ? parentId : 0L);
        comment.setContent(content);
        commentMapper.insert(comment);

        Note note = noteMapper.selectById(noteId);
        if (note != null) {
            note.setCommentCount(note.getCommentCount() + 1);
            noteMapper.updateById(note);

            // 给笔记作者发通知
            if (!userId.equals(note.getUserId())) {
                User fromUser = userMapper.selectById(userId);
                String fromName = fromUser != null ? fromUser.getNickname() : "有人";
                messageService.createNotification(note.getUserId(), "comment",
                        fromName + " 评论了你的笔记", content, noteId);
            }
        }

        return toCommentVO(comment);
    }

    public List<CommentVO> getCommentsByNoteId(Long noteId) {
        List<Comment> allComments = commentMapper.selectList(
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getNoteId, noteId)
                        .orderByAsc(Comment::getCreateTime));

        Map<Long, List<Comment>> parentToReplies = allComments.stream()
                .filter(c -> c.getParentId() != 0)
                .collect(Collectors.groupingBy(Comment::getParentId));

        List<CommentVO> vos = new ArrayList<>();
        for (Comment comment : allComments) {
            if (comment.getParentId() == 0) {
                CommentVO vo = toCommentVO(comment);
                List<Comment> replies = parentToReplies.getOrDefault(comment.getId(), List.of());
                vo.setReplies(replies.stream().map(this::toCommentVO).collect(Collectors.toList()));
                vos.add(vo);
            }
        }

        return vos;
    }

    private CommentVO toCommentVO(Comment comment) {
        CommentVO vo = new CommentVO();
        vo.setId(comment.getId());
        vo.setNoteId(comment.getNoteId());
        vo.setParentId(comment.getParentId());
        vo.setContent(comment.getContent());
        vo.setCreateTime(comment.getCreateTime());

        User user = userMapper.selectById(comment.getUserId());
        if (user != null) {
            UserVO userVO = new UserVO();
            userVO.setId(user.getId());
            userVO.setNickname(user.getNickname());
            userVO.setAvatarUrl(user.getAvatarUrl());
            vo.setUser(userVO);
        }

        return vo;
    }
}
