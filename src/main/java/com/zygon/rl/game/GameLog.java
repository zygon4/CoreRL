package com.zygon.rl.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simple game log. Still needs a throttling mechanism.
 *
 * @author zygon
 */
public class GameLog {

    private final List<String> messages;

    public GameLog(List<String> messages) {
        this.messages = messages != null
                ? Collections.unmodifiableList(messages) : Collections.emptyList();
    }

    public GameLog() {
        this(null);
    }

    public GameLog add(String message) {
        List<String> messages = new ArrayList<>(this.messages);

        // TODO: throttle repeated messages
        messages.add(message);

        return new GameLog(messages);
    }

    public String getLast() {
        return !messages.isEmpty()
                ? messages.get(messages.size() - 1) : null;
    }

    public List<String> getMessages() {
        return messages;
    }

    public List<String> getRecent(int count) {
        int reCount = Math.min(count, this.messages.size());

        List<String> recentMessages = new ArrayList<>();
        for (int i = this.messages.size() - reCount; i < this.messages.size(); i++) {
            recentMessages.add(this.messages.get(i));
        }
        return recentMessages;
    }
}
