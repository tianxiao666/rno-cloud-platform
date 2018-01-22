package com.hgicreate.rno.service.gsm.dto;

import lombok.Data;

import java.util.List;

@Data
public class GsmStsQueryResultDTO {

    private int totalCnt = 0;
    private boolean hasMore = false;
    private List<GsmStsResultDTO> rnoStsResults;
    private int startIndex = 0;

}
