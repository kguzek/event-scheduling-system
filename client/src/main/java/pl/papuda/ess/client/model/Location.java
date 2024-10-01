package pl.papuda.ess.client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Location  {
    private String street;
    private String city;
    private String code;
    private String country;
    private String additionalInformation;
}
