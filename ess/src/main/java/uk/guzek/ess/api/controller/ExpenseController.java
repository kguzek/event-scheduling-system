package uk.guzek.ess.api.controller;

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

import uk.guzek.ess.api.model.ErrorResponse;
import uk.guzek.ess.api.model.Event;
import uk.guzek.ess.api.model.Expense;
import uk.guzek.ess.api.model.body.ExpenseCreationRequest;
import uk.guzek.ess.api.repo.EventRepository;
import uk.guzek.ess.api.repo.ExpenseRepository;

@RestController
@RequestMapping("/api/v1/staff/expense")
public class ExpenseController {
  @Autowired
  private ExpenseRepository expenseRepository;
  @Autowired
  private EventRepository eventRepository;

  @GetMapping
  public ResponseEntity<List<Expense>> getAllExpenses() {
    return ResponseEntity.ok(expenseRepository.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getExpense(@PathVariable Long id) {
    Optional<Expense> expenseData = expenseRepository.findById(id);
    if (expenseData.isEmpty()) {
      return ErrorResponse.generate("Expense not found", HttpStatus.NOT_FOUND);
    }
    return ResponseEntity.ok(expenseData.get());
  }

  @PostMapping
  public ResponseEntity<?> request(@RequestBody ExpenseCreationRequest request, Principal principal) {
    Optional<Event> eventData = eventRepository.findById(request.getEventId());
    if (eventData.isEmpty()) {
      return ErrorResponse.generate("Invalid event id");
    }
    Event event = eventData.get();
    if (!event.getCreator().getUsername().equals(principal.getName())) {
      return ErrorResponse.generate("You cannot create expenses for events that were not created by you", HttpStatus.FORBIDDEN);
    }
    Expense expense = Expense.builder().title(request.getTitle()).costCents(request.getCostCents()).datetime(request.getDatetime()).event(event).build();
    List<Expense> expenses = event.getExpenses();
    expenses.add(expense);
    event.setExpenses(expenses);
    eventRepository.save(event);
    return ResponseEntity.ok(expenseRepository.save(expense));
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> updateExpense(@PathVariable Long id, @RequestBody ExpenseCreationRequest expense, Principal principal) {
    Optional<Expense> expenseData = expenseRepository.findById(id);
    if (expenseData.isEmpty()) {
      return ErrorResponse.generate("Expense not found", HttpStatus.NOT_FOUND);
    }
    Expense oldExpense = expenseData.get();
    if (!oldExpense.getEvent().getCreator().getUsername().equals(principal.getName())) {
      return ErrorResponse.generate("You cannot modify expenses for events that were not created by you", HttpStatus.FORBIDDEN);
    }
    oldExpense.setCostCents(expense.getCostCents());
    oldExpense.setTitle(expense.getTitle());
    oldExpense.setDatetime(expense.getDatetime());
    return ResponseEntity.ok(expenseRepository.save(oldExpense));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteExpense(@PathVariable Long id, Principal principal) {
    Optional<Expense> expenseData = expenseRepository.findById(id);
    if (expenseData.isPresent()) {
      Expense expense = expenseData.get();
      Event event = expense.getEvent();
      if (!event.getCreator().getUsername().equals(principal.getName())) {
        return ErrorResponse.generate("You cannot remove expenses for events that were not created by you", HttpStatus.FORBIDDEN);
      }
      List<Expense> expenses = event.getExpenses();
      expenses.removeAll(List.of(expense));
      event.setExpenses(expenses);
      eventRepository.save(event);
      expenseRepository.delete(expense);
    }
    return ResponseEntity.noContent().build(); 
  }
}
