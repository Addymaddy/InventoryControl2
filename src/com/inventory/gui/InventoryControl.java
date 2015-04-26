package com.inventory.gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.apache.poi.hssf.model.Model;
import org.jdesktop.swingx.JXDatePicker;

import com.Employee.Entity.Inventory;
import com.Employee.Entity.inventoryA;
import com.Employee.Entity.inventoryB;
import com.inventory.backendCode.InvoiceGenerator;
import com.inventory.backendCode.Service;

import java.awt.event.InputMethodListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.Toolkit;
import javax.swing.ImageIcon;

public class InventoryControl {
	
	//Constants :
	private static final String LABOUR_CODE="LBR";
	private static final double VAT_DEFAULT_PERCENT=12.5;
	
	
	private JXDatePicker picker;
	private JXDatePicker retSoldPicker;
	private JFrame frmInventoryControl;
	private JTextField txtItemCode;
	private JTextField txtDescription;
	private JTextField txtPrice;
	private JLabel lblMessage;
	private JTable table;
	private Service serv;
	private JTextField txtQuantity;

	//Bill for inventoryA
	private Map<inventoryA,Integer> billA;
	//Bill for inventoryB
	private Map<inventoryB,Integer> billB;
	//Tax for inventoryA
	private Map<String,Double> taxA;
	//Tax for inventoryB
	private Map<String,Double> taxB;
	//labour
	LabourCharge labour=null;

	
	
	
	private inventoryA searchedProductA;
	private inventoryB searchedProductB;
	private InvoiceGenerator invoiceService;
	private JTextField txtVatDisplay;
	private JTextField txtTotalDisplay;
	private String billRequired;
	private JTextField txtServiceCharge;
	private JButton btnPrint;
	private JButton btnSave;
	private JTextField txtItemCode_P;
	private JTextField txtDescription_P;
	private JTextField txtQuantity_P;
	private JTextField txtPrice_P;
	private JTextField txtMinQuantity_P;
	
	private JButton btnAdd_Purchase;
	private JButton  btnUpdate_Purchase;
	
	private JRadioButton rdbtnA;
	private JRadioButton rdbtnB;
	private JTextField txtFile;
	private JRadioButton rdbtnA_Sell;
	private JRadioButton rdbtnB_Sell;
	private JTextField txtAvlblQuant;
	private JTextField txtTax;

	private JComboBox cmbDesc;
	
	private List<Inventory> result;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					InventoryControl window = new InventoryControl();
					window.frmInventoryControl.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public InventoryControl() {
		initialize();
		Thread splashThread=new SplashScreenThread();
		splashThread.start();
		 
		serv=new Service();
		splashThread.stop(); 
		 
		 invoiceService=new InvoiceGenerator();
		 billA=new HashMap<inventoryA,Integer>();
		 billB=new HashMap<inventoryB,Integer>();
		 taxA=new HashMap<String,Double>();
		 taxB=new HashMap<String,Double>();
	}

	
	private void calculate(Map<inventoryA,Integer> billA,Map<inventoryB,Integer> billB,Map<String,Double> taxA,Map<String,Double> taxB){
		System.out.println("Number of elements in BillA is-->"+billA.size());
		System.out.println("Number of Elements in BillB is --->"+billB.size());
		
		System.out.println("Value of taxA-->"+taxA.size());
		System.out.println("Value of taxB is --->"+taxB.size());
		
		
		Iterator itA=billA.entrySet().iterator();
		Iterator itB=billB.entrySet().iterator();
		double total=0;
		double vatA=0;
		double vatB=0;
			while(itA.hasNext()){
				Map.Entry<inventoryA, Integer> pair=(Map.Entry)itA.next();
				double price=pair.getKey().getPrice();
				Integer quant=pair.getValue();
				total+=price*quant;
				double vatpercent=taxA.get(pair.getKey().getMaterialcode());
				double temp=(vatpercent/100)*(price*quant);
				vatA+=temp;
				
			}
			while(itB.hasNext()){
				Map.Entry<inventoryB, Integer> pair=(Map.Entry)itB.next();
				double price=pair.getKey().getPrice();
				Integer quant=pair.getValue();
				total+=price*quant;
				double vatpercent=taxB.get(pair.getKey().getMaterialcode());
				double temp=(vatpercent/100)*(price*quant);
				vatB+=temp;
			}
			
			if(labour!=null){
				double temp=labour.getCharge()*(labour.getServiceTax()/100);
				txtServiceCharge.setText(Double.toString(temp));
				total+=(temp+labour.getCharge());
			}
			
			double Vat=vatA+vatB;
			double GrandTotal=total+Vat;
			//display the total and VAT
			DecimalFormat df=new DecimalFormat("#.00");
			
			txtVatDisplay.setText(df.format(Vat));
			txtTotalDisplay.setText(df.format(GrandTotal));
			
			
			
			
	}
	

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmInventoryControl = new JFrame();
		frmInventoryControl.setIconImage(Toolkit.getDefaultToolkit().getImage("C:\\Users\\ADMIN\\Downloads\\SNAP dwnlds\\Bullet_Guru_icon.jpg"));
		frmInventoryControl.setTitle("Inventory Control");
		frmInventoryControl.setBounds(100, 100, 617, 613);
		frmInventoryControl.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frmInventoryControl.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		tabbedPane.setVisible(true);
		ButtonGroup group1=new ButtonGroup();
		
		
		
