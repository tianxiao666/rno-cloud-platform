package com.hgicreate.rno.web.rest.gsm.vm;

import lombok.Data;

import java.util.Date;
@Data

public class GsmStsConditionVM {
	private Date stsDate;
	private String stsPeriod;
	private Long areaId;
	private String engName;
	private String cell;
	private String cellChineseName;
	private Date staticTime;
	private String grade;
	
	private Long cityId;
	private Date beginTime;
	private Date latestAllowedTime;
	private String stsDateStr;
	private String stsType;
	

	public String toString() {
		return "areaId=" + areaId + ",stsDate=" + stsDate + ",stsPeriod=" + stsPeriod
				+ ",areaId=" + areaId + ",engName=" + engName + ",cell="
				+ cell + ",cellChineseName=" + cellChineseName+ ",grade=" + grade+ ",staticTime=" + staticTime+ ",cityId=" + cityId+ ",beginTime=" + beginTime+ ",latestAllowedTime=" + latestAllowedTime;
	}
}
