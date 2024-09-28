package pl.papuda.ess.client.model;

import lombok.Data;

@Data
public class Location  {
    private String street;
    private String city;
    private String code;
    private String country;
    private String additionalInformation;
}
