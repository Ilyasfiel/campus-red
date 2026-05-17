package com.campusred.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campusred.entity.TreeHole;
import com.campusred.entity.TreeHoleComment;
import com.campusred.entity.TreeHoleLike;
import com.campusred.mapper.TreeHoleCommentMapper;
import com.campusred.mapper.TreeHoleLikeMapper;
import com.campusred.mapper.TreeHoleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class TreeHoleService {
    private final TreeHoleMapper holeMapper;
    private final TreeHoleCommentMapper commentMapper;
    private final TreeHoleLikeMapper likeMapper;

    public TreeHoleService(TreeHoleMapper holeMapper, TreeHoleCommentMapper commentMapper,
                           TreeHoleLikeMapper likeMapper) {
        this.holeMapper = holeMapper;
        this.commentMapper = commentMapper;
        this.likeMapper = likeMapper;
    }

    public Page<TreeHole> getList(int page, int size) {
        Page<TreeHole> p = new Page<>(page, size);
        return holeMapper.selectPage(p,
                new LambdaQueryWrapper<TreeHole>()
                        .eq(TreeHole::getIsDeleted, 0)
                        .orderByDesc(TreeHole::getCreateTime));
    }

    public TreeHole create(String content, String tag) {
        TreeHole hole = new TreeHole();
        hole.setContent(content);
        hole.setTag(tag != null ? tag : "");
        hole.setLikeCount(0);
        hole.setCommentCount(0);
        hole.setIsDeleted(0);
        holeMapper.insert(hole);
        return hole;
    }

    @Transactional
    public Map<String, Object> toggleLike(Long holeId, Long userId) {
        TreeHole hole = holeMapper.selectById(holeId);
        if (hole == null) return Map.of("liked", false, "likeCount", 0);

        TreeHoleLike exist = likeMapper.selectOne(
                new LambdaQueryWrapper<TreeHoleLike>()
                        .eq(TreeHoleLike::getUserId, userId)
                        .eq(TreeHoleLike::getHoleId, holeId));

        if (exist != null) {
            likeMapper.deleteById(exist.getId());
            hole.setLikeCount(Math.max(0, hole.getLikeCount() - 1));
            holeMapper.updateById(hole);
            return Map.of("liked", false, "likeCount", hole.getLikeCount());
        } else {
            TreeHoleLike record = new TreeHoleLike();
            record.setUserId(userId);
            record.setHoleId(holeId);
            likeMapper.insert(record);
            hole.setLikeCount(hole.getLikeCount() + 1);
            holeMapper.updateById(hole);
            return Map.of("liked", true, "likeCount", hole.getLikeCount());
        }
    }

    public List<TreeHoleComment> getComments(Long holeId) {
        return commentMapper.selectList(
                new LambdaQueryWrapper<TreeHoleComment>()
                        .eq(TreeHoleComment::getHoleId, holeId)
                        .orderByAsc(TreeHoleComment::getCreateTime));
    }

    @Transactional
    public TreeHoleComment addComment(Long holeId, String content) {
        TreeHoleComment c = new TreeHoleComment();
        c.setHoleId(holeId);
        c.setContent(content);
        commentMapper.insert(c);
        TreeHole hole = holeMapper.selectById(holeId);
        if (hole != null) {
            hole.setCommentCount(hole.getCommentCount() + 1);
            holeMapper.updateById(hole);
        }
        return c;
    }
}
