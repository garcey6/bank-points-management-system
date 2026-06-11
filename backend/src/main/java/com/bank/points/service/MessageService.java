package com.bank.points.service;

import com.bank.points.entity.Message;
import com.bank.points.entity.User;
import com.bank.points.repository.MessageRepository;
import com.bank.points.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Message> getAllMessagesForAdmin() {
        List<Message> messages = messageRepository.findAll();
        for (Message message : messages) {
            User user = userRepository.findById(message.getUserId()).orElse(null);
            if (user != null) {
                message.setUsername(user.getUsername());
                message.setRealName(user.getRealName());
                message.setPhone(user.getPhone());
            }
        }
        return messages;
    }

    public List<Message> getMessagesByUserId(Long userId) {
        return messageRepository.findByUserId(userId);
    }

    public List<Message> getUnrepliedMessages() {
        List<Message> messages = messageRepository.findByStatus(0);
        for (Message message : messages) {
            User user = userRepository.findById(message.getUserId()).orElse(null);
            if (user != null) {
                message.setUsername(user.getUsername());
                message.setRealName(user.getRealName());
                message.setPhone(user.getPhone());
            }
        }
        return messages;
    }

    @Transactional
    public Message addMessage(Message message) {
        message.setStatus(0);
        message.setCreateTime(LocalDateTime.now());
        message.setUpdateTime(LocalDateTime.now());
        return messageRepository.save(message);
    }

    @Transactional
    public Message replyMessage(Long id, String replyContent) {
        Message message = messageRepository.findById(id).orElse(null);
        if (message == null) {
            throw new RuntimeException("留言不存在");
        }
        message.setReplyContent(replyContent);
        message.setReplyTime(LocalDateTime.now());
        message.setStatus(1);
        message.setUpdateTime(LocalDateTime.now());
        return messageRepository.save(message);
    }

    @Transactional
    public void deleteMessage(Long id) {
        messageRepository.deleteById(id);
    }
}
