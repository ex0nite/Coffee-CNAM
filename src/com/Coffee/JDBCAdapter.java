package com.Coffee;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Vector;
import java.io.File;
import java.sql.*;
import javax.swing.table.AbstractTableModel;

import com.CoffeeSwing.error;

import javax.swing.JFileChooser;
import javax.swing.event.TableModelEvent;
import javax.swing.filechooser.FileSystemView;

public class JDBCAdapter extends AbstractTableModel {
	public static final String pilote = "net.ucanaccess.jdbc.UcanaccessDriver"; /* Driver pour BDD Access */
	// public static final String host = "jdbc:ucanaccess://Y:/Bibliotheques/GestionDB/DB_Coffee1.mdb"; /* Adresse de la BDD */ 
	public static final String host = "jdbc:ucanaccess://./DB_Coffee1.mdb";
	public static final String login = "";// USER
	public static final String pw = "";//PASSWORD
	
    public Connection   connection;
    Statement           statement;
    ResultSet           resultSet;
    String[]            columnNames = {};
    Vector				rows = new Vector();
    ResultSetMetaData   metaData;
    private String url;

    public JDBCAdapter(String _url) {
    	String driverName = pilote; 
    	String user = login;
    	String passwd = pw;     	
    	//String url = host; 
    	if (_url == "") url = host; 
    	else { url = "jdbc:ucanaccess://" + _url; }
    	
        try {
            Class.forName(driverName);
            System.out.println("[BDD] Connexion");
            System.out.println(url);
            connection = DriverManager.getConnection(url, user, passwd);
            //statement = connection.createStatement();
            statement = connection.createStatement(	ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE );
        }
        catch (ClassNotFoundException ex) {
            System.err.println("[BDD] Impossible de trouver les drivers");
            System.err.println(ex);
        }
        catch (SQLException ex) {
            System.err.println("[BDD] Impossible de se connecter a cette base");
            System.err.println(ex);
        }
     }
    
    public void setUrl(String _url) {
    	url = _url; 
    }
    
    public ArrayList<String> executeQueryToArray(String query) {
    	ArrayList<String> qTA = new ArrayList<String>(); 
    	if (connection == null || statement == null) {
            System.err.println("[BDD] Impossible d'executer cette requête : aucune base");
            
        }
        try {
            resultSet = statement.executeQuery(query);
            metaData = resultSet.getMetaData();

            int numberOfColumns =  metaData.getColumnCount();
            columnNames = new String[numberOfColumns];
            // Get the column names and cache them.
            // Then we can close the connection.
            for(int column = 0; column < numberOfColumns; column++) {
                columnNames[column] = metaData.getColumnLabel(column+1);
            }

            // Get all rows.
            rows = new Vector();
            while (resultSet.next()) {
                Vector newRow = new Vector();
                for (int i = 1; i <= getColumnCount(); i++) {
                	qTA.add(resultSet.getObject(i).toString());
                }
               rows.addElement(qTA);
            }
            
           /* for (String s : qTA) {
            	System.out.println("qTA:" + s);
            } /**/
            
            //  close(); Need to copy the metaData, bug in jdbc:odbc driver.
            fireTableChanged(null); // Tell the listeners a new table has arrived.
        }
        catch (SQLException ex) {
        	error e = new error(ex); 
        	e.setVisible(true);
            System.err.println(ex);
        }
    	return qTA; 
    }
    
    public String exec_wMeta(String command) {
  	  String s = ""; 
  	  s += getMetaData(command); 
  	  s += exec(command); 
  	  return s; 
    }
    
