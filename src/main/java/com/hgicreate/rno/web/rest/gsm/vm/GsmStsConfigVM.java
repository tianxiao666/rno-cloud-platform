package com.hgicreate.rno.web.rest.gsm.vm;

import lombok.Data;

import java.io.Serializable;

/**
 * 分析列表
 *
 * @author brightming
 */
@Data
public class GsmStsConfigVM implements Serializable {

    private long configId;
    private boolean isFromQuery;
    private boolean isSelected;
    private GsmStsAnaItemDetailVM stsAnaItemDetail;
    private GsmStsConditionVM stsCondition;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (configId ^ (configId >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GsmStsConfigVM other = (GsmStsConfigVM) obj;
        if (configId != other.configId)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "StsConfig [configId=" + configId + ", isFromQuery="
                + isFromQuery + ", isSelected=" + isSelected
                + ", stsAnaItemDetail=" + stsAnaItemDetail + ", stsCondition="
                + stsCondition + "]";
    }

}
