package com.sleepy.onlinebankingsystem.utils;


import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SessionListener implements HttpSessionListener {

    private static int online = 0;
    private static int visited = 0;

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        online++;
        visited++;
        log.info("Session created | Online: {} | Visited: {}", online, visited);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        online--;
        log.info("Session destroyed | Online: {}", online);
    }

    public static int getOnline() { return online; }
    public static int getVisited() { return visited; }
}