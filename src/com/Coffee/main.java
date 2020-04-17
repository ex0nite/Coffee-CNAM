package com.Coffee;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;

import com.CoffeeGui.CoffeeWin;
import com.CoffeeGui.connexion;
import com.CoffeeSwing.*;

public class main {
	static JDBCAdapter n; 

	public static void main(String[] args) {
		String bdd = getBDD(); 
		try {
			n = new JDBCAdapter(bdd); 
			// connexion w = new connexion(n); // send w to coffeewin
			CoffeeWin p = new CoffeeWin(n);
			p.setVisible(true);
			
	  } catch (Exception e) {
			e.printStackTrace();
	  } 
	}
	
	
	private static String getBDD() {
		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory()); 
		String fileName = "";
		 
		int returnValue = jfc.showOpenDialog(null);
		File selectedFile = null;
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			selectedFile = jfc.getSelectedFile();
			// System.out.println(selectedFile.getAbsolutePath());
			fileName = selectedFile.getAbsolutePath().toString();
		}
   
		
		return fileName; 
	}
}
