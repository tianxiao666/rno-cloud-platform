package com.hgicreate.rno.web.rest.gsm.vm;

import lombok.Data;

@Data
public class GsmFrequencyReuseInfoVM {
	private Integer freq;//分析的频点
	private Integer bcchCount;//该频点被BCCH使用的次数
	private Integer tchCount;//该频点被TCH使用的次数

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GsmFrequencyReuseInfoVM other = (GsmFrequencyReuseInfoVM) obj;
		if (freq != other.freq)
			return false;
		return true;
	}
	

}
