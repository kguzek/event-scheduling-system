package uk.guzek.ess.server.api.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class Location implements Serializable {

    private String street;
    private String city;
    private String code;
    private String country;
    private String additionalInformation;
}
