package server;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import org.json.JSONObject;


public class ConnectionThread extends Thread {
	private ArrayList <ConnectionThread> list;
	private Socket socket;
	private String nickname;
	private boolean flag=false;
	public DataInputStream in = null;
	public DataOutputStream out = null;
	
		ConnectionThread (Socket socket,ArrayList<ConnectionThread> list) throws IOException{
			this.socket=socket;
			this.list=list;
			start();
		}
		
	public void run() {
		try {
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
				while (!flag){
					sleep(1000);
					if (in.available() > 0){
						String buf = "";
						int len = in.readInt();
						char bufer[] = new char[len];
							for(int i=0;i<len;i++){
								bufer[i]=in.readChar();
								buf+=bufer[i];
								}
							String method = Utility.getFunction(buf);
								switch (method){
									case "connect": {
														nickname = Utility.getNickname(buf);
														boolean flag = true;
														for(ConnectionThread connect:list)
															if((connect!=this&&connect.nickname.equals(this.nickname))||this.nickname.equals("all"))
																flag=false;
																	if(flag && BasicServer.authUser(nickname))
																		Utility.sendMsg(out, Utility.createResultJson("ok").toString());
																	else if(!this.nickname.equals("all")&&!this.nickname.equals("Всем"))
																		Utility.sendMsg(out, Utility.createResultJson("nickname_is_already_used").toString());
																else 
																	Utility.sendMsg(out, Utility.createResultJson("nickname_is_system_reserved").toString());
															break;}
									case "send_msg": {  
														String nick = Utility.getParams(buf,0);
														String msg = Utility.getParams(buf,1);
														boolean msgFlag = false;
															if(nick.equals("all")){ 
																for (ConnectionThread connect:list){
																	Utility.sendMsg(connect.out,Utility.createMsgJson(msg,this.nickname).toString());
																	msgFlag=true;
																	}
															if (msgFlag)
																Utility.sendMsg(out, Utility.createResultJson("completed").toString());
															else 
																Utility.sendMsg(out, Utility.createResultJson("users_not_found").toString());
															}
														else {
															for(ConnectionThread connect:list)
																if(connect.nickname.equals(nick)){
																	Utility.sendMsg(connect.out,Utility.createMsgJson(msg,this.nickname).toString());
																	msgFlag=true;
																	}
															if(msgFlag)
																Utility.sendMsg(out, Utility.createResultJson("completed").toString());
															else 
																Utility.sendMsg(out, Utility.createResultJson("user_not_found").toString());
														}
													break;}
									case "get_list": {
														String[] user = BasicServer.selectUser();
														Utility.sendMsg(out, Utility.createListJson(user).toString());
														break;
													}
									case "disconnect": {
														try{socket.close();}
														catch (IOException e){System.err.println(e.toString());}
														flag=true;
														break;}
									default:break;
								}
				
					}
				}
			}
		catch (IOException e) {
	         System.err.println("IO Exception"+e.toString());
	      }
		catch (InterruptedException e){
			System.err.println(e.toString());}
				finally{
						list.remove(this);
				}
	}
	

}
