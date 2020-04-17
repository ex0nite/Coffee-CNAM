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

public class addBon extends JFrame implements ActionListener {
		// Gestion SQL
		private JDBCAdapter sqls;
		
		// Var
		Date dateCmd;
		
		String frnCmd;
		Double montantBon;
		String numBon; 
		String secteurBon;
		
		// Fenetre
		private JPanel contentPane;
		private JTextField txt_NumEng;
		private JComboBox cmbBox_frnsr, cb_Sec;
		private JButton btnAddBon;
		private JLabel lblMontantEngag;
		private JTextField txt_Montant;
		private JLabel lbl_sec;
		
		/**
		 * Create the frame.
		 */
		public addBon(JDBCAdapter _sqls) {
			setTitle("Coffee - Ajout de Bon v1.0");
			// csvLoader csvL = new csvLoader(_sqls); 
			sqls = _sqls; 
			
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setBounds(100, 100, 310, 189);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);
			
			txt_NumEng = new JTextField();
			txt_NumEng.setBounds(106, 15, 180, 20);
			contentPane.add(txt_NumEng);
			txt_NumEng.setColumns(10);
			
			cmbBox_frnsr = new JComboBox();
			cmbBox_frnsr.setBounds(106, 65, 179, 20);
			contentPane.add(cmbBox_frnsr);
			cmbBox_frnsr.addActionListener(this);
			
			btnAddBon = new JButton("Cr\u00E9er");
			btnAddBon.addActionListener(this);
			btnAddBon.setBounds(10, 120, 275, 23);
			contentPane.add(btnAddBon);
			
			JLabel lblNDengagement = new JLabel("N\u00B0 d'engagement :");
			lblNDengagement.setBounds(10, 20, 110, 14);
			contentPane.add(lblNDengagement);
			
			lblMontantEngag = new JLabel("Montant engag\u00E9 :");
			lblMontantEngag.setBounds(10, 45, 110, 14);
			contentPane.add(lblMontantEngag);
			
			txt_Montant = new JTextField();
			txt_Montant.setColumns(10);
			txt_Montant.setBounds(106, 40, 179, 20);
			contentPane.add(txt_Montant);
			
			JLabel lblFournisseur = new JLabel("Fournisseur :");
			lblFournisseur.setBounds(10, 70, 86, 14);
			contentPane.add(lblFournisseur);
			
			lbl_sec = new JLabel("Secteur :");
			lbl_sec.setBounds(10, 95, 85, 14);
			contentPane.add(lbl_sec);
			
			cb_Sec = new JComboBox();
			cb_Sec.setBounds(106, 90, 179, 20);
			contentPane.add(cb_Sec);
			
			// Populate the comboBox 'frnsr'
			String rq_Fournisseurs = "SELECT Code FROM Codes_Fournisseurs"; 
			List<String> myList = null;
			myList = sqls.executeQueryToArray(rq_Fournisseurs);
				
			for (int i = 0; i < myList.size(); i++) {
				cmbBox_frnsr.addItem(myList.get(i));
			}
			
			String rq_Secteur = "SELECT Nom_Section FROM Sections";
			List<String> listSecteur = null;
			listSecteur = sqls.executeQueryToArray(rq_Secteur);
			
			for (String s : listSecteur) cb_Sec.addItem(s);
			
			
		}
		
		@Override
		public void actionPerformed(ActionEvent ae) {	
			if (ae.getSource() == btnAddBon) {
				/* Créer string pour renvoi des données
				* Il faut envoyer :
				* 	fileName
				frnCmd = cmbBox_frnsr.getSelectedItem().toString();
				codeCmd = cmbBox_cmd.getSelectedItem().toString();
				dateCmd = dateField.getText()
				* 
				*/				
				java.util.Date d1 = new java.util.Date();
				java.sql.Date d2 = new java.sql.Date(d1.getTime());
								
				dateCmd = d2;
				frnCmd = cmbBox_frnsr.getSelectedItem().toString();		
				numBon = txt_NumEng.getText();
				montantBon = Double.parseDouble(txt_Montant.getText());
				secteurBon = cb_Sec.getSelectedItem().toString();
				
				boolean statutBon = false; 
				
				System.out.println(
						"\n\n" +
						"Ajout de bon -- Info sur l'ajout" +
						"\nFournisseur: " + frnCmd + 
						"\nNum du bon: " + numBon + 
						"\nDate de la création: " + dateCmd + 
						"\nMontant du bon:" + montantBon +
						"\nSecteur :" + secteurBon);
				try {
			    	String query = "Insert into Liste_Bons "
			    			+ "(		Num_engagement,	Fournisseur,	Montant,	Statut,	Date_crea_bon, Secteur) "
			    			+ "values(	?,				?,				?,			?,		?,				?)";
			    	
		    	PreparedStatement ps = sqls.connection.prepareStatement(query);
	            
	            ps.setString(1, numBon);// Num_Engagement
	            ps.setString(2, frnCmd); // Fournisseur
	            ps.setDouble(3, montantBon); // Montant
	            ps.setBoolean(4,  statutBon); // Statut
	            ps.setDate(5,dateCmd);// Date de création du bon 
				ps.setString(6, secteurBon);
	            
				ps.executeUpdate();
	            ps.close();
	            
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					error n = new error(e); 
					e.printStackTrace();
				}/**/
			}
			
			
		}
	}


