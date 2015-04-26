package com.Employee.Entity;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Invoice")
public class Invoice {
	@Id
	@Column
	//@GeneratedValue(strategy=GenerationType.AUTO)
	int invoiceNum;
	@Column
	Date InvDT;
	@Column
	String printReqInd;
	@Column
	private byte[] billdoc;
	public int getInvoiceNumber() {
		return invoiceNum;
	}
	public void setInvoiceNumber(int invoiceNumber) {
		this.invoiceNum = invoiceNumber;
	}
	public Date getInvDT() {
		return InvDT;
	}
	public void setInvDT(Date invDT) {
		InvDT = invDT;
	}
	public String getPrintReqInd() {
		return printReqInd;
	}
	public void setPrintReqInd(String printReqInd) {
		this.printReqInd = printReqInd;
	}
	public byte[] getBilldoc() {
		return billdoc;
	}
	public void setBilldoc(byte[] billdoc) {
		this.billdoc = billdoc;
	}
	
	

}
