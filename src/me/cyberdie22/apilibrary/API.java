package me.cyberdie22.apilibrary;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class API
{
    public static class Hypixel
    {

    }

    public static class Mojang
    {

        public static Map<String, String> cachedUUIDs = new HashMap<>();
        private String playerName = "";

        /**
         *
         * @param player Player name to set active player to
         * @return {@link API.Mojang} class refrence
         */
        public API.Mojang setPlayer(String player)
        {
            if(apiStatus() == Status.GREEN && doesPlayerExist(player))
                this.playerName = player;
            else
                this.playerName = null;
            return this;
        }

        /**
         * Gets UUID of player.
         * <p>
         * Will return the players name if the Mojang API is offline.
         * </p>
         *
         * @return The uuid of player
         */
        public String getUUIDFromPlayer()
        {
            Preconditions.checkArgument(playerName.length() < 16, "Player name length must be less than 16!");
            playerName = playerName.toLowerCase();
            // Check if the player's UUID is cached, if it is, return the cached UUID
            if (cachedUUIDs.containsKey(playerName)) return cachedUUIDs.get(playerName);
            // Return the players name if the Mojang API is down
            if (!(apiStatus() == Status.GREEN)) return playerName;
            // Try to get the players UUID from Mojangs servers
            try
            {
                URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName);
                JsonObject data = (JsonObject) Json.getURLAsJsonObject(url);
                String uuid = data.get("id").getAsString();
                if (uuid != null)
                    cachedUUIDs.put(playerName, uuid);
                return uuid;
            } catch (Exception e){
                e.printStackTrace();
            }
            return playerName;
        }

        /**
         * Checks if the provided player exists
         * in the Mojang Database
         *
         * @param name Player name to check
         * @return If the player exists
         */
        public static boolean doesPlayerExist(String name)
        {
            Preconditions.checkArgument(name.length() < 16, "Player name length must be less than 16!");
            name = name.toLowerCase();
            // Return the players name if the Mojang API is down
            if (!(apiStatus() == Status.GREEN)) return false;
            // Try to get the players UUID from Mojangs servers
            try
            {
                URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
                JsonObject data = (JsonObject) Json.getURLAsJsonObject(url);
                String uuid = data.get("id").getAsString();
                return uuid != null;
            } catch (Exception e){
                e.printStackTrace();
            }
            return false;
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

        public static Status apiStatus()
        {
            try {
                URL statusURL = new URL("https://status.mojang.com/check");
                JsonArray array = (JsonArray) Json.getURLAsJsonObject(statusURL);
                String element = array.get(5).getAsJsonObject().get("api.mojang.com").getAsString();
                return Status.fromString(element);
            } catch (Exception ignored){}
            return Status.RED;
        }
    }
}
