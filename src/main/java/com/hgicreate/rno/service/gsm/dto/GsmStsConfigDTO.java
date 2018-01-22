package com.hgicreate.rno.service.gsm.dto;

import com.hgicreate.rno.web.rest.gsm.vm.GsmStsAnaItemDetailVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmStsConditionVM;
import lombok.Data;

import java.io.Serializable;

/**
 * 分析列表
 *
 * @author brightming
 */
@Data
public class GsmStsConfigDTO implements Serializable {

    private long configId;
    private boolean fromQuery;
    private boolean selected;
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
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        GsmStsConfigDTO other = (GsmStsConfigDTO) obj;

        return (configId == other.configId);
    }

    @Override
    public String toString() {
        return "StsConfig [configId=" + configId + ", isFromQuery="
                + fromQuery + ", isSelected=" + selected
                + ", stsAnaItemDetail=" + stsAnaItemDetail + ", stsCondition="
                + stsCondition + "]";
    }

}
