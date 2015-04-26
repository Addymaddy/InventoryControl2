package com.Employee.Entity;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="sold_Item_Notification")
public class SoldItemNotification {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO, generator="my_seq_gen")
	@SequenceGenerator(name="my_seq_gen", sequenceName="inventory_sold_seq")
	int Id;

	@Column
	String materialcode;
	@Column
	String description;
	@Column
	double price_sold;
	@Column
	int quantity_sold;
	@Column
	Date sold_date;
	

	public String getMaterialcode() {
		return materialcode;
	}
	public void setMaterialcode(String materialcode) {
		this.materialcode = materialcode;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public double getPrice_sold() {
		return price_sold;
	}
	public void setPrice_sold(double price_sold) {
		this.price_sold = price_sold;
	}
	public int getQuantity_sold() {
		return quantity_sold;
	}
	public void setQuantity_sold(int quantity_sold) {
		this.quantity_sold = quantity_sold;
	}

	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}

	public Date getSold_date() {
		return sold_date;
	}
	public void setSold_date(Date sold_date) {
		this.sold_date = sold_date;
	}
}
