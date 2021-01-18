package com.zygon.rl.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Simple game log. Still needs a throttling mechanism.
 *
 * @author zygon
 */
public class GameLog {

    public static final class CountedMessage {

        private final String message;
        private final int count;

        private CountedMessage(String message, int count) {
            this.message = message;
            this.count = count;
        }

        private CountedMessage add() {
            return create(message, count + 1);
        }

        private static CountedMessage create(String message, int count) {
            return new CountedMessage(message, count);
        }

        private static CountedMessage create(String message) {
            return create(message, 1);
        }

        public int getCount() {
            return count;
        }

        public String getMessage() {
            return message;
        }

        private String getDisplay() {
            String msg = getMessage();
            if (getCount() > 1) {
                msg += " " + getCount() + "x";
            }

            return msg;
        }

        private boolean isSame(String message) {
            return this.message.equals(message);
        }
    }

    private final List<CountedMessage> messages;

    private GameLog(List<CountedMessage> messages) {
        this.messages = messages != null
                ? Collections.unmodifiableList(messages) : Collections.emptyList();
    }

    public GameLog() {
        this(null);
    }

    public GameLog add(String message) {

        List<CountedMessage> messages = new ArrayList<>(this.messages);

        if (!messages.isEmpty()) {
            CountedMessage last = messages.get(messages.size() - 1);
            if (last.isSame(message)) {
                messages.set(messages.size() - 1, last.add());
            } else {
                messages.add(CountedMessage.create(message));
            }
        } else {
            messages.add(CountedMessage.create(message));
        }

        return new GameLog(messages);
    }

    private String getLast(List<CountedMessage> messages) {
        return !messages.isEmpty()
                ? messages.get(messages.size() - 1).getDisplay() : null;
    }

    public String getLast() {
        return getLast(messages);
    }

    public List<String> getMessages() {
        return messages.stream()
                .map(CountedMessage::getDisplay)
                .collect(Collectors.toList());
    }

    public List<String> getRecent(int count) {
        int reCount = Math.min(count, this.messages.size());

        List<String> recentMessages = new ArrayList<>();

        for (int i = this.messages.size() - reCount; i < this.messages.size(); i++) {
            CountedMessage countedMsg = this.messages.get(i);
            recentMessages.add(countedMsg.getDisplay());
        }
        return recentMessages;
    }
}
