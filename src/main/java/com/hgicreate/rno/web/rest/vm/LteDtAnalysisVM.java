package com.hgicreate.rno.web.rest.vm;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Data
public class LteDtAnalysisVM {
    private Long areaId;
    private String dataType;
    private String areaType;
    private String createdDate;

    public String[] getDataType() {
        return dataType.split(",");
    }

    public String[] getAreaType() {
        return areaType.split(",");
    }

    public Date getCreatedDate() {
        return LocalDateToDate(createdDate);
    }

    private static Date LocalDateToDate(String d) {
        LocalDate localDate = LocalDate.parse(d);
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        return Date.from(instant);
    }
}
