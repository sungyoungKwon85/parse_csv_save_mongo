package com.skplanet.amdt.bob.mig;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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

//    @Autowired
//    private AddressRepository addressRepository;

    @Autowired
    private AdministrativeAreaRepository administrativeAreaRepository;

    @PostConstruct
    public void load1() {
        List<Sgis> sgis = new ArrayList<>();
        try (Reader reader = new BufferedReader(new FileReader("SGIS.csv"))) {
            CsvToBean<Sgis> csvToBean = new CsvToBeanBuilder(reader)
                .withType(Sgis.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

            sgis = csvToBean.parse();
        } catch (Exception ex) {
            log.error("Exception occurred at FileReader SGIS.csv, {}", ex);
        }

        List<AdministrativeArea> sdList = sgis.stream()
            .map(e -> e.getSdcd() + "," + e.getSdname())
            .distinct()
            .map(str -> {
                String[] split = StringUtils.split(str, ",");
                return AdministrativeArea.builder()
                    .id(split[0])
                    .name(split[1])
                    .type("sd")
                    .build();
            })
            .distinct()
            .collect(Collectors.toList());

        List<AdministrativeArea> sggList = sgis.stream()
            .map(e -> e.getSggcd() + "," + e.getSggname() + "," + e.getSdcd() + "," + e.getSdname())
            .distinct()
            .map(str -> {
                String[] split = StringUtils.split(str, ",");
                return AdministrativeArea.builder()
                    .id(split[0])
                    .name(split[1])
                    .sdId(split[2])
                    .sdName(split[3])
                    .type("sgg")
                    .build();
            })
            .distinct()
            .collect(Collectors.toList());

        List<AdministrativeArea> umdList = sgis.stream()
            .map(e -> AdministrativeArea.builder()
                .id(e.getUmdcd())
                .name(e.getUmdname())
                .sdId(e.getSdcd())
                .sdName(e.getSdname())
                .sggId(e.getSggcd())
                .sggName(e.getSggname())
                .type("umd")
                .build())
            .distinct()
            .collect(Collectors.toList());

        System.out.println();

        administrativeAreaRepository.saveAll(sdList);
        administrativeAreaRepository.saveAll(sggList);
        administrativeAreaRepository.saveAll(umdList);
    }

    //    @PostConstruct
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
            log.error("Exception occurred at FileReader SGIS.csv, {}", ex);
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

//        addressRepository.saveAll(addresses);
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
