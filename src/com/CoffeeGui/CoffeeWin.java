package com.CoffeeGui;

import javax.swing.*;
import java.awt.*;

import com.Coffee.JDBCAdapter;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CoffeeWin extends JFrame implements ActionListener {
	// Var GUI
	private JButton btnImporter, btnRservataires, btnReInit, btnBon, btnCmd, btnAddCmdCode, btnAjoutBudget, btnMajPartage, btnAddFact ;
	private JPanel LignesBudget;
	private LookAndFeel laf;
	private JLabel lblTest;
	private JTable tb_ListeBons, tb_Bons, tb_Lignes;
	private JTable jtb_Sections;
	private JTable tb_BudInit;
	private JTable jtb_Cmd;
	
	// Var SQL
	private JDBCAdapter sqls;
	private ResultSetTableModel rsm, rstm_ListeAchats, rstm_Cmd, rstm_ListeBons;
	
	private String section; 
	
	// Var Liste Bons
	private ResultSetTableModel rstm_Bons, rstm_Factures;
	private String bonSelec, factureSelec; 
	
	// Var Lignes budgetaires
	private ResultSetTableModel rstm_LignesBud;
	private JLabel lbl_SectionDocsTotalAff, lbl_SectionFictionTotalAff, lbl_SectionDocsDepAff, lbl_SectionFictionDepAff, lbl_SectionBudget, lbl_SectionNom, lbl_SectionBudgetDep, lbl_LigneVolRecu;
	private JLabel lbl_LigneBudAjoutAff, lbl_LignePrixMoyenN1Aff, lbl_LignePrixMoyenAff, lbl_LigneVolCmdAff, lbl_LigneBudDepAff, lbl_LigneBudRestantAff, lbl_LigneInfos;
	private String ligneSelec;
	private JTable tb_ListeFactures;
	private JTable tb_ListeAchats;
	
	public CoffeeWin(JDBCAdapter _sqls) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		// bdd
		sqls = _sqls;
		// look and feel
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		
		// Gestion de la fenetre de base
		setTitle("Coffee - v 1.0");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
	    setPreferredSize(new Dimension(1280, 900));
	    setLocation(0,0); 
	    
		// Design général
		JLabel lblSections = new JLabel("Sections");
		lblSections.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblSections.setBounds(10, 11, 137, 14);
		
		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		separator.setBounds(157, 11, 8, 839);
		
		JLabel lblCmd = new JLabel("Commandes");
		lblCmd.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblCmd.setBounds(10, 134, 137, 14);
		
		btnReInit = new JButton("R\u00E9initialisation");
		btnReInit.setBounds(10, 448, 137, 23);
		btnReInit.addActionListener(this);
		
		btnAddCmdCode = new JButton("<html>Ajouter un <br>code commande</html>");
		btnAddCmdCode.setBounds(10, 350, 137, 55);
		btnAddCmdCode.addActionListener(this);
		
		// Gestion de la liste des sections 
		String rq_Sec = "SELECT Nom_Section FROM Sections";
		rsm = new ResultSetTableModel(sqls, rq_Sec);
		jtb_Sections = new JTable(rsm);
		jtb_Sections.setBounds(10, 36, 137, 90);
		
		// Gestion de la liste des commandes
		String rq_Cmd = "SELECT Code FROM Codes_Commandes";
		rstm_Cmd = new ResultSetTableModel(sqls, rq_Cmd);
		jtb_Cmd = new JTable(rstm_Cmd);
		jtb_Cmd.setBounds(10, 162, 101, 180);
		JScrollPane sp_Cmd = new JScrollPane(jtb_Cmd);
		sp_Cmd.setBounds(10, 159, 137, 180);
		
		// Panels
		JTabbedPane tpContent = new JTabbedPane(JTabbedPane.TOP); // paneau "principal"
		tpContent.setBounds(175, 11, 1079, 839);
		
		// Gestion de Liste Achat
		JPanel ListeAchats = panelListeAchats(); 
		
		// Gestion de Lignes_Budgetaires
		JPanel LignesBudget = panelLignesBudget();
		
		// Gestion des bons 
		JPanel ListeBon = panelBons();
		
		//lblTest = new JLabel("Test");
		//ListeBon.add(lblTest);
		
		// Gestion des onglets
		tpContent.addTab("Lignes budgétaires", LignesBudget);
		tpContent.setEnabledAt(0, true);
		tpContent.addTab("Liste Achats", ListeAchats);
		tpContent.addTab("Liste des Bons", ListeBon );
		
		// Ajout des éléments dans la JFrame
			// Elements généraux
		getContentPane().add(jtb_Sections);
		getContentPane().add(btnReInit);
		getContentPane().add(btnAddCmdCode);
	
			// Elements locaux
		getContentPane().add(lblSections);
		getContentPane().add(separator);
		getContentPane().add(tpContent);
		getContentPane().add(sp_Cmd);
		getContentPane().add(lblCmd);
		


		// Gestion de la mise à jour des différentes tables
		jtb_Sections.addMouseListener(new MouseAdapter() {
			  public void mousePressed(MouseEvent e) {
				  // L'usager sélectionne une section
				  // On stocke le nom de la section
				  section = jtb_Sections.getValueAt(jtb_Sections.getSelectedRow(), 0).toString();
				  
				  // On met à jour les listes
				  String upListe = "SELECT * FROM Liste_Achats WHERE Secteur='" + section + "'"; // On update la liste achat
				  String upBudget = "SELECT Description,Champdoc,Budget FROM Lignes_Budgetaires WHERE Secteur='" + section +"'"; // On update la liste des lignes budgétaires
				  String upBons = "SELECT Num_engagement FROM Liste_Bons WHERE Secteur='" + section +"'"; // On update la liste des bons 
				  String upCmd = "SELECT Code FROM Codes_Commandes WHERE Secteur='" + section + "'"; // On update la liste des commandes 
				  try {
					rstm_ListeAchats.setQuery(upListe);
					rstm_LignesBud.setQuery(upBudget);
					rstm_Bons.setQuery(upBons);
					rstm_Cmd.setQuery(upCmd);
				  } catch (IllegalStateException | SQLException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				  }
				  
				  // On met à jour le panel Lignes budgetaires
				  // Requêtes pour Section 
				  
				  /***
				   * TODO 
				   * 
				   * Mettre les blocs de MAJ suivant onglets / zone dans des fonctions
				   * Appeler les fonctions pour mettre à jour
				   */
				  	Double budget = 0.00, budgetFiction = 0.00, budgetDocs = 0.00;
				  	
				  	String rq_Budget = "SELECT Budget_init from Sections WHERE Nom_Section='" + section + "'";	
				  	String result_Budget = sqls.exec(rq_Budget); 
				  	lbl_SectionBudget.setText("Budget : " + result_Budget + " € ");
				  	budget = Double.parseDouble(result_Budget);
				  	
				  	String rq_BudgetFiction = "SELECT Budget_Fiction from Sections WHERE Nom_Section='" + section + "'";
				  	String result_BudgetFiction = sqls.exec(rq_BudgetFiction); 
				  	lbl_SectionFictionTotalAff.setText(result_BudgetFiction + " € ");
				  	budgetFiction = Double.parseDouble(result_BudgetFiction);
				  	
				  	budgetDocs = budget - budgetFiction; 
				  	lbl_SectionDocsTotalAff.setText(budgetDocs.toString());
				  	
				  	String rq_BudgetDep = "SELECT sum(Prix_rem)" + 
				  			" FROM Liste_Achats" + 
				  			" INNER JOIN Lignes_budgetaires ON Liste_Achats.Champdoc = Lignes_budgetaires.Champdoc" + 
				  			" WHERE Lignes_budgetaires.Secteur='"+ section +"'";
				  	String result_BudgetDep = sqls.exec(rq_BudgetDep); 
				  	lbl_SectionBudgetDep.setText("Dépenses : " + result_BudgetDep + " €"); 
				  	
				  	String rq_BudgetDepFiction = "SELECT sum(Prix_rem)" + 
				  			" FROM Liste_Achats" + 
				  			" INNER JOIN Lignes_budgetaires ON Liste_Achats.Champdoc = Lignes_budgetaires.Champdoc" + 
				  	        " WHERE Lignes_budgetaires.bud_type='fiction' " +
				  			" AND Lignes_budgetaires.Secteur='"+ section +"'"; 
				  	String result_BudgetDepFiction = sqls.exec(rq_BudgetDepFiction);
				  	lbl_SectionFictionDepAff.setText(result_BudgetDepFiction);
				  	
				  	String rq_BudgetDepDocs = "SELECT sum(Prix_rem)" + 
				  			" FROM Liste_Achats" + 
				  			" INNER JOIN Lignes_budgetaires ON Liste_Achats.Champdoc = Lignes_budgetaires.Champdoc" + 
				  	        " WHERE Lignes_budgetaires.bud_type='doc' " +
				  			" AND Lignes_budgetaires.Secteur='"+ section +"'"; 
				  	String result_BudgetDepDocs = sqls.exec(rq_BudgetDepDocs);
				  	lbl_SectionDocsDepAff.setText(result_BudgetDepDocs);

				  	// Requête pour Lignes du bugdget
			    	String rq_CoutMoyenAnMU = "SELECT PrixMoyenNMU from Lignes_budgetaires WHERE Champdoc='" + section + "'";
			    	String rq_NbDocumentsRecu = "SELECT Count(*) from Liste_Achats WHERE Champdoc='" + section + "' AND Statut='Reçu'";
			    	String rq_NbDocumentsCmd = "SELECT Count(*) from Liste_Achats WHERE Champdoc='" + section + "'";
			    	String rq_Name = "SELECT Description from Lignes_budgetaires WHERE Champdoc='"+ section +"'";
			    	
			    	String rq_BudDepense = "SELECT Sum(Prix_rem) from Liste_Achats WHERE Champdoc='" + ligneSelec + "'";
			    	lbl_LigneBudDepAff.setText("");
			    	
			    	
			    	
				  // On active les boutons
					btnImporter.setEnabled(true);
					btnRservataires.setEnabled(true);
					btnReInit.setEnabled(true);
					btnBon.setEnabled(true);
					btnCmd.setEnabled(true);
					btnAddFact.setEnabled(true);
					btnAddCmdCode.setEnabled(true);
					btnAjoutBudget.setEnabled(true);
					btnMajPartage.setEnabled(true);

			  }
			});
		
		jtb_Cmd.addMouseListener(new MouseAdapter() {
			  public void mousePressed(MouseEvent e) {
			    	String selectedCmd = jtb_Cmd.getValueAt(jtb_Cmd.getSelectedRow(), 0).toString();
			    	String upCmd = "SELECT * FROM Liste_Achats WHERE Code_Cmd='" + selectedCmd + "'";
			    	try {
						rstm_ListeAchats.setQuery(upCmd);
					} catch (IllegalStateException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			  }
			});
		
		// Récupération des codes cmd déjà existant
		/*ArrayList<String> ALCodeCmd = _sqls.executeQueryToArray("SELECT Code_cmd FROM Liste_Achats"); 
        Set set = new HashSet() ;
        set.addAll(ALCodeCmd) ;
        ArrayList<String> distinctList = new ArrayList(set) ;
		
        for(String elem: distinctList) {
        	 System.out.println (elem);
        }*/
        
		this.pack();
	}

	private JPanel panelBons() throws ClassNotFoundException, SQLException {
		JPanel ListeBon = new JPanel();
		ListeBon.setLayout(null);
		
		btnBon = new JButton("<html><center>Cr\u00E9er<br>un bon</center></html>");
		btnBon.setBounds(10, 419, 120, 48);
		ListeBon.add(btnBon);
		btnBon.setEnabled(false);
		btnBon.addActionListener(this);
		
		String rq_Bons = "Select Num_engagement FROM Liste_Bons";
		rstm_Bons = new ResultSetTableModel(sqls, rq_Bons);
		tb_ListeBons = new JTable(rstm_Bons);
		tb_ListeBons.setBounds(538, 6, 0, 0);
		ListeBon.add(tb_ListeBons);
		JScrollPane jsBons = new JScrollPane(tb_ListeBons);
		jsBons.setBounds(10, 41, 120, 206);
		ListeBon.add(jsBons);
		
		
		JList<?> list = new JList<Object>();
		list.setBounds(543, 6, 0, 0);
		ListeBon.add(list);
		
		JLabel lbl_BonsSection = new JLabel("Bons de ");
		lbl_BonsSection.setBounds(10, 16, 120, 14);
		ListeBon.add(lbl_BonsSection);
		
		JLabel lbl_BonInfos = new JLabel("Infos sur le bon n\u00B0");
		lbl_BonInfos.setBounds(10, 261, 155, 14);
		ListeBon.add(lbl_BonInfos);
		
		JLabel lbl_InfosFournisseur = new JLabel("Fournisseur : ");
		lbl_InfosFournisseur.setBounds(20, 286, 145, 14);
		ListeBon.add(lbl_InfosFournisseur);
		
		JLabel lbl_InfosMontant = new JLabel("Montant :");
		lbl_InfosMontant.setBounds(20, 311, 145, 14);
		ListeBon.add(lbl_InfosMontant);
		
		JLabel lbl_InfosStatut = new JLabel("Statut :");
		lbl_InfosStatut.setBounds(20, 337, 120, 14);
		ListeBon.add(lbl_InfosStatut);
		
		JLabel lbl_InfosDateCrea = new JLabel("Date de cr\u00E9ation : ");
		lbl_InfosDateCrea.setBounds(20, 362, 110, 14);
		ListeBon.add(lbl_InfosDateCrea);
			
		String rq_Factures = "SELECT Num_Facture FROM Liste_Factures";  
		rstm_Factures = new ResultSetTableModel(sqls, rq_Factures); 
		tb_ListeFactures = new JTable(rstm_Factures);
		
		JScrollPane jsFacture = new JScrollPane(tb_ListeFactures);
		jsFacture.setBounds(193, 38, 216, 209);
		ListeBon.add(jsFacture);
			
		JLabel lbl_FacturesLiees = new JLabel("Factures li\u00E9es au bon n\u00B0");
		lbl_FacturesLiees.setBounds(193, 16, 216, 14);
		ListeBon.add(lbl_FacturesLiees);
		
		JLabel lbl_FactureInfos = new JLabel("Infos sur la facture n\u00B0 ");
		lbl_FactureInfos.setBounds(203, 261, 198, 14);
		ListeBon.add(lbl_FactureInfos);
		
		JLabel lbl_FactureDate = new JLabel("Date :");
		lbl_FactureDate.setBounds(213, 286, 196, 14);
		ListeBon.add(lbl_FactureDate);
		
		JLabel lbl_FactureMontant = new JLabel("Montant : ");
		lbl_FactureMontant.setBounds(213, 311, 196, 14);
		ListeBon.add(lbl_FactureMontant);
		
		JLabel lbl_FactureCommande = new JLabel("Commande li\u00E9e : ");
		lbl_FactureCommande.setBounds(213, 337, 196, 14);
		ListeBon.add(lbl_FactureCommande);
		
		btnAddFact = new JButton("<html>Ajouter une facture</html>");
		btnAddFact.setEnabled(false);
		btnAddFact.setBounds(193, 419, 216, 48);
		ListeBon.add(btnAddFact);
		btnAddFact.addActionListener(this);
		
		tb_ListeBons.addMouseListener(new MouseAdapter() {
			  public void mousePressed(MouseEvent e) {
			  // On récupère le truc pressé
				  bonSelec = tb_ListeBons.getValueAt(tb_ListeBons.getSelectedRow(), 0).toString();
				  lbl_BonInfos.setText("Infos sur le bon " + bonSelec);
				  lbl_FacturesLiees.setText("Factures liées au bon " + bonSelec);
				  try {
					rstm_Factures.setQuery("SELECT Num_Facture FROM Liste_Factures WHERE N_Bon='" + bonSelec + "'");
				} catch (IllegalStateException | SQLException e1) {
					e1.printStackTrace();
				}
				  
				  String rq_Fournisseur = "SELECT Fournisseur FROM Liste_Bons WHERE Num_engagement='" + bonSelec + "'";
				  String bonFournisseur = sqls.exec(rq_Fournisseur);
				  lbl_InfosFournisseur.setText("Fournisseur : " + bonFournisseur);
				  
				  String rq_Montant = "SELECT Montant FROM Liste_Bons WHERE Num_engagement='" + bonSelec + "'";
				  String bonMontant = sqls.exec(rq_Montant); 
				  lbl_InfosMontant.setText("Montant : " + bonMontant);
				  
				  String rq_Statut = "SELECT Statut FROM Liste_Bons WHERE Num_engagement='" + bonSelec + "'";
				  String bonStatut = sqls.exec(rq_Statut);
				  if (bonStatut.equals("FALSE")) {
					  lbl_InfosStatut.setText("Statut : ouvert"); 
				  }
				  else {
					  lbl_InfosStatut.setText("Statut : clos");
				  }

				  String rq_Date = "SELECT Date_crea_bon FROM Liste_Bons WHERE Num_engagement='" + bonSelec + "'";
				  String bonDate = sqls.exec(rq_Date);
				  lbl_InfosDateCrea.setText("Date de création : " + bonDate);
				  
			  }
		});
		
		tb_ListeFactures.addMouseListener(new MouseAdapter() {
			  public void mousePressed(MouseEvent e) {
				  factureSelec = tb_ListeFactures.getValueAt(tb_ListeFactures.getSelectedRow(), 0).toString();
				  lbl_FactureInfos.setText("Infos sur la facture " + factureSelec);
				  
				  String rq_DateFacture = "SELECT Date_Facture FROM Liste_Factures WHERE Num_Facture='" + factureSelec + "'";
				  String rq_MontantFacture = "SELECT Montant_Facture FROM Liste_Factures WHERE Num_Facture='" + factureSelec + "'";
				  String rq_Commande = "SELECT Commande FROM Liste_Factures WHERE Num_Facture='" + factureSelec + "'";
				  lbl_FactureDate.setText("Date : " + sqls.exec(rq_DateFacture).substring(0, 10));
				  lbl_FactureMontant.setText("Montant : " + sqls.exec(rq_MontantFacture));
				  lbl_FactureCommande.setText("Commande liée : " + sqls.exec(rq_Commande));
			  }
		});
		
		return ListeBon; 
	}
	
	private JPanel panelLignesBudget() {
		String secteurSelec; 
		
		JPanel LignesBudgets = new JPanel();
		LignesBudgets.setLayout(null);
				
		try {
			rstm_LignesBud = new ResultSetTableModel(sqls,"SELECT Description,Champdoc,Budget FROM Lignes_budgetaires");
		} catch (ClassNotFoundException | SQLException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		
		lbl_SectionNom = new JLabel("Section");
		lbl_SectionNom.setFont(new Font("Calibri", Font.BOLD, 16));
		lbl_SectionNom.setBounds(44, 6, 123, 25);
		LignesBudgets.add(lbl_SectionNom);
		lbl_SectionBudget = new JLabel("Budget total : ");
		lbl_SectionBudget.setFont(new Font("Calibri", Font.BOLD, 14));
		lbl_SectionBudget.setBounds(10, 40, 261, 17);
		LignesBudgets.add(lbl_SectionBudget);
		// totalSection = Double.parseDouble(totalSectionStr);
		lbl_SectionBudget.setText("Budget : ");
		
		JLabel lbl_SectionFictionTotal = new JLabel("Fiction :");
		lbl_SectionFictionTotal.setFont(new Font("Tahoma", Font.BOLD, 11));
		lbl_SectionFictionTotal.setBounds(10, 64, 43, 17);
		LignesBudgets.add(lbl_SectionFictionTotal);
		
        lbl_SectionFictionTotalAff = new JLabel("");
        lbl_SectionFictionTotalAff.setBounds(74, 65, 55, 14);
        LignesBudgets.add(lbl_SectionFictionTotalAff);
		
		JLabel lbl_SectionDocsTotal = new JLabel("Docs :");
		lbl_SectionDocsTotal.setFont(new Font("Tahoma", Font.BOLD, 11));
		lbl_SectionDocsTotal.setBounds(10, 92, 33, 14);
		LignesBudgets.add(lbl_SectionDocsTotal);
		//aff_SectionFictionDep.setText(String.valueOf(ficDepense));
		
		lbl_SectionDocsTotalAff = new JLabel("");
		lbl_SectionDocsTotalAff.setBounds(74, 92, 67, 14);
		LignesBudgets.add(lbl_SectionDocsTotalAff);
		
		
		lbl_SectionBudgetDep = new JLabel("D\u00E9penses : ");
		lbl_SectionBudgetDep.setFont(new Font("Calibri", Font.BOLD, 14));
		lbl_SectionBudgetDep.setBounds(10, 120, 261, 17);
		LignesBudgets.add(lbl_SectionBudgetDep);
		
		JLabel lbl_SectionFictionDep = new JLabel("Fiction :");
		lbl_SectionFictionDep.setFont(new Font("Tahoma", Font.BOLD, 11));
		lbl_SectionFictionDep.setBounds(10, 148, 43, 14);
		LignesBudgets.add(lbl_SectionFictionDep);
		
		lbl_SectionFictionDepAff = new JLabel("");
		lbl_SectionFictionDepAff.setBounds(74, 148, 115, 14);
		LignesBudgets.add(lbl_SectionFictionDepAff);
		
		JLabel lbl_SectionDocsDep = new JLabel("Docs :");
		lbl_SectionDocsDep.setFont(new Font("Tahoma", Font.BOLD, 11));
		lbl_SectionDocsDep.setBounds(10, 173, 33, 14);
		LignesBudgets.add(lbl_SectionDocsDep);
		//aff_SectionFiction.setText(totalFictionStr);
		
		lbl_SectionDocsDepAff = new JLabel("");
		lbl_SectionDocsDepAff.setBounds(74, 173, 113, 14);
		LignesBudgets.add(lbl_SectionDocsDepAff);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(20, 207, 251, 2);
		LignesBudgets.add(separator);
		
		btnAjoutBudget = new JButton("Modifier le budget");
		btnAjoutBudget.setEnabled(false);
		btnAjoutBudget.setBounds(10, 476, 141, 23);
		LignesBudgets.add(btnAjoutBudget);
		btnAjoutBudget.addActionListener(this);
		
		btnMajPartage = new JButton("Modifier le partage");
		btnMajPartage.setEnabled(false);
		btnMajPartage.setBounds(155, 476, 123, 23);
		LignesBudgets.add(btnMajPartage);
		btnMajPartage.addActionListener(this);
				
		/* Listing des bons */		
		
		String rq_Budget = "SELECT Description,Champdoc,Budget FROM Lignes_budgetaires"; //WHERE Secteur='"+secteur+"'";
		try {
			rstm_LignesBud = new ResultSetTableModel(sqls, rq_Budget);
		} catch (ClassNotFoundException | SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(25, 453, 246, 12);
		LignesBudgets.add(separator_1);
		
		JLabel lbl_LigneBudDep = new JLabel("Budget d\u00E9pens\u00E9");
		lbl_LigneBudDep.setBounds(10, 253, 78, 14);
		LignesBudgets.add(lbl_LigneBudDep);
		
		lbl_LigneBudDepAff = new JLabel("");
		lbl_LigneBudDepAff.setBounds(117, 253, 150, 14);
		LignesBudgets.add(lbl_LigneBudDepAff);
		
		JLabel lbl_LigneBudRestant = new JLabel("<html><b>Budget restant</b></html>");
		lbl_LigneBudRestant.setBounds(10, 280, 85, 14);
		LignesBudgets.add(lbl_LigneBudRestant);
		
		lbl_LigneBudRestantAff = new JLabel("");
		lbl_LigneBudRestantAff.setFont(new Font("Tahoma", Font.BOLD, 11));
		lbl_LigneBudRestantAff.setBounds(117, 278, 105, 14);
		LignesBudgets.add(lbl_LigneBudRestantAff);
		
		JLabel lbl_LigneBudAjout = new JLabel("Ajout ");
		lbl_LigneBudAjout.setBounds(10, 305, 29, 14);
		LignesBudgets.add(lbl_LigneBudAjout);
		
		lbl_LigneBudAjoutAff = new JLabel("");
		lbl_LigneBudAjoutAff.setBounds(134, 305, 89, 14);
		LignesBudgets.add(lbl_LigneBudAjoutAff);
		
		JLabel lbl_LigneVolCmd = new JLabel("Volumes command\u00E9s");
		lbl_LigneVolCmd.setBounds(10, 327, 98, 14);
		LignesBudgets.add(lbl_LigneVolCmd);
		
		lbl_LigneVolCmdAff = new JLabel("");
		lbl_LigneVolCmdAff.setBounds(134, 327, 123, 14);
		LignesBudgets.add(lbl_LigneVolCmdAff);
		
		 lbl_LigneVolRecu = new JLabel("Volumes re\u00E7us");
		lbl_LigneVolRecu.setBounds(10, 352, 212, 14);
		LignesBudgets.add(lbl_LigneVolRecu);
		
		JLabel lbl_LignePrixMoyen = new JLabel("Prix moyen n");
		lbl_LignePrixMoyen.setBounds(10, 377, 62, 14);
		LignesBudgets.add(lbl_LignePrixMoyen);
		
		lbl_LignePrixMoyenAff = new JLabel("");
		lbl_LignePrixMoyenAff.setBounds(134, 377, 127, 14);
		LignesBudgets.add(lbl_LignePrixMoyenAff);
		
		JLabel lbl_LignePrixMoyenN1 = new JLabel("Prix moyen n-1");
		lbl_LignePrixMoyenN1.setBounds(10, 402, 72, 14);
		LignesBudgets.add(lbl_LignePrixMoyenN1);
		
		lbl_LignePrixMoyenN1Aff = new JLabel("");
		lbl_LignePrixMoyenN1Aff.setBounds(117, 402, 154, 14);
		LignesBudgets.add(lbl_LignePrixMoyenN1Aff);
		
		lbl_LigneInfos = new JLabel("Aucune ligne sélectionnée");
		lbl_LigneInfos.setFont(new Font("Calibri", Font.BOLD, 16));
		lbl_LigneInfos.setBounds(44, 220, 179, 20);
		LignesBudgets.add(lbl_LigneInfos);
		//rstb = new ResultSetTableModel(sqls,"Adulte");
		tb_BudInit = new JTable(rstm_LignesBud);
		
		tb_BudInit.setBounds(10, 70, 95, 208);
		JScrollPane jsB=new JScrollPane(tb_BudInit);
		jsB.setBounds(283, 10, 469, 523);
		jsB.setVisible(true);
		//aff_SectionDocsDep.setText(String.valueOf(docDepense));
		//aff_SectionDocsDep.setText(String.valueOf(depenseDocs));
		LignesBudgets.add(jsB);
		
		// aff_SectionDocsDep
		
		// String rq
		// try { String sqls.exec(rq); } catch {}
		// aff.setText()
		
		// String rq_DocsDep
		// String rq_FicDep 
		
		tb_BudInit.addMouseListener(new MouseAdapter() {
			  public void mousePressed(MouseEvent e) {
			    	String selected = tb_BudInit.getValueAt(tb_BudInit.getSelectedRow(), 1).toString();
			    	
			    	// Données à récupérer
			    	String rq_Name = "SELECT Description from Lignes_budgetaires WHERE Champdoc='"+ selected +"'";
			    	String rq_BudDepense = "SELECT Sum(Prix_rem) from Liste_Achats WHERE Champdoc='" + selected + "'";
			    	String rq_BudgetAjout = "SELECT Ajout_budget from Lignes_budgetaires WHERE Champdoc='" + selected + "'";
			    	String rq_NbDocumentsCmd = "SELECT Count(*) from Liste_Achats WHERE Champdoc='" + selected + "'";
			    	String rq_NbDocumentsRecu = "SELECT Count(*) from Liste_Achats WHERE Champdoc='" + selected + "' AND Statut='Reçu'";
			    	String rq_CoutMoyenAnMU = "SELECT PrixMoyenNMU from Lignes_budgetaires WHERE Champdoc='" + selected + "'";
			    	
			    	// Données à calculer : 
			    	// budget restant 
			    	// cout moyen en cours 
			    	try {
			    		
			    		String name = sqls.exec(rq_Name); 
			    		lbl_LigneInfos.setText(name);
			    		
			    		String budDepense = sqls.exec(rq_BudDepense); 
			    		lbl_LigneBudDepAff.setText(budDepense + " €");
			    		
			    		String budInit = sqls.exec("SELECT Budget FROM Lignes_budgetaires WHERE Champdoc='" + selected + "'");
			    		Double budRestant = Double.parseDouble(budInit) - Double.parseDouble(budDepense); 
			    		lbl_LigneBudRestantAff.setText(budRestant.toString());;
			    		
			    		String budAjout = sqls.exec(rq_BudgetAjout);
			    		lbl_LigneBudAjoutAff.setText(budAjout + " € ");
			    		
			    		String volCmd = sqls.exec(rq_NbDocumentsCmd);
			    		lbl_LigneVolCmdAff.setText(volCmd);
			    		
			    		String nbLivre = sqls.exec(rq_NbDocumentsRecu); 
			    		lbl_LigneVolRecu.setText("Volumes re\u00E7us : "+ nbLivre);
			    		
			    		Double coutMoyen = 0.00;
			    		coutMoyen = Double.parseDouble(budDepense) / Double.parseDouble(volCmd); 
			    		if (Double.isNaN(coutMoyen)) coutMoyen = 0.00;
			    		lbl_LignePrixMoyenAff.setText(coutMoyen.toString()); 
			    		
			    		lbl_LignePrixMoyenN1Aff.setText(sqls.exec(rq_CoutMoyenAnMU));

			    		if (budDepense.equals("null\n")) budDepense = "0.00";
			    		lbl_LigneBudDepAff.setText(budDepense); 
			    		// System.out.println(budDepense);
			    		
					} catch (IllegalStateException e1) {
						e1.printStackTrace();
					}
			    }
			  
			});
		
		String rq_SectionBudgetInit = "SELECT Budget_Init FROM Sections WHERE Nom_Section='" + section +"'"; 
		double totalSection = 0.00;
		String totalSectionStr = sqls.exec(rq_SectionBudgetInit);
		
		/* String rq_Reserve = "SELECT Reserve FROM Sections WHERE Nom_Section='" + secteurSelec +"'"; 
		double reserveSection = 0.00;
		String reserveSectionStr = sqls.exec(rq_Reserve);
		// aff_SectionReserve.setText(reserveSectionStr);
		reserveSection = Double.parseDouble(reserveSectionStr);*/
		
    	// Partage : budget fiction depensé
		String typeDoc = "fiction"; 
		Double ficDepense = budgetDepense(typeDoc, section);
        
        // Partage : budget documentaire depensé
        typeDoc = "doc";
        Double docDepense = budgetDepense(typeDoc, section);
		
		String rq_BudgetFiction = "SELECT Budget_Fiction FROM Sections WHERE Nom_Section='" + section +"'"; 
		double totalFiction = 0.00;
		String totalFictionStr = sqls.exec(rq_BudgetFiction);
		// totalFiction = Double.parseDouble(totalFictionStr);
		
        // aff_SectionPartFicEuros
        Double percentFiction = 0.00;
        percentFiction = totalFiction * 100 / totalSection;
        
        Double totalDoc = totalSection - totalFiction;
        
        // aff_SectionPartDocEuros
      //  Double percentDoc = 0.00;
      //  percentDoc = 100 - percentFiction;
        
    	// Partage : budget docs depensé
		String rq_docs = "SELECT Budget,Ajout_budget FROM Lignes_budgetaires WHERE bud_type='docs'";
    	ArrayList<String> ALDocs = sqls.executeQueryToArray(rq_docs); 
        double depenseDocs = 0.00;
        for(String elem: ALDocs) depenseDocs = depenseDocs + Double.parseDouble(elem);

        /*
         * SELECT sum(Prix_rem) 
        FROM Liste_Achats 
        INNER JOIN Lignes_budgetaires ON Liste_Achats.Champdoc = Lignes_budgetaires.Champdoc
        WHERE Lignes_budgetaires.bud_type='doc'
        AND Lignes_budgetaires.Secteur='Adulte';

         */
		
		
		return LignesBudgets;
	}

	private JPanel panelListeAchats() {
		JPanel ListeAchats = new JPanel();
		ListeAchats.setLayout(null);
		String rq_ListeAchats = "Select * FROM Liste_Achats";

		try {
			rstm_ListeAchats = new ResultSetTableModel(sqls, rq_ListeAchats);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		
		// Boutons
		btnImporter = new JButton("<html>Importer<br>une liste d'achats</html>");
		btnImporter.setBounds(10, 11, 156, 48);
		ListeAchats.add(btnImporter);
		btnImporter.setEnabled(false);
		
		JScrollPane js_ListeAchats = new JScrollPane();
		js_ListeAchats.setBounds(176, 11, 888, 730);
		ListeAchats.add(js_ListeAchats);
		
		tb_ListeAchats = new JTable(rstm_ListeAchats);
		js_ListeAchats.setViewportView(tb_ListeAchats);
		
		btnCmd = new JButton("<html>R\u00E9ceptionner<br>une commande<html>");
		btnCmd.setBounds(10, 70, 156, 48);
		ListeAchats.add(btnCmd);
		btnCmd.setEnabled(false);
		
		btnRservataires = new JButton("<html><center>Lister les r\u00E9servataires</center></html>");
		btnRservataires.setEnabled(false);
		btnRservataires.setBounds(10, 129, 156, 48);
		
		// ListeAchats.add(btnRservataires);
		
		btnRservataires.addActionListener(this);
		btnCmd.addActionListener(this);
		btnImporter.addActionListener(this);

		
		return ListeAchats;
	}
	
	private double budgetDepenseGeneral(String bud_type, String secteurSelec) {
		double r = 0.00;
		String rq_Liste = "SELECT sum(Prix_rem) "
				+ "FROM Liste_Achats " 
				+ "INNER JOIN Lignes_budgetaires ON Liste_Achats.Champdoc = Lignes_budgetaires.Champdoc"  
				+ "WHERE Lignes_budgetaires.bud_type='doc'"  
				+ "AND Lignes_budgetaires.Secteur='"+ secteurSelec +"'";
		ArrayList<String> ALFiction = sqls.executeQueryToArray(rq_Liste); 

		// On construit la requête 
		
		String bDG = "";
		for (int i = 0; i <= ALFiction.size(); i++) {
			if (i < ALFiction.size()-1) { bDG = bDG + "Champdoc='" +ALFiction.get(i)+"' OR "; }
			if (i == ALFiction.size()-1) { bDG = bDG + "Champdoc='" + ALFiction.get(i) + "'"; }
		}
		
		String rq_bDG = "SELECT sum(Prix_rem) FROM Liste_achats WHERE " + bDG;
		String ficDep = sqls.exec(rq_bDG);
		System.out.println("bDG : " + ficDep); 

		return r; 
	}

	private double budgetDepense(String bud_type, String secteurSelec) {
		double r = 0.00;
		
		// On fait une liste de tous les champdoc dont on veut récupéré les dépenses
		String rq_Liste = "SELECT Champdoc from Lignes_budgetaires WHERE bud_type='"+ bud_type +"' AND Secteur='"+ secteurSelec +"'";
		ArrayList<String> ALFiction = sqls.executeQueryToArray(rq_Liste); 
		// On construit la requête 
		String fic = "";
		for (int i = 0; i <= ALFiction.size(); i++) {
			if (i < ALFiction.size()-1) { fic = fic + "Champdoc='" +ALFiction.get(i)+"' OR "; }
			if (i == ALFiction.size()-1) { fic = fic + "Champdoc='" + ALFiction.get(i) + "'"; }
		}
		
		String rq_fiction = "SELECT sum(Prix_rem) FROM Liste_achats WHERE " + fic;
		String ficDep = sqls.exec(rq_fiction);
		
		System.out.println("ficDep : " + ficDep); 
		if (ficDep.equals("null")) {
			//r = Double.parseDouble(ficDep);
			System.out.println("bah c'est un string en fait");
		}
		System.out.println(ficDep.getClass());
		
		//r = Double.parseDouble(sqls.exec(rq_fiction));
		
		return r; 
	}
	
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == btnReInit) {
			String reInitQueryLA = "SELECT * FROM Liste_Achats";
			// String reInitQueryLBu = "SELECT * FROM Lignes_budgetaires";
			try {
				rstm_ListeAchats.setQuery(reInitQueryLA);
				// LignesBudget.updateTable(reInitQueryLBu); 
				jtb_Cmd.clearSelection();
				
				
			} catch (IllegalStateException | SQLException e) {
				e.printStackTrace();
			}
		}
		
		if (ae.getSource() == btnAddCmdCode) {
			addCmdCode addccmd = new addCmdCode(sqls);
			addccmd.setVisible(true);
		}
		
		if (ae.getSource() == btnAddFact) {
			addFact addFacture = new addFact(sqls);
			addFacture.setVisible(true);
		}
		
		
		if (ae.getSource() == btnRservataires) {	  
			// Renvoie la liste des personnes ayant réservés des documents
			if (jtb_Cmd.getValueAt(jtb_Cmd.getSelectedRow(), 0).toString() == "") {
				System.out.println("pas de commande sélectionnée");
			} else {
				String selectedCmd = jtb_Cmd.getValueAt(jtb_Cmd.getSelectedRow(), 0).toString();
		    	String rq = "SELECT Reservataire FROM Liste_Achats WHERE Code_cmd='" + selectedCmd + "' AND Reservataire <> ' - '";
		    	ArrayList<String> ALCodeCmd = sqls.executeQueryToArray(rq); 
		        Set<String> set = new HashSet<String>();
		        set.addAll(ALCodeCmd);
		        ArrayList<String> distinctList = new ArrayList<String>(set) ;
				
		        for(String elem: distinctList) {
		        	 System.out.println (elem);
		        }
			}
			
			/***
			 * TODO 
			 * Gestion des impressions des réservataires 
			 * https://code.google.com/archive/p/escprinter/source
			 * 
			 */
		}
		if (ae.getSource() == btnAjoutBudget) {
			
			// Var
			String budgetString = ""; 
			Double budget = 0.00;
		    // Récupération du nouveau budget
			budgetString = JOptionPane.showInputDialog("Quel budget alloué à la section " + section + ":", budget); 
		    budget = Double.parseDouble(budgetString);
		    // Requête
		    String rq_UpdateBudget = "UPDATE Sections " +
		    		"SET Budget_init='" + budget + "'" +
		    		"WHERE Nom_Section='" + section +"'";
		    sqls.exec(rq_UpdateBudget);
		    
		    // MAJ de l'affichage
		  	lbl_SectionBudget.setText("Budget : " + budgetString + " € ");
		}
		
		if (ae.getSource() == btnMajPartage) {
			
			// Var
			String budgetFicString = ""; 
			Double budgetFic = 0.00;
		    // Récupération du nouveau budget
			budgetFicString = JOptionPane.showInputDialog("Définir le budget FICTION pour " + section + ":", budgetFic); 
		    budgetFic = Double.parseDouble(budgetFicString);
		    // Requête
		    String rq_UpdateBudget = "UPDATE Sections " +
		    		"SET Budget_Fiction='" + budgetFic + "'" +
		    		"WHERE Nom_Section='" + section +"'";
		    sqls.exec(rq_UpdateBudget);
		    
		    // Calcul du budget doc 
		    Double budgetDocs = 0.00, budgetTot = 0.00;
		    String rq_BudgetTot = "SELECT Budget_init FROM Sections WHERE Nom_Section='" + section + "'";
		    budgetTot = Double.parseDouble(sqls.exec(rq_BudgetTot));
		    budgetDocs = budgetTot - budgetFic;
		    
		    // MAJ de l'affichage
		  	lbl_SectionFictionTotalAff.setText(budgetFicString + " € ");
		  	lbl_SectionDocsTotalAff.setText(budgetDocs.toString());
		}
		
		
		if (ae.getSource() == btnImporter) {
			System.out.println("csvgui");
			CsvLoaderGui csvGui = new CsvLoaderGui(sqls, section); 
			csvGui.setVisible(true); 
		}
		if (ae.getSource() == btnBon) {
			addBon add = new addBon(sqls); 
			add.setVisible(true);
		}
		if (ae.getSource() == btnCmd) {
			addCmd cmd = new addCmd(sqls);
			cmd.setVisible(true);
		}
	}
}
