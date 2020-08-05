package com.skplanet.amdt.bob.mig;

import com.opencsv.bean.CsvBindByName;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Sgis {

    @CsvBindByName
    private String sdcd;
    @CsvBindByName
    private String sdname;
    @CsvBindByName
    private String sggcd;
    @CsvBindByName
    private String sggname;
    @CsvBindByName
    private String umdcd;
    @CsvBindByName
    private String umdname;

}
