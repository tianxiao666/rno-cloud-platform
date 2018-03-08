package com.hgicreate.rno.domain.indoor;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chao.xj
 */
@Data
public class DmLayerElementAttrPk implements Serializable{

    private Long elementId;
    private String attrName;
}
