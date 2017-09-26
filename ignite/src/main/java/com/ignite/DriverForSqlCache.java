package com.ignite;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

public class DriverForSqlCache {

	@QuerySqlField(index = true)
	private String ownCode;
	@QuerySqlField
	private String jobNumber;
	@QuerySqlField
	private String busTimeTable;
	@QuerySqlField
	private String runState;
	@QuerySqlField
	private String XM;
	@QuerySqlField
	private String CKRYIDO;

	public String getOwnCode() {
		return ownCode;
	}

	public void setOwnCode(String ownCode) {
		this.ownCode = ownCode;
	}

	public String getJobNumber() {
		return jobNumber;
	}

	public void setJobNumber(String jobNumber) {
		this.jobNumber = jobNumber;
	}

	public String getBusTimeTable() {
		return busTimeTable;
	}

	public void setBusTimeTable(String busTimeTable) {
		this.busTimeTable = busTimeTable;
	}

	public String getRunState() {
		return runState;
	}

	public void setRunState(String runState) {
		this.runState = runState;
	}

	public String getXM() {
		return XM;
	}

	public void setXM(String XM) {
		this.XM = XM;
	}

	public String getCKRYIDO() {
		return CKRYIDO;
	}

	public void setCKRYIDO(String CKRYIDO) {
		this.CKRYIDO = CKRYIDO;
	}

	public DriverForSqlCache() {
		super();
	}

	public DriverForSqlCache(String ownCode, String jobNumber,
			String busTimeTable, String runState, String XM, String CKRYIDO) {
		super();
		this.ownCode = ownCode;
		this.jobNumber = jobNumber;
		this.busTimeTable = busTimeTable;
		this.runState = runState;
		this.XM = XM;
		this.CKRYIDO = CKRYIDO;
	}

}
