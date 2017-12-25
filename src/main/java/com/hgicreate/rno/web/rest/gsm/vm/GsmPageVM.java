package com.hgicreate.rno.web.rest.gsm.vm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GsmPageVM {

	private int totalPageCnt = 0;// 总页数
	private int pageSize = 25;// 每页记录数
	private int currentPage = 1;// 当前页数，从1开始
	private int totalCnt = -1;// 总记录数
	private int forcedStartIndex = -1;// 如果这个值大于0，说明直接使用这个值，而不是使用currentPage*pageSize这样计算得到

	public int calculateStart(){
		if(forcedStartIndex>0){
			return forcedStartIndex;
		}else{
			return (this.getCurrentPage() - 1) * this.getPageSize();
		}
	}
}
