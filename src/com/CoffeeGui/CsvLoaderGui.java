package com.CoffeeGui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JComboBox;
import javax.swing.SwingConstants;

import com.Coffee.JDBCAdapter;
import com.CoffeeCSV.csvLoader;
import com.CoffeeSwing.DateTextField;
import com.CoffeeSwing.error;
import javax.swing.JLabel;



public class CsvLoaderGui extends JFrame implements ActionListener {
	// Gestion SQL
	private JDBCAdapter sqls;
	
	// Var
	File file;
	String fileName; 
	java.util.Date dateCmd;
	String frnCmd;
	String codeCmd;
	
	// Fenetre
	private JPanel contentPane;
	private JTextField textField;
	private DateTextField dateField;
	private JComboBox<String> cmbBox_frnsr;
	private JComboBox<String> cmbBox_cmd;
	private JButton btnFileChooser;
	private JButton btnImport;
	private String section;
	
	/**
	 * Create the frame.
	 */
	public CsvLoaderGui(JDBCAdapter _sqls, String s) {
		setTitle("Coffee - Importeur v1.0");
		// csvLoader csvL = new csvLoader(_sqls); 
		sqls = _sqls; 
		section = s;
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 364, 185);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		textField = new JTextField();
		textField.setEnabled(false);
		textField.setBounds(55, 10, 234, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		btnFileChooser = new JButton("...");
		btnFileChooser.addActionListener(this);
		btnFileChooser.setBounds(299, 9, 39, 23);
		contentPane.add(btnFileChooser);
		
		dateField = new DateTextField();
		dateField.setHorizontalAlignment(SwingConstants.CENTER);
		dateField.setBounds(128, 40, 210, 20);
		contentPane.add(dateField);
		dateField.setColumns(10);
		
		cmbBox_frnsr = new JComboBox<String>();
		cmbBox_frnsr.setBounds(75, 70, 107, 20);
		contentPane.add(cmbBox_frnsr);
		cmbBox_frnsr.addActionListener(this);
		
		cmbBox_cmd = new JComboBox<String>();
		cmbBox_cmd.setBounds(229, 70, 109, 20);
		contentPane.add(cmbBox_cmd);
		
		btnImport = new JButton("Import");
		btnImport.addActionListener(this);
		btnImport.setBounds(10, 100, 328, 35);
		contentPane.add(btnImport);
		
		JLabel lblFichier = new JLabel("Fichier :");
		lblFichier.setBounds(10, 13, 46, 14);
		contentPane.add(lblFichier);
		
		JLabel lblDateDeLa = new JLabel("Date de la commande : ");
		lblDateDeLa.setBounds(10, 45, 172, 14);
		contentPane.add(lblDateDeLa);
		
		JLabel lblFournisseur = new JLabel("Fournisseur :");
		lblFournisseur.setBounds(10, 75, 70, 14);
		contentPane.add(lblFournisseur);
		
		JLabel lblCodeDeLa = new JLabel("Code :");
		lblCodeDeLa.setBounds(192, 75, 46, 14);
		contentPane.add(lblCodeDeLa);
		
		// Populate the comboBox 'frnsr'
		String query = "SELECT Code FROM Codes_Fournisseurs"; 
		List<String> myList = null;
		myList = sqls.executeQueryToArray(query);
			
		for (int i = 0; i < myList.size(); i++) {
			cmbBox_frnsr.addItem(myList.get(i));
		}
	}
	
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == cmbBox_frnsr) {
			cmbBox_cmd.removeAllItems();
			int k = cmbBox_frnsr.getSelectedIndex(); 
			String frn = cmbBox_frnsr.getSelectedItem().toString();
			String query = "SELECT Code FROM Codes_Commandes WHERE Fournisseur='" + frn + "'";
			List<String> mylist = sqls.executeQueryToArray(query);
			
			System.out.println(query);
			
			mylist = sqls.executeQueryToArray(query);
			for (int i = 0; i < mylist.size(); i++) {
				cmbBox_cmd.addItem(mylist.get(i)); 
			}	
		}
		
		if (ae.getSource() == btnFileChooser) {
			file = csvLoader.csvL_file();
			fileName = file.getAbsolutePath();
			System.out.println(fileName);
			textField.setText(fileName);
		}
		
		if (ae.getSource() == btnImport) {
			frnCmd = cmbBox_frnsr.getSelectedItem().toString();
			codeCmd = cmbBox_cmd.getSelectedItem().toString();
			dateCmd = dateField.getDate(); // java.util.Date(); 
			String _dateCmd = dateCmd.toString(); 
						
			if (frnCmd == null || codeCmd == null || fileName == null) {
				System.out.println(						
						"\n\n" +
						"CsvLoaderGui -- Info sur l'import" +
						"L'import a échoué\n"		
						);
				if (frnCmd == null) {
					System.out.println("Merci d'indiquer un fournisseur");
				}
				if (codeCmd == null) {
					System.out.println("Merci d'indiquer un code commande");
				}
				if (fileName == null) {
					System.out.println("Merci de sélectionner un fichier");
				}
				
				return;
			} else {				
				System.out.println(
						"\n\n" +
						"CsvLoaderGui -- Info sur l'import" +
						"\nfileName: " + fileName + 
						"\nFournisseur: " + frnCmd + 
						"\nCode commande: " + codeCmd + 
						"\nDate de la commande: " + dateCmd);
				try {
					csvLoader.csvLoad(sqls, file, frnCmd, codeCmd, dateCmd, section);
					this.dispose();
				} catch (IOException | SQLException e) {
					error n = new error(e); 
					e.printStackTrace();
				}
			}
		}
		
	}
}
