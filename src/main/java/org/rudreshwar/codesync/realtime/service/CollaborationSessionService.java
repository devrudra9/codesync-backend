package org.rudreshwar.codesync.realtime.service;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CollaborationSessionService {

    private final ConcurrentHashMap<Long, Set<String>> activeUsers = new ConcurrentHashMap<>();

    public void joinProject(Long projectId, String username) {
        activeUsers
                .computeIfAbsent(projectId, id -> ConcurrentHashMap.newKeySet())
                .add(username);
    }

    public void leaveProject(Long projectId, String username) {
        Set<String> users = activeUsers.get(projectId);
        if (users == null) {
            return;
        }
        users.remove(username);
        if (users.isEmpty()) {
            activeUsers.remove(projectId);
        }

    }

    public Set<String> getActiveUsers(Long projectId) {
        return activeUsers.getOrDefault(projectId, Collections.emptySet());
    }

}