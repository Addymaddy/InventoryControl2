package com.Employee.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="inventoryA")
public class inventoryA implements Inventory{
	@Id
	@Column
String materialcode;
	

	@Column
String description;
@Column
double price;
@Column
String remarks;
@Column
int quantity;
@Column
int minQuantity;





public inventoryA(){
	
}

public inventoryA(String materialCode, String Description ,int price,String remarks){
	this.materialcode=materialCode;
	this.description=Description;
	this.price=price;
	this.remarks=remarks;
		
}

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

public double getPrice() {
	return price;
}

public void setPrice(double  price) {
	this.price = price;
}

public String getRemarks() {
	return remarks;
}

public void setRemarks(String remarks) {
	this.remarks = remarks;
}

public int getQuantity() {
	return quantity;
}

public void setQuantity(int quantity) {
	this.quantity = quantity;
}

public int getMinQunatity() {
	return minQuantity;
}

public void setMinQunatity(int minQunatity) {
	this.minQuantity = minQunatity;
}



}
