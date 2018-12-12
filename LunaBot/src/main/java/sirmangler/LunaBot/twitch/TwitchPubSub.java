package sirmangler.LunaBot.twitch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.JSONObject;

import sirmangler.LunaBot.discord.LunaBot;

public class TwitchPubSub implements Runnable {

	BufferedWriter w;
	public void establishConnection() {
		try {
			@SuppressWarnings("resource")
			Socket s = new Socket("pubsub-edge.twitch.tv", 443);
			
			BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream()));
			w = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			
			String line;	
			while ((line = r.readLine()) != null) {
				JSONObject obj = new JSONObject(line);
				if (obj.getString("type").equalsIgnoreCase("RECONNECT")) {
					s.close();
					r.close();
					establishConnection();
					return;
				} else if (obj.getString("type").equalsIgnoreCase("MESSAGE")) {
					JSONObject data = obj.getJSONObject("data");
					if (data.getString("topic").equalsIgnoreCase("channel-bits-events-v1.51375532")) {
						JSONObject message = data.getJSONObject("message");
						LunaBot.jda.getTextChannelById(LunaBot.data.announcementChannel).sendMessage(message.getString("user_name")+"has just given "+message.getInt("bits_used")+" bits!").queue();
					}
				}  else if (obj.getString("type").equalsIgnoreCase("MESSAGE")) {
					JSONObject data = obj.getJSONObject("data");
					if (data.getString("topic").equalsIgnoreCase("channel-subscribe-events-v1.51375532")) {
						JSONObject message = data.getJSONObject("message");
						if (message.has("recipient_user_name")) {
							
						} else {
							if (message.getInt("months") > 0) {
								LunaBot.jda.getTextChannelById(LunaBot.data.announcementChannel).sendMessage(message.getString("user_name")+"has resubscribed for "+message.getInt("months")+" months!").queue();
							} else {
								LunaBot.jda.getTextChannelById(LunaBot.data.announcementChannel).sendMessage(message.getString("user_name")+"has subscribed!").queue();
							}
						}
						
					}
				}
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void subscribeToTopics() {
		try {
			w.write("{\"type\": \"LISTEN\",\"data\": { \"topics\": [\"channel-bits-events-v1.44322889\"],\"auth_token\": \"cfabdegwdoklmawdzdo98xt2fo512y\"}}");
			w.write("{\"type\": \"LISTEN\",\"data\": { \"topics\": [\"channel-subscribe-events-v1.44322889\"],\"auth_token\": \"cfabdegwdoklmawdzdo98xt2fo512y\"}}");		
			w.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void pingClock() {
		new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(181000);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				ping();
			}
		}).start();
	}
	
	public void ping() {
		try {
			w.write("{\"type\":\"PING\"}");
			w.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		establishConnection();
	}
}
