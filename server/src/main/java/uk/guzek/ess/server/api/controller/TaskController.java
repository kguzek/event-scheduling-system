package uk.guzek.ess.server.api.controller;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

import uk.guzek.ess.server.api.model.ErrorResponse;
import uk.guzek.ess.server.api.model.Event;
import uk.guzek.ess.server.api.model.Task;
import uk.guzek.ess.server.api.model.TaskStatus;
import uk.guzek.ess.server.api.model.User;
import uk.guzek.ess.server.api.model.body.TaskCreationRequest;
import uk.guzek.ess.server.api.repo.EventRepository;
import uk.guzek.ess.server.api.repo.TaskRepository;
import uk.guzek.ess.server.api.repo.UserRepository;

@RestController
@RequestMapping("/api/v1/staff/task")
public class TaskController {
    @Autowired
  private TaskRepository taskRepository;
  @Autowired
  private EventRepository eventRepository;
  @Autowired
  private UserRepository userRepository;

  @GetMapping
  public ResponseEntity<List<Task>> getAllTasks() {
    return ResponseEntity.ok(taskRepository.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getTask(@PathVariable Long id) {
    Optional<Task> taskData = taskRepository.findById(id);
    if (taskData.isEmpty()) {
      return ErrorResponse.generate("Task not found", HttpStatus.NOT_FOUND);
    }
    return ResponseEntity.ok(taskData.get());
  }

  private Task mergeTasks(Task oldTask, TaskCreationRequest newTask) {
    String description = newTask.getDescription();
    Date deadline = newTask.getDeadline();
    Long assigneeId = newTask.getAssigneeId();
    TaskStatus status = newTask.getStatus();
    if (assigneeId != null) {
      Optional<User> userData = userRepository.findById(assigneeId);
      if (userData.isEmpty())
        return null;
      oldTask.setAssignee(userData.get());
    }
    oldTask.setDescription(description);
    oldTask.setDeadline(deadline);
    oldTask.setStatus(status == null ? assigneeId == null ? TaskStatus.OPEN : TaskStatus.ASSIGNED : status);
    return oldTask;
  }

  @PostMapping
  public ResponseEntity<?> createTask(@RequestBody TaskCreationRequest request) {
    Long eventId = request.getEventId();
    if (eventId == null) {
      return ErrorResponse.generate("Request body must contain parent event id");
    }
    Optional<Event> eventData = eventRepository.findById(eventId);    
    if (eventData.isEmpty()) {
      return ErrorResponse.generate("Event not found", HttpStatus.NOT_FOUND);
    }
    Event event = eventData.get();
    Task task = mergeTasks(new Task(), request);
    if (task == null) {
      return ErrorResponse.generate("Invalid assignee user id");
    }
    task.setEvent(event);
    Task savedTask = taskRepository.save(task);
    List<Task> tasks = event.getTasks();
    tasks.add(savedTask);
    event.setTasks(tasks);
    eventRepository.save(event);
    return ResponseEntity.ok(savedTask);
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody TaskCreationRequest request) {
    Optional<Task> taskData = taskRepository.findById(id);
    if (taskData.isEmpty()) {
      return ErrorResponse.generate("Task not found", HttpStatus.NOT_FOUND);
    }
    Task task = mergeTasks(taskData.get(), request);
    if (task == null) {
      return ErrorResponse.generate("Invalid assignee user id");
    }
    return ResponseEntity.ok(taskRepository.save(task));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteTask(@PathVariable Long id, Principal principal) {
    Optional<Task> taskData = taskRepository.findById(id);
    if (taskData.isPresent()) {
      Task task = taskData.get();
      Event event = task.getEvent();
      if (!event.getCreator().getUsername().equals(principal.getName())) {
        return ErrorResponse.generate("You cannot remove tasks for events that were not created by you", HttpStatus.FORBIDDEN);
      }
      List<Task> tasks = event.getTasks();
      tasks.removeAll(List.of(task));
      event.setTasks(tasks);
      eventRepository.save(event);
      taskRepository.delete(task);
    }
    return ResponseEntity.noContent().build(); 
  }
}