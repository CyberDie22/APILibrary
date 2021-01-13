package me.cyberdie22.apilibrary;

import com.google.common.base.Preconditions;
import com.google.common.flogger.FluentLogger;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class API
{
    public static class Hypixel
    {

    }

    public static class Mojang
    {

        private static final FluentLogger LOGGER = FluentLogger.forEnclosingClass();

        private static Map<String, String> cachedUUIDs = new HashMap<>();
        private static Map<String, URL> cachedSkinTextures = new HashMap<>();
        private String playerName = "";
        private String playerUuid = "";

        public static API.Mojang create(){return new API.Mojang();}

        /*
         * API Methods
         */

        /**
         *
         * @param player Player name to set active player to
         * @return {@link API.Mojang} class refrence
         */
        public API.Mojang setPlayer(String player)
        {
            Preconditions.checkArgument(player.length() < 16, "Player name length must be less than 16!");
            player = player.toLowerCase();
            if(apiStatus() == Status.GREEN && doesPlayerExist(player))
            {
                this.playerName = player;
                this.playerUuid = this.getUUIDFromPlayer();
            }
            else
            {
                this.playerName = "";
                this.playerUuid = "";
            }
            return this;
        }

        /*
         * API Accessing Methods
         */

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
            // Check if the players UUID is cached, if it is, return the cached UUID
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
                LOGGER.atSevere().withCause(e).log();
            }
            return playerName;
        }

        public URL getSkinTexture()
        {
            // Check if the players skin texture is cached, if it is, return the cached skin texture
            if (cachedSkinTextures.containsKey(playerUuid)) return cachedSkinTextures.get(playerUuid);
            // Return null of the Mojang API is down
            if (!(apiStatus() == Status.GREEN)) return null;
            // Try to get the players skin+cape textures
            try
            {
                URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + playerUuid);
                JsonObject data = (JsonObject) Json.getURLAsJsonObject(url);
                JsonArray properties = data.getAsJsonArray("properties");
                JsonObject texture = properties.get(0).getAsJsonObject();
                LOGGER.atInfo().log("Player Texture Properties: %s", texture);
                String textureb64 = texture.get("value").getAsString();
                LOGGER.atInfo().log("Player Texture Properties Value: %s", textureb64);
                JsonObject textureData = (JsonObject) Json.getStringAsJsonObject(new String(Base64.getDecoder().decode(textureb64)));
                LOGGER.atInfo().log("data=%s", textureData);
                JsonObject textures = textureData.getAsJsonObject("textures");
                LOGGER.atInfo().log("textures=%s", textures);
                JsonObject skin = textures.getAsJsonObject("SKIN");
                LOGGER.atInfo().log("skin=%s", skin);
                URL textureUrl = new URL(skin.get("url").getAsString());
                LOGGER.atInfo().log("url=%s", textureUrl);
            } catch (Exception e)
            {
                LOGGER.atSevere().withCause(e).log();
            }
            return null;
        }

        /*
         * Static Methods
         */

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
                // Player UUID Cache
                FileWriter fstream = new FileWriter("resources/caches/player_uuids.json");
                BufferedWriter out = new BufferedWriter(fstream);
                out.write(Json.convertMapToJsonObject(cachedUUIDs));
                out.close();

                // Player Skin Cache
                fstream = new FileWriter("resources/caches/player_skin_texture.json");
                out = new BufferedWriter(fstream);
                out.write(Json.convertMapToJsonObject(cachedUUIDs));
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
                // Player UUID Cache
                FileReader fstream = new FileReader("resources/caches/player_uuids.json");
                BufferedReader in = new BufferedReader(fstream);
                cachedUUIDs = Json.convertJsonStringToMap(in.readLine());
                in.close();

                // Player Skin Texture Cache
                fstream = new FileReader("resources/caches/player_skin_texture.json");
                in = new BufferedReader(fstream);
                cachedUUIDs = Json.convertJsonStringToMap(in.readLine());
                in.close();
            }catch (Exception e){ //Catch exception if any
                e.printStackTrace();
            }
        }

        // TODO: Cache API Status for 1 minute to prevent from sending too many requests to Mojang servers

        /**
         * Retrieve current status for <b>api.mojang.com</b> from <b>https://status.mojang.com/check</b>
         * @return Status of API
         */
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
