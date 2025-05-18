package com.virtue.app.service;

import com.virtue.app.domain.Event;
import com.virtue.app.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.Sort;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class EventServiceTest {

    @InjectMocks
    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

    private Event sampleEvent;

    @BeforeEach
    void setUp() {
        sampleEvent = new Event();
        sampleEvent.setId(1L);
        sampleEvent.setTitle("테스트 제목");
        sampleEvent.setDescription("테스트 설명");
        sampleEvent.setDate("2025-12-31");
        sampleEvent.setImgUrl("event.jpg");
    }

    @Test
    void 모든_이벤트_조회_성공() {
        when(eventRepository.findAll(any(Sort.class))).thenReturn(List.of(sampleEvent));

        List<Event> result = eventService.getAllEvents();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("테스트 제목");
    }

    @Test
    void 이벤트_생성_성공() {
        Map<String, String> request = Map.of(
                "title", "신규 이벤트",
                "description", "설명입니다",
                "date", "2025-11-01",
                "imgUrl", "img.png"
        );

        eventService.createEvent(request);

        ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
        verify(eventRepository).save(captor.capture());

        Event saved = captor.getValue();
        assertThat(saved.getTitle()).isEqualTo("신규 이벤트");
        assertThat(saved.getImgUrl()).isEqualTo("img.png");
    }

    @Test
    void 이벤트_수정_성공() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(sampleEvent));

        Map<String, String> update = Map.of(
                "title", "수정된 제목",
                "description", "수정 설명",
                "date", "2025-01-01",
                "imgUrl", "updated.png"
        );

        eventService.updateEvent(1L, update);

        assertThat(sampleEvent.getTitle()).isEqualTo("수정된 제목");
        assertThat(sampleEvent.getImgUrl()).isEqualTo("updated.png");

        verify(eventRepository).save(sampleEvent);
    }

    @Test
    void 이벤트_수정_실패_존재하지않음() {
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        Map<String, String> update = Map.of("title", "fail");

        assertThrows(IllegalArgumentException.class, () -> {
            eventService.updateEvent(999L, update);
        });
    }

    @Test
    void 이벤트_삭제_성공() {
        eventService.deleteEvent(1L);
        verify(eventRepository).deleteById(1L);
    }
}