package ru.otus.listener.homework;

import java.util.Map;
import ru.otus.listener.Listener;
import ru.otus.model.Message;
import java.util.Optional;

public class HistoryListener implements Listener, HistoryReader {

    private final Map<Long, Message> messageHistory;

    public HistoryListener(Map<Long, Message> messageHistory) {
        this.messageHistory = messageHistory;
    }

    @Override
    public void onUpdated(Message msg) {
        Message newMessage = msg.toBuilder().build();
        messageHistory.put(msg.getId(), newMessage);
    }

    @Override
    public Optional<Message> findMessageById(long id) {
        Message message = messageHistory.get(id).toBuilder().build();
        return Optional.ofNullable(message);
    }
}
