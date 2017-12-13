package com.hgicreate.rno.web.rest.gsm.vm;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Cell implements Serializable {

    private static final long serialVersionUID = 1L;

    String cellId, bandType;
    Double longitude, latitude;
    String bcch;
    int pci;
    String enodebId;
    Integer stationSpace;

    public Cell() {
    }
}
