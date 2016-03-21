package server;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONTokener;

public class Utility {
	static String getFunction(String message){
		try{
			JSONObject object = (JSONObject) new JSONTokener(message).nextValue();
			message = object.getString("method");
			}
		catch (JSONException e) {
			e.printStackTrace();
			}
		return message; 
	}
	static String getNickname(String message){
		try{
			JSONObject object = (JSONObject) new JSONTokener(message).nextValue();
			JSONArray params = object.getJSONArray("params");
			message = params.getString(0);
			}
		catch (JSONException e) {
			e.printStackTrace();
			}
		return message;
	}
	static String getParams(String message,int flag){
		String param="";
		try{
			JSONObject object = (JSONObject) new JSONTokener(message).nextValue();
			JSONArray params = object.getJSONArray("params");
				switch (flag){
					case 0: param = params.getString(0);
							break;
					case 1: param = params.getString(1);
							break;
					}
			}
		catch (JSONException e) {
			e.printStackTrace();
			}
		return param;
	}
	static JSONObject createResultJson (String message){
		JSONObject obj=null;
		try {
			obj = new JSONObject ();
			obj.put("result",message);
		}
		catch (JSONException e){
			e.printStackTrace();
		}
		return obj;
	}
	
	static JSONObject createMsgJson (String message,String nickname){
		JSONObject obj=null;
		try {
			obj = new JSONObject ();
			obj.put("result","send_msg");
			JSONArray param = new JSONArray();
			param.put(nickname);
			param.put(message);
			obj.put("params", param);
		}
		catch (JSONException e){
			e.printStackTrace();
		}
		return obj;
	}
	
	static JSONObject createListJson (String[] user){
		JSONObject obj=null;
		try {
			obj = new JSONObject ();
			obj.put("result","list");
			JSONArray param = new JSONArray();
			for (int i=0;i<user.length;i++)
				param.put(user[i]);
			obj.put("params", param);
		}
		catch (JSONException e){
			e.printStackTrace();
		}
		return obj;
	}
	
	
	static int sendMsg(DataOutputStream out,String msg){
        try{
        	out.writeInt(msg.length());
        	char buf[] = new char[msg.length()];
        		for (int i = 0; i < msg.length(); i++) {
        			buf[i] = msg.charAt(i);
        			out.writeChar(buf[i]);
        		}
        	out.flush();
        }
        catch (IOException e){
        	return -1;
        	}
        return 0;
    }
}
