package me.cyberdie22.apilibrary;

import com.google.common.flogger.FluentLogger;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.ParseException;

import java.net.URL;
import java.util.*;

public class API
{
    public static class Hypixel
    {

    }

    public static class Mojang
    {

        private Map<ServiceType, ServiceStatus> apiStatus;

        public Mojang()
        {
            apiStatus = new HashMap<>();
        }

        private static final FluentLogger LOGGER = FluentLogger.forEnclosingClass();

        public static Map<String, String> cachedUUIDs = new HashMap<>();
        public static Map<String, URL> cachedSkinTextures = new HashMap<>();
        public static Map<String, URL> cachedCapeTextures = new HashMap<>();
        public static Map<String, Map<String, Long>> cachedNameHistory = new HashMap<>();
        public static Map<String, PlayerProfile> cachedPlayerProfiles = new HashMap<>();

        /**
         * Connects to the Mojang API ({@literal https://api.mojang.com/}) and retrieves current service statuses
         * @return {@link me.cyberdie22.apilibrary.API.Mojang} class
         */
        public API.Mojang connect()
        {
            JsonArray array = Json.getJSONArrayFromUrl("https://status.mojang.com/check");

            for (int i = 0; i <= 7; i++)
            {
                ServiceType service = ServiceType.values()[i];
                JsonObject obj = (JsonObject) array.get(i);

                apiStatus.put(service,
                    ServiceStatus.valueOf(obj.get(service.toString()).getAsString().toUpperCase()));
            }

            return this;
        }

        /**
         * Clears the caches
         */
        public static void clearCaches()
        {
            cachedUUIDs.clear();
            cachedSkinTextures.clear();
            cachedCapeTextures.clear();
            cachedNameHistory.clear();
            cachedPlayerProfiles.clear();
        }

        /**
         * Gets the status of a service
         * @param service {@link me.cyberdie22.apilibrary.API.Mojang.ServiceType} to get {@link me.cyberdie22.apilibrary.API.Mojang.ServiceStatus} of
         * @return {@link me.cyberdie22.apilibrary.API.Mojang.ServiceStatus} of a service
         */
        public ServiceStatus getStatus(ServiceType service)
        {
            if (service == null) return ServiceStatus.UNKNOWN;
            return apiStatus.get(service);
        }

        /*
         * API Methods
         */

        /**
         * <p>Gets a players UUID from {@literal https://api.mojang.com/}</p>
         * @param username Players name
         * @return Players UUID as a {@link java.lang.String}
         */
        public String getUuidOfPlayer(String username)
        {
            // Return Cached UUID
            if (cachedUUIDs.containsKey(username.toLowerCase())) return cachedUUIDs.get(username.toLowerCase());
            LOGGER.atInfo().log("Not getting UUID from cache!");
            // Return null if https://api.mojang.com/ is down
            if (!apiStatus.get(ServiceType.API_MOJANG_COM).equals(ServiceStatus.GREEN)) return null;
            String uuid = Json.getJSONObjectFromURL("https://api.mojang.com/users/profiles/minecraft/" + username).get("id").getAsString();
            if (uuid != null) cachedUUIDs.put(username.toLowerCase(), uuid);
            return uuid;
        }

        /**
         * <p>Gets a players UUID from {@literal https://api.mojang.com/}</p>
         * @param username Players name
         * @param timestamp The time for when to check for the name
         * @return Players UUID as a {@link java.lang.String}
         */
        public String getUuidOfPlayer(String username, long timestamp)
        {
            // Return null if https://api.mojang.com/ is down
            if (apiStatus.get(ServiceType.API_MOJANG_COM).equals(ServiceStatus.GREEN)) return null;
            return Json.getJSONObjectFromURL("https://api.mojang.com/users/profiles/minecraft/" + username + "?at=" + timestamp).get("id").getAsString();
        }

