package sirmangler.LunaBot.AI;

import java.util.HashMap;

public class Subject {

	/**
	 * @author SirMangler (catty610)
	 * 27 Apr 2018
	 */
	
	HashMap<String, Integer> keywords;
	String[] lines;
	String[] responses = { "{null}" };
	int bounds = 6;
	boolean equals = false;
	int cooldown = 2;
	int itimer = 0;
	String name;
	
	public Subject(String[] lines, String[] responses, int bounds, HashMap<String, Integer> keywords, boolean equals, int cooldown, String name) {
		this.lines=lines;
		this.responses=responses;
		this.bounds=bounds;
		this.keywords=keywords;
		this.equals=equals;
		this.cooldown=cooldown;
		this.name=name;
	}
	
	public int evaluateSubject(String in) {
		int distance = 100;
		String input = in.replaceAll("[^a-zA-Z ]", "").toLowerCase();
		for (String line : lines) {
			int tempDistance;
			
			if (equals == false) tempDistance = AI.minDistance(input, line)+10;
			else tempDistance = input.equalsIgnoreCase(line) ? 0 : 100;

			for (String keyword : keywords.keySet()) {
				if (input.contains(keyword)) {
					tempDistance = tempDistance - keywords.get(keyword);
				}
			}
			if (tempDistance < distance) {
				distance = tempDistance;
			}
		}
		return distance;
	}
}
