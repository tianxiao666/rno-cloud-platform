package com.hgicreate.rno.web.rest.gsm.vm;

import lombok.Data;

import java.util.List;
@Data
public class GsmGisCellQueryResultVM {

	transient private int totalCnt;
	private List<GsmGisCellVM> gisCells;
	private GsmPageVM page;

}
