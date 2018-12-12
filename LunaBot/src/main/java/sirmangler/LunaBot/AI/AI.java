package sirmangler.LunaBot.AI;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import sirmangler.LunaBot.discord.LunaBot;

public class AI {

	/**
	 * @author SirMangler (catty610)
	 * 3 Apr 2018
	 */
	
	long timer;
	public AI() {
		loadDictionary();
		startClock();
	}
	
	public void startClock() {
		Thread thr = new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(1000);
					
					for (Subject subject : subjects) {
						if (subject.itimer != 0) {
							subject.itimer--;
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				timer++;
			}
		});
		
		thr.start();
	}
	
	Subject[] subjects;
	public void loadDictionary() {
		subjects = new Subject[7];
		
		String[] lines = new String[] { "why do your emotes start with xxsupe", "why do the emotes start with xxsupe" };
		String[] responses = new String[] {"Twitch doesn't allow you to change the prefixes of your emotes until you're partnered, so until then the emotes will all start like so.",
		"Star isn't yet a partner, so she cannot yet change the emote prefixes!" };
		HashMap<String, Integer> keywords = new HashMap<String, Integer>();
		keywords.put("emote", 3);
		keywords.put("xxsupe", 3);
		keywords.put("do", 1);
		keywords.put("does", 1);
		keywords.put("your", 1);
		keywords.put("the", 1);
		
		subjects[0] = new Subject(lines, responses, 5, keywords, false, 2, "emotes");

		lines = new String[] { "why did star change her name", "why was stars name changed", "why did you switch your screenname" };
		responses = new String[] { "The answer is quite simple, Star simply outgrew the name and chose to rebrand the channel!" };
		keywords = new HashMap<String, Integer>();
		keywords.put("name", 3);
		keywords.put("star", 4);
		keywords.put("old", 2);
		keywords.put("you", 1);
		keywords.put("did", 1);
		keywords.put("why", 2);
		keywords.put("change", 2);
		
		subjects[1] = new Subject(lines, responses, 5, keywords, false, 2, "namechange");

		lines = new String[] { "how do i donate to your stream", "how do i donate", "how do you donate" };
		responses = new String[] { "It's very kind of you to consider donating. You can do so by going to my streamlabs page: https://streamlabs.com/xxsupernovastar",
				"Donations aren't required but we very much appreciate them, you can do so by checking out https://streamlabs.com/xxsupernovastar! Thank you all dearly.",
				"psst, you should make star jump by triggering a voice alert: https://streamlabs.com/xxsupernovastar"};
		keywords = new HashMap<String, Integer>();
		keywords.put("donate", 4);
		keywords.put("money", 3);
		keywords.put("do", 2);
		keywords.put("to", 2);
		
		subjects[2] = new Subject(lines, responses, 7, keywords, false, 2, "donate");
		
		responses = new String[] { "Hey, {username}!", "What's up, {username}!", "Welcome to the Constellation", "Greetings from the constellation, {username}." };
		lines = new String[] { "hello", "greetings", "sup", "howdy", "yo", "heya", "hey", "hi" };
		keywords = new HashMap<String, Integer>();
		
		subjects[3] = new Subject(lines, responses, 1, keywords, true, 10, "greeting");

		lines = new String[] { "hey lunar", "greetings lunar", "sup lunar", "howdy lunar", "whats up lunar", "yo lunar", "heya lunar", "hey lunar" };
		responses = new String[] { "Hey {username}!", "Greetings from the constellation, {username}.", "Hi. How has your day been?", "Hello there, {username}!" };
		keywords = new HashMap<String, Integer>();
		keywords.put("luna", 4);
		
		subjects[4] = new Subject(lines, responses, 5, keywords, false, 2, "lunagreet");

		lines = new String[] { "goodbye lunar", "good night", "cya lunar" };
		responses = new String[] { "Farewell, until I see you next, {username}", "May your return be swift.", "Goodbye already? Goodbye." };
		keywords = new HashMap<String, Integer>();
		keywords.put("luna", 4);
		keywords.put("night", 2);
		keywords.put("bye", 2);
		keywords.put("cya", 2);
		
		subjects[5] = new Subject(lines, responses, 5, keywords, false, 2, "lunafarewell");
		
		lines = new String[] { "what are the perks of being a subscriber", "what do you get when you subscribe", "what does subscribing do", "what can i get from subscribing" };
		responses = new String[] { "Thanks for the consideration! You get many things such as subscriber only emotes, a magnificent sound announcing your arrival, and access to our secret subscriber only channel on discord!"};
		keywords = new HashMap<String, Integer>();
		keywords.put("subscribe", 4);
		keywords.put("perks", 2);
		keywords.put("what", 1);

		subjects[6] = new Subject(lines, responses, 5, keywords, false, 2, "subscriber");
	}
	
	Random r = new Random();
	
	public String eval(String message, User user) {
		int dist = 100;
		Subject subject = null;
		for (Subject sub : subjects) {
			int evaled = sub.evaluateSubject(message);
			if (evaled < dist) {
				dist = evaled;
				subject = sub;
			}
		}	
		
		if (subject != null) {
			if (dist < subject.bounds && subject.itimer == 0) {
				subject.itimer=subject.cooldown;
				if (subject.name.equalsIgnoreCase("greeting") || subject.name.equalsIgnoreCase("lunagreet")) {
					for (Entry<String, String> set : LunaBot.data.customGreetings.entrySet()) {
						String id = set.getKey();
						String response = set.getValue();
						
						if (user.getId().equalsIgnoreCase(id)) {
							return response;
						}
					}
				}
				return subject.responses[r.nextInt(subject.responses.length)].replace("{username}", user.getName());
			} 
		}	
		return null;
	}
	
	public String eval(String message, String name) {
		int dist = 100;
		Subject subject = null;
		for (Subject sub : subjects) {
			int evaled = sub.evaluateSubject(message);
			if (evaled < dist) {
				dist = evaled;
				subject = sub;
			}
		}	
		
		if (subject != null) {
			if (dist < subject.bounds && subject.itimer == 0) {
				subject.itimer=subject.cooldown;
				return subject.responses[r.nextInt(subject.responses.length)].replace("{username}", name);
			} 
		}	
		return null;
	}

	public void replyTo(String message, User user, TextChannel channel) {	
		String response = eval(message, user);
		if (response != null) {
			queueMessage(response, channel);
		}
	}
	
	Queue<String> messages = new LinkedList<String>();
	Thread queue;
	public void queueMessage(String msg, TextChannel channel) {	
		if (queue == null || !queue.isAlive()) {
			queue = new Thread(new Runnable() {
				
				@Override
				public void run() {
					String message;
					while ((message = messages.poll()) != null) {
						try {
							channel.sendMessage(message).complete();
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
		
		messages.add(msg);
	}
	
	public static int minDistance(String word1, String word2) {
		int len1 = word1.length();
		int len2 = word2.length();
	 
		// len1+1, len2+1, because finally return dp[len1][len2]
		int[][] dp = new int[len1 + 1][len2 + 1];
	 
		for (int i = 0; i <= len1; i++) {
			dp[i][0] = i;
		}
	 
		for (int j = 0; j <= len2; j++) {
			dp[0][j] = j;
		}
	 
		//iterate though, and check last char
		for (int i = 0; i < len1; i++) {
			char c1 = word1.charAt(i);
			for (int j = 0; j < len2; j++) {
				char c2 = word2.charAt(j);
	 
				//if last two chars equal
				if (c1 == c2) {
					//update dp value for +1 length
					dp[i + 1][j + 1] = dp[i][j];
				} else {
					int replace = dp[i][j] + 1;
					int insert = dp[i][j + 1] + 1;
					int delete = dp[i + 1][j] + 1;
	 
					int min = replace > insert ? insert : replace;
					min = delete > min ? min : delete;
					dp[i + 1][j + 1] = min;
				}
			}
		}
	 
		return dp[len1][len2];
	}
}