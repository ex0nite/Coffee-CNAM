package com.CoffeeGui;

import java.awt.Container;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.UnsupportedLookAndFeelException;

import com.Coffee.JDBCAdapter;

import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JButton;

public class connexion extends JFrame implements ActionListener {
	private JDBCAdapter sqls;
	
	private JFrame Connexion;
	private JTextField txt_User;
	private JPasswordField txt_Mdp;
	private JButton btn_Connexion = new JButton("Connexion");
	
	Container frame; 

	public connexion(JDBCAdapter _sqls) {
		/*
		 * En prévision de l'update permettant de gérer les users depuis la bdd
		 * Une fois le user récupéré, le transmettre à CoffeeWin
		 * Créer un fichier log depuis JDBCAdapter où on enregistre toutes les requêtes 
		 */
		sqls = _sqls;
		
		
		setTitle("Connexion");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocation(400,100);
		setSize(250,185);
		frame = getContentPane();
		getContentPane().setLayout(null);
		
		Connexion = new JFrame();
		Connexion.setBounds(100, 100, 250, 190);
		Connexion.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Connexion.getContentPane().setLayout(null);
		
		txt_User = new JTextField();
		txt_User.setBounds(64, 13, 156, 22);
		frame.add(txt_User);
		txt_User.setColumns(10);
		
		txt_Mdp = new JPasswordField();
		txt_Mdp.setBounds(64, 42, 156, 22);
		frame.add(txt_Mdp);
		txt_Mdp.setColumns(10);
		
		JLabel lbl_User = new JLabel("User");
		lbl_User.setBounds(12, 16, 56, 16);
		frame.add(lbl_User);
		
		JLabel lbl_Mdp = new JLabel("Mdp");
		lbl_Mdp.setBounds(12, 45, 56, 16);
		frame.add(lbl_Mdp);
		
		btn_Connexion.setBounds(12, 77, 208, 25);
		frame.add(btn_Connexion);
		btn_Connexion.addActionListener(this);
		
		JButton btn_NoID = new JButton("Connexion sans ID");
		btn_NoID.setBounds(12, 112, 208, 25);
		frame.add(btn_NoID);
		btn_NoID.addActionListener(this);
	}
	
	private static boolean isPasswordCorrect(char[] input, char[] verif) {
	    boolean isCorrect = true;

	    if (input.length != verif.length) {
	        isCorrect = false;
	    } else {
	        isCorrect = Arrays.equals (input, verif);
	    }

	    // On vide les tableaux
	    Arrays.fill(input,'0');
	    Arrays.fill(verif,'0');

	    return isCorrect;
	}
	
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == btn_Connexion) {
			
			boolean v = false; // passwords
			boolean w = false; // users
			boolean z = false; // somme
			
			// sqls = new JDBCAdapter(); // On se connecte 
			
			try {
				String r_usr = sqls.exec_p("SELECT usr_name FROM Users");  // Requête user
				String r_pass = sqls.exec_p("SELECT usr_pass FROM Users"); // Requête password

				v = isPasswordCorrect(txt_Mdp.getPassword(), r_pass.toCharArray()); // Verifications 
				w = isPasswordCorrect(txt_User.getText().toCharArray(), r_usr.toCharArray());
				
				if (!v) { // Gestion des erreurs
					System.out.println("Erreur de password");
				}
				else if (!w) {
					System.out.println("Erreur d'identifiant"); 
				}
				else {
					z = v & w; 
				}
			}catch (Exception e) {
				System.out.println("" + e);
			}
			
			if (z) { 
				CoffeeWin mw;
				try {
					mw = new CoffeeWin(sqls);
					this.setVisible(false);
					mw.setVisible(true);
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException
						| UnsupportedLookAndFeelException e) {
					e.printStackTrace();
				} 

			}
		}

	}
	
}
