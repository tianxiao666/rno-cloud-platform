package com.hgicreate.rno.web.rest.gsm.vm;

import lombok.Data;

@Data
public class GsmFrequencyUpdateVM {
    private String bcch;
    private String tch;
    private String cellId;
}
