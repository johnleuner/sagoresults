package za.co.sagoclubs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import static za.co.sagoclubs.Constants.TAG;

public class InternetActions {

	private static Player[] playerData = null;
	private static String username="";
	private static String password="";
	
	public static void setUsername(String name) {
        Log.d(TAG, "Set username to " +name);
		InternetActions.username = name;
		playerData = null;
	}
	
	public static void setPassword(String pw) {
        Log.d(TAG, "Set password to " + pw);
		InternetActions.password = pw;		
		playerData = null;
	}

	public static Player[] getPlayerArray() {
    	if (playerData!=null) {
    		return playerData;
    	}
    	ArrayList<Player> list = new ArrayList<Player>();
    	for (String item:getRawPlayerList()) {
    		if (item.startsWith("s/,")) {
	    		String[] parts = item.split(",");
	    		String id = parts[1];
	    		String name = parts[3];
	    		list.add(new Player(id, name));
    		}
    	}
    	
//    	list.add(new Player("jgelant", "James Gelant"));
//    	list.add(new Player("cwelsh", "Chris Welsh"));

    	Player[] template = new Player[]{};
    	playerData = list.toArray(template);
    	Arrays.sort(playerData);
    	return playerData;
    }

	public static Player[] getFavouritePlayers(SharedPreferences preferences) {
		String save = preferences.getString("favourite_players", "");
		List<String> items = Arrays.asList(save.split(","));
		Player[] allPlayers = getPlayerArray();
		List<Player> list = new ArrayList<Player>();
		for (Player player: allPlayers) {
			if (items.contains(player.getId())) {
				list.add(player);
			}
		}
    	Player[] template = new Player[]{};
    	Player[] result = list.toArray(template);
    	return result;
	}
	
    public static void executeUrl(String url) {
    	openConnection(url);
    }

    public static String openPage(String url) {
    	HttpURLConnection c = openConnection(url);
        BufferedReader reader = null;
        String result = ""; 
        try {
            reader = new BufferedReader(new InputStreamReader(c.getInputStream(), "UTF-8"), 8192);
            for (String line; (line = reader.readLine()) != null;) {
            	result = result + line + "\n";
            }
        } catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
            if (reader != null) try { reader.close(); } catch (IOException logOrIgnore) {}
            c.disconnect();
        }
        return result;
    }

    public static String getPreBlock(String url) {
    	HttpURLConnection c = openConnection(url);
        BufferedReader reader = null;
        String result = ""; 
        try {
            reader = new BufferedReader(new InputStreamReader(c.getInputStream(), "UTF-8"), 8192);
            for (String line; (line = reader.readLine()) != null;) {
            	result = result + line + "\n";
            }
        } catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
            if (reader != null) try { reader.close(); } catch (IOException logOrIgnore) {}
            c.disconnect();
        }
        int index = result.toLowerCase().indexOf("<pre>");
        if (index>0) {
        	result = result.substring(index+6);
        	index = result.toLowerCase().indexOf("</pre>");
            if (index>0) {
            	result = result.substring(0,index);
            }
        }
        return result;
    }
    
    private static HttpURLConnection openConnection(String url) {
        HttpURLConnection c = null;
		try {
			URL u = new URL(url);
			c = (HttpURLConnection) u.openConnection();
			Log.d(TAG, "openConnection: url="+url+", username="+username+", password="+password);
			String basicAuth = "Basic " + new String(Base64.encode((username+":"+password).getBytes(),Base64.NO_WRAP ));
			c.setRequestProperty ("Authorization", basicAuth);
			c.connect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return c;
    }
    
    private static ArrayList<String> getRawPlayerList() {
        HttpURLConnection c = openConnection("http://rank.sagoclubs.co.za/sed_script");
//        HttpURLConnection c = null;
//		try {
//			url = new URL("http://rank.sagoclubs.co.za/sed_script");
//
//			c = (HttpURLConnection) url.openConnection();
//			String basicAuth = "Basic " + new String(Base64.encode((userid+":"+password).getBytes(),Base64.NO_WRAP ));
//			c.setRequestProperty ("Authorization", basicAuth);
//			c.connect();
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
        BufferedReader reader = null;
        ArrayList<String> list = new ArrayList<String>(); 
        try {
            reader = new BufferedReader(new InputStreamReader(c.getInputStream(), "UTF-8"), 8192);
            for (String line; (line = reader.readLine()) != null;) {
            	list.add(line.trim());
            }
        } catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
            if (reader != null) try { reader.close(); } catch (IOException logOrIgnore) {}
            c.disconnect();
        }
        return list;
    }
    
}
