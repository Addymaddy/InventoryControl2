package com.Employee.Entity;

public interface Inventory {

	public String getMaterialcode();

	public void setMaterialcode(String materialcode) ;

	public String getDescription();

	public void setDescription(String description);

	public double getPrice();
	public void setPrice(double  price);

	public String getRemarks() ;

	public void setRemarks(String remarks);
	public int getQuantity();

	public void setQuantity(int quantity);
	public int getMinQunatity();

	public void setMinQunatity(int minQunatity);

}
