package ru.petrelevich.controllers;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.util.HtmlUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.petrelevich.component.DeadRooms;
import ru.petrelevich.domain.Message;

@RequiredArgsConstructor
@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    private static final String TOPIC_TEMPLATE = "/topic/response.";

    private final WebClient datastoreClient;
    private final SimpMessagingTemplate template;
    private final DeadRooms deadRooms;

    @MessageMapping("/message.{roomId}")
    public void getMessage(@DestinationVariable String roomId, Message message) {
        logger.info("get message:{}, roomId:{}", message, roomId);
        if (!deadRooms.contains(Long.valueOf(roomId))) {
            saveMessage(roomId, message)
                .subscribe(msgId -> logger.info("message send id:{}", msgId));
            sendToClient(roomId, message);
        }
    }

    private void sendToClient(String roomId, Message message) {
        List<Long> rooms = deadRooms.getRooms();
        rooms.add(Long.valueOf(roomId));
        rooms.forEach(room -> {
            template.convertAndSend(String.format("%s%s", TOPIC_TEMPLATE, room),
                new Message(HtmlUtils.htmlEscape(message.messageStr())));
        });
    }


    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        var genericMessage = (GenericMessage<byte[]>) event.getMessage();
        var simpDestination = (String) genericMessage.getHeaders().get("simpDestination");
        if (simpDestination == null) {
            logger.error("Can not get simpDestination header, headers:{}", genericMessage.getHeaders());
            throw new ChatException("Can not get simpDestination header");
        }
        var roomId = parseRoomId(simpDestination);

        getMessagesByRoomId(roomId)
            .doOnError(ex -> logger.error("getting messages for roomId:{} failed", roomId, ex))
            .subscribe(message -> template.convertAndSend(simpDestination, message));
    }

    private long parseRoomId(String simpDestination) {
        try {
            return Long.parseLong(simpDestination.replace(TOPIC_TEMPLATE, ""));
        } catch (Exception ex) {
            logger.error("Can not get roomId", ex);
            throw new ChatException("Can not get roomId");
        }
    }

    private Mono<Long> saveMessage(String roomId, Message message) {
        return datastoreClient.post().uri(String.format("/msg/%s", roomId))
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(message)
                .exchangeToMono(response -> response.bodyToMono(Long.class));
    }

    private Flux<Message> getMessagesByRoomId(long roomId) {
        String url = deadRooms.contains(roomId) ? String.format("/msg") : String.format("/msg/%s", roomId);
        return datastoreClient.get().uri(url)
                .accept(MediaType.APPLICATION_NDJSON)
                .exchangeToFlux(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return response.bodyToFlux(Message.class);
                    } else {
                        return response.createException().flatMapMany(Mono::error);
                    }
                });
    }
}
