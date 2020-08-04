package com.skplanet.amdt.bob.mig;

import com.opencsv.bean.CsvBindByName;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CsvModel {

    @CsvBindByName
    private String code;
    @CsvBindByName
    private String d1;
    @CsvBindByName
    private String d2;
    @CsvBindByName
    private String d3;
    @CsvBindByName
    private String d4;
    @CsvBindByName
    private String d5;
    @CsvBindByName
    private String full;


}