    public String exec_p(String command) {
  	  // Methode de récupération de mdp (pas de retour chariot dans le return)
  	  String result = "";
  	    try {
  	      PreparedStatement pstmt = connection.prepareStatement(command);
  	      pstmt.executeQuery();
  	      
  	      ResultSet rs = statement.executeQuery(command);
  	      ResultSetMetaData resultSchema = rs.getMetaData();
  	      
  	      // récupère le schéma du résultat (nom des colonnes, type, ...)
  	      int nbCol = resultSchema.getColumnCount();

  	      // affiche le contenu des colonnes
  	      while (rs.next()) {
  	        for (int i = 1; i <= nbCol; i++) {
  	          result += rs.getString(i);
  	          if (i < nbCol) result += "\t";
  	        }
  	        // result += "\n";
  	      }
  	      
  	    }
  	    catch(SQLException e) {
  	      result += "[SQLService] [!] - Exception ->" + e.getMessage() + "\n";
  	    }
  	    return result;
    }
    
    public String exec(String command) {
    	String r = ""; 
      if (command.toUpperCase().startsWith("SELECT")) r = execQuery(command);
      else r = execUpdate(command);
      return r; 
    }

    public String getMetaData(String command) {
  	  String result = "";
  	    try {
  	        ResultSet rs = statement.executeQuery(command);
  	        ResultSetMetaData resultSchema = rs.getMetaData();
  	      
  	      // récupère le schéma du résultat (nom des colonnes, type, ...)
  	      int nbCol = resultSchema.getColumnCount();
  	      
  	      // affiche les entêtes des colonnes
  	      for (int i = 1; i <= nbCol; i++) {
  	        result += resultSchema.getColumnLabel(i);
  	        if (i < nbCol) result += "\t";
  	      }
  	      result += "\n";	      
  	    }
  	    catch(SQLException e) {
  	      result += "[SQLService] [!] - Exception ->" + e.getMessage() + "\n";
  	    }
  	    return result;
    }
    
    public ResultSet getRs(String command) {
  	  ResultSet rs = null; 
  	  try {
  	      PreparedStatement pstmt = connection.prepareStatement(command);
  	      rs = pstmt.executeQuery();
  	      while (rs.next()) {
  	    	  System.out.println("getRs : " + rs.getString(1));
  	      }
  	      
  	      // rs = statement.executeQuery(command);
  	  } catch(SQLException e) {
  		  e.printStackTrace();
  	  }
  	  return rs; 
    }

    private String execQuery(String command) {
      String result = "";
      try {
        PreparedStatement pstmt = connection.prepareStatement(command);
        pstmt.executeQuery();
        
        ResultSet rs = statement.executeQuery(command);
        ResultSetMetaData resultSchema = rs.getMetaData();
        
        // récupère le schéma du résultat (nom des colonnes, type, ...)
        int nbCol = resultSchema.getColumnCount();
     
        // affiche le contenu des colonnes
        while (rs.next()) {
          for (int i = 1; i <= nbCol; i++) {
              result += rs.getString(i);
              if (i < nbCol) result += "\t";
              System.out.println("execQuery : \"" + command + "\" - " + resultSchema.getColumnTypeName(i) + ", " + result + ", " + result.getClass());
          }
          // gestion basique des requêtes sans réponses
          if (result.equals("null")) {
        	  result = "0.00";
          }
          result += "\n";
        }
        
      }
      catch(SQLException e) {
        result += "[!] [SQLService - execQuery] - Exception ->" + e.getMessage() + "\n";
      }
      return result;
    }    
    
    private String execUpdate(String command) {
      String result = "";
      try {
        statement.executeUpdate(command);
      }
      catch(SQLException e) {
        result = "[SQLService] [!] - Exception ->" + e.getMessage() + "\n";
      }
      return result;
    }

    public void executeQuery(String query) {
        if (connection == null || statement == null) {
            System.err.println("[BDD] Impossible d'executer cette requête : aucune base");
            return;
        }
        try {
        	System.out.println("executeQuery : " + query); 

            resultSet = statement.executeQuery(query);
            metaData = resultSet.getMetaData();

            int numberOfColumns =  metaData.getColumnCount();
            columnNames = new String[numberOfColumns];
            // Get the column names and cache them.
            // Then we can close the connection.
            for(int column = 0; column < numberOfColumns; column++) {
                columnNames[column] = metaData.getColumnLabel(column+1);
            }

            // Get all rows.
            rows = new Vector();
            while (resultSet.next()) {
                Vector newRow = new Vector();
                for (int i = 1; i <= getColumnCount(); i++) {
                	newRow.addElement(resultSet.getObject(i));
                }
                rows.addElement(newRow);
            }
            //  close(); Need to copy the metaData, bug in jdbc:odbc driver.
            fireTableChanged(null); // Tell the listeners a new table has arrived.
        }
        catch (SQLException ex) {
        	error e = new error(ex); 
        	e.setVisible(true);
            System.err.println(ex);
        }
    }

