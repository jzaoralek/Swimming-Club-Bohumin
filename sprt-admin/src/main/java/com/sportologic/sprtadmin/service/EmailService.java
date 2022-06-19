package com.sportologic.sprtadmin.service;

public interface EmailService {
    void sendSimpleMessage(String to, String subject, String text);
}
