package com.inventory.gui;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.Employee.Entity.SoldItemNotification;
import com.inventory.backendCode.Service;

public class InventoryASoldStock extends JFrame {

	private JPanel contentPane;
	private JTable table;

	/**
	 * Launch the application.
	 */
	/*public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					InventoryASoldStock frame = new InventoryASoldStock();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/

	/**
	 * Create the frame.
	 */
	public InventoryASoldStock(final String date) {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				Service service=new Service();
				List<SoldItemNotification> itemList= service.getInventoryASoldItems(date);
				
				if(itemList.size()==0)
				{
					JOptionPane.showMessageDialog(null, "No Items From InventoryA Sold ");
					return;
					
				}
				
				DefaultTableModel model=(DefaultTableModel) table.getModel();
				int counter=0;
				for( SoldItemNotification item:itemList){
					System.out.println(item.getMaterialcode());
					System.out.println(item.getDescription());
					System.out.println(item.getPrice_sold());
					System.out.println(item.getQuantity_sold());

					
					model.addRow(new Object[]{Integer.toString(++counter),item.getMaterialcode(),item.getDescription(),item.getPrice_sold(),item.getQuantity_sold()});
				}
			}
		});
		
		setTitle("Inventory A Sold Stock");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		contentPane.add(scrollPane, gbc_scrollPane);
		
		table = new JTable();
		table.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Sr.No", "Material Code", "Description", "Price Sold", "Quantity Sold"
			}
		));
		scrollPane.setViewportView(table);
		
	}
}
