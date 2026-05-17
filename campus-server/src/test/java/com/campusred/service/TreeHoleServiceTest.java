package com.campusred.service;

import com.campusred.entity.TreeHole;
import com.campusred.entity.TreeHoleComment;
import com.campusred.mapper.TreeHoleCommentMapper;
import com.campusred.mapper.TreeHoleLikeMapper;
import com.campusred.mapper.TreeHoleMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TreeHoleServiceTest {

    @Mock
    private TreeHoleMapper holeMapper;
    @Mock
    private TreeHoleCommentMapper commentMapper;
    @Mock
    private TreeHoleLikeMapper likeMapper;

    @InjectMocks
    private TreeHoleService treeHoleService;

    @Test
    void createTreeHole() {
        TreeHole hole = treeHoleService.create("今天心情不错", "😊 开心");

        assertNotNull(hole);
        assertEquals("今天心情不错", hole.getContent());
        assertEquals("😊 开心", hole.getTag());
        assertEquals(0, hole.getLikeCount());
        assertEquals(0, hole.getCommentCount());
        assertEquals(0, hole.getIsDeleted());
    }

    @Test
    void addComment() {
        TreeHole hole = new TreeHole();
        hole.setId(1L);
        hole.setCommentCount(3);

        when(holeMapper.selectById(1L)).thenReturn(hole);

        TreeHoleComment comment = treeHoleService.addComment(1L, "加油");

        assertNotNull(comment);
        assertEquals(1L, comment.getHoleId());
        assertEquals("加油", comment.getContent());
        assertEquals(4, hole.getCommentCount());
    }
}
