package com.CoffeeGui;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.Coffee.JDBCAdapter;
import com.CoffeeCSV.csvLoader;
import com.CoffeeSwing.DateTextField;
import com.CoffeeSwing.error;

import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JComboBox;
import javax.swing.SwingConstants;
import javax.swing.JLabel;

public class addCmd extends JFrame implements ActionListener {
		// Gestion SQL
		private JDBCAdapter sqls;
		
		// Var
		Date horodatage, dateCmd;
		int nbVol;
		Double montantCmd;
		String numBon, codeCmd; 
		
		// Fenetre
		private JPanel contentPane;
		private JTextField txt_NbVol;
		private JButton btnAddCmd;
		private JLabel lblNbVol;
		private JTextField txt_Montant;
		private DateTextField dateTextField;
		private JComboBox cbBon, cbCmd;
		
		/**
		 * Create the frame.
		 */
		public addCmd(JDBCAdapter _sqls) {
			setTitle("Coffee - Ajout de Commande v1.0");
			sqls = _sqls; 
			
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setBounds(100, 100, 331, 228);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);
			
			dateTextField = new DateTextField();
			dateTextField.setHorizontalAlignment(SwingConstants.CENTER);
			dateTextField.setColumns(10);
			dateTextField.setBounds(130, 11, 179, 20);
			contentPane.add(dateTextField);
			
			txt_NbVol = new JTextField();
			txt_NbVol.setBounds(130, 39, 179, 20);
			
			txt_NbVol.setColumns(10);
			
			txt_Montant = new JTextField();
			txt_Montant.setColumns(10);
			txt_Montant.setBounds(130, 67, 179, 20);

			// JLABEL
			JLabel lblMontantDeLa = new JLabel("Montant de la cmd :");
			lblMontantDeLa.setBounds(10, 70, 110, 14);
			
			lblNbVol = new JLabel("Nombre de volumes :");
			lblNbVol.setBounds(10, 42, 110, 14);
			
			JLabel lblBon = new JLabel("Num\u00E9ro de bon :");
			lblBon.setBounds(10, 96, 110, 14);

			JLabel lblCode = new JLabel("Code commande :");
			lblCode.setBounds(10, 124, 110, 14);
			
			JLabel lblDateCmd = new JLabel("Date de la commande :");
			lblDateCmd.setBounds(10, 14, 110, 14);
			
			cbBon = new JComboBox();
			cbBon.setBounds(130, 95, 179, 20);
			contentPane.add(cbBon);
 
			cbCmd = new JComboBox();
			cbCmd.setBounds(130, 121, 179, 20);
			contentPane.add(cbCmd);
			contentPane.add(lblCode);
			contentPane.add(lblBon);
			contentPane.add(lblMontantDeLa);
			contentPane.add(lblNbVol);
			contentPane.add(lblDateCmd);
			
			// BUTTON
			btnAddCmd = new JButton("Cr\u00E9er");
			btnAddCmd.addActionListener(this);
			btnAddCmd.setBounds(10, 154, 299, 23);
			contentPane.add(btnAddCmd);
			contentPane.add(txt_NbVol);
			contentPane.add(txt_Montant);
			
			// Populate the comboBox 'cbCmd'
			String qCmd = "SELECT Code FROM Codes_Commandes"; 
			List<String> myList = null;
			myList = sqls.executeQueryToArray(qCmd);	
			for (String s : myList) cbCmd.addItem(s);
			
			// Populate the comboBox 'cbBon'
			String qBon = "SELECT Num_engagement FROM Liste_Bons";
			List<String> mlBon = null;
			mlBon = sqls.executeQueryToArray(qBon);
			for (String s : mlBon) cbBon.addItem(s);
		}
		
		@Override
		public void actionPerformed(ActionEvent ae) {	
			if (ae.getSource() == btnAddCmd) {
				// horodatage, dateCmd, nbVol, montantCmd, numBon, codeCmd
				
				java.util.Date d1 = new java.util.Date();
				java.util.Date d2 = dateTextField.getDate();
				horodatage = new java.sql.Date(d1.getTime());
				dateCmd =  new java.sql.Date(d2.getTime()); // java.util.Date(); 
				nbVol = Integer.parseInt(txt_NbVol.getText());
				montantCmd = Double.parseDouble(txt_Montant.getText());
				numBon = cbBon.getSelectedItem().toString();
				codeCmd = cbCmd.getSelectedItem().toString();
				
				boolean statutBon = false; 
				
				System.out.println(
						"\n\n" +
						"Ajout de commande -- Info sur l'ajout" +
						"\nHorodatage: " + horodatage + 
						"\nDate de la commande:" + dateCmd.toString() + 
						"\nNombre de volumes: " + nbVol + 
						"\nMontant de la commande : " + montantCmd +
						"\nNuméro du bon : " + numBon + 
						"\nCode de la commande:" + codeCmd);
				try {
			    	String query = "Insert into Liste_Commandes "
			    			+ "(		Date_Reception,	Volumes,	Montant,	Bon_rattachement,	Code_Cmd,	Horodatage) "
			    			+ "values(	?,				?,			?,			?,					?,			?)";
			    		
		    	PreparedStatement ps = sqls.connection.prepareStatement(query);
	            
		    	ps.setDate(1, dateCmd);
		    	ps.setInt(2, nbVol);
		    	ps.setDouble(3,  montantCmd);
		    	ps.setString(4, numBon);
		    	ps.setString(5,  codeCmd);
		    	ps.setDate(6, horodatage);
		    	
				ps.executeUpdate();
	            ps.close();
	            
	            // Passer la commande codeCmd en statut "Reçu" dans Liste_Achats
	            
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					error n = new error(e); 
					e.printStackTrace();
				}/**/
			}
			
			
		}
	}