        /**
         * Gets the name history of the provided uuid from {@literal https://api.mojang.com/}
         * @param uuid UUID to get name history of
         * @return Name history of provided UUID
         */
        public Map<String, Long> getNameHistoryOfPlayer(String uuid)
        {
            // Return cached name history if possible
            if (cachedNameHistory.containsKey(uuid)) return cachedNameHistory.get(uuid);
            LOGGER.atInfo().log("Not getting Name History from cache!");
            JsonArray names = (JsonArray) Json.getUrlAsJsonObject("https://api.mojang.com/user/profiles/" + uuid + "/names");
            Map<String, Long> history = new HashMap<>();
            names.forEach(o ->
            {
                JsonObject obj = (JsonObject) o;
                history.put(obj.get("name").getAsString(), obj.get("changedToAt") == null ? 0L : Long.parseLong(obj.get("changedToAt").toString()));
            });
            return history;
        }

        /**
         * Gets player profile of provided uuid from {@literal https://api.mojang.com/}
         * @param uuid UUID to get player profile of
         * @return Player Profile of provided UUID using a {@link me.cyberdie22.apilibrary.API.PlayerProfile} (Only returns with Mojang data, does not return with Hypixel data)
         */
        public PlayerProfile getPlayerProfile(String uuid) {
            // Return cached player profile if possible
            if (cachedPlayerProfiles.containsKey(uuid)) return cachedPlayerProfiles.get(uuid);
            LOGGER.atInfo().log("Not getting Player Profile from cache!");
            JsonObject obj = Json.getJSONObjectFromURL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);
            String name = obj.get("name").getAsString();
            Set<PlayerProfile.Property> properties = new HashSet<>();
            for (JsonElement o : obj.get("properties").getAsJsonArray())
            {
                PlayerProfile.Property property;
                JsonObject prop = (JsonObject) o;

                String propName = prop.get("name").getAsString();
                String propValue = prop.get("value").getAsString();
                if (propName.equals("textures")) {
                    JsonObject tex;
                    try {
                        tex = (JsonObject) new JsonParser().parse(new String(Base64.getDecoder().decode(propValue)));
                    } catch (ParseException e2) {
                        throw new RuntimeException(e2);
                    }
                    PlayerProfile.TexturesProperty textureProperties = new PlayerProfile.TexturesProperty();
                    textureProperties.timestamp = tex.get("timestamp").getAsLong();
                    textureProperties.profileId = tex.get("profileId").getAsString();
                    textureProperties.profileName = tex.get("profileName").getAsString();
                    try
                    {
                        textureProperties.signatureRequired = tex.get("signatureRequired").getAsBoolean();
                    } catch (Exception ignored) {}
                    URL skin = null;
                    URL cape = null;
                    try
                    {
                        JsonObject textures = tex.getAsJsonObject("textures");
                        skin = new URL(textures.getAsJsonObject("SKIN").get("url").getAsString());
                        try
                        {
                            cape = new URL(textures.getAsJsonObject("CAPE").get("url").getAsString());
                        } catch (Exception ignored) {}
                    } catch (Exception e)
                    {
                        LOGGER.atSevere().withCause(e).log();
                    }
                    Map<String, URL> textures = new HashMap<>();
                    textures.put("SKIN", skin);
                    textures.put("CAPE", cape);
                    textureProperties.textures = textures;
                    property = textureProperties;
                } else
                    property = new PlayerProfile.Property();
                property.name = propName;
                try
                {
                    property.signature = prop.get("signature").getAsString();
                } catch (Exception ignored) {}
                property.value = propValue;
                properties.add(property);
            }
            PlayerProfile playerProfile = new PlayerProfile(uuid, name, properties);
            cachedPlayerProfiles.put(uuid, playerProfile);
            return playerProfile;
        }

        /*
         * Static Methods
         */
        /*
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
                Object dataobj = Json.getURLAsJsonObject(url);
                Preconditions.checkArgument(!dataobj.equals("204"), "Player %s doesn't exist!", name);
                assert dataobj instanceof JsonObject;
                JsonObject data = (JsonObject) dataobj;
                String uuid = data.get("id").getAsString();
                return uuid != null;
            } catch (Exception e){
                e.printStackTrace();
            }
            return false;
        }
         */

        public enum ServiceStatus
        {
            RED,
            YELLOW,
            GREEN,
            UNKNOWN
        }

        /**
         * This enum represents the various portions of the Mojang API.
         */
        public enum ServiceType
        {
            MINECRAFT_NET,
            SESSION_MINECRAFT_NET,
            ACCOUNT_MOJANG_COM,
            AUTHSERVER_MOJANG_COM,
            SESSIONSERVER_MOJANG_COM,
            API_MOJANG_COM,
            TEXTURES_MINECRAFT_NET,
            MOJANG_COM;

