package com.mitra.controller;

import com.mitra.model.User;
import com.mitra.service.NotificationService;
import com.mitra.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/user/notifications")
    public String viewNotifications(Authentication auth, Model model) {
        User user = userService.findByEmail(auth.getName()).orElse(null);
        if (user != null) {
            model.addAttribute("notifications", notificationService.getUserNotifications(user));
            model.addAttribute("unreadCount", notificationService.getUnreadCount(user));
        }
        return "user/notifications";
    }
    
    @PostMapping("/api/notifications/{id}/read")
    @ResponseBody
    public ResponseEntity<String> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok("Marked as read");
    }
    
    @GetMapping("/api/notifications/unread-count")
    @ResponseBody
    public ResponseEntity<Long> getUnreadCount(Authentication auth) {
        User user = userService.findByEmail(auth.getName()).orElse(null);
        if (user != null) {
            return ResponseEntity.ok(notificationService.getUnreadCount(user));
        }
        return ResponseEntity.ok(0L);
    }
}