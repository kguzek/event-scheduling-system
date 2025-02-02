package pl.papuda.ess.server.api.controller.priv;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.papuda.ess.server.api.model.*;
import pl.papuda.ess.server.api.model.body.TaskStatusUpdateRequest;
import pl.papuda.ess.server.api.repo.TaskRepository;
import pl.papuda.ess.server.api.repo.UserRepository;
import pl.papuda.ess.server.common.RestResponse;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/private/task")
public class TaskPrivateController {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    private ResponseEntity<?> setTaskAssignee(Long taskId, Principal principal, boolean volunteering) {
        Optional<Task> taskData = taskRepository.findById(taskId);
        if (taskData.isEmpty()) {
            return RestResponse.notFound("Task");
        }
        Optional<User> userData = userRepository.findByUsername(principal.getName());
        if (userData.isEmpty()) {
            return RestResponse.generate("Authenticated as non-existing user; cannot modify task assignee",
                    HttpStatus.UNAUTHORIZED);
        }
        Task task = taskData.get();
        User user = userData.get();
        User previousAssignee = task.getAssignee();
        if (volunteering) {
            if (previousAssignee != null) {
                return RestResponse.generate("This task already has an assignee", HttpStatus.CONFLICT);
            }
            task.setAssignee(user);
            task.setStatus(TaskStatus.ASSIGNED);
        } else if (previousAssignee == null) {
            return RestResponse.generate("There was already nobody assigned to this task", HttpStatus.CONFLICT);
        } else if (previousAssignee.getId().equals(user.getId())) {
            task.setAssignee(null);
            task.setStatus(TaskStatus.OPEN);
        } else {
            return RestResponse.forbidden("You are not the user that the task is assigned to");
        }
        return ResponseEntity.ok(taskRepository.save(task));
    }


    @PostMapping("/{id}/assignee")
    public ResponseEntity<?> volunteerAsTaskAssignee(@PathVariable Long id, Principal principal) {
        return setTaskAssignee(id, principal, true);
    }

    @DeleteMapping("/{id}/assignee")
    public ResponseEntity<?> quitAsTaskAssignee(@PathVariable Long id, Principal principal) {
        return setTaskAssignee(id, principal, false);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateTaskStatus(@PathVariable Long id, Principal principal,
                                              @RequestBody TaskStatusUpdateRequest request) {
        Optional<Task> taskData = taskRepository.findById(id);
        if (taskData.isEmpty()) {
            return RestResponse.notFound("Task");
        }
        Task task = taskData.get();
        if (!task.getAssignee().getUsername().equals(principal.getName())) {
            return RestResponse.forbidden("You can only modify the status of tasks which you are assigned to");
        }
        TaskStatus status = request.getStatus();
        if (status == TaskStatus.OPEN) {
            return RestResponse
                    .badRequest("Cannot manually set task status to OPEN; instead, unassign yourself from the task");
        }
        task.setStatus(status);
        return ResponseEntity.ok(taskRepository.save(task));
    }
}
