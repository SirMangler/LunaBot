package sirmangler.LunaBot.twitch;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Request.Builder;
import sirmangler.LunaBot.discord.LunaBot;

public class TranslateUserId {

	public static String getDisplayName(String id) {			
		Builder builder = new Builder().url("https://api.twitch.tv/helix/users?id="+id);
		builder.addHeader("Client-ID", "294fvy3epe78czy1bvi8rilcw5x0ai");
		Request r = builder.get().build();

		try {
			Response resp = LunaBot.okclient.newCall(r).execute();
			JSONArray arr = new JSONArray(resp.body().string());
			return ((JSONObject) arr.get(0)).getString("display_name");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * @author SirMangler (catty610)
	 * 2 Oct 2018
	 */
}
