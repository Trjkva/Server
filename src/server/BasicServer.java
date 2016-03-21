package server;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.sql.*;



/*
 *	Connection package JSON {"method":"connect",
 *								"params":"nickname"}
 *	return package JSON {"result":"ok|nickname_is_already_used"}
 *
 *	Send message package JSON {"method":"send_msg",
 *							   "params":"[nickname","msg"]}
 *	return package JSON {"result":"completed|error"}
 *	
 *	Get nickname JSON {"method":"get_list"}
 *	return JSON {"result":"list",
 *					"params":["",...,""]}
 *
 *	Destroy socket JSON {"method":"disconnect"}
 */

public class BasicServer {
	static ArrayList <ConnectionThread> list;
	static final int PORT = 8000;
	static Connection dbc;
	static Statement st;
	static PreparedStatement ps;
	static ResultSet rsNickname,rsUser;
	
	static int getResultSetCount(ResultSet rs){
		int count = 0;
			try {
				while(rs.next())count++;
			}
		catch(SQLException e) {
		    return -1;
		}
		return count;
		}
	
	static boolean authUser(String nickname){
		try{
			String query = "select * from User where login = ?";
			ps = dbc.prepareStatement(query);
			ps.setString(1, nickname);
			rsNickname = ps.executeQuery();			
				if(getResultSetCount(rsNickname)==0){
					query = "insert into User (login) values (?)";
					ps = dbc.prepareStatement(query);
					ps.setString(1, nickname);
					ps.executeUpdate();
					return true;
				}
		}
	catch (Exception e){
		System.out.println(e.toString());
	}
		return true;
	}

	static String[] selectUser(){
			String query = "select * from User";
			String[] user = null;
			try{
				rsUser = st.executeQuery(query);
				user = new String [getResultSetCount(rsUser)];
				rsUser=st.executeQuery(query);
				int i=0;
					while (rsUser.next()){
						user[i]=rsUser.getString(1);
						i++;
						}
			}
			catch(SQLException e){
				System.err.println(e.toString());
				}
		return user;
	}
	   
public static void main(String[] args) throws IOException {
ServerSocket servSock = new ServerSocket(PORT);
	list = new ArrayList ();
	      try {
	         while (true) {
	            Socket socket = servSock.accept();
	            	try {
	            		Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");      
	            		dbc=DriverManager.getConnection("jdbc:ucanaccess://F:/java/eclipse/projects/server/DB.accdb");//Ïóòü ê ÁÄ.
	            		st=dbc.createStatement(); 
	            		ConnectionThread thread = new ConnectionThread(socket,list);
	            		list.add(thread);
	            	}
	               catch(Exception e){
	            	   System.err.println(e.getMessage());
	               }
	         }
	      }
	      finally {
	    	  	try {
	    	        servSock.close();
	    	      } 
	    	  	catch(IOException e) {
	    	         e.printStackTrace(); 
	    	      }
	      		}
	 }

}
	   
	
