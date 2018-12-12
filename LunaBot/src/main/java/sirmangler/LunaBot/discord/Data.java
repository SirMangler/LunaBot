package sirmangler.LunaBot.discord;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.json.JSONObject;

public class Data {

	/**
	 * @author SirMangler (catty610)
	 * 5 Apr 2018
	 */
	
	File f = new File("LunarBot.dat");
	JSONObject fileData;
	
	public String prefix = "?";
	HashMap<String, String> afkmembers = new HashMap<String, String>();
	
	public long lastSubscribed = -1;
	public boolean debugmode = false;
	public double deleteDelay = 3;
	
	public String announcementChannel = "442774379223449600";
	public String musicChannel = "451783850624942080";
	public boolean followAlert = true;
	public boolean liveAlert = true;
	public LinkedHashMap<String, Boolean> toDo = new LinkedHashMap<String, Boolean>();
	HashMap<String, Long> reminders = new HashMap<String, Long>();
	public List<Integer> followers = new ArrayList<Integer>();
	public HashMap<String, String> customGreetings = new HashMap<String, String>();
	public HashMap<String, UserLevel> userXP = new HashMap<String, UserLevel>();
	
	public void addFollower(int user) {
		followers.add(user);
		
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(f));
			
			fileData.remove("followers");
			fileData.put("followers", followers);
			
			writer.write(fileData.toString(1));
			writer.flush();
			
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setUserXP(String id, long xp, int level) {
		userXP.put(id, new UserLevel(xp, level));
		
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(f));
			
			fileData.remove("userXP");
			fileData.put("userXP", userXP);
			
			writer.write(fileData.toString(1));
			writer.flush();
			
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setCustomGreeting(String line, String id) {
		customGreetings.put(id, line);
		
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(f));
			
			fileData.remove("customGreeting");
			fileData.put("customGreeting", customGreetings);
			
			writer.write(fileData.toString(1));
			writer.flush();
			
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void removeCustomGreeting(String id) {
		customGreetings.remove(id);
		
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(f));
			
			fileData.remove("followers");
			
			writer.write(fileData.toString(1));
			writer.flush();
			
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addRemindMe(String line, long time) {
		reminders.put(line, time);

		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(f));
			
			fileData.remove("reminders");
			fileData.put("reminders", reminders);
			
