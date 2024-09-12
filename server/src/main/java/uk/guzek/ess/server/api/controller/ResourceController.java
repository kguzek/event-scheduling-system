package uk.guzek.ess.server.api.controller;

import java.security.Principal;
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
import uk.guzek.ess.server.api.model.Resource;
import uk.guzek.ess.server.api.model.body.ResourceCreationRequest;
import uk.guzek.ess.server.api.repo.EventRepository;
import uk.guzek.ess.server.api.repo.ResourceRepository;

@RestController
@RequestMapping("/api/v1/staff/resource")
public class ResourceController {

    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private EventRepository eventRepository;

    @GetMapping
    public ResponseEntity<List<Resource>> getAllResources() {
        return ResponseEntity.ok(resourceRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getResource(@PathVariable Long id) {
        Optional<Resource> resourceData = resourceRepository.findById(id);
        if (resourceData.isEmpty()) {
            return ErrorResponse.generate("Resource not found", HttpStatus.NOT_FOUND);
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
            return ErrorResponse.generate("Request body must contain parent event id");
        }
        Optional<Event> eventData = eventRepository.findById(eventId);
        if (eventData.isEmpty()) {
            return ErrorResponse.generate("Event not found", HttpStatus.NOT_FOUND);
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
            return ErrorResponse.generate("Resource not found", HttpStatus.NOT_FOUND);
        }
        Resource resource = mergeResources(resourceData.get(), request);
        if (resource == null) {
            return ErrorResponse.generate("Invalid assignee user id");
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
                return ErrorResponse.generate("You cannot remove resources for events that were not created by you", HttpStatus.FORBIDDEN);
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
