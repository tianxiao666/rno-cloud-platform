package com.hgicreate.rno.web.rest.gsm.vm;

import lombok.Data;

import java.util.List;

@Data
public class GsmStsQueryResultVM {

    private int totalCnt = 0;
    private boolean hasMore = false;
    private List<GsmStsResultVM> rnoStsResults;
    private int startIndex = 0;

}
