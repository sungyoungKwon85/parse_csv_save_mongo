package com.skplanet.amdt.bob.mig;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Getter;

@Document(collation = "AdministrativeArea")
@Builder
@Getter
public class AdministrativeArea {
    @Id
    private String id;
    private String name;
    private String type;
    private String sdId;
    private String sdName;
    private String sggId;
    private String sggName;
}
