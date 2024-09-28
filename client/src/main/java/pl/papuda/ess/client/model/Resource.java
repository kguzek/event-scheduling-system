
package pl.papuda.ess.client.model;

import lombok.Data;


@Data
public class Resource {
    private Long id;
    private String name;
    private Integer currentAmount;
    private Integer goal;
    private String imageUrl;
}
