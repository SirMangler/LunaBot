package sirmangler.LunaBot.twitch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONObject;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;
import sirmangler.LunaBot.discord.LunaBot;

public class FollowAlert {

	public FollowAlert() {
		startServer();
		
		new Thread(() -> {
			while (true) {
				subscribeFollowers();
				try {
					Thread.sleep(518400000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public void subscribeFollowers() {
		Builder builder = new Builder().url("https://api.twitch.tv/helix/webhooks/hub");
		builder.addHeader("Client-ID", "294fvy3epe78czy1bvi8rilcw5x0ai");
		
		JSONObject object = new JSONObject();
		object.put("hub.mode", "subscribe");
		object.put("hub.callback", "http://34.217.133.178:57072/");
		object.put("hub.topic", "https://api.twitch.tv/helix/users/follows?first=1&to_id=51375532");
		object.put("lease_seconds", "518400");
		RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), object.toString());
		
		Request r = builder.post(body).build();
		try {
			Response resp = LunaBot.okclient.newCall(r).execute();
			//JSONObject obj = new JSONObject(resp.body().string());
			//System.out.println(obj.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void startServer() {
		Undertow server = Undertow.builder()
                .addHttpListener(57072, "localhost")
                .setHandler(new HttpHandler() {
                    @Override
                    public void handleRequest(final HttpServerExchange exchange) throws Exception {
                        if (exchange.getRequestMethod().equalToString("GET")) {
                        	BufferedReader reader = null;

                        	try {
                        	    exchange.startBlocking();
                        	    reader = new BufferedReader(new InputStreamReader(exchange.getInputStream()));

                        	    String line;
                        	    while((line = reader.readLine()) != null) {
                        	    	if (line.startsWith("hub.challenge=")) {
                        	    		exchange.getResponseSender().send(line.replace("hub.challenge=", ""));
                        	    		reader.close();
                        	    		return;
                        	    	}
                        	    }
                        	} catch(IOException e) {
                        	    e.printStackTrace();
                        	}
                        } else if (exchange.getRequestMethod().equalToString("POST")) {
                        	BufferedReader reader = null;
                        	StringBuilder builder = new StringBuilder();

                        	try {
                        	    exchange.startBlocking();
                        	    reader = new BufferedReader(new InputStreamReader(exchange.getInputStream()));

                        	    String line;
                        	    while((line = reader.readLine()) != null) {
                        	    	builder.append(line);
                        	    }
                        	} catch(IOException e) {
                        	    e.printStackTrace();
                        	}
                        	
                        	JSONObject source = new JSONObject(builder.toString());
                    		JSONObject data;
                    		try {
                    			data = source.getJSONObject("data");
                    		} catch (Exception e) {
                    			data = source.getJSONArray("data").getJSONObject(0);
                    		}
                        	
                        	String from_id = data.getString("from_id");
                        	if (LunaBot.data.followers.contains(Integer.parseInt(from_id))) {
                        		return;
                        	} else {
                            	LunaBot.jda.getTextChannelById(LunaBot.data.announcementChannel).sendMessage(data.getString("from_name") + "has just followed!").complete();
                            	
                            	LunaBot.data.addFollower(Integer.parseInt(data.getString("from_id")));
                        	}
                        }
                    } 
                }).build();
        server.start();
	}

}
	

