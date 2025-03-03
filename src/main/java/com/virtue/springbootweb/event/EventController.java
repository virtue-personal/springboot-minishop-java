package com.virtue.springbootweb.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class EventController {

    private final EventRepository eventRepository;

    @GetMapping("/event")
    public String event(Model model) {
        List<Event> result = eventRepository.findAll();

        model.addAttribute("events", result);

        return "event.html";
    }
}
