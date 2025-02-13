package pl.papuda.ess.server.api.controller.staff;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.papuda.ess.server.common.RestResponse;
import pl.papuda.ess.server.api.model.Event;
import pl.papuda.ess.server.api.model.Resource;
import pl.papuda.ess.server.api.model.body.ResourceCreationRequest;
import pl.papuda.ess.server.api.repo.EventRepository;
import pl.papuda.ess.server.api.repo.ResourceRepository;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/staff/resource")
public class ResourceController {

    private final ResourceRepository resourceRepository;
    private final EventRepository eventRepository;

    @GetMapping
    public ResponseEntity<List<Resource>> getAllResources() {
        return ResponseEntity.ok(resourceRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getResource(@PathVariable Long id) {
        Optional<Resource> resourceData = resourceRepository.findById(id);
        if (resourceData.isEmpty()) {
            return RestResponse.notFound("Resource");
        }
        return ResponseEntity.ok(resourceData.get());
    }

    private Resource mergeResources(Resource oldResource, ResourceCreationRequest newResource) {
        String name = newResource.getName();
        String imageUrl = newResource.getImageUrl();
        int currentAmount = newResource.getCurrentAmount();
        int goal = newResource.getGoal();
        oldResource.setName(name);
        oldResource.setImageUrl(imageUrl);
        oldResource.setCurrentAmount(currentAmount);
        oldResource.setGoal(goal);
        return oldResource;
    }

    @PostMapping
    public ResponseEntity<?> createResource(@RequestBody ResourceCreationRequest request) {
        Long eventId = request.getEventId();
        if (eventId == null) {
            return RestResponse.badRequest("Request body must contain parent event id");
        }
        Optional<Event> eventData = eventRepository.findById(eventId);
        if (eventData.isEmpty()) {
            return RestResponse.notFound("Event");
        }
        Event event = eventData.get();
        Resource resource = mergeResources(new Resource(), request);
        resource.setEvent(event);
        Resource savedResource = resourceRepository.save(resource);
        List<Resource> resources = event.getResources();
        resources.add(savedResource);
        event.setResources(resources);
        eventRepository.save(event);
        return ResponseEntity.ok(savedResource);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateResource(@PathVariable Long id, @RequestBody ResourceCreationRequest request) {
        Optional<Resource> resourceData = resourceRepository.findById(id);
        if (resourceData.isEmpty()) {
            return RestResponse.notFound("Resource");
        }
        Resource resource = mergeResources(resourceData.get(), request);
        if (!request.getEventId().equals(resource.getEvent().getId())) {
            return RestResponse.badRequest("Invalid assignee user id");
        }
        return ResponseEntity.ok(resourceRepository.save(resource));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteResource(@PathVariable Long id, Principal principal) {
        Optional<Resource> resourceData = resourceRepository.findById(id);
        if (resourceData.isPresent()) {
            Resource resource = resourceData.get();
            Event event = resource.getEvent();
            if (!event.getCreator().getUsername().equals(principal.getName())) {
                return RestResponse.forbidden("You cannot remove resources for events that were not created by you");
            }
            List<Resource> resources = event.getResources();
            resources.removeAll(List.of(resource));
            event.setResources(resources);
            eventRepository.save(event);
            resourceRepository.delete(resource);
        }
        return ResponseEntity.noContent().build();
    }
}