    public void close() throws SQLException {
        System.out.println("[BDD] Déconnexion");
        resultSet.close();
        statement.close();
        connection.close();
    }

    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

    //////////////////////////////////////////////////////////////////////////
    //
    //             Implementation of the TableModel Interface
    //
    //////////////////////////////////////////////////////////////////////////

    // Gestion des MetaData

    public String getColumnName(int column) {
        if (columnNames[column] != null) {
            return columnNames[column];
        } else {
            return "";
        }
    }

    public Class getColumnClass(int column) {
        int type;
        try {
            type = metaData.getColumnType(column+1);
        }
        catch (SQLException e) {
            return super.getColumnClass(column);
        }

        switch(type) {
        case Types.CHAR:
        case Types.VARCHAR:
        case Types.LONGVARCHAR:
            return String.class;

        case Types.BIT:
            return Boolean.class;

        case Types.TINYINT:
        case Types.SMALLINT:
        case Types.INTEGER:
            return Integer.class;

        case Types.BIGINT:
            return Long.class;

        case Types.FLOAT:
        case Types.DOUBLE:
            return Double.class;

        case Types.DATE:
            return java.sql.Date.class;

        default:
            return Object.class;
        }
    }

    public boolean isCellEditable(int row, int column) {
       try {
            return metaData.isWritable(column+1);
       }
        catch (SQLException e) {
            return false;
        }
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    // Data methods

    public int getRowCount() {
        return rows.size();
    }

    public Object getValueAt(int aRow, int aColumn) {
        Vector row = (Vector)rows.elementAt(aRow);
        return row.elementAt(aColumn);
    }

    public String dbRepresentation(int column, Object value) {
        int type;

        if (value == null) {
            return "null";
        }

        try {
            type = metaData.getColumnType(column+1);
        }
        catch (SQLException e) {
            return value.toString();
        }

        switch(type) {
        case Types.INTEGER:
        case Types.DOUBLE:
        case Types.FLOAT:
            return value.toString();
        case Types.BIT:
            return ((Boolean)value).booleanValue() ? "1" : "0";
        case Types.DATE:
            return value.toString(); // This will need some conversion.
        default:
            return "\""+value.toString()+"\"";
        }

    }

    public void setValueAt(Object value, int row, int column) {
        try {
            String tableName = metaData.getTableName(column+1);
            // tableName != null obligatoire
            if (tableName == null) {
                System.out.println("setValueAt : Table name returned null.");
            }
            String columnName = getColumnName(column);
            String query =
                "update "+tableName+
                " set "+columnName+" = "+dbRepresentation(column, value)+
                " where ";
            // We don't have a model of the schema so we don't know the
            // primary keys or which columns to lock on. To demonstrate
            // that editing is possible, we'll just lock on everything.
            for(int col = 0; col<getColumnCount(); col++) {
                String colName = getColumnName(col);
                if (colName.equals("")) {
                    continue;
                }
                if (col != 0) {
                    query = query + " and ";
                }
                query = query + colName +" = "+
                    dbRepresentation(col, getValueAt(row, col));
            }
            System.out.println("setValueAt : " + query);
            System.out.println("Not sending update to database");
             statement.executeQuery(query);
        }
        catch (SQLException e) {
            //     e.printStackTrace();
            System.err.println("Update failed");
        }
        Vector dataRow = (Vector)rows.elementAt(row);
        dataRow.setElementAt(value, column);

    }
}
