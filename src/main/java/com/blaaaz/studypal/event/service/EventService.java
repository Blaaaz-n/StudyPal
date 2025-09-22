package com.blaaaz.studypal.event.service;

import com.blaaaz.studypal.event.model.EventEntity;
import com.blaaaz.studypal.event.repository.EventRepository;
import com.blaaaz.studypal.plan.model.PlanEntity;
import com.blaaaz.studypal.plan.repository.PlanRepository;
import com.blaaaz.studypal.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

public class EventService {

    private final EventRepository events;
    private final PlanRepository plans;
    private final UserRepository users;

    public EventService(EventRepository events, PlanRepository plans, UserRepository users) {
        this.events = events;
        this.plans = plans;
        this.users = users;
    }

    public EventEntity createEvent(Long planId, Long userId, String title, String startTs, String endTs) {
        if (planId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Plan id is required");
        if (userId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User id is required");
        if (title == null || title.isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title is required");
        if (startTs == null || startTs.isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start time is required");
        if (endTs == null || endTs.isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End time is required");

        var plan = requireOwnedPlan(planId, userId);

        // Validate time order (expects ISO-8601 strings, e.g., 2025-10-14T18:00:00Z)
        try {
            var start = Instant.parse(startTs);
            var end = Instant.parse(endTs);
            if (!end.isAfter(start)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "endTs must be after startTs");
            }
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid timestamp format (use ISO-8601)");
        }

        var e = new EventEntity();
        e.setPlan(plan);
        e.setTitle(title);
        e.setStartTs(startTs);
        e.setEndTs(endTs);
        return events.save(e);
    }

    // List all events for a user's plan
    public List<EventEntity> listEvents(Long planId, Long userId) {
        var plan = requireOwnedPlan(planId, userId);
        return events.findByPlan(plan);
    }

    // Update an event (title and/or times)
    public EventEntity updateEvent(Long eventId, Long userId, String title, String startTs, String endTs) {
        var e = events.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        // Ownership via plan
        var planOwnerId = e.getPlan().getUser().getId();
        if (!planOwnerId.equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your event");
        }

        if (title != null && !title.isBlank()) e.setTitle(title);

        if (startTs != null || endTs != null) {
            var newStart = startTs != null ? startTs : e.getStartTs();
            var newEnd   = endTs   != null ? endTs   : e.getEndTs();
            try {
                var s = Instant.parse(newStart);
                var t = Instant.parse(newEnd);
                if (!t.isAfter(s)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "endTs must be after startTs");
            } catch (Exception ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid timestamp format (use ISO-8601)");
            }
            e.setStartTs(newStart);
            e.setEndTs(newEnd);
        }

        return events.save(e);
    }

    // Delete an event
    public void deleteEvent(Long eventId, Long userId) {
        var e = events.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        if (!e.getPlan().getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your event");
        }
        events.delete(e);
    }

    // Helper: find plan and ensure it belongs to user
    private PlanEntity requireOwnedPlan(Long planId, Long userId) {
        var plan = plans.findById(planId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan not found"));
        if (!plan.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your plan");
        }
        return plan;
    }


}
