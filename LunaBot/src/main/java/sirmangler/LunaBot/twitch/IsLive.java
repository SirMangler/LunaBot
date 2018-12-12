package sirmangler.LunaBot.twitch;

import java.awt.Color;
import java.io.IOException;

import org.json.JSONObject;

import net.dv8tion.jda.core.EmbedBuilder;
import okhttp3.Request;
import okhttp3.Request.Builder;
import sirmangler.LunaBot.discord.LunaBot;
import okhttp3.Response;

public class IsLive {

	boolean live;
	public IsLive() {
		new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(60000);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				check();
			}
		}).start();
	}
	
	public void check() {
		Builder builder = new Builder().url("https://api.twitch.tv/kraken/streams/51375532");
		builder.addHeader("Client-ID", "294fvy3epe78czy1bvi8rilcw5x0ai");
		Request r = builder.get().build();

		try {
			Response resp = LunaBot.okclient.newCall(r).execute();
			JSONObject obj = new JSONObject(resp.body().string());

			if (obj.isNull("stream")) {
				live = false;
				return;
			} else {			
				if (live != true) {
					JSONObject stream  = obj.getJSONObject("stream");
					live = true;
					EmbedBuilder embed = new EmbedBuilder();
					embed.setColor(Color.magenta);
					embed.setTitle("**KelsiLynStar has gone live!**");
					embed.setDescription("\n"+obj.getJSONObject("channel").getString("status")+"\n\nStar has gone live playing: "+stream.getString("game")+"\nhttps://www.twitch.tv/kelsilynstar");
					LunaBot.jda.getTextChannelById(LunaBot.data.announcementChannel).sendMessage(embed.build()).queue();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

	