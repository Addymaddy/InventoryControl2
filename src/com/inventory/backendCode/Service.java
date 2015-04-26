package com.inventory.backendCode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JOptionPane;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;

import com.Employee.Entity.Inventory;
import com.Employee.Entity.Invoice;
import com.Employee.Entity.InvoiceNumber;
import com.Employee.Entity.SoldItemNotification;
import com.Employee.Entity.inventoryA;
import com.Employee.Entity.inventoryB;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;

public class Service{
	Configuration cfg;
	SessionFactory sf;
	
	
	public Service(){
		/*Configuration cfg = new Configuration();
		cfg.configure("Hibernate.cfg.xml");*/
		URL url = Thread.currentThread().getContextClassLoader().getResource("resources/hibernate.cfg.xml"); 
		Configuration cfg = new Configuration();
		//cfg.configure("hibernate.cfg.xml");
		cfg.configure(url);
		sf = cfg.buildSessionFactory();
		
	}
	






public inventoryA getProductByItemCodeFromInventoryA(String materialCode){
	
	Session s = sf.openSession();
	Transaction tx = s.beginTransaction();
	

	String hql=null;
		hql = "FROM inventoryA A WHERE A.materialcode = :materialCode";
	
	Query query = s.createQuery(hql);
	query.setParameter("materialCode",materialCode);
	List results = query.list();
	s.flush();
	tx.commit();
	s.close();
	if(results.size()!=0)
		return (inventoryA) results.get(0);
	
	return null;
	
}

public inventoryB getProductByItemCodeFromInventoryB(String materialCode){
	
	Session s = sf.openSession();
	Transaction tx = s.beginTransaction();
	
	String hql=null;
		hql = "FROM inventoryB A WHERE A.materialcode = :materialCode";
	
	Query query = s.createQuery(hql);
	query.setParameter("materialCode",materialCode);
	List results = query.list();
	s.flush();
	tx.commit();
	s.close();
	if(results.size()!=0)
	return (inventoryB)results.get(0);
	return null;
}



public int getInvoiceNumber(){
	Session s =sf.openSession();
	Transaction tx=s.beginTransaction();
	String hql="FROM InvoiceNumber";
	Query query = s.createQuery(hql);
	List results = query.list();
	
	
	InvoiceNumber invNum=(InvoiceNumber)results.get(0);
	int invSeq=invNum.getInvoiceNumberSeq();
	
	hql="UPDATE InvoiceNumber set invoiceNumberSeq = :seq";
	query=s.createQuery(hql);
	query.setParameter("seq", invSeq+1);
	int rowsUpdated = query.executeUpdate();

	s.flush();
	tx.commit();
	s.close();
	
	return invSeq;
}


public void savePDF(String filename,String invNumber,String billPrintRequired){
	Session session = sf.openSession();
	Transaction tx=session.beginTransaction();
	     
	File file = new File(filename);
	byte[] PDFData = new byte[(int) file.length()];
	 
	try {
	    FileInputStream fileInputStream = new FileInputStream(file);
	    fileInputStream.read(PDFData);
	    fileInputStream.close();
	} catch (Exception e) {
	    e.printStackTrace();
	}
	

	 
	  Calendar cal = Calendar.getInstance(); 
	  Date curDate=cal.getTime();
	  java.sql.Date sqlCurDate=new java.sql.Date(curDate.getTime());

	
	Invoice invoice = new Invoice();
	invoice.setInvoiceNumber(Integer.parseInt(invNumber));
	invoice.setInvDT(sqlCurDate);
	invoice.setPrintReqInd(billPrintRequired);
	invoice.setBilldoc(PDFData);
	 
	session.save(invoice);    //Save the data
	 
	
	session.flush();
	tx.commit();
	session.close();
}

public boolean retrievePDF(String filename,String date) {
	Session session = sf.openSession();
	Transaction tx=session.beginTransaction();
	
	DateFormat format=new SimpleDateFormat("MM/dd/yyyy");
	Date invDate=null;
	java.sql.Date sqlDate=null;
	try {
		invDate = format.parse(date);
		sqlDate=new java.sql.Date(invDate.getTime());
		System.out.println("Invoice Date is --->"+sqlDate);

	} catch (ParseException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	String hql="from Invoice where InvDT like :invDT";
	Query query=session.createQuery(hql);
	query.setParameter("invDT", sqlDate);
	
	List<Invoice> result=query.list();
	
	boolean append=false;
	
	System.out.println("result size is ---->"+result.size());
	FileOutputStream fos=null;

	if(result.size()==0){
		JOptionPane.showMessageDialog(null, "No Bills On Selected Date");
		return false;
	}
	
	try{
		  Document document = new Document();
	        // step 2
	        PdfCopy copy = new PdfCopy(document, new FileOutputStream(filename));
	        // step 3
	        document.open();
	        // step 4
	        PdfReader reader;
	        int n;
	        // loop over the documents you want to concatenate
	        for (int i = 0; i < result.size(); i++) {
	        	Invoice invoice=result.get(i);
	        	byte[] bill = invoice.getBilldoc();
	            reader = new PdfReader(bill);
	            // loop over the pages in that document
	            n = reader.getNumberOfPages();
	            for (int page = 0; page < n; ) {
	                copy.addPage(copy.getImportedPage(reader, ++page));
	            }
	            copy.freeReader(reader);
	            reader.close();
	        }
	        // step 5
	        document.close();
	}
	catch(Exception e){
		System.out.println("Exception Occured");
	}
	
	
	tx.commit();
	session.close();
	
	return true;
	}

	
public List<Inventory> getDepletingItems(){
	Session s = sf.openSession();
	Transaction tx = s.beginTransaction();
	
	
	
	String hql = "FROM inventoryA P WHERE P.quantity <= P.minQuantity";
	Query query = s.createQuery(hql);
	List<inventoryA> resultsA = query.list();
	
	String hqlB="FROM inventoryB P WHERE P.quantity <= P.minQuantity";
	Query queryB=s.createQuery(hqlB);
	List<inventoryB> resultsB=queryB.list();
	
	List<Inventory> results=new ArrayList<Inventory>();
	results.addAll(resultsA);
	results.addAll(resultsB);
	
	
	s.flush();
	tx.commit();
	s.close();
	return results;
}






public void updateInventoryItemForPurchase(Inventory inventoryProduct, String purchaseMode){
	Session s =sf.openSession();
	Transaction tx=s.beginTransaction();
	s.saveOrUpdate(inventoryProduct);
	
	s.flush();
	tx.commit();
	s.close();
	
	JOptionPane.showMessageDialog(null, "Succesfully updated !");
}








public void saveTable_Test(inventoryA t){
	Session session=sf.openSession();
	Transaction tx=session.beginTransaction();
	session.save(t);
	session.flush();
	tx.commit();
	session.close();
}


public void updatePurchasedOrder(File file, String inventory){
	FileInputStream input_document;
	Session session=sf.openSession();
	Transaction tx;
	inventoryA tst;
	 String model=null;
     int quantity;
     double price;
     short count;
     int minquant;
	try {
		input_document = new FileInputStream(file);
		 /* Load workbook */
        HSSFWorkbook my_xls_workbook = new HSSFWorkbook(input_document);
        /* Load worksheet */
        HSSFSheet my_worksheet = my_xls_workbook.getSheetAt(0);
        // we loop through and insert data
        Iterator<HSSFRow> rowIterator =my_worksheet.rowIterator();
        
       
        while(rowIterator.hasNext()){
        	
        	HSSFRow row=rowIterator.next();
        	//Iterator cellitr=row.cellIterator();
        	
        	 count=0;
        	//Creating an object to insert
        	HSSFCell mycell=row.getCell(count);
        	if(mycell.getCellType()==mycell.CELL_TYPE_STRING)
        		//System.out.println("Cell Value is --->"+mycell.getStringCellValue());
        		model=mycell.getStringCellValue();
        		else if(mycell.getCellType()==mycell.CELL_TYPE_NUMERIC)
        			//System.out.println("Cell Numeric Value is --->"+mycell.getNumericCellValue());
        		{
        			int code=(int)mycell.getNumericCellValue();
        			model=Integer.toString(code);
        		}
        	mycell=row.getCell(++count);
        	price= mycell.getNumericCellValue();
        	mycell=row.getCell(++count);
        	quantity=(int) mycell.getNumericCellValue();
        	mycell=row.getCell(++count);
        	if(mycell==null)
        		minquant=0;
        	else
        		minquant=(int) mycell.getNumericCellValue();
        	
        	if(inventory.equals("A")){
        	tst = (inventoryA) session.get(inventoryA.class, model.trim());
        	
        	String materialcode=tst.getMaterialcode();
        	String desc=tst.getDescription();
        	double price3=tst.getPrice();
        	int quant=tst.getQuantity();
        	
        	
        		
        	tst.setPrice(price);
        	tst.setQuantity(tst.getQuantity()+quantity);
        	
        	
        	 materialcode=tst.getMaterialcode();
        	 desc=tst.getDescription();
        	 price3=tst.getPrice();
        	 quant=tst.getQuantity();
        	
        	tx=session.beginTransaction();
        	
        	session.update(tst);
        	session.flush();
        	tx.commit();
        	}
        	else if(inventory.equals("B")){
        		inventoryB object=(inventoryB) session.get(inventoryB.class, model.trim());
        		int existingQuantity=object.getQuantity();
        		object.setPrice(price);
        		object.setQuantity(existingQuantity+quantity);//update the qunatity properly
        		
        		object.setMinQunatity(minquant);
        		tx=session.beginTransaction();
        		session.update(object);
        		session.flush();
            	tx.commit();
        		
        	}
        	
        	
        	
        }
        
        session.close();
        
        JOptionPane.showMessageDialog(null, "Inventory Updated Successfully");
}
	catch(Exception e){
        	JOptionPane.showMessageDialog(null, "ERROR : Unable to Update the Inventory");
			e.printStackTrace();
			
	}
	
	
}



public void updateInventory(Map<inventoryA,Integer> billA, Map<inventoryB,Integer> billB){
	Session session=sf.openSession();
	Transaction tx=session.beginTransaction();
	
	String materialCode;
	int quantity;
	String hql = "UPDATE inventoryA set quantity = :quantity "  + 
            "WHERE materialcode = :materialcode";
	Query query = session.createQuery(hql);
	
	Iterator itr=billA.entrySet().iterator();
	while (itr.hasNext()){
		Map.Entry<inventoryA, Integer> pair=(Entry<inventoryA, Integer>) itr.next();
		materialCode=pair.getKey().getMaterialcode();
		quantity=pair.getValue();
		int itemCurrentQuantity=pair.getKey().getQuantity();
		query.setParameter("quantity", itemCurrentQuantity-quantity);
		query.setParameter("materialcode", materialCode);
		int result = query.executeUpdate();
		System.out.println("Updated row----> "+ result );
		
	}

	
	//insert Call to another fucntion to update the table which maintains the white good sold
	
	
	//updating inventoryB

	System.out.println("BillB size is "+billB.size());
	
	 hql = "UPDATE inventoryB set quantity = :quantity "  + 
            "WHERE materialcode = :materialcode";
	 query = session.createQuery(hql);
	
	Iterator itrB=billB.entrySet().iterator();
	while (itrB.hasNext()){
		Map.Entry<inventoryB, Integer> pair=(Entry<inventoryB, Integer>) itrB.next();
		inventoryB temp=pair.getKey();
		materialCode=pair.getKey().getMaterialcode();
		quantity=pair.getValue();
		int itemCurrentQuantity=pair.getKey().getQuantity();
		query.setParameter("quantity", itemCurrentQuantity-quantity);
		query.setParameter("materialcode", materialCode);
		int result = query.executeUpdate();
		
		System.out.println("result is --->"+result);
		
	}
	insertSoldWhiteItems(billA);
	
	session.flush();
	tx.commit();
	session.close();
	
	
	
}



public void insertSoldWhiteItems(Map<inventoryA,Integer> billA){
	String materialCode;
	String description;
	double price_sold;
	int quantity_sold;
	
	SoldItemNotification soldItemNotification=new SoldItemNotification();
	
	Session session=sf.openSession();
	Transaction tx=session.beginTransaction();
	Iterator itr=billA.entrySet().iterator();
	
	//Getting the Current date to Set in the DB
	 Calendar cal = Calendar.getInstance(); 
	  Date curDate=cal.getTime();
	  java.sql.Date sqlCurDate=new java.sql.Date(curDate.getTime());

	
	while(itr.hasNext()){
		Map.Entry<inventoryA, Integer> pair=(Entry<inventoryA, Integer>) itr.next();
		inventoryA inventoryItem=pair.getKey();
		quantity_sold=pair.getValue();
		soldItemNotification.setMaterialcode(inventoryItem.getMaterialcode());
		soldItemNotification.setDescription(inventoryItem.getDescription());
		soldItemNotification.setPrice_sold(inventoryItem.getPrice());
		soldItemNotification.setQuantity_sold(quantity_sold);
		soldItemNotification.setSold_date(sqlCurDate);
		session.save(soldItemNotification);
	}
	
	session.flush();
	tx.commit();
	session.close();

}


public List<SoldItemNotification> getInventoryASoldItems(String date) {
	Session session=sf.openSession();
	Transaction tx=session.beginTransaction();
	
	
	DateFormat format=new SimpleDateFormat("MM/dd/yyyy");
	Date soldDate=null;
	java.sql.Date sqlDate=null;
	try {
		soldDate = format.parse(date);
		sqlDate=new java.sql.Date(soldDate.getTime());
		System.out.println("Invoice Date is --->"+sqlDate);

	} catch (ParseException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	
	
	
	String hql = "FROM SoldItemNotification where sold_date like :soldDate";
	Query query = session.createQuery(hql);
	query.setParameter("soldDate", sqlDate);
	List<SoldItemNotification> results = query.list();
	
	for(SoldItemNotification item:results){
		System.out.println(item.getMaterialcode());
		System.out.println(item.getDescription());
		System.out.println(item.getPrice_sold());
		System.out.println(item.getQuantity_sold());

	}
	session.flush();
	tx.commit();
	session.close();
	return results;
}

public List<Inventory> getItemByDescription(String inventory,String description){
	Session s = sf.openSession();
	Transaction tx = s.beginTransaction();
	

	String hql=null;
	if(inventory.equals("A")){
		
		hql = "FROM inventoryA A WHERE A.description like :description";
	}
	else if(inventory.equals("B"))
	{
		hql = "FROM inventoryB B WHERE B.description like :description";
	}
	Query query = s.createQuery(hql);
	query.setParameter("description","%" + description + "%");
	List<Inventory> results = query.list();
	s.flush();
	tx.commit();
	s.close();
	if(results.size()!=0)
		return results;
	
	return null;
	
}


public void deleteInvoicebyDate(String date) {
	Session session = sf.openSession();
	Transaction tx=session.beginTransaction();
	
	DateFormat format=new SimpleDateFormat("MM/dd/yyyy");
	Date invDate=null;
	java.sql.Date sqlDate=null;
	try {
		invDate = format.parse(date);
		sqlDate=new java.sql.Date(invDate.getTime());
		System.out.println("Invoice Date is --->"+invDate);

	} catch (ParseException e1) {
		e1.printStackTrace();
	}
	String hql="Delete Invoice where InvDT like :invDT";
	Query query=session.createQuery(hql);
	query.setParameter("invDT", sqlDate);
	int count=query.executeUpdate();
	
	session.flush();
	tx.commit();
	session.close();
	
	if(count!=0){
		JOptionPane.showMessageDialog(null, "Deleted Succesfully");
	}
	else 
		JOptionPane.showMessageDialog(null, "No Items to Delete");
	
	
	
}


public void deleteSoldByDate(String date) {
	Session session = sf.openSession();
	Transaction tx=session.beginTransaction();
	
	DateFormat format=new SimpleDateFormat("MM/dd/yyyy");
	Date soldDate=null;
	java.sql.Date sqlDate=null;
	try {
		soldDate = format.parse(date);
		sqlDate=new java.sql.Date(soldDate.getTime());
		System.out.println("Invoice Date is --->"+soldDate);

	} catch (ParseException e1) {
		e1.printStackTrace();
	}
	String hql="Delete SoldItemNotification where sold_date like :soldDate";
	Query query=session.createQuery(hql);
	query.setParameter("soldDate", sqlDate);
	int count=query.executeUpdate();
	
	session.flush();
	tx.commit();
	session.close();
	
	if(count!=0){
		JOptionPane.showMessageDialog(null, "Deleted Succesfully");
	}
	else 
		JOptionPane.showMessageDialog(null, "No Items to Delete");
	
	
}

}





