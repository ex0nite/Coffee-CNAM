package com.CoffeeGui;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.Coffee.JDBCAdapter;
import com.CoffeeSwing.DateTextField;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.SwingConstants;
import javax.swing.JLabel;

public class addFact extends JFrame implements ActionListener {
		// Gestion SQL
		private JDBCAdapter sqls;
		
		// Var
		private Date horodatage, dateFact;
		private Double montantFact;
		private String numFact, codeCmd, numBon; 

		// Fenetre
		private JPanel contentPane;
		private JTextField txt_numFact;
		private JButton btnAddCmd;
		private JLabel lblNumFact;
		private JTextField txt_Montant;
		private DateTextField dateTextField;
		private JComboBox<String> cbCmd, cbBon;
		
		/**
		 * Create the frame.
		 */
		public addFact(JDBCAdapter _sqls) {
			setTitle("Coffee - Ajout de Facture v1.0");
			sqls = _sqls; 
			
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setBounds(100, 100, 331, 216);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);
			
			dateTextField = new DateTextField();
			dateTextField.setHorizontalAlignment(SwingConstants.CENTER);
			dateTextField.setColumns(10);
			dateTextField.setBounds(130, 35, 179, 20);
			contentPane.add(dateTextField);
			
			txt_numFact = new JTextField();
			txt_numFact.setBounds(130, 10, 179, 20);
			
			txt_numFact.setColumns(10);
			
			txt_Montant = new JTextField();
			txt_Montant.setColumns(10);
			txt_Montant.setBounds(130, 60, 179, 20);

			// JLABEL
			JLabel lblMontantFact = new JLabel("Montant de la facture :");
			lblMontantFact.setBounds(10, 62, 120, 14);
			
			lblNumFact = new JLabel("Num\u00E9ro de la facture :");
			lblNumFact.setBounds(10, 12, 110, 14);

			JLabel lblCode = new JLabel("Code commande :");
			lblCode.setBounds(10, 87, 110, 14);
			
			JLabel lblDateFact = new JLabel("Date de la facture :");
			lblDateFact.setBounds(10, 37, 110, 14);
 
			cbCmd = new JComboBox<String>();
			cbCmd.setBounds(130, 85, 179, 20);
			contentPane.add(cbCmd);
			contentPane.add(lblCode);
			contentPane.add(lblMontantFact);
			contentPane.add(lblNumFact);
			contentPane.add(lblDateFact);
			
			// BUTTON
			btnAddCmd = new JButton("Cr\u00E9er");
			btnAddCmd.addActionListener(this);
			btnAddCmd.setBounds(10, 145, 299, 23);
			contentPane.add(btnAddCmd);
			contentPane.add(txt_numFact);
			contentPane.add(txt_Montant);
			
			cbBon = new JComboBox<String>();
			cbBon.setBounds(130, 110, 179, 20);
			contentPane.add(cbBon);
			
			JLabel lblNumBon = new JLabel("Bon de r\u00E9f\u00E9rence : ");
			lblNumBon.setBounds(10, 112, 110, 14);
			contentPane.add(lblNumBon);
			
			// Populate the comboBox 'cbCmd'
			String qCmd = "SELECT Code FROM Codes_Commandes"; 
			List<String> myList = null;
			myList = sqls.executeQueryToArray(qCmd);	
			for (String s : myList) cbCmd.addItem(s);
			
			// Populate the cb cbBon
			String rq_Bon = "SELECT Num_engagement FROM Liste_Bons";
			List<String> listBon = null;
			listBon = sqls.executeQueryToArray(rq_Bon);
			for (String s : listBon) cbBon.addItem(s);
		}
		
		public void actionPerformed(ActionEvent ae) {	
			if (ae.getSource() == btnAddCmd) {
				java.util.Date d1 = new java.util.Date();
				java.util.Date d2 = dateTextField.getDate();
				horodatage = new java.sql.Date(d1.getTime());
				dateFact =  new java.sql.Date(d2.getTime()); 
				numFact = txt_numFact.getText();
				montantFact = Double.parseDouble(txt_Montant.getText());
				codeCmd = cbCmd.getSelectedItem().toString();
				numBon = cbBon.getSelectedItem().toString();
				
				System.out.println(
						"\n\n" +
						"Ajout de facture -- Info sur l'ajout" +
						"\nHorodatage: " + horodatage + 
						"\nDate de la facture:" + dateFact.toString() + 
						"\nMontant de la facture : " + montantFact +
						"\nNuméro de la facture: " + numFact + 
						"\nCode de la commande:" + codeCmd +
						"\nNuméro d'engagement:" + numBon);
				try {
			    	String query = "Insert into Liste_Factures "
			    			+ "(		Num_Facture,	Date_Facture,	Montant_Facture,	Commande,	Horodatage,	N_Bon) "
			    			+ "values(	?,				?,				?,					?,			?,			?)";
			    		
		    	PreparedStatement ps = sqls.connection.prepareStatement(query);
	            
		    	ps.setString(1, numFact);
		    	ps.setDate(2,  dateFact);
		    	ps.setDouble(3, montantFact);
		    	ps.setString(4, codeCmd);
		    	ps.setDate(5, horodatage);
		    	ps.setString(6, numBon);
		    	
				ps.executeUpdate();
	            ps.close();
	            
				} catch (SQLException e) {
					// error n = new error(e); 
					e.printStackTrace();
				}
			}
			
			
		}
	}


