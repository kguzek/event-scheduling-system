package uk.guzek.ess.server.api.controller;

import java.security.Principal;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import uk.guzek.ess.server.api.model.ErrorResponse;
import uk.guzek.ess.server.api.model.Event;
import uk.guzek.ess.server.api.model.Task;
import uk.guzek.ess.server.api.model.TaskStatus;
import uk.guzek.ess.server.api.model.User;
import uk.guzek.ess.server.api.model.body.TaskStatusUpdateRequest;
import uk.guzek.ess.server.api.repo.EventRepository;
import uk.guzek.ess.server.api.repo.TaskRepository;
import uk.guzek.ess.server.api.repo.UserRepository;


@RestController
@RequestMapping("/api/v1/private")
public class PrivateController {
  @Autowired
  private EventRepository eventRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private TaskRepository taskRepository;

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
    if ((previousAssignee != null) == volunteering) {
      // case when user is volunteering but someone is already assigned
      // or when user is not volunteering (i.e. quitting) but nobody is assigned
      return ErrorResponse.generate(volunteering ? "This task already has an assignee" : "There was already nobody assigned to this task", HttpStatus.CONFLICT);
    }
    if (volunteering) {
      task.setAssignee(user);
      task.setStatus(TaskStatus.ASSIGNED);
    } else if (previousAssignee.getId().equals(user.getId())) {
      task.setAssignee(null);
      task.setStatus(TaskStatus.OPEN);
    } else {
      return ErrorResponse.generate("You are not the user that the task is assigned to", HttpStatus.FORBIDDEN);
    }
    return ResponseEntity.ok(taskRepository.save(task));
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
  public ResponseEntity<?> updateTaskStatus(@PathVariable Long id, Principal principal, @RequestBody TaskStatusUpdateRequest request) {
    Optional<Task> taskData = taskRepository.findById(id);
    if (taskData.isEmpty()) {
      return ErrorResponse.generate("Task not found", HttpStatus.NOT_FOUND);
    }
    Task task = taskData.get();
    if (!task.getAssignee().getUsername().equals(principal.getName())) {
      return ErrorResponse.generate("You can only modify the status of tasks which you are assigned to", HttpStatus.FORBIDDEN);
    }
    TaskStatus status = request.getStatus();
    if (status == TaskStatus.OPEN) {
      return ErrorResponse.generate("Cannot manually set task status to OPEN; instead, unassign yourself from the task");
    }
    task.setStatus(status);
    return ResponseEntity.ok(taskRepository.save(task));
  }
}