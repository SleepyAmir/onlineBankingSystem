package com.sleepy.onlinebankingsystem.security;


import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SessionManager {
    private static final Map<String, HttpSession> sessionMap = new HashMap<>();

    public static void addSession(String username, HttpSession session) {
        sessionMap.put(username, session);
    }

    public static void removeSession(String username) {
        sessionMap.remove(username);
    }

    public static Set<String> getLoggedInUsers() {
        return sessionMap.keySet();
    }

    public static HttpSession getSession(String username) {
        return sessionMap.get(username);
    }
}