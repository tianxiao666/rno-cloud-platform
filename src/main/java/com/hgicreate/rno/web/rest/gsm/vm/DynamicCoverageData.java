package com.hgicreate.rno.web.rest.gsm.vm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DynamicCoverageData {
    @JsonProperty("CELL_ID")
    private String cellId;
    @JsonProperty("CELL_LON")
    private Double cellLon;
    @JsonProperty("CELL_LAT")
    private Double cellLat;
    @JsonProperty("NCELL_ID")
    private String ncellId;
    @JsonProperty("NCELL_LON")
    private Double ncellLon;
    @JsonProperty("NCELL_LAT")
    private Double ncellLat;
    @JsonProperty("RSRPTIMES0")
    private Integer rsrpTimes0;
    @JsonProperty("RSRPTIMES2")
    private Integer rsrpTimes2;
    @JsonProperty("VAL1")
    private Double val1;
    @JsonProperty("VAL2")
    private Double val2;
    @JsonProperty("DISTANCE")
    private Double distance;
}
