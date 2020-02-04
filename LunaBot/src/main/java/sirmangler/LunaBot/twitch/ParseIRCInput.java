package sirmangler.LunaBot.twitch;

public class ParseIRCInput {

	public static String parse(String raw) {
		if (!raw.contains("=") && !raw.contains(";")) {
			return null;
		}
		
		String variablesraw = raw.substring(0, raw.lastIndexOf(':'));
		String[] variables = variablesraw.split(";");
		ircInput input = new ircInput();
		
		
		for (String variable : variables) {
			String[] keyval = variable.split("=", 2);
			if (keyval.length == 1) continue;
			
			String name = keyval[0];
			String value = keyval[1];
			
			switch (name.toLowerCase()) {
			case "display-name":
				input.display_name=value;
				break;
			case "bits":
				input.bits=value;
				break;
			case "message":
				input.message=value;
				break;
			case "msg-id":
				input.msg_id=value;
				break;
			case "msg_param_displayName":
				input.msg_param_displayName=value;
				break;
			case "msg_param_login":
				input.msg_param_login=value;
				break;
			case "msg_param_months":
				input.msg_param_months=value;
				break;
			case "msg_param_recipient_display_name":
				input.msg_param_recipient_display_name=value;
				break;
			case "msg_param_recipient_id":
				input.msg_param_recipient_id=value;
				break;
			case "msg_param_recipient_sub_plan":
				input.msg_param_recipient_sub_plan=value;
				break;
			case "msg_param_sub_plan_name":
				input.msg_param_sub_plan_name=value;
				break;	
			case "msg_param_viewerCount":
				input.msg_param_viewerCount=value;
				break;	
			case "msg_param_ritual_name":
				input.msg_param_ritual_name=value;
				break;	
			case "system_msg":
				input.system_msg=value;
				break;	
			}
		}
		
		String response = null;
		if (input.bits != null) {
			response = input.display_name+" has given **"+input.bits+"** bits with the message: {msg}";
			return response;
		} else if (input.msg_id != null) {
			if (input.msg_id.equalsIgnoreCase("sub")) {
				response = input.display_name+" has just subscribed with the "+input.msg_param_sub_plan_name+" plan! Thanks!";
			} else if (input.msg_id.equalsIgnoreCase("resub")) {
				response = input.display_name+" has just resubscribed for "+input.msg_param_months+" months in a row with the "+input.msg_param_sub_plan_name+" plan! Thanks a bunch!";
			} else if (input.msg_id.equalsIgnoreCase("subgift")) {
				response = input.display_name+" has just gifted a subscription to "+input.msg_param_recipient_display_name+"! You're awesome!";
			} else if (input.msg_id.equalsIgnoreCase("anonsubgift")) {
				response = "An anonymous user has just gifted a subsciption to "+input.msg_param_recipient_display_name+"! You're awesome!";
			} else if (input.msg_id.equalsIgnoreCase("raid")) {
				response = input.msg_param_displayName+ " has raided with "+input.msg_param_viewerCount+" users!";
			}
			
			if (response == null) {
				System.err.println("UNKOWN MSG_ID ERROR");
			return response;
			}
		}
		
		return null;
	}

}

class ircInput {
	String display_name;
	String bits;
	String message;
	String msg_id;
	String msg_param_displayName;
	String msg_param_login;
	String msg_param_months;
	String msg_param_recipient_display_name;
	String msg_param_recipient_id;
	String msg_param_recipient_sub_plan;
	String msg_param_sub_plan_name;
	String msg_param_viewerCount;
	String msg_param_ritual_name;
	String system_msg;
}
