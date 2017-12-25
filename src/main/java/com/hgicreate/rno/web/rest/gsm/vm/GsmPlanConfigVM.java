package com.hgicreate.rno.web.rest.gsm.vm;

import lombok.Data;

@Data
public class GsmPlanConfigVM {

	private String configId;
	private boolean isTemp;
	private boolean isSelected;
	private String type;

	private String title;
	private String name;
	private String collectTime;
	private String areaName;
	
	private Object obj;


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GsmPlanConfigVM other = (GsmPlanConfigVM) obj;
		if (configId != other.configId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PlanConfig [configId=" + configId + ", isTemp=" + isTemp
				+ ", isSelected=" + isSelected + ", type=" + type + ", title="
				+ title + ", name=" + name + ", collectTime=" + collectTime
				+ ", areaName=" + areaName + "]";
	}

}