            /**
             * <p>This method overrides {@code java.lang.Object.toString()} and returns the address of the mojang api portion a certain enum constant represents.
             * <p><strong>Example:</strong>
             * {@code me.cyberdie22.apilibrary.API.Mojang.ServiceType.MINECRAFT_NET.toString()} will return {@literal minecraft.net}
             *
             * @return the string
             */
            @Override
            public String toString()
            {
                return name().toLowerCase().replace("_", ".");
            }
        }

        /**
         * This enum represents the skin types "Alex" and "Steve".
         */
        public enum SkinType
        {
            /**
             * Steve
             */
            DEFAULT,
            /**
             * Alex
             */
            SLIM;

            /**
             * Returns the query parameter version for these skin types in order to send HTTP requests to the API.
             *
             * @return the string
             */
            @Override
            public String toString()
            {
                return this == DEFAULT ? "" : "slim";
            }
        }
    }
    public static class PlayerProfile {

        private String uuid;
        private String username;
        private Set<Property> properties;
        private Optional<TexturesProperty> textures;

        /**
         * Represents a property.
         */
        public static class Property
        {
            String name;
            String value;
            String signature;

            public String getName() {
                return name;
            }

            public String getValue() {
                return value;
            }

            public String getSignature() {
                return signature;
            }
        }

        public static class TexturesProperty extends Property {
            long timestamp;
            String profileId, profileName;
            boolean signatureRequired = false;
            Map<String, URL> textures;

            public long getTimestamp() {
                return timestamp;
            }

            public String getProfileId() {
                return profileId;
            }

            public String getProfileName() {
                return profileName;
            }

            public boolean isSignatureRequired() {
                return signatureRequired;
            }

            public Map<String, URL> getTextures() {
                return textures;
            }

            public Optional<URL> getSkin() {
                return Optional.ofNullable(textures.get("SKIN"));
            }

            public Optional<URL> getCape() {
                return Optional.ofNullable(textures.get("CAPE"));
            }

            @Override
            public String toString()
            {
                return "TexturesProperty{" +
                    "name='" + name + '\'' +
                    ", value='" + value + '\'' +
                    ", signature='" + signature + '\'' +
                    ", timestamp=" + timestamp +
                    ", profileId='" + profileId + '\'' +
                    ", profileName='" + profileName + '\'' +
                    ", signatureRequired=" + signatureRequired +
                    ", textures=" + textures +
                    '}';
            }
        }

        /**
         * <p>Constructor for the class.
         * <p>You may use {@code new API.Mojang().connect().getPlayerProfile(uuid)} to retrieve the instance as it will verify the validity of the parameters.
         *
         * @param uuid the UUID of the player this object should represent
         * @param username the username of said player (you may use {@code API.Mojang().connect().getNameHistoryOfPlayer(uuid)} to retrieve it).
         * @param properties the properties for that player. Depends on what you wish to do with the object
         */
        public PlayerProfile(String uuid, String username, Set<Property> properties) {
            this.uuid = uuid;
            this.username = username;
            this.properties = properties;
            this.textures = properties.stream().filter(p -> p.getName().equals("textures")).map(p -> (TexturesProperty) p).findAny();
        }

        /**
         * Gets the UUID of the player.
         *
         * @return the uuid as a {@link java.lang.String String}
         */
        public String getUUID() {
            return uuid;
        }

        /**
         * Gets the username of the player.
         *
         * @return the username as a {@link java.lang.String String}
         */
        public String getUsername() {
            return username;
        }

        /**
         * <p>Returns the properties this object has.
         * <p>This method exists for transparency, as the properties set is used internally.
         *
         * @return the properties {@link java.util.Set Set}
         */
        public Set<Property> getProperties() {
            return properties;
        }

        public Optional<TexturesProperty> getTextures() {
            return textures;
        }

        @Override
        public String toString()
        {
            return "PlayerProfile{" +
                "uuid='" + uuid + '\'' +
                ", username='" + username + '\'' +
                ", properties=" + properties.toString() +
                ", \ntextures=" + textures.toString() +
                '}';
        }
    }
}
