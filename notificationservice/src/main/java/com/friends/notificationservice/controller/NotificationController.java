package com.friends.notificationservice.controller;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository repository;

    @GetMapping
    public Flux<NotificationLog> getAllNotifications() {
        return repository.findAll();
    }
}