package com.hgicreate.rno.web.rest.gsm.vm;

import lombok.Data;

import java.io.Serializable;
@Data
public class GsmGisCellVM implements Serializable{

	private String cell;
	private String chineseName;
	private Double lng;// 经度
	private Double lat;// 纬度
	private String freqType;// gsm900，gsm1800，td
	private Float azimuth;// 方向角
	private String allLngLats;//显示所需要的所有经纬度坐标:经度+","+纬度+","+下一个
	
	private String site;
	private Long lac;
	private Long ci;
	private Long bcch;
	private String tch;

	@Override
	public String toString() {
		return "RnoGisCell [cell=" + cell + ", chineseName=" + chineseName
				+ ", lng=" + lng + ", lat=" + lat + ", freqType=" + freqType
				+ ", azimuth=" + azimuth + ", allLngLats=" + allLngLats
				+ ", site=" + site + ", lac=" + lac + ", ci=" + ci + ", bcch="
				+ bcch + ", tch=" + tch + "]";
	}




	

}
