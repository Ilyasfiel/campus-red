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
        Object toObj = body.get("toUserId");
        Object contentObj = body.get("content");
        if (toObj == null || contentObj == null) return Result.fail(400, "参数不完整");
        String content = contentObj.toString().trim();
        if (content.isEmpty()) return Result.fail(400, "消息不能为空");
        if (content.length() > 2000) return Result.fail(400, "消息过长");
        Long toUserId = Long.parseLong(toObj.toString());
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
        Object typeObj = body.get("type");
        Object titleObj = body.get("title");
        if (typeObj == null || titleObj == null) return Result.fail(400, "参数不完整");
        String type = typeObj.toString();
        String title = titleObj.toString();
        String content = body.get("content") != null ? body.get("content").toString() : "";
        Long relatedId = body.get("relatedId") != null ? Long.parseLong(body.get("relatedId").toString()) : null;
        messageService.createNotification(userId, type, title, content, relatedId);
        return Result.ok();
    }
}
