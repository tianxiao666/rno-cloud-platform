package com.hgicreate.rno.service.gsm.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class GsmStsResultDTO implements Serializable{

	private String cell;
	private Float maxValue;
	private Float avgValue;
	private Float minValue;
	private Integer cnt;
	private String code;

	@Override
	public String toString() {
		return "RnoStsResult [cell=" + cell + ", maxValue=" + maxValue
				+ ", avgValue=" + avgValue + ", minValue=" + minValue
				+ ", cnt=" + cnt + ", code=" + code + "]";
	}

}
