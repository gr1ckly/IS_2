package org.example.lab1.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.lab1.model.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.management.Notification;
import java.io.IOException;

@RestController
@Slf4j
public class SseController {
    private NotificationService notificationService;

    @Autowired
    public SseController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping(path="/get-sse", produces = "text/event-stream")
    public SseEmitter getSseEmitter() {
        SseEmitter emitter = new SseEmitter(0L);
        emitter.onCompletion(() -> this.notificationService.unregisterSseEmitter(emitter));
        emitter.onTimeout(() -> this.notificationService.unregisterSseEmitter(emitter));
        emitter.onError((Throwable e) -> this.notificationService.unregisterSseEmitter(emitter));
        notificationService.registerSseEmitter(emitter);
        log.info("SSE Emitter registered: {}", emitter);
        try {
            emitter.send(SseEmitter.event().name("ping"));
        } catch (IOException e) {
            notificationService.unregisterSseEmitter(emitter);
        }
        return emitter;
    }
}
