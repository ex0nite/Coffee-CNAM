package com.CoffeeGui;

import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.Coffee.JDBCAdapter;

public class JPanelRS extends JPanel{
    JTable jt;
    public JPanelRS(ResultSetTableModel rsLA){
    	System.out.println(rsLA.toString()); 
        JTable table = new JTable(rsLA);
        table.setPreferredScrollableViewportSize(new Dimension(980,800));
        table.setFillsViewportHeight(true);
        
        table.setAutoCreateRowSorter(true);
        
        JScrollPane js=new JScrollPane(table);
        js.setVisible(true);
        add(js);
        System.out.println("ajout" + js.toString());
    }
}