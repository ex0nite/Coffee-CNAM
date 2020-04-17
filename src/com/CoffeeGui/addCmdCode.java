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
import javax.swing.JTextPane;

public class addCmdCode extends JFrame implements ActionListener {
		// Gestion SQL
		private JDBCAdapter sqls;
		
		// Var
		Date horodatage, dateCmd;
		String secteur, codeCmd, fournisseur, notes; 
		
		// Fenetre
		private JPanel contentPane;
		private JTextField txt_CodeCmd;
		private JButton btnAddCmd;
		private JLabel lblFour;
		private JComboBox cbSecteur, cbCmd;
		private DateTextField txt_Date;
		private JTextPane txtP_Notes;
		/**
		 * Create the frame.
		 */
		public addCmdCode(JDBCAdapter _sqls) {
			setTitle("Coffee - Ajout d'un Code Commande v1.0");
			sqls = _sqls; 
			
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setBounds(100, 100, 388, 333);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);
			
			txt_CodeCmd = new JTextField();
			txt_CodeCmd.setBounds(106, 8, 256, 20);
			
			txt_CodeCmd.setColumns(10);

			// JLABEL
			JLabel lblMontantDeLa = new JLabel("Rattach\u00E9 \u00E0 :");
			lblMontantDeLa.setBounds(10, 36, 110, 14);
			
			lblFour = new JLabel("Fournisseur :");
			lblFour.setBounds(10, 61, 110, 14);
			
			JLabel lblNotes = new JLabel("Notes :");
			lblNotes.setBounds(10, 111, 110, 14);

			JLabel lblCode = new JLabel("Code commande :");
			lblCode.setBounds(10, 11, 110, 14);
			
			cbSecteur = new JComboBox();
			cbSecteur.setBounds(106, 33, 110, 20);
			contentPane.add(cbSecteur);
 
			cbCmd = new JComboBox();
			cbCmd.setBounds(106, 58, 110, 20);
			contentPane.add(cbCmd);
			contentPane.add(lblCode);
			contentPane.add(lblNotes);
			contentPane.add(lblMontantDeLa);
			contentPane.add(lblFour);
			
			// BUTTON
			btnAddCmd = new JButton("Cr\u00E9er");
			btnAddCmd.addActionListener(this);
			btnAddCmd.setBounds(10, 136, 86, 147);
			contentPane.add(btnAddCmd);
			contentPane.add(txt_CodeCmd);
			
			JLabel lblDate = new JLabel("Date pr\u00E9vue :");
			lblDate.setBounds(10, 86, 86, 14);
			contentPane.add(lblDate);
			
			txt_Date = new DateTextField();
			txt_Date.setHorizontalAlignment(SwingConstants.CENTER);
			txt_Date.setColumns(10);
			txt_Date.setBounds(106, 83, 256, 20);
			contentPane.add(txt_Date);
			
			txtP_Notes = new JTextPane();
			txtP_Notes.setBounds(106, 111, 256, 172);
			contentPane.add(txtP_Notes);
			
			// Populate the comboBox 'cbSecteur'
			String qCmd = "SELECT Nom_Section FROM Sections"; 
			List<String> myList = null;
			myList = sqls.executeQueryToArray(qCmd);	
			for (String s : myList) cbSecteur.addItem(s);
			
			// Populate the comboBox 'cbCmd'
			String qBon = "SELECT Code FROM Codes_Fournisseurs";
			List<String> mlBon = null;
			mlBon = sqls.executeQueryToArray(qBon);
			for (String s : mlBon) cbCmd.addItem(s);
			//for (String s : mlBon) cbCmd.addItem(s);
		}
		
		public void actionPerformed(ActionEvent ae) {	
			if (ae.getSource() == btnAddCmd) {
				// horodatage, dateCmd, nbVol, montantCmd, numBon, codeCmd
				
				java.util.Date d1 = new java.util.Date();
				java.util.Date d2 = txt_Date.getDate();
				horodatage = new java.sql.Date(d1.getTime());
				dateCmd =  new java.sql.Date(d2.getTime()); // java.util.Date(); 

				secteur = cbSecteur.getSelectedItem().toString();
				codeCmd = txt_CodeCmd.getText();
				fournisseur = cbCmd.getSelectedItem().toString();
				notes = txtP_Notes.getText();
				
				boolean statutBon = false; 
				
				System.out.println(
						"\n\n" +
						"Ajout de commande -- Info sur l'ajout" +
						"\nCode : " + codeCmd +
						"\nFournisseur : " + fournisseur +
						"\nDate_prevue : " + dateCmd + 
						"\nSecteur : " + secteur +
						"\nNotes : " + notes +
						"\nHorodatage : " + horodatage);
						
				try {
			    	String query = "Insert into Codes_Commandes "
			    			+ "(		Code,	Fournisseur,	Date_prevue,	Secteur,	Notes, 	Horodatage)" 
			    			+ "values(	?,		?,				?,				?,			?,		?)";
			    		
		    	PreparedStatement ps = sqls.connection.prepareStatement(query);
		    	
		    	ps.setString(1, codeCmd);
		    	ps.setString(2, fournisseur); // CHANGER POUR CODE FOURNISSEUR
		    	ps.setDate(3,  dateCmd);
		    	ps.setString(4, secteur);
		    	ps.setString(5, notes);
		    	ps.setDate(6, horodatage);

				ps.executeUpdate();
	            ps.close();
	            
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					error n = new error(e); 
					e.printStackTrace();
				}
			}
			
			
		}
}

