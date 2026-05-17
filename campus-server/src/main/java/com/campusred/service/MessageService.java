package com.campusred.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campusred.dto.UserVO;
import com.campusred.entity.Message;
import com.campusred.entity.Notification;
import com.campusred.entity.User;
import com.campusred.mapper.MessageMapper;
import com.campusred.mapper.NotificationMapper;
import com.campusred.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessageService {
    private final MessageMapper messageMapper;
    private final NotificationMapper notificationMapper;
    private final UserMapper userMapper;

    public MessageService(MessageMapper messageMapper, NotificationMapper notificationMapper, UserMapper userMapper) {
        this.messageMapper = messageMapper;
        this.notificationMapper = notificationMapper;
        this.userMapper = userMapper;
    }

    // ===== 私信 =====
    public Message sendMessage(Long fromUserId, Long toUserId, String content) {
        Message msg = new Message();
        msg.setFromUserId(fromUserId);
        msg.setToUserId(toUserId);
        msg.setContent(content);
        msg.setIsRead(0);
        messageMapper.insert(msg);
        return msg;
    }

    public List<Map<String, Object>> getConversations(Long userId) {
        // 找到所有有私信往来的用户
        List<Message> sent = messageMapper.selectList(
                new LambdaQueryWrapper<Message>().eq(Message::getFromUserId, userId));
        List<Message> received = messageMapper.selectList(
                new LambdaQueryWrapper<Message>().eq(Message::getToUserId, userId));

        Set<Long> partnerIds = new HashSet<>();
        sent.forEach(m -> partnerIds.add(m.getToUserId()));
        received.forEach(m -> partnerIds.add(m.getFromUserId()));

        List<Map<String, Object>> conversations = new ArrayList<>();
        for (Long partnerId : partnerIds) {
            User partner = userMapper.selectById(partnerId);
            if (partner == null) continue;

            // 获取最后一条消息
            List<Message> msgs = messageMapper.selectList(
                    new LambdaQueryWrapper<Message>()
                            .and(w -> w.eq(Message::getFromUserId, userId).eq(Message::getToUserId, partnerId)
                                    .or(q -> q.eq(Message::getFromUserId, partnerId).eq(Message::getToUserId, userId)))
                            .orderByDesc(Message::getCreateTime)
                            .last("LIMIT 1"));

            // 统计未读
            long unread = messageMapper.selectCount(
                    new LambdaQueryWrapper<Message>()
                            .eq(Message::getFromUserId, partnerId)
                            .eq(Message::getToUserId, userId)
                            .eq(Message::getIsRead, 0));

            Map<String, Object> conv = new LinkedHashMap<>();
            conv.put("user", toUserVO(partner));
            conv.put("lastMessage", msgs.isEmpty() ? "" : msgs.get(0).getContent());
            conv.put("lastTime", msgs.isEmpty() ? null : msgs.get(0).getCreateTime());
            conv.put("unread", (int) unread);
            conversations.add(conv);
        }

        conversations.sort((a, b) -> {
            if (a.get("lastTime") == null) return 1;
            if (b.get("lastTime") == null) return -1;
            return ((java.time.LocalDateTime) b.get("lastTime")).compareTo((java.time.LocalDateTime) a.get("lastTime"));
        });

        return conversations;
    }

    public List<Message> getMessagesWith(Long userId, Long partnerId) {
        List<Message> msgs = messageMapper.selectList(
                new LambdaQueryWrapper<Message>()
                        .and(w -> w.eq(Message::getFromUserId, userId).eq(Message::getToUserId, partnerId)
                                .or(q -> q.eq(Message::getFromUserId, partnerId).eq(Message::getToUserId, userId)))
                        .orderByAsc(Message::getCreateTime));

        // 标记对方发来的消息为已读
        for (Message m : msgs) {
            if (m.getToUserId().equals(userId) && m.getIsRead() == 0) {
                m.setIsRead(1);
                messageMapper.updateById(m);
            }
            User from = userMapper.selectById(m.getFromUserId());
            m.setFromUser(from);
        }
        return msgs;
    }

    public int getUnreadMessageCount(Long userId) {
        return messageMapper.selectCount(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getToUserId, userId)
                        .eq(Message::getIsRead, 0)).intValue();
    }

    // ===== 系统通知 =====
    public void createNotification(Long userId, String type, String title, String content, Long relatedId) {
        Notification notif = new Notification();
        notif.setUserId(userId);
        notif.setType(type);
        notif.setTitle(title);
        notif.setContent(content != null ? content : "");
        notif.setRelatedId(relatedId != null ? relatedId : 0L);
        notif.setIsRead(0);
        notificationMapper.insert(notif);
    }

    public List<Notification> getNotifications(Long userId) {
        return notificationMapper.selectList(
                new LambdaQueryWrapper<Notification>()
                        .eq(Notification::getUserId, userId)
                        .orderByDesc(Notification::getCreateTime));
    }

    public int getUnreadNotificationCount(Long userId) {
        return notificationMapper.selectCount(
                new LambdaQueryWrapper<Notification>()
                        .eq(Notification::getUserId, userId)
                        .eq(Notification::getIsRead, 0)).intValue();
    }

    public void markNotificationRead(Long notifId, Long userId) {
        Notification notif = notificationMapper.selectById(notifId);
        if (notif != null && notif.getUserId().equals(userId)) {
            notif.setIsRead(1);
            notificationMapper.updateById(notif);
        }
    }

    public void markAllNotificationsRead(Long userId) {
        // 用LambdaUpdateWrapper批量更新
        com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<Notification> wrapper =
                new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<>();
        wrapper.eq(Notification::getUserId, userId)
               .eq(Notification::getIsRead, 0)
               .set(Notification::getIsRead, 1);
        notificationMapper.update(null, wrapper);
    }

    private UserVO toUserVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setNickname(user.getNickname());
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setCampus(user.getCampus());
        return vo;
    }
}
