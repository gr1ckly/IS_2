package org.example.lab1.model;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class NotificationService {

    private final List<SseEmitter> emitters;

    public NotificationService() {
        this.emitters = new CopyOnWriteArrayList<>();
    }

    public NotificationService(List<SseEmitter> emitters) {
        this.emitters = emitters;
    }

    public void sendMessage(String message) {
        for (SseEmitter currEmitter : this.emitters) {
            try {
                currEmitter.send(SseEmitter.event().name(message).data(message));
            } catch (Exception e) {
                try {
                    currEmitter.completeWithError(e);
                } catch (Exception ignored) {
                }
                unregisterSseEmitter(currEmitter);
            }
        }
    }

    public void registerSseEmitter(SseEmitter emitter) {
        this.emitters.add(emitter);
    }

    public void unregisterSseEmitter(SseEmitter emitter) {
        this.emitters.remove(emitter);
    }
}
