package com.campusred.controller;

import com.campusred.auth.CurrentUserId;
import com.campusred.dto.Result;
import com.campusred.service.MessageService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/send")
    public Result<?> send(@RequestBody Map<String, Object> body,
                           @CurrentUserId Long fromUserId) {
        Long toUserId = Long.parseLong(body.get("toUserId").toString());
        String content = body.get("content").toString();
        messageService.sendMessage(fromUserId, toUserId, content);
        return Result.ok();
    }

    @GetMapping("/conversations")
    public Result<?> conversations(@CurrentUserId Long userId) {
        return Result.ok(messageService.getConversations(userId));
    }

    @GetMapping("/with/{partnerId}")
    public Result<?> withUser(@CurrentUserId Long userId, @PathVariable Long partnerId) {
        return Result.ok(messageService.getMessagesWith(userId, partnerId));
    }

    @GetMapping("/unread")
    public Result<?> unreadCount(@CurrentUserId Long userId) {
        return Result.ok(Map.of("count", messageService.getUnreadMessageCount(userId)));
    }

    @GetMapping("/notifications")
    public Result<?> notifications(@CurrentUserId Long userId) {
        return Result.ok(messageService.getNotifications(userId));
    }

    @GetMapping("/notifications/unread")
    public Result<?> unreadNotifCount(@CurrentUserId Long userId) {
        return Result.ok(Map.of("count", messageService.getUnreadNotificationCount(userId)));
    }

    @PutMapping("/notifications/{id}/read")
    public Result<?> readNotif(@PathVariable Long id, @CurrentUserId Long userId) {
        messageService.markNotificationRead(id, userId);
        return Result.ok();
    }

    @PutMapping("/notifications/read-all")
    public Result<?> readAllNotifs(@CurrentUserId Long userId) {
        messageService.markAllNotificationsRead(userId);
        return Result.ok();
    }

    @PostMapping("/notifications")
    public Result<?> createNotification(@RequestBody Map<String, Object> body,
                                         @CurrentUserId Long userId) {
        String type = body.get("type").toString();
        String title = body.get("title").toString();
        String content = body.containsKey("content") ? body.get("content").toString() : "";
        Long relatedId = body.containsKey("relatedId") ? Long.parseLong(body.get("relatedId").toString()) : null;
        messageService.createNotification(userId, type, title, content, relatedId);
        return Result.ok();
    }
}
