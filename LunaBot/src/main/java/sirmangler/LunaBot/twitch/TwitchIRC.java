package sirmangler.LunaBot.twitch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;

import sirmangler.LunaBot.discord.LunaBot;

public class TwitchIRC implements Runnable {
	
	/**
	 * @author SirMangler (catty610)
	 * 2 Apr 2018
	 */
	
	BufferedWriter writer;
	boolean connected = false;
	public void StartTwitchIRC() {
		try {
			Socket sock = new Socket("irc.twitch.tv", 6667);
			BufferedReader reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			writer = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
			
			writeLine("CAP REQ :twitch.tv/membership");
			writeLine("CAP REQ :twitch.tv/tags");
			writeLine("CAP REQ :twitch.tv/commands");
			writeLine("PASS oauth:ulxndoa17gtirnq667g0q33yj478yt");
			writeLine("NICK kelsilunabot");
			writeLine("USER kelsilunabot 8 *:kelsilunabot");
			writeLine("PING");

			String line;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("PING")) {
					writeLine(line.replace("PING", "PONG"));
				}
				
				if (line.equalsIgnoreCase(":tmi.twitch.tv 376 kelsilunabot :>")) {
					connected = true;
					writeLine("JOIN #kelsilynstar");
				}

				if (connected) {
					if (line.contains("PRIVMSG #kelsilynstar :")) {
						String message = line.substring(line.indexOf(":", 1)+1);
						String name = line.substring(1, line.indexOf('!'));
						
						//System.out.println(" [:] "+name+": "+message);
						String response = LunaBot.ai.eval(line, name);
						if (response != null) {
							queueMessage(response, "#kelsilynstar");
						}
					}// else if (line.con)
				}
				
			}
			sock.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeLine(String line) {
		try {
			writer.write(line+"\r\n");
			System.out.println(" < "+line);
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	Queue<String> messages = new LinkedList<String>();
	Thread queue;
	public void queueMessage(String msg, String chan) {	
		if (queue == null || !queue.isAlive()) {
			queue = new Thread(new Runnable() {
				
				@Override
				public void run() {
					String message;
					while ((message = messages.poll()) != null) {
						try {
							writeLine(message);
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			});
			queue.start();
		}
		
		messages.add("PRIVMSG #"+chan+" :"+msg);
	}

	@Override
	public void run() {
		StartTwitchIRC();
	}
	
}
