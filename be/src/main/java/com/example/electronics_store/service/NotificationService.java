package com.example.electronics_store.service;

import com.example.electronics_store.dto.NotificationDTO;

import java.util.List;

public interface NotificationService {
    NotificationDTO createNotification(Integer userId, String title, String message, String type, Integer relatedId, String relatedType);
    
    NotificationDTO getNotificationById(Integer id);
    
    List<NotificationDTO> getNotificationsByUserId(Integer userId);
    
    List<NotificationDTO> getUnreadNotificationsByUserId(Integer userId);
    
    void markNotificationAsRead(Integer id);
    
    void markAllNotificationsAsRead(Integer userId);
    
    void deleteNotification(Integer id);
    
    void deleteAllNotificationsByUserId(Integer userId);
    
    Long countUnreadNotificationsByUserId(Integer userId);
}
