package com.CoffeeSwing;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import java.awt.SystemColor;

public class error extends JDialog {

	private final JPanel contentPanel = new JPanel();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			Exception e = null; 
			error dialog = new error(e);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public error(Exception e) {
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(SystemColor.textHighlight);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER); 
		contentPanel.setLayout(null);
		 {
		 	JTextArea error_Area = new JTextArea();
		 	error_Area.setLineWrap(true);
		 	error_Area.setWrapStyleWord(true);
		 	error_Area.setForeground(SystemColor.controlLtHighlight);
		 	error_Area.setBackground(SystemColor.textHighlight);
		 	error_Area.setBounds(10, 11, 414, 200);
		 	error_Area.setEditable(false);
		 	error_Area.setEnabled(false);
		 	error_Area.setText(e.toString());
		 	
		 	
		 	contentPanel.add(error_Area);
		 }
				{
					JPanel buttonPane = new JPanel();
					buttonPane.setBackground(SystemColor.textHighlight);
					buttonPane.setBounds(0, 222, 434, 39);
					contentPanel.add(buttonPane);
			buttonPane.setLayout(null);
			{
				JButton okButton = new JButton("OK");
				okButton.setBounds(10, 0, 414, 23);
				okButton.setActionCommand("OK");
				
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}

}