			writer.write(fileData.toString(1));
			writer.flush();
			
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		reminders.clear();
		reminders.forEach((message, duration) -> {
			remindersQueue(message, duration, this);
		});
	}
	
	static Thread queue;
	public static void remindersQueue(String reminder, long duration, Data data) {
		if (queue == null || !queue.isAlive()) {
			queue = new Thread(new Runnable() {
				
				@SuppressWarnings("unchecked")
				@Override
				public void run() {
					while (!Commands.reminders.isEmpty()) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						for (Object item : Commands.reminders.toArray()) {
							Entry<String, Long> entry = (Entry<String, Long>) item;
							Date d = new Date();
							if (d.getTime() > entry.getValue()) {
								LunaBot.jda.getUserById("176128994176008192").openPrivateChannel().complete().sendMessage("Reminder: "+entry.getKey()).complete();
								Commands.reminders.remove(entry);
								data.removeRemindMe(entry.getKey());
							}
						}
					}
				}
			});
			queue.start();
		}
		
		Commands.reminders.add(new AbstractMap.SimpleEntry<String, Long>(reminder, duration));
	}
	
	public void clearReminders() {
		reminders.clear();
		
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(f));
			
			fileData.remove("reminders");
			fileData.put("reminders", reminders);
			
			writer.write(fileData.toString(1));
			writer.flush();
			
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void removeRemindMe(String line) {
		reminders.remove(line);

		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(f));
			
			fileData.remove("reminders");
			fileData.put("reminders", reminders);
			
			writer.write(fileData.toString(1));
			writer.flush();
			
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addToDo(String line) {
		toDo.put(line, false);
		
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(f));
			
			fileData.remove("toDo");
			fileData.put("toDo", toDo);
			
			writer.write(fileData.toString(1));
			writer.flush();
			
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void removeToDo(int i) {
		toDo.remove(toDo.keySet().toArray()[i]);
		
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(f));
			
			fileData.remove("toDo");
			fileData.put("toDo", toDo);
			
			writer.write(fileData.toString(1));
			writer.flush();
			
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void completeToDo(int i) {
		Entry<String, Boolean> line = (Entry<String, Boolean>) toDo.entrySet().toArray()[i];
		toDo.remove(line);
		toDo.put(line.getKey(), true);
		
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(f));
			
			fileData.remove("toDo");
			fileData.put("toDo", toDo);
			
			writer.write(fileData.toString(1));
			writer.flush();
			
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void clearToDo() {
		toDo.clear();
		
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(f));
			
			fileData.remove("toDo");
			fileData.put("toDo", toDo);
			
			writer.write(fileData.toString(1));
			writer.flush();
			
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setFollowAlert(boolean follow) {
		followAlert = follow;
		
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(f));
			
			fileData.remove("followAlert");
			fileData.put("followAlert", followAlert);
			
			writer.write(fileData.toString(1));
			writer.flush();
			
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void setGoLive(boolean live) {
		liveAlert = live;
		
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(f));
			
			fileData.remove("liveAlert");
			fileData.put("liveAlert", liveAlert);
			
			writer.write(fileData.toString(1));
			writer.flush();
			
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setAnnouncementChannel(String id) {
		announcementChannel = id;
		
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(f));
			
			fileData.remove("anouncementChannel");
			fileData.put("anouncementChannel", announcementChannel);
			
			writer.write(fileData.toString(1));
			writer.flush();
			
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setMusicChannel(String id) {
		musicChannel = id;
		
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(f));
			
			fileData.remove("musicChannel");
			fileData.put("musicChannel", musicChannel);
			
			writer.write(fileData.toString(1));
			writer.flush();
			
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setLastSubscribed(long date) {
		lastSubscribed = date;
		
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(f));
			
			fileData.remove("lastSubscribed");
			fileData.put("lastSubscribed", lastSubscribed);
			
			writer.write(fileData.toString(1));
			writer.flush();
			
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void putAFKMember(String member, String reason) {
		afkmembers.put(member, reason);
		
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(f));
			
			fileData.remove("afkMembers");
			fileData.put("afkMembers", afkmembers);
			
			writer.write(fileData.toString(1));
			writer.flush();
			
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void removeAFKMember(String memberid) {
		afkmembers.remove(memberid);
		
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(f));
			
			fileData.remove("afkMembers");
			fileData.put("afkMembers", afkmembers);
			
			writer.write(fileData.toString(1));
			writer.flush();
			
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setPrefix(String pref) {
		prefix = pref;
		
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(f));
			
			fileData.remove("prefix");
			fileData.put("prefix", prefix);
			
			writer.write(fileData.toString(1));
			writer.flush();
			
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setDeleteDelay(double delay) {
		deleteDelay = delay;
		
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(f));
			
			fileData.remove("deleteDelay");
			fileData.put("deleteDelay", deleteDelay);
			
			writer.write(fileData.toString(1));
			writer.flush();
			
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setDebugMode(boolean debug) {
		debugmode = debug;
		
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(f));
			
			fileData.remove("debugMode");
			fileData.put("debugMode", debugmode);
			
			writer.write(fileData.toString(1));
			writer.flush();
			
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Data() {		
		if (!f.exists()) {
			createDataFile();
		} else {
			loadData();
		}
	}
	
	public void loadData() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(f));
			
			StringBuilder builder = new StringBuilder();
			
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			
			try {
				fileData = new JSONObject(builder.toString());
				prefix = fileData.getString("prefix");
				debugmode = fileData.getBoolean("debugMode");
				deleteDelay = fileData.getDouble("deleteDelay");
				
				final JSONObject afko = fileData.getJSONObject("afkMembers");
				
				HashMap<String, String> afk = new HashMap<String, String>(); 
				afko.keys().forEachRemaining(key -> {
					afk.put(key, afko.getString(key));
				});
				
				afkmembers = afk;
				
				lastSubscribed = fileData.getLong("lastSubscribed");
				announcementChannel = fileData.getString("announcementChannel");
				liveAlert = fileData.getBoolean("liveAlert");
				followAlert = fileData.getBoolean("followAlert");
				
				JSONObject reminderso = fileData.getJSONObject("reminders");
				
				HashMap<String, Long> reminder = new HashMap<String, Long>(); 
				reminderso.keys().forEachRemaining(key -> {
					reminder.put(key, Long.parseLong(reminderso.getString(key)));
				});
				

				reminders = reminder;
				
				JSONObject toDoo = fileData.getJSONObject("toDo");
				LinkedHashMap<String, Boolean> tod = new LinkedHashMap<String, Boolean>(); 
				toDoo.keys().forEachRemaining(key -> {
					tod.put(key, toDoo.getBoolean(key));
				});
				
				
				toDo = tod;
				
				JSONObject followerso = fileData.getJSONObject("followers");
				List<Integer> follower = new ArrayList<Integer>(); 
				followerso.keys().forEachRemaining(key -> {
					follower.add(Integer.parseInt(key));
				});
				
				followers = follower;
				musicChannel = fileData.getString("musicChannel");
				
				JSONObject customGreeting = fileData.getJSONObject("customGreetings");
				
				HashMap<String, String> greeting = new HashMap<String, String>(); 
				customGreeting.keys().forEachRemaining(key -> {
					greeting.put(key, customGreeting.getString(key));
				});
			} catch (Exception e) {
				e.printStackTrace();
				createDataFile();
			}
			
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void createDataFile() {
		try {
			f.createNewFile();
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(f));
			fileData = new JSONObject();
			fileData.put("prefix", prefix);
			fileData.put("debugMode", debugmode);
			fileData.put("deleteDelay", deleteDelay);
			fileData.put("afkMembers", afkmembers);
			fileData.put("lastSubscribed", lastSubscribed);
			fileData.put("announcementChannel", announcementChannel);
			fileData.put("liveAlert", liveAlert);
			fileData.put("followAlert", followAlert);
			fileData.put("toDo", toDo);
			fileData.put("reminders", reminders);
			fileData.put("followers", followers);
			fileData.put("musicChannel", musicChannel);
			fileData.put("customGreeting", customGreetings);
			fileData.put("userXP", userXP);
			
			writer.write(fileData.toString(1));
			writer.flush();
			
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
