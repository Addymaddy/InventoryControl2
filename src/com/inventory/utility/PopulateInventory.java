package com.inventory.utility;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.Employee.Entity.inventoryA;
import com.inventory.backendCode.Service;

public class PopulateInventory {

	public static void main(String args[]){
		Service service=new Service();
		/*try {
			BufferedReader br =new BufferedReader(new FileReader("price_list.csv"));
			String newline=null;
			//Service service=new Service();
			newline=br.readLine();
			while((newline=br.readLine())!=null){
				String token[]=newline.split(",");
				
				//token[3].format("%2f", args)
				
				//System.out.println("Converting the Value to Double----->"+Double.parseDouble(token[3]));
				//System.out.println(Integer.parseInt(token[3]));
				
				//service.saveTable_Test(t);
				
				System.out.println("serial no is ---->"+token[0]);
				System.out.println("Material code is --->"+token[1]);
				System.out.println("Description is --->"+token[2]);
				System.out.println("MRP is ---->"+token[3]);
				//System.out.println("Remarks is --->"+token[4]);
				
			}*/
			
			
			FileInputStream input_document;
			try {
				input_document = new FileInputStream(new File("price_list3.xls"));
				 /* Load workbook */
	            HSSFWorkbook my_xls_workbook = new HSSFWorkbook(input_document);
	            /* Load worksheet */
	            HSSFSheet my_worksheet = my_xls_workbook.getSheetAt(0);
	            // we loop through and insert data
	            Iterator<HSSFRow> rowIterator = my_worksheet.rowIterator(); 
	            
	            
	            
	            
	            rowIterator.next();
	         short count;
	         inventoryA tst=new inventoryA();
	            while(rowIterator.hasNext()){
	            	HSSFRow row=rowIterator.next();
	            	//Iterator cellitr=row.cellIterator();
	            	
	            	 count=1;
	            	//Creating an object to insert
	            	HSSFCell mycell=row.getCell(count);
	            	if(mycell.getCellType()==mycell.CELL_TYPE_STRING)
	            		//System.out.println("Cell Value is --->"+mycell.getStringCellValue());
	            		tst.setMaterialcode(mycell.getStringCellValue());
	            		else if(mycell.getCellType()==mycell.CELL_TYPE_NUMERIC)
	            			//System.out.println("Cell Numeric Value is --->"+mycell.getNumericCellValue());
	            		{
	            			int code=(int)mycell.getNumericCellValue();
	            			tst.setMaterialcode(Integer.toString(code));
	            		}
	            	
	            	mycell=row.getCell(++count);
	            	if(mycell.getCellType()==mycell.CELL_TYPE_STRING)
	            		//System.out.println("Cell Value is --->"+mycell.getStringCellValue());
	            		tst.setDescription(mycell.getStringCellValue().trim());
	            	
	            	
	            	mycell=row.getCell(++count);

	            	if(mycell.getCellType()==mycell.CELL_TYPE_STRING)
	            		System.out.println("Cell Value is --->"+mycell.getStringCellValue());
	            		else if(mycell.getCellType()==mycell.CELL_TYPE_NUMERIC)
	            			//System.out.println("Cell Numeric Value is --->"+mycell.getNumericCellValue());
	            		{
	            			tst.setPrice(mycell.getNumericCellValue());
	            		}
	            	
	            	mycell=row.getCell(++count);
	            	if(mycell==null)
	            		//set the remark column as ""
	            		tst.setRemarks("");
	            	else {
	            	if(mycell.getCellType()==mycell.CELL_TYPE_STRING)
	            		tst.setRemarks(mycell.getStringCellValue());
	            		else if(mycell.getCellType()==mycell.CELL_TYPE_NUMERIC)
	            			//System.out.println("Cell Numeric Value is --->"+mycell.getNumericCellValue());
	            			tst.setRemarks(Double.toString(mycell.getNumericCellValue()));
	            	}
	            	
	            
	            	
	            	
	            	service.saveTable_Test(tst);
	            	
	            	
	            	
	            	
	            	
	            	
	        /*    	while(cellitr.hasNext()){
	            		HSSFCell mycell=(HSSFCell) cellitr.next();
	            		
	            		if(mycell.getCellType()==mycell.CELL_TYPE_STRING && mycell.getStringCellValue().equals(""))
	            			break;
	            		if(mycell.getCellType()==mycell.CELL_TYPE_STRING)
	            		System.out.println("Cell Value is --->"+mycell.getStringCellValue());
	            		else if(mycell.getCellType()==mycell.CELL_TYPE_NUMERIC)
	            			System.out.println("Cell Numeric Value is --->"+mycell.getNumericCellValue());
	            	}*/
	            }
	            
	            
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
           
            
            
			
			
			
			
			
		
		

	}
}
