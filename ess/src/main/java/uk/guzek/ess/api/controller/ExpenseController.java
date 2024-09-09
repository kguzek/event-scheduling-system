package uk.guzek.ess.api.controller;

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
  public ResponseEntity<?> createExpense(@RequestBody ExpenseCreationRequest expense) {
    Optional<Event> event = eventRepository.findById(expense.getEventId());
    if (event.isEmpty()) {
      return ErrorResponse.generate("Invalid event id");
    }
    Expense expenseObj = Expense.builder().title(expense.getTitle()).costCents(expense.getCostCents()).datetime(expense.getDatetime()).event(event.get()).build();
    return ResponseEntity.ok(expenseRepository.save(expenseObj));
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> updateExpense(@PathVariable Long id, @RequestBody ExpenseCreationRequest expense) {
    Optional<Expense> expenseData = expenseRepository.findById(id);
    if (expenseData.isEmpty()) {
      return ErrorResponse.generate("Expense not found", HttpStatus.NOT_FOUND);
    }
    Expense oldExpense = expenseData.get();
    oldExpense.setCostCents(expense.getCostCents());
    oldExpense.setTitle(expense.getTitle());
    oldExpense.setDatetime(expense.getDatetime());
    return ResponseEntity.ok(expenseRepository.save(oldExpense));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
    expenseRepository.deleteById(id);
    return ResponseEntity.noContent().build(); 
  }
}
