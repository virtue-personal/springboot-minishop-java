package com.virtue.app.controller;

import com.virtue.app.domain.Event;
import com.virtue.app.repository.EventRepository;
import com.virtue.app.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
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

    private final EventRepository eventRepository;
    private final S3Service s3Service;

    @GetMapping("/event")
    public String event(Model model) {
        List<Event> events = eventRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("events", events);
        model.addAttribute("totalEvents", events.size());
        return "event";
    }

    @GetMapping("/event/presigned-url")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public String getEventImagePresignedUrl(@RequestParam String filename) {
        return s3Service.createPresignedUrl("events/" + filename);
    }

    @PostMapping("/api/events")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ResponseEntity<?> createEvent(@RequestBody Map<String, String> request) {
        try {
            Event event = new Event();
            event.setTitle(request.get("title"));
            event.setDescription(request.get("description"));
            event.setDate(request.get("date"));
            event.setImgUrl(request.get("imgUrl"));
            eventRepository.save(event);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("이벤트 등록 중 오류가 발생했습니다.");
        }
    }

    @PutMapping("/api/events/{id}")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ResponseEntity<?> updateEvent(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            Event event = eventRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("이벤트를 찾을 수 없습니다."));
            
            event.setTitle(request.get("title"));
            event.setDescription(request.get("description"));
            event.setDate(request.get("date"));
            event.setImgUrl(request.get("imgUrl"));
            eventRepository.save(event);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("이벤트 수정 중 오류가 발생했습니다.");
        }
    }

    @DeleteMapping("/api/events/{id}")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        try {
            eventRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("이벤트 삭제 중 오류가 발생했습니다.");
        }
    }
}
