package com.skplanet.amdt.bob.mig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MigExecutor {

    @Autowired
    private AddressRepository addressRepository;

    @PostConstruct
    public void load2() {
        List<Address> addresses = new ArrayList<>();
        List<CsvModel> csvModels = new ArrayList<>();
        try (Reader reader = new BufferedReader(new FileReader("asdf3.csv"))) {
            CsvToBean<CsvModel> csvToBean = new CsvToBeanBuilder(reader)
                .withType(CsvModel.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

            csvModels = csvToBean.parse();
            System.out.println();
        } catch (Exception ex) {
            log.error("Exception occurred at FileReader skhsm_0713.csv, {}", ex);
        }

        for (CsvModel csv : csvModels) {
            Address address = new Address();
            address.setCode(csv.getCode());

            String name;
            int depth;
            String parent = null;
            if (!StringUtils.isEmpty(csv.getD5())) {
                name = csv.getD5();
                depth = 5;
                parent = getParentCode(addresses, getParentString(csv.getFull(), name));
            } else if (!StringUtils.isEmpty(csv.getD4())) {
                name = csv.getD4();
                depth = 4;
                parent = getParentCode(addresses, getParentString(csv.getFull(), name));
            } else if (!StringUtils.isEmpty(csv.getD3())) {
                name = csv.getD3();
                depth = 3;
                parent = getParentCode(addresses, getParentString(csv.getFull(), name));
            } else if (!StringUtils.isEmpty(csv.getD2())) {
                name = csv.getD2();
                depth = 2;
                parent = getParentCode(addresses, getParentString(csv.getFull(), name));
            } else if (!StringUtils.isEmpty(csv.getD1())) {
                name = csv.getD1();
                depth = 1;
            } else {
                continue;
            }
            address.setName(name);
            address.setDepth(depth);
            address.setParent(parent);
            address.setFullName(csv.getFull());
            addresses.add(address);
        }

        addressRepository.saveAll(addresses);
    }

    private String getParentString(String full, String name) {
        return StringUtils.replace(full, name, "").trim();
    }


    private String getParentCode(List<Address> addresses, String parent) {
        return addresses.stream()
            .filter(address -> StringUtils.equals(address.getFullName(), parent))
            .findFirst()
            .map(address -> address.getCode())
            .orElse(null);
    }
}
