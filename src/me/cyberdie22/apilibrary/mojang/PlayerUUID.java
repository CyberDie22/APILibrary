package me.cyberdie22.apilibrary.mojang;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import me.cyberdie22.apilibrary.Json;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PlayerUUID {

    // TODO: store cache
    public static Map<String, String> cachedUUIDs = new HashMap<String, String>();


    /**
     * Gets UUID of player.
     * <p>
     * Will return the players name if the Mojang API is offline.
     * </p>
     *
     * @param name The name of the player
     * @return The uuid of player
     */
    public static String getUUIDFromPlayer(@NotNull String name)
    {
        Preconditions.checkArgument(name.length() < 16, "Player name length must be less than 16!");
        name = name.toLowerCase();
        // Check if the player's UUID is cached
        if (cachedUUIDs.containsKey(name)) return cachedUUIDs.get(name);
        if (!ApiStatus.isOnline()) return name;
        try
        {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            JsonObject data = (JsonObject) Json.getURLAsJsonObject(url);
            String uuid = data.get("id").getAsString();
            if (uuid != null)
                cachedUUIDs.put(name, uuid);
            return uuid;
        } catch (Exception e){
            e.printStackTrace();
        }
        return name;
    }

    /**
     * Store Caches
     */
    public static void storeCaches()
    {
        try{
            // Create file
            FileWriter fstream = new FileWriter("resources/caches/player_uuids.json");
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(Json.convertMapToJsonObject(cachedUUIDs));
            //Close the output stream
            out.close();
        }catch (Exception e){//Catch exception if any
            e.printStackTrace();
        }
    }

    /**
     * Retrieve caches.
     */
    public static void retrieveCaches()
    {
        try{
            // Create file
            FileReader fstream = new FileReader("resources/caches/player_uuids.json");
            BufferedReader in = new BufferedReader(fstream);
            String line = in.readLine();
            cachedUUIDs = Json.convertJsonStringToMap(line);
            in.close();
        }catch (Exception e){ //Catch exception if any
            e.printStackTrace();
        }
    }
}
