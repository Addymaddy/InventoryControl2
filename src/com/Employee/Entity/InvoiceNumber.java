package com.Employee.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="InvoiceNumber")
public class InvoiceNumber {
	@Id
	@Column
	//@GeneratedValue(strategy=GenerationType.AUTO)
	int invoiceNumberSeq;
	
	public InvoiceNumber(){
		
	}

	public int getInvoiceNumberSeq() {
		return invoiceNumberSeq;
	}

	public void setInvoiceNumberSeq(int invoiceNumberSeq) {
		this.invoiceNumberSeq = invoiceNumberSeq;
	}
}
