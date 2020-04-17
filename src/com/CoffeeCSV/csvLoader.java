/*
 * CSV Loader v1
 * 
 * JFileChooser pour allez chercher le fichier
 * Stockage du fichier 
 *   > Echo 
 * 
 * Spécification des champs supplémentaires non récupérable dans al commande
 *   > Echo de la commande complete 
 * 
 * Etablissement de la connexion -> JDBC Adapter
 * 
 * Préparation de la requête SQL -> preparedStatement -> https://java.developpez.com/faq/jdbc?page=Les-instructions-parametrees-moins-PreparedStatement
 * 
 * Execution de la requête 
 * 
 */

package com.CoffeeCSV;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.*;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileSystemView;

import com.Coffee.JDBCAdapter;
import com.opencsv.CSVIterator;
import com.opencsv.CSVReader;

public class csvLoader {
	
    private final static String RESOURCES_PATH = "./";
    private final static String FILE_NAME = "electre.csv";
    private final static char SEPARATOR = ';';
    
    public static File csvL_file() {
		 JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory()); 
		 String fileName = "";
		 
		 int returnValue = jfc.showOpenDialog(null);
		File selectedFile = null;
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			selectedFile = jfc.getSelectedFile();
			// System.out.println(selectedFile.getAbsolutePath());
			fileName = selectedFile.getAbsolutePath().toString();
		}
    
		return selectedFile;
    }
	
    public static void csvLoad(JDBCAdapter sqls, File selectedFile, String _frn, String _code, java.util.Date dateCmd, String section) throws IOException, SQLException {
    	File n = null;
    	n = selectedFile;
    	
    	System.out.println("\n\ncsvLoad -- Info sur l'import");
    	/* CSV : 				DB: 						Loader: 
    	 *  1- Quantité			 1- ID (autogénéré)			 1-
    	 *  2- Ean				 2- Date_cmd (loader)		 
    	 *  3- Titre			 3- Code_cmd (loader)		
    	 *  4- Auteur			 4- Titre					
    	 *  5- Editeur			 5- Auteur					
    	 *  6- Collection		 6- Prix remisé (calculé)	
    	 *  7- PrixHT			 7- Champdoc				
    	 *  8- PrixTTC			 8- Fournisseur (loader)	
    	 *  9- Devise			 9- Réservataire			
    	 * 10- Livré			10- Statut					
    	 * 11- RemiseMaj		11-	Argumentaire			
    	 * 12- Utilisateur		12- Notes					
    	 * 13- Champdoc			
    	 * 14- Notes			
    	 * 15- N° de carte		
    	 * 16- Argumentaire		 
    	 * 17- Commentaire		 
    	 * 	
    	 */
    	
    	/***************************
    	 * Préparation des arguments
    	 * "fileName: " + fileName + "\nFournisseur: " + frnCmd + " " + codeCmd + " " + dateCmd);
    	 ************************* */
    	System.out.println(
    			"fileName: " + selectedFile +
    			"\nFournisseur: " + _frn + 
    			"\nCode Commande: " + _code + 
    			"\nDate Commande: " + dateCmd);
    	
    	//String q_Secteur = "SELECT Secteur FROM Codes_Commandes WHERE Code='" + _code + "'";
    	//String r_Secteur = sqls.exec(q_Secteur);
    	//System.out.println("Le Secteur lié au code commande est : " + r_Secteur);
    	String r_Secteur = section;
    	/***************************
    	 *  Préparation de la requête SQL 
    	 ************************* */
    	String [] nextLine;
    	CSVReader reader = null; 
    	String query = "Insert into Liste_Achats "
    			+ "(		Date_cmd,	Code_cmd,	Titre,	Auteur,	Prix_rem,	Champdoc,	Fournisseur,	Reservataire,	Statut,	Argumentaire,	Notes, 	Secteur) "
    			+ "values(	?,			?,			?,		?,		?,			?,			?,				?,				?,		?,				?,		?)";
    	
    	//reader = new CSVReader(new FileReader("C:\\Users\\prome\\workspace\\Coffee\\electre.csv"), ';'); // selectedFile
    	reader = new CSVReader(new FileReader(selectedFile), ';');
		double prix, remise, prixrem = 0.00; 
		int cpt = 0; 
    	
		while ((nextLine = reader.readNext()) != null) {
			// nextLine[] is an array of values from the line
			for (int i = 0; i < nextLine.length; i++) {
				// Lecture de la ligne >
				System.out.print(i + ": " + nextLine[i] + "\t");
			}
			/* https://stackoverflow.com/questions/30726978/how-to-insert-values-from-csv-into-database-in-java */
			
			/* statut */
			String _statut = "ATL";

			/* 0: Quantité	
			 * 1: Ean	
			 * 2: Titre	
			 * 3: Auteur	
			 * 4: Editeur	
			 * 5: Collection	
			 * 6: PrixHT	
			 * 7: PrixTTC	
			 * 8: Devise	
			 * 9: Livré	
			 * 10: RemiseMaj	
			 * 11: Utilisateur	
			 * 12: Champdoc	
			 * 13: Notes	
			 * 14: N° de carte	
			 * 15: Argumentaire	
			 * 16: Commentaire	
			 * 28/12/2018 SFL01 Auteur
			*/
			
			// String dateCmd
			long longDate = dateCmd.getTime();
			java.sql.Date psDate = new java.sql.Date(dateCmd.getTime());
			
			// new Date(dateCmd);
			
			System.out.println("dateCmd:" + dateCmd + " - psDate:" + psDate);
			
			// PreparedStatement
			
			// https://stackoverflow.com/questions/27893630/unsupported-collating-sort-order-error-updating-access-database-from-java
			// https://stackoverflow.com/questions/31948096/how-to-change-sortorder-to-avoid-unsupported-collating-sort-order-error/31972659#31972659
			
			
			// Réunir tous les éléments propre au fournisseur pour switcher en fonction de de frnCmd 
			
			PreparedStatement ps = sqls.connection.prepareStatement(query);
            ps.setDate(1,psDate); // Date_Cmd
            ps.setString(2,_code); // Code_cmd
            
            ps.setString(3, nextLine[2]); // Titre
            ps.setString(4, nextLine[3]); // Auteur
            
            /* calcul du prix remisé */
			if (cpt != 0) {
				// compilation de la regex avec le motif : ","
				Pattern p = Pattern.compile(",");
				
				// création du moteur associé à la regex
				Matcher mPrix = p.matcher(nextLine[6]);
				Matcher mRemise = p.matcher(nextLine[10]);
				
				// remplacement de toutes les occurrences 
				String sPrix = mPrix.replaceAll(".");
				String sRemise = mRemise.replaceAll(".");

				prix = Double.parseDouble(sPrix); // PrixHT. Si PrixTTC pour calcul, alors nextLine[7]
				remise = Double.parseDouble(sRemise);
				prixrem = prix - (prix * remise / 100);

			} else {
				prixrem = 0.00;
			}
            ps.setDouble(5, prixrem); // Prix_remisé
            System.out.println("\tPrix remisé :" + prixrem);
            
            ps.setString(6, nextLine[12]); // Champdoc12
            System.out.println("\tChampdoc : " + nextLine[12]);
            
            ps.setString(7, _frn); // Fournisseur
            
            ps.setString(8, nextLine[14]); // Réservataire
            
            ps.setString(9, _statut); // Statut
            
            ps.setString(10, nextLine[15]); // Argumentaires
            ps.setString(11, nextLine[16]); // Notes
            ps.setString(12, r_Secteur); // Secteur
            
			// Passage à la ligne suivante
			System.out.println("");
			
			// execution du insert ici 	
			ps.executeUpdate();
            ps.close();
            System.out.println("Déconnexion depuis csvLoader");
			cpt++;
		}
		reader.close();
	    JFrame parent = new JFrame();
	    JOptionPane.showMessageDialog(parent, "Import réussi.\nNombre de ligne traitées: " + cpt);
		
		//System.out.println("Nombre de ligne traitées: " + cpt);
	    
	    
    }


}