		 Object rowData[][] = { { "Row1-Column1", "Row1-Column2", "Row1-Column3" },
			        { "Row2-Column1", "Row2-Column2", "Row2-Column3" } };
			    Object columnNames[] = { "Column One", "Column Two", "Column Three" };
		
		JPanel Sell = new JPanel();
		tabbedPane.addTab("Sell", null, Sell, null);
		tabbedPane.setEnabledAt(0, true);
		Sell.setLayout(null);
		
		JLabel lblItemCode = new JLabel("Item Code");
		lblItemCode.setBounds(10, 4, 80, 14);
		Sell.add(lblItemCode);
		
		txtItemCode = new JTextField();
		txtItemCode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				searchItem();
			}
		});
		txtItemCode.setBounds(112, 1, 192, 20);
		Sell.add(txtItemCode);
		txtItemCode.setColumns(10);
		
		JLabel lblDescription = new JLabel("Description");
		lblDescription.setBounds(10, 35, 82, 14);
		Sell.add(lblDescription);
		
		txtDescription = new JTextField();
		txtDescription.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ComboBoxModel model=cmbDesc.getModel();
				cmbDesc.removeAllItems();
				System.out.println("Action performed");

				//System.out.println("Value of the Descripiton is ---->"+cmbDesc.getSelectedItem().toString().toUpperCase());
				if(rdbtnA_Sell.isSelected())
					result=serv.getItemByDescription("A", txtDescription.getText().toUpperCase());
				else if(rdbtnB_Sell.isSelected())
					result=serv.getItemByDescription("B", txtDescription.getText().toUpperCase());

				for(Inventory item: result){
					cmbDesc.addItem(item.getDescription());
				}

			}
		});
		txtDescription.setBounds(112, 32, 192, 20);
		Sell.add(txtDescription);
		txtDescription.setColumns(10);
		
		btnSave = new JButton("Save");
		btnSave.setBounds(99, 462, 91, 23);
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(billA.size()==0 && billB.size()==0)
				{
					lblMessage.setText("No Items To Bill");
					return;
				}
				calculate(billA,billB,taxA,taxB);
				//List<Product> unAvailableItems=serv.getUnavailableItems(Bill);
				/*if(unAvailableItems.size()!=0)
				{
					StringBuffer unavailableItemsString =new StringBuffer("Items Unavailable :     ");
					for(Product p:unAvailableItems)
						unavailableItemsString.append(p.getITEMCODE()).append(":").append(p.getQUNATITY()).append("  ");
					JOptionPane.showMessageDialog(null,unavailableItemsString.toString());
					return;
				}*/
					
				serv.updateInventory(billA, billB);
				
				if(!printBillPDF(billA,billB))
				{
					//enable print button 
					//disable Save button 
					if(!btnPrint.isEnabled())
						btnPrint.setEnabled(true);
					if(btnSave.isEnabled())
						btnSave.setEnabled(false);
					return;
				}
				
				
				clearAllControls_Sell();
			}
		});
		Sell.add(btnSave);
		btnSave.setVisible(true);
		
		JLabel lblPrice = new JLabel("Price");
		lblPrice.setBounds(10, 66, 67, 14);
		Sell.add(lblPrice);
		
		txtPrice = new JTextField();
		txtPrice.setBounds(112, 63, 192, 20);
		Sell.add(txtPrice);
		txtPrice.setColumns(10);
		
		JLabel lblQuantity = new JLabel("Avlbl Quantity");
		lblQuantity.setBounds(10, 94, 82, 14);
		Sell.add(lblQuantity);
		
		txtQuantity = new JTextField();
		txtQuantity.setBounds(254, 94, 52, 20);
		Sell.add(txtQuantity);
		txtQuantity.setColumns(10);
		
		JButton btnAdd = new JButton("Add");
		btnAdd.setBounds(427, 184, 67, 23);
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				lblMessage.setText("");
				DefaultTableModel model=(DefaultTableModel) table.getModel();
				//To add the Labour
				if(!txtItemCode.getText().equals("") &&  txtItemCode.getText().equals(LABOUR_CODE))
				{	labour=new LabourCharge();
					labour.setCharge(Double.parseDouble(txtPrice.getText()));
					labour.setServiceTax(Double.parseDouble(txtTax.getText()));
					model.addRow(new Object[]{"-",txtItemCode.getText(),txtDescription.getText(),txtPrice.getText(),txtQuantity.getText()});
					return;
				}
				
				
				if(!txtItemCode.getText().equals("")){
					if(txtQuantity.getText()==null || txtQuantity.getText().equals(""))
						lblMessage.setText("Please Enter the Valid Quantity");
					else{
						if(rdbtnA_Sell.isSelected()){
					model.addRow(new Object[]{"A",txtItemCode.getText(),txtDescription.getText(),txtPrice.getText(),txtQuantity.getText()});
					//Product searchedProduct=null;
					//Add the Item and Quantity to the Bill
					if(!txtPrice.getText().equals(Double.toString(searchedProductA.getPrice())))
							searchedProductA.setPrice(Double.parseDouble(txtPrice.getText()));
					billA.put(searchedProductA,Integer.parseInt(txtQuantity.getText()));
					taxA.put(searchedProductA.getMaterialcode(), Double.parseDouble(txtTax.getText().trim()));
						}
						else if(rdbtnB_Sell.isSelected())
						{
							model.addRow(new Object[]{"B",txtItemCode.getText(),txtDescription.getText(),txtPrice.getText(),txtQuantity.getText()});
							//Product searchedProduct=null;
							//Add the Item and Quantity to the Bill
							if(!txtPrice.getText().equals(Double.toString(searchedProductB.getPrice())))
								searchedProductB.setPrice(Double.parseDouble(txtPrice.getText()));
							billB.put(searchedProductB,Integer.parseInt(txtQuantity.getText()));
							taxB.put(searchedProductB.getMaterialcode(), Double.parseDouble(txtTax.getText().trim()));
						}
						
						clearAfterAdd();
					}
				}
				else 
					lblMessage.setText("Please Enter and Lookup the Item First");
				
			}
		});
		Sell.add(btnAdd);
		
		JButton btnRemove = new JButton("Remove");
		btnRemove.setBounds(504, 184, 80, 23);
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Inside Action Perfomed of Remove button");
				
				lblMessage.setText("");
				DefaultTableModel model=(DefaultTableModel) table.getModel();
				if(table.getSelectedRow()==-1){
					if(table.getRowCount()==0)
						lblMessage.setText("Table is Empty");
					else 
						lblMessage.setText("Please Select a Row First");
				}
				else {
					String purchaseMode=(String) model.getValueAt(table.getSelectedRow(), 0);
					String materialCodetmp=(String) model.getValueAt(table.getSelectedRow(), 1);
					System.out.println("Purchased mode is "+purchaseMode);
					if(purchaseMode.equals("A"))
					{
						model.removeRow(table.getSelectedRow());
						Set<inventoryA> keySet=billA.keySet();
						for(inventoryA p:keySet)
						{
							if(p.getMaterialcode().equals(materialCodetmp))
							{
								System.out.println("before remove the size of BillA is "+billA.size());
								billA.remove(p);
								System.out.println("After Remove the Size of Bill A is "+billA.size());
								taxA.remove(p.getMaterialcode());
								
								break;
							}

						}
					}
					else if(purchaseMode.equals("B"))
					{	
						model.removeRow(table.getSelectedRow());
						Set<inventoryB> keySet=billB.keySet();
						for(inventoryB p:keySet)
						{
							if(p.getMaterialcode().equals(materialCodetmp))
							{
								billB.remove(p);
								taxB.remove(p.getMaterialcode());
								break;
							}

						}	
					}
					else if(purchaseMode.equals("-"))
					{
						labour=null;
					}
			}
			}});
		Sell.add(btnRemove);
		
		lblMessage = new JLabel("   ");
		lblMessage.setBounds(0, 203, 202, 14);
		Sell.add(lblMessage);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 222, 595, 229);
		Sell.add(scrollPane);
		
		table = new JTable();
		table.setPreferredSize(new Dimension(450, 200));
		/*table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				DefaultTableModel model=(DefaultTableModel) table.getModel();
				txtItemCode.setText((String) model.getValueAt(table.getSelectedRow(), 0));
				txtDescription.setText((String) model.getValueAt(table.getSelectedRow(), 1));
				txtPrice.setText((String) model.getValueAt(table.getSelectedRow(), 2));
				txtQuantity.setText((String) model.getValueAt(table.getSelectedRow(), 3));
				
			}
		});*/
		table.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"mode","Item Code", "Description", "Price","Quantity"
				
			}
		));
		scrollPane.setViewportView(table);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBounds(0, 0, 0, 0);
		Sell.add(panel_3);
		panel_3.setLayout(new GridLayout(0, 18, 0, 0));
		
		JLabel lblVat = new JLabel("VAT");
		lblVat.setBounds(377, 462, 46, 17);
		Sell.add(lblVat);
		
		JLabel lblTotal = new JLabel("Total");
		lblTotal.setBounds(377, 521, 46, 14);
		Sell.add(lblTotal);
		
		txtVatDisplay = new JTextField();
		txtVatDisplay.setEditable(false);
		txtVatDisplay.setBounds(458, 459, 86, 20);
		Sell.add(txtVatDisplay);
		txtVatDisplay.setColumns(10);
		
		txtTotalDisplay = new JTextField();
		txtTotalDisplay.setEditable(false);
		txtTotalDisplay.setBounds(458, 516, 86, 20);
		Sell.add(txtTotalDisplay);
		txtTotalDisplay.setColumns(10);
		
		JLabel lblServiceTax = new JLabel("Service Tax");
		lblServiceTax.setBounds(344, 490, 67, 14);
		Sell.add(lblServiceTax);
		
		txtServiceCharge = new JTextField();
		txtServiceCharge.setEditable(false);
		txtServiceCharge.setBounds(458, 490, 86, 20);
		Sell.add(txtServiceCharge);
		txtServiceCharge.setColumns(10);
		
		btnPrint = new JButton("Print");
		btnPrint.setEnabled(false);
		btnPrint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			//	printBillPDF(Bill);
			}
		});
		btnPrint.setBounds(0, 462, 89, 23);
		Sell.add(btnPrint);
		
		JButton btnCalculate = new JButton("Calculate");
		btnCalculate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				calculate(billA,billB,taxA,taxB);
			}
		});
		btnCalculate.setBounds(215, 462, 89, 23);
		Sell.add(btnCalculate);
		
		rdbtnA_Sell = new JRadioButton("A");
		rdbtnA_Sell.setBounds(112, 131, 109, 23);
		Sell.add(rdbtnA_Sell);
		rdbtnA_Sell.setSelected(true);
		
		rdbtnB_Sell = new JRadioButton("B");
		rdbtnB_Sell.setBounds(223, 131, 109, 23);
		Sell.add(rdbtnB_Sell);
		group1.add(rdbtnA_Sell);
		group1.add(rdbtnB_Sell);
		
		JLabel lblPurchaseMode_1 = new JLabel("Purchase Mode");
		lblPurchaseMode_1.setBounds(10, 135, 96, 14);
		Sell.add(lblPurchaseMode_1);
		
		JLabel lblSellQuantity = new JLabel("Sell Quantity");
		lblSellQuantity.setBounds(168, 94, 91, 14);
		Sell.add(lblSellQuantity);
		
		txtAvlblQuant = new JTextField();
		txtAvlblQuant.setEditable(false);
		txtAvlblQuant.setBounds(112, 94, 46, 23);
		Sell.add(txtAvlblQuant);
		txtAvlblQuant.setColumns(10);
		
		JLabel lblVatserviceTax = new JLabel("VAT/Service Tax");
		lblVatserviceTax.setBounds(10, 161, 100, 14);
		Sell.add(lblVatserviceTax);
		
		txtTax = new JTextField();
		txtTax.setBounds(112, 158, 86, 20);
		Sell.add(txtTax);
		txtTax.setColumns(10);
		
		 cmbDesc = new JComboBox();
		 cmbDesc.getModel().setSelectedItem(null);
		 cmbDesc.addItemListener(new ItemListener() {
		 	public void itemStateChanged(ItemEvent arg0) {
		 		if(cmbDesc.getSelectedItem()==null)
		 			return;
		 		System.out.println("Inside Item Changed Event handler");
		 		String itemSelected=cmbDesc.getSelectedItem().toString();
		 		System.out.println("Selected Items is --->"+itemSelected);
		 		System.out.println("Value of Result is "+result.size());
		 		
				for(Inventory item: result){
					if(item.getDescription().equals(itemSelected)){
						txtItemCode.setText(item.getMaterialcode());
						txtDescription.setText(item.getDescription());
						txtPrice.setText(Double.toString(item.getPrice()));
						txtAvlblQuant.setText(Integer.toString(item.getQuantity()));
						if(rdbtnA_Sell.isSelected())
							searchedProductA=(inventoryA) item;
						else 
							searchedProductB=(inventoryB) item;
						
					}
				}
			}
		 	
		 	
		 });
		 cmbDesc.setBounds(314, 32, 180, 20);
		 Sell.add(cmbDesc);
		 
		 JButton btnNewOrder = new JButton("New Order");
		 btnNewOrder.addActionListener(new ActionListener() {
		 	public void actionPerformed(ActionEvent arg0) {
		 		clearAllControls_Sell();
		 	}
		 });
		 btnNewOrder.setBounds(243, 184, 109, 23);
		 Sell.add(btnNewOrder);
		
		JPanel Modify = new JPanel();
		tabbedPane.addTab("Modify", null, Modify, null);
		Modify.setLayout(null);
		
		JLabel lblItemcode = new JLabel("ItemCode");
		lblItemcode.setBounds(30, 32, 72, 14);
		Modify.add(lblItemcode);
		
		JLabel lblDescrition = new JLabel("Descrition");
		lblDescrition.setBounds(30, 57, 72, 14);
		Modify.add(lblDescrition);
		
		JLabel lblQuantity_1 = new JLabel("Quantity");
		lblQuantity_1.setBounds(30, 82, 46, 14);
		Modify.add(lblQuantity_1);
		
		JLabel lblPurchasedMode = new JLabel("Purchased Mode");
		lblPurchasedMode.setBounds(30, 107, 98, 14);
		Modify.add(lblPurchasedMode);
		
		JLabel lblPrice_1 = new JLabel("Price");
		lblPrice_1.setBounds(30, 132, 46, 14);
		Modify.add(lblPrice_1);
		
		JLabel lblMinquantity = new JLabel("MinQuantity");
		lblMinquantity.setBounds(30, 157, 82, 14);
		Modify.add(lblMinquantity);
		
		txtItemCode_P = new JTextField();
		txtItemCode_P.setBounds(112, 29, 86, 20);
		Modify.add(txtItemCode_P);
		txtItemCode_P.setColumns(10);
		
		txtDescription_P = new JTextField();
		txtDescription_P.setBounds(112, 54, 133, 20);
		Modify.add(txtDescription_P);
		txtDescription_P.setColumns(10);
		
		txtQuantity_P = new JTextField();
		txtQuantity_P.setBounds(112, 79, 86, 20);
		Modify.add(txtQuantity_P);
		txtQuantity_P.setColumns(10);
		
		txtPrice_P = new JTextField();
		txtPrice_P.setBounds(112, 129, 86, 20);
		Modify.add(txtPrice_P);
		txtPrice_P.setColumns(10);
		
		txtMinQuantity_P = new JTextField();
		txtMinQuantity_P.setBounds(112, 154, 86, 20);
		Modify.add(txtMinQuantity_P);
		txtMinQuantity_P.setColumns(10);
		
		JButton btnLookUp = new JButton("Look Up");
		btnLookUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				searchItem_Modification();
		}});
		
		
		
		
		
		btnLookUp.setBounds(254, 28, 89, 23);
		Modify.add(btnLookUp);
		
		btnUpdate_Purchase = new JButton("Update");
		btnUpdate_Purchase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				
				if(rdbtnA.isSelected()){
					inventoryA inventoryAProduct=new inventoryA();
					inventoryAProduct.setMaterialcode(txtItemCode_P.getText());
					inventoryAProduct.setDescription(txtDescription_P.getText());
					inventoryAProduct.setPrice(Double.parseDouble(txtPrice_P.getText()));
					inventoryAProduct.setQuantity(Integer.parseInt(txtQuantity_P.getText()));
					inventoryAProduct.setMinQunatity(Integer.parseInt(txtMinQuantity_P.getText()));
				
				
				
	
				
				//Add Safety Net here :
				serv.updateInventoryItemForPurchase(inventoryAProduct,"A");
				}
				else if (rdbtnB.isSelected()){
					Inventory inventoryBProduct=new inventoryB();
					inventoryBProduct.setMaterialcode(txtItemCode_P.getText());
					inventoryBProduct.setDescription(txtDescription_P.getText());
					inventoryBProduct.setPrice(Double.parseDouble(txtPrice_P.getText()));
					inventoryBProduct.setQuantity(Integer.parseInt(txtQuantity_P.getText()));
					inventoryBProduct.setMinQunatity(Integer.parseInt(txtMinQuantity_P.getText()));
					
					//Add Safety Net here :
					serv.updateInventoryItemForPurchase(inventoryBProduct,"B");
				}

			}
		});
		btnUpdate_Purchase.setEnabled(false);
		btnUpdate_Purchase.setBounds(254, 219, 89, 23);
		Modify.add(btnUpdate_Purchase);
		
		 rdbtnA = new JRadioButton("A");
		 rdbtnA.setSelected(true);
		rdbtnA.setBounds(175, 103, 109, 23);
		Modify.add(rdbtnA);
		
		 rdbtnB = new JRadioButton("B");
		rdbtnB.setBounds(304, 103, 109, 23);
		Modify.add(rdbtnB);
		
		 ButtonGroup bG = new ButtonGroup();
	     bG.add(rdbtnA);
	     bG.add(rdbtnB);
		
		btnAdd_Purchase = new JButton("Add");
		btnAdd_Purchase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
		
			}
		});
		btnAdd_Purchase.setEnabled(false);
		btnAdd_Purchase.setBounds(254, 185, 89, 23);
		Modify.add(btnAdd_Purchase);
		
		JButton btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			
			}
		});
		btnDelete.setBounds(254, 253, 89, 23);
		Modify.add(btnDelete);
		
		JPanel Notification = new JPanel();
		tabbedPane.addTab("Notification", null, Notification, null);
		Notification.setLayout(null);
		
		//Add the Calendar to select Date for REtrieve PDF
		 	picker = new JXDatePicker();
	        picker.setDate(Calendar.getInstance().getTime());
	        //picker.setFormats(new SimpleDateFormat("MM/dd/yyyy"));
	        picker.setFormats(new DateFormat[]{new SimpleDateFormat("MM/dd/YYYY")});
	        picker.setBounds(296, 33, 113, 23);
	        Notification.add(picker);
	        
	     //Add the Calendar ro Pick the date for White Retrieve Goods
	        retSoldPicker=new JXDatePicker();
	        retSoldPicker.setDate(Calendar.getInstance().getTime());
	        retSoldPicker.setFormats(new DateFormat[]{new SimpleDateFormat("MM/dd/YYYY")});
	        retSoldPicker.setBounds(296, 137, 113, 23);
	        Notification.add(retSoldPicker);
		
		
		JButton btnRetrive = new JButton("Retrive");
		btnRetrive.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

					File filename=new File("Test_PDF2.pdf");
					boolean isBillRetrieved=serv.retrievePDF("Test_PDF2.pdf",dateFormat.format(picker.getDate()));
					
					if(!isBillRetrieved)
						return;
					
					//open the file
					Desktop desktop=Desktop.getDesktop();
					try {
						desktop.open(filename);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				
			}
		});
		btnRetrive.setBounds(419, 33, 97, 23);
		Notification.add(btnRetrive);
		
		JLabel lblTestLabel = new JLabel("Check the Pending Bills To be Printed ");
		lblTestLabel.setBounds(26, 33, 305, 23);
		Notification.add(lblTestLabel);
		
		JLabel lblViewDepletingItems = new JLabel("Check Depleting Items ");
		lblViewDepletingItems.setBounds(26, 84, 305, 23);
		Notification.add(lblViewDepletingItems);
		
		JButton btnView = new JButton("View");
		btnView.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							DepletingItemViewFrame frame = new DepletingItemViewFrame(serv);
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		btnView.setBounds(419, 84, 97, 23);
		Notification.add(btnView);
		
		JLabel lblRetrieveTheStock = new JLabel("Retrieve the Stock Sold from InventoryA");
		lblRetrieveTheStock.setBounds(26, 141, 281, 14);
		Notification.add(lblRetrieveTheStock);
		
		JButton btnRetrieveSold = new JButton("Retrieve Sold");
		btnRetrieveSold.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
							InventoryASoldStock frame = new InventoryASoldStock(dateFormat.format(retSoldPicker.getDate()));
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		btnRetrieveSold.setBounds(419, 137, 97, 23);
		Notification.add(btnRetrieveSold);
		
		JButton btnRetPDFDel = new JButton("Delete");
		btnRetPDFDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

				serv.deleteInvoicebyDate(dateFormat.format(picker.getDate()));
				
			}
		});
		btnRetPDFDel.setBounds(526, 33, 70, 23);
		Notification.add(btnRetPDFDel);
		
		JButton btnRetSoldDel = new JButton("Delete");
		btnRetSoldDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

				serv.deleteSoldByDate(dateFormat.format(retSoldPicker.getDate()));
				
				
			}
		});
		btnRetSoldDel.setBounds(526, 137, 70, 23);
		Notification.add(btnRetSoldDel);
		
		JPanel Purchase = new JPanel();
		tabbedPane.addTab("Purchase Order", null, Purchase, null);
		Purchase.setLayout(null);
		
		txtFile = new JTextField();
		txtFile.setBounds(45, 44, 348, 20);
		Purchase.add(txtFile);
		txtFile.setColumns(10);
		
		final JFileChooser fc=new JFileChooser();
		
		JButton btnBrowse = new JButton("Browse");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int returnVal = fc.showOpenDialog(new JPanel());

			      if (returnVal == JFileChooser.APPROVE_OPTION) {
			       File  file = fc.getSelectedFile();
			        txtFile.setText(file.getAbsolutePath());
			}
		}});
		btnBrowse.setBounds(448, 43, 89, 23);
		Purchase.add(btnBrowse);
		
		
		
		final JRadioButton rdbtnPurchaseA = new JRadioButton("A");
		rdbtnPurchaseA.setBounds(160, 88, 62, 23);
		Purchase.add(rdbtnPurchaseA);
		
		JRadioButton rdbtnPurchaseB = new JRadioButton("B");
		rdbtnPurchaseB.setBounds(270, 88, 62, 23);
		Purchase.add(rdbtnPurchaseB);
		
		ButtonGroup group = new ButtonGroup();
		group.add(rdbtnPurchaseA);
		group.add(rdbtnPurchaseB);
		
		JButton btnUpdateInventory = new JButton("Update Inventory");
		btnUpdateInventory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if(rdbtnPurchaseA.isSelected())
			serv.updatePurchasedOrder(fc.getSelectedFile(),"A");
				else 
					serv.updatePurchasedOrder(fc.getSelectedFile(),"B");
			}
		});
		btnUpdateInventory.setBounds(58, 144, 126, 23);
		Purchase.add(btnUpdateInventory);
	
		
		
		
		JLabel lblPurchaseMode = new JLabel("Purchase Mode");
		lblPurchaseMode.setBounds(45, 92, 109, 14);
		Purchase.add(lblPurchaseMode);
		
		 
		 
		
		
		
		}
	
	
	public boolean printBillPDF(Map<inventoryA,Integer> billA,Map<inventoryB,Integer> billB){
		
		//Service Call to get the Invoice Number
		int invoiceSeq=serv.getInvoiceNumber();
		String invoiceNumber=Integer.toString(invoiceSeq);
		//Calling invoice Service to print the invoice
		File tempFile=new File("Test_PDF.pdf");
	
		
		HashMap<Inventory,Integer> combinedBill=new HashMap<Inventory,Integer>();
		combinedBill.putAll(billA);
		combinedBill.putAll(billB);
	
		File file=invoiceService.createPDF("Test_PDF.pdf", combinedBill,labour,txtVatDisplay.getText(),txtTotalDisplay.getText(),invoiceNumber,txtServiceCharge.getText());
		
		//Saving the Bill
		serv.savePDF("Test_PDF.pdf", invoiceNumber, billRequired);

		
		if(file==null)
		{
			JOptionPane.showMessageDialog(null, "File is Opened in Another Application , Please Close the File & Click on Print");
			return false;
		}
		
		//open the file
		Desktop desktop=Desktop.getDesktop();
		try {
			desktop.open(file);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//Enable Save Button if not enabled 
		//disable Print button if not diabled 
		//Clear all controls
		if(!btnSave.isEnabled())
			btnSave.setEnabled(true);
		if(btnPrint.isEnabled())
			btnPrint.setEnabled(true);
		clearAllControls_Sell();
		
		return true;
	}

	private void clearAllControls_Sell() {
		billA.clear();
		billB.clear();
		taxA.clear();
		taxB.clear();
		labour=null;
		DefaultTableModel dm = (DefaultTableModel) table.getModel();
		for(int i=dm.getRowCount()-1;i>-1;i--)
			dm.removeRow(i);
		 
		
		txtItemCode.setText("");
		txtDescription.setText("");
		txtPrice.setText("");
		txtQuantity.setText("");	
		txtAvlblQuant.setText("");
		cmbDesc.removeAllItems();
		
		txtVatDisplay.setText("");
		txtServiceCharge.setText("");
		txtTotalDisplay.setText("");
		}
	
	private void clearAfterAdd(){
		txtItemCode.setText("");
		txtDescription.setText("");
		txtPrice.setText("");
		txtQuantity.setText("");	
		txtAvlblQuant.setText("");
		cmbDesc.removeAllItems();
		
		//clear the total
		txtVatDisplay.setText("");
		txtServiceCharge.setText("");
		txtTotalDisplay.setText("");
		
	}
	
	private void searchItem(){
		if(txtItemCode.getText().equals("") || txtItemCode.getText()==null)
		{
			JOptionPane.showMessageDialog(null, "Please Enter the Valid the ItemCode");
		}
		else 
		{	if(rdbtnA_Sell.isSelected())
			{
			searchedProductA=serv.getProductByItemCodeFromInventoryA(txtItemCode.getText().trim());
			if(searchedProductA==null)
			{
				JOptionPane.showMessageDialog(null, "No Item Found with the ItemCode: " +txtItemCode.getText());
			}
			
			if(searchedProductA!=null){
				txtDescription.setText(searchedProductA.getDescription());
				txtPrice.setText(Double.toString(searchedProductA.getPrice()));
				txtAvlblQuant.setText(Integer.toString(searchedProductA.getQuantity()));
				txtTax.setText(Double.toString(VAT_DEFAULT_PERCENT));

			}
			}
		else{
			searchedProductB=(inventoryB)serv.getProductByItemCodeFromInventoryB(txtItemCode.getText().trim());
			if(searchedProductB==null)
			{
				JOptionPane.showMessageDialog(null, "No Item Found with the ItemCode: " +txtItemCode.getText());
			}
			
			if(searchedProductB!=null){
				txtDescription.setText(searchedProductB.getDescription());
				txtPrice.setText(Double.toString(searchedProductB.getPrice()));
				txtAvlblQuant.setText(Integer.toString(searchedProductB.getQuantity()));
				txtTax.setText(Double.toString(VAT_DEFAULT_PERCENT));


			}
			
			
		}

		}
	}
	
	
	private void searchItem_Modification(){
		if(txtItemCode_P.getText().equals("") || txtItemCode_P.getText()==null)
		{
			JOptionPane.showMessageDialog(null, "Please Enter the Valid the ItemCode");
		}
		else 
		{	if(rdbtnA.isSelected())
			{
			inventoryA searchProduct_modify=serv.getProductByItemCodeFromInventoryA(txtItemCode_P.getText().trim());
			if(searchProduct_modify==null)
			{
				JOptionPane.showMessageDialog(null, "No Item Found with the ItemCode: " +txtItemCode_P.getText());
				btnAdd_Purchase.setEnabled(true);
				btnUpdate_Purchase.setEnabled(false);
				clearAllControls_Purchase();
			}
			
			if(searchProduct_modify!=null){
				txtDescription_P.setText(searchProduct_modify.getDescription());
				txtPrice_P.setText(Double.toString(searchProduct_modify.getPrice()));
				txtQuantity_P.setText(Integer.toString(searchProduct_modify.getQuantity()));
				txtMinQuantity_P.setText(Integer.toString(searchProduct_modify.getMinQunatity()));
			}
			}
		else{
			inventoryB searchedProductB_modify=(inventoryB)serv.getProductByItemCodeFromInventoryB(txtItemCode_P.getText().trim());
			if(searchedProductB_modify==null)
			{
				JOptionPane.showMessageDialog(null, "No Item Found with the ItemCode: " +txtItemCode_P.getText());
				btnAdd_Purchase.setEnabled(true);
				btnUpdate_Purchase.setEnabled(false);
				clearAllControls_Purchase();
			}
			
			if(searchedProductB_modify!=null){
				txtDescription_P.setText(searchedProductB_modify.getDescription());
				txtPrice_P.setText(Double.toString(searchedProductB_modify.getPrice()));
				txtQuantity_P.setText(Integer.toString(searchedProductB_modify.getQuantity()));
				txtMinQuantity_P.setText(Integer.toString(searchedProductB_modify.getMinQunatity()));
				

			}
			
			
		}

		
		btnUpdate_Purchase.setEnabled(true);
		btnAdd_Purchase.setEnabled(false);
		}
	}
	
	
	private void clearAllControls_Purchase(){
		txtDescription_P.setText("");
		txtPrice_P.setText("");
		txtQuantity_P.setText("");
		txtMinQuantity_P.setText("");
		rdbtnA.setSelected(false);
		rdbtnB.setSelected(false);
		
	}
}