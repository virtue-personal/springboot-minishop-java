package com.virtue.app.controller;

import com.virtue.app.domain.Event;
import com.virtue.app.service.EventService;
import com.virtue.app.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final S3Service s3Service;

    /**
     * 이벤트 페이지 렌더링
     */
    @GetMapping("/event")
    public String event(Model model) {
        List<Event> events = eventService.getAllEvents();
        model.addAttribute("events", events);
        model.addAttribute("totalEvents", events.size());
        return "event";
    }

    /**
     * 이벤트 이미지 업로드용 presigned URL 발급
     */
    @GetMapping("/event/presigned-url")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public String getEventImagePresignedUrl(@RequestParam String filename) {
        return s3Service.createPresignedUrl("events/" + filename);
    }

    /**
     * 이벤트 생성
     */
    @PostMapping("/api/events")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ResponseEntity<?> createEvent(@RequestBody Map<String, String> request) {
        try {
            eventService.createEvent(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("이벤트 등록 중 오류가 발생했습니다.");
        }
    }

    /**
     * 이벤트 수정
     */
    @PutMapping("/api/events/{id}")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ResponseEntity<?> updateEvent(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            eventService.updateEvent(id, request);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("이벤트 수정 중 오류가 발생했습니다.");
        }
    }

    /**
     * 이벤트 삭제
     */
    @DeleteMapping("/api/events/{id}")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        try {
            eventService.deleteEvent(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("이벤트 삭제 중 오류가 발생했습니다.");
        }
    }
}