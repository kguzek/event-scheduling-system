package pl.papuda.ess.client.model;

import lombok.Data;

@Data
public class Expense {
    private Long id;
    private String title;
    private Integer costCents;
    private String datetime;
}
