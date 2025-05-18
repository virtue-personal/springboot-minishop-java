package com.virtue.app.service;

import com.virtue.app.domain.Event;
import com.virtue.app.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    /**
     * 모든 이벤트 조회
     */
    @Transactional(readOnly = true)
    public List<Event> getAllEvents() {
        return eventRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    /**
     * 이벤트 생성
     */
    @Transactional
    public void createEvent(Map<String, String> request) {
        Event event = new Event();
        event.setTitle(request.get("title"));
        event.setDescription(request.get("description"));
        event.setDate(request.get("date"));
        event.setImgUrl(request.get("imgUrl"));
        eventRepository.save(event);
    }

    /**
     * 이벤트 수정
     */
    @Transactional
    public void updateEvent(Long id, Map<String, String> request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("이벤트를 찾을 수 없습니다."));

        event.setTitle(request.get("title"));
        event.setDescription(request.get("description"));
        event.setDate(request.get("date"));
        event.setImgUrl(request.get("imgUrl"));
        eventRepository.save(event);
    }

    /**
     * 이벤트 삭제
     */
    @Transactional
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }
}