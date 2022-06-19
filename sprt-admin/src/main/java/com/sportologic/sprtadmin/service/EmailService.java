package com.sportologic.sprtadmin.service;

public interface EmailService {
    void sendMessage(String to, String subject, String text);
}
