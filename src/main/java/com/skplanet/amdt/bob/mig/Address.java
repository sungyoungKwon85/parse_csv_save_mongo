package com.skplanet.amdt.bob.mig;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document(collation = "Address")
@Getter
@Setter
public class Address {
    @Id
    private String code;
    private String name;
    private int depth;
    private String parent;
    private String fullName;
}
