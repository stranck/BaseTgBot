package ovh.stranck.BaseTgBot;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class Settings {
	public static String botUser;
	public static String telegramToken;
	public static String channel;
	public static String group;
	public static boolean maintenance;	
	public static ArrayList<Long> admin = new ArrayList<Long>();

	
	public static void loadSettings() throws Exception {
    	App.LOGGER.config("Loading settings");
    	JSONObject data = new JSONObject(new String(Files.readAllBytes(Paths.get("settings.json"))));
    	
		telegramToken = data.getString("telegramToken");
		channel = data.getString("channel");
		group = data.getString("group");
		
		App.LOGGER.config("Loading dynamic settings");
		JSONObject dynamic = new JSONObject(new String(Files.readAllBytes(Paths.get("dynamicSettings.json"))));
		maintenance = dynamic.getBoolean("maintenance");
		
		App.LOGGER.config("Connecting to DB");
		UsersDB.getConn();
	    UsersDB.createUsersTable();
	    
		App.LOGGER.config("Loading admins");
		JSONArray admins = data.getJSONArray("admin");
		for(int i = 0; i < admins.length(); i++)
			admin.add(admins.getLong(i));
		
	}
	
	public static void saveDynamicSettings() {
		try {
			JSONObject json = new JSONObject();
			json.put("maintenance", maintenance);
			Files.write(Paths.get("dynamicSettings.json"), json.toString().getBytes(), CREATE, TRUNCATE_EXISTING);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static boolean hasPermissions(long id){
		return isAdmin(id);
	}
	public static boolean isAdmin(long id){
		for(long user : admin)
			if(user == id) return true;
		return false;
	}
}
