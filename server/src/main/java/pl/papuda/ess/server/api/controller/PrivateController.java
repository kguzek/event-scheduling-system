package pl.papuda.ess.server.api.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.papuda.ess.server.api.model.ErrorResponse;
import pl.papuda.ess.server.api.model.Event;
import pl.papuda.ess.server.api.model.Task;
import pl.papuda.ess.server.api.model.TaskStatus;
import pl.papuda.ess.server.api.model.User;
import pl.papuda.ess.server.api.model.body.PermissionsResponse;
import pl.papuda.ess.server.api.model.body.TaskStatusUpdateRequest;
import pl.papuda.ess.server.api.repo.EventRepository;
import pl.papuda.ess.server.api.repo.TaskRepository;
import pl.papuda.ess.server.api.repo.UserRepository;

@RestController
@RequestMapping("/api/v1/private")
public class PrivateController {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;

    @GetMapping("/event")
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(eventRepository.findAll());
    }

    @GetMapping("/event/{id}")
    public ResponseEntity<?> getEvent(@PathVariable Long id) {
        Optional<Event> eventData = eventRepository.findById(id);
        if (eventData.isEmpty()) {
            return ErrorResponse.generate("Event not found", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(eventData.get());
    }

    private ResponseEntity<?> setAttendanceStatus(Long eventId, Principal principal, boolean attending) {
        Optional<Event> eventData = eventRepository.findById(eventId);
        if (eventData.isEmpty()) {
            return ErrorResponse.generate("Event not found", HttpStatus.NOT_FOUND);
        }
        Optional<User> userData = userRepository.findByUsername(principal.getName());
        if (userData.isEmpty()) {
            return ErrorResponse.generate("Authenticated as non-existing user; cannot create event",
                    HttpStatus.UNAUTHORIZED);
        }
        Event event = eventData.get();
        User user = userData.get();
        Set<User> attendees = event.getAttendees();
        // Set<Event> attendedEvents = user.getAttendedEvents();
        if (attendees.contains(user) == attending) {
            return ErrorResponse.generate("The attendance status is already at that value", HttpStatus.CONFLICT);
        }
        if (attending) {
            attendees.add(user);
            // attendedEvents.add(event);
        } else {
            attendees.remove(user);
            // attendedEvents.remove(event);
        }
        event.setAttendees(attendees);
        // user.setAttendedEvents(attendedEvents);
        // eventRepository.save(event);
        // return ResponseEntity.ok(userRepository.save(user));
        return ResponseEntity.ok(eventRepository.save(event));
    }

    private ResponseEntity<?> setTaskAssignee(Long taskId, Principal principal, boolean volunteering) {
        Optional<Task> taskData = taskRepository.findById(taskId);
        if (taskData.isEmpty()) {
            return ErrorResponse.generate("Task not found", HttpStatus.NOT_FOUND);
        }
        Optional<User> userData = userRepository.findByUsername(principal.getName());
        if (userData.isEmpty()) {
            return ErrorResponse.generate("Authenticated as non-existing user; cannot modify task assignee",
                    HttpStatus.UNAUTHORIZED);
        }
        Task task = taskData.get();
        User user = userData.get();
        User previousAssignee = task.getAssignee();
        if (volunteering) {
            if (previousAssignee != null) {
                return ErrorResponse.generate("This task already has an assignee", HttpStatus.CONFLICT);
            }
            task.setAssignee(user);
            task.setStatus(TaskStatus.ASSIGNED);
        } else if (previousAssignee == null) {
            return ErrorResponse.generate("There was already nobody assigned to this task", HttpStatus.CONFLICT);
        } else if (previousAssignee.getId().equals(user.getId())) {
            task.setAssignee(null);
            task.setStatus(TaskStatus.OPEN);
        } else {
            return ErrorResponse.generate("You are not the user that the task is assigned to", HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(taskRepository.save(task));
    }

    @GetMapping("/permissions")
    public ResponseEntity<?> getUserPermissions(Principal principal) {
        if (principal == null) {
            return ErrorResponse.generate("You are not logged in", HttpStatus.UNAUTHORIZED);
        }
        Optional<User> userData = userRepository.findByUsername(principal.getName());
        if (userData.isEmpty()) {
            return ErrorResponse.generate("Invalid logged in username", HttpStatus.UNAUTHORIZED);
        }
        User user = userData.get();
        PermissionsResponse response = PermissionsResponse.builder().id(user.getId()).email(user.getEmail())
                .username(user.getUsername()).role(user.getRole().name()).build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/event/{id}/attendee")
    public ResponseEntity<?> declareAttendance(@PathVariable Long id, Principal principal) {
        return setAttendanceStatus(id, principal, true);
    }

    @DeleteMapping("/event/{id}/attendee")
    public ResponseEntity<?> withdrawAttendance(@PathVariable Long id, Principal principal) {
        return setAttendanceStatus(id, principal, false);
    }

    @PostMapping("/task/{id}/assignee")
    public ResponseEntity<?> volunteerAsTaskAssignee(@PathVariable Long id, Principal principal) {
        return setTaskAssignee(id, principal, true);
    }

    @DeleteMapping("/task/{id}/assignee")
    public ResponseEntity<?> quitAsTaskAssignee(@PathVariable Long id, Principal principal) {
        return setTaskAssignee(id, principal, false);
    }

    @PutMapping("/task/{id}/status")
    public ResponseEntity<?> updateTaskStatus(@PathVariable Long id, Principal principal,
            @RequestBody TaskStatusUpdateRequest request) {
        Optional<Task> taskData = taskRepository.findById(id);
        if (taskData.isEmpty()) {
            return ErrorResponse.generate("Task not found", HttpStatus.NOT_FOUND);
        }
        Task task = taskData.get();
        if (!task.getAssignee().getUsername().equals(principal.getName())) {
            return ErrorResponse.generate("You can only modify the status of tasks which you are assigned to",
                    HttpStatus.FORBIDDEN);
        }
        TaskStatus status = request.getStatus();
        if (status == TaskStatus.OPEN) {
            return ErrorResponse
                    .generate("Cannot manually set task status to OPEN; instead, unassign yourself from the task");
        }
        task.setStatus(status);
        return ResponseEntity.ok(taskRepository.save(task));
    }
}
