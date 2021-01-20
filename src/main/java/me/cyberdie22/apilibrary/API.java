package me.cyberdie22.apilibrary;

import com.google.common.flogger.FluentLogger;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.hypixel.api.HypixelAPI;
import net.hypixel.api.reply.*;
import net.hypixel.api.util.GameType;
import org.apache.http.ParseException;

import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class API {

    /**
     * Fixes a UUID to add dashes in it
     *
     * @param uuid UUID to fix
     * @return Fixed UUID
     */
    public static UUID fixUUID(String uuid) {
        final FluentLogger LOGGER = FluentLogger.forEnclosingClass();
        try {
            return UUID.fromString(uuid);
        } catch (Exception ignored) {
        }
        List<Character> characters = new ArrayList<>();
        for (char i : uuid.toCharArray()) {
            characters.add(i);
        }
        AtomicReference<String> newUuid = new AtomicReference<>("");
        AtomicReference<String> dash = new AtomicReference<>("-");
        AtomicInteger count = new AtomicInteger();
        characters.forEach(character -> {
            switch (count.get()) {
                case 8:
                case 12:
                case 16:
                case 20:
                    newUuid.updateAndGet(v -> v + dash + character);
                    break;
                default:
                    newUuid.updateAndGet(v -> v + character);
                    break;
            }
            count.getAndIncrement();
        });
        return UUID.fromString(newUuid.get());
    }

    public static class Hypixel {
        private static final FluentLogger LOGGER = FluentLogger.forEnclosingClass();
        public static HypixelAPI API;

        public Hypixel(String apiKey) {
            API = new HypixelAPI(fixUUID(apiKey));
        }

        /**
         * Returns the amount of players online on Hypixel
         *
         * @return The amount of players online on Hypixel
         */
        public int getOnlinePlayers() {
            try {
                return API.getGameCounts().get().getPlayerCount();
            } catch (Exception e) {
                LOGGER.atSevere().withCause(e).log();
            }
            return 0;
        }

        /**
         * Returns the amount of players online in a specific game on Hypixel
         *
         * @param gameType Game to get amount of players playing
         * @return the amount of players online in a specific game on Hypixel
         */
        public int getOnlinePlayers(GameType gameType) {
            try {
                return API.getGameCounts().get().getGames().get(gameType.convert().toString()).getPlayers();
            } catch (Exception e) {
                LOGGER.atSevere().withCause(e).log();
            }
            return 0;
        }

        /**
         * Gets the Guild ID of a specific guild
         *
         * @param name the name of the guild to get the guild id of
         * @return the guild id of a named guild
         */
        public String getGuildID(String name) {
            try {
                return API.getGuildByName(name).get().toString();
            } catch (Exception e) {
                LOGGER.atSevere().withCause(e).log();
            }
            return "";
        }

        public GuildReply.Guild getGuild(String id) {
            try {
                return API.getGuildById(id).get().getGuild();
            } catch (Exception e) {
                LOGGER.atSevere().withCause(e).log();
            }
            return null;
        }

        public List<FriendsReply.FriendShip> getFriends(String uuid) {
            try {
                return API.getFriends(uuid).get().getFriendShips();
            } catch (Exception e) {
                LOGGER.atSevere().withCause(e).log();
            }
            return null;
        }

        /**
         * Gets a list of active/queued boosters
         *
         * @return list of active/queued boosters
         */
        public List<BoostersReply.Booster> getBoosters() {
            try {
                return API.getBoosters().get().getBoosters();
            } catch (Exception e) {
                LOGGER.atSevere().withCause(e).log();
            }
            return null;
        }

        /**
         * Gets the booster state of all boosters on the network
         *
         * @return the booster state of all boosters on the network
         */
        public BoostersReply.BoosterState getBoosterState() {
            try {
                return API.getBoosters().get().getBoosterState();
            } catch (Exception e) {
                LOGGER.atSevere().withCause(e).log();
            }
            return null;
        }

        public KeyReply.Key getKeyInfo() {
            try {
                return API.getKey().get().getRecord();
            } catch (Exception e) {
                LOGGER.atSevere().withCause(e).log();
            }
            return null;
        }

        /**
         * Gets all leaderboards for a specific game
         *
         * @param gameType game to get leaderboards of
         * @return all leaderboards for a specific game
         */
        public List<LeaderboardsReply.Leaderboard> getLeaderboard(net.hypixel.api.util.GameType gameType) {
            try {
                return API.getLeaderboards().get().getLeaderboards().get(gameType);
            } catch (Exception e) {
                LOGGER.atSevere().withCause(e).log();
            }
            return null;
        }

        /**
         * Gets a specific leaderboard for a specific game
         *
         * @param gameType game to get leaderboard od
         * @param path     path of leaderboard
         * @param prefix   prefix of leaderboard
         * @return a specific leaderboard for a specific game
         */
        public LeaderboardsReply.Leaderboard getLeaderboard(net.hypixel.api.util.GameType gameType, LeaderboardPaths path, LeaderboardPrefix prefix) {
            try {
                AtomicReference<LeaderboardsReply.Leaderboard> selectedLeaderboard = new AtomicReference<>();
                API.getLeaderboards().get().getLeaderboards().get(gameType).forEach(leaderboard -> {
                    if(leaderboard.getPath().equals(path.toString()) && leaderboard.getPrefix().equals(prefix.toString()))
                        selectedLeaderboard.set(leaderboard);
                });
                return selectedLeaderboard.get();
            } catch (Exception e) {
                LOGGER.atSevere().withCause(e).log();
            }
            return null;
        }

        public List<RecentGamesReply.GameSession> getRecentGames(UUID uuid) {
            try {
                return API.getRecentGames(uuid).get().getGames();
            } catch (Exception e) {
                LOGGER.atSevere().withCause(e).log();
            }
            return null;
        }

        public StatusReply.Session getStatus(UUID uuid) {
            try {
                return API.getStatus(uuid).get().getSession();
            } catch (Exception e) {
                LOGGER.atSevere().withCause(e).log();
            }
            return null;
        }

        public int getDailyStaffBans() {
            try {
                return API.getWatchdogStats().get().getStaffRollingDaily();
            } catch (Exception e) {
                LOGGER.atSevere().withCause(e).log();
            }
            return 0;
        }

        public int getDailyWatchdogBans() {
            try {
                return API.getWatchdogStats().get().getWatchdogRollingDaily();
            } catch (Exception e) {
                LOGGER.atSevere().withCause(e).log();
            }
            return 0;
        }

        public int getLastMinuteWatchdogBans() {
            try {
                return API.getWatchdogStats().get().getWatchdogLastMinute();
            } catch (Exception e) {
                LOGGER.atSevere().withCause(e).log();
            }
            return 0;
        }

        public int getStaffTotalBans() {
            try {
                return API.getWatchdogStats().get().getStaffTotal();
            } catch (Exception e) {
                LOGGER.atSevere().withCause(e).log();
            }
            return 0;
        }

        public int getWatchdogTotalBans() {
            try {
                return API.getWatchdogStats().get().getWatchdogTotal();
            } catch (Exception e) {
                LOGGER.atSevere().withCause(e).log();
            }
            return 0;
        }

        public JsonObject getPlayerData(UUID uuid) {
            try {
                return API.getPlayerByUuid(uuid).get().getPlayer();
            } catch (Exception e) {
                LOGGER.atSevere().withCause(e).log();
            }
            return null;
        }

        public long getFirstLoginTime(UUID uuid) {
            try {
                return API.getPlayerByUuid(uuid).get().getPlayer().get("firstLogin").getAsLong();
            } catch (Exception e) {
                LOGGER.atInfo().withCause(e).log();
            }
            return 0L;
        }

        public List<String> getKnownAliases(UUID uuid) {
            try {
                AtomicReference<List<String>> knownAliases = new AtomicReference<>();
                API.getPlayerByUuid(uuid).get().getPlayer().get("knownAliases").getAsJsonArray().forEach(alias -> knownAliases.get().add(alias.getAsString()));
                return knownAliases.get();
            } catch (Exception e) {
                LOGGER.atSevere().withCause(e).log();
            }
            return null;
        }

        public long getLastLogin(UUID uuid) {
            try {
                return API.getPlayerByUuid(uuid).get().getPlayer().get("lastLogin").getAsLong();
            } catch (Exception e) {
                LOGGER.atSevere().withCause(e).log();
            }
            return 0L;
        }

        public Rank getRank(UUID uuid) {
            try {
                return Rank.valueOf(API.getPlayerByUuid(uuid).get().getPlayer().get("newPackageRank").getAsString());
            } catch (Exception e) {
                LOGGER.atSevere().withCause(e).log();
            }
            return Rank.NONE;
        }

        public long getLastLogout(UUID uuid) {
            try {
                return API.getPlayerByUuid(uuid).get().getPlayer().get("lastLogout").getAsLong();
            } catch (Exception e) {
                LOGGER.atSevere().withCause(e).log();
            }
            return 0L;
        }

        public long getKarma(UUID uuid) {
            try {
                return API.getPlayerByUuid(uuid).get().getPlayer().get("karma").getAsLong();
            } catch (Exception e) {
                LOGGER.atSevere().withCause(e).log();
            }
            return 0L;
        }

        public double getNetworkExp(UUID uuid) {
            try {
                return API.getPlayerByUuid(uuid).get().getPlayer().get("networkExp").getAsDouble();
            } catch (Exception e) {
                LOGGER.atSevere().withCause(e).log();
            }
            return 0D;
        }

        public long getAchievementPoints(UUID uuid) {
            try {
                return API.getPlayerByUuid(uuid).get().getPlayer().get("achievementPoints").getAsLong();
            } catch (Exception e) {
                LOGGER.atSevere().withCause(e).log();
            }
            return 0L;
        }

        public String getSocialMediaLink(UUID uuid, SocialMedia socialMedia) {
            try {
                return API.getPlayerByUuid(uuid).get().getPlayer().getAsJsonObject("socialMedia").getAsJsonObject("links").get(socialMedia.toString()).getAsString();
            } catch (Exception e) {
                LOGGER.atSevere().withCause(e).log();
            }
            return null;
        }

        public Locale getPlayerLanguage(UUID uuid) {
            try {
                return Locale.forLanguageTag(API.getPlayerByUuid(uuid).get().getPlayer().get("userLanguage").getAsString());
            } catch (Exception e) {
                LOGGER.atSevere().withCause(e).log();
            }
            return Locale.ENGLISH;
        }

        public String getCurrentPet(UUID uuid) {
            try {
                return API.getPlayerByUuid(uuid).get().getPlayer().get("currentPet").getAsString();
            } catch (Exception e) {
                LOGGER.atSevere().withCause(e).log();
            }
            return null;
        }

        public String getCurrentGadget(UUID uuid) {
            try {
                return API.getPlayerByUuid(uuid).get().getPlayer().get("currentGadget").getAsString();
            } catch (Exception e) {
                LOGGER.atSevere().withCause(e).log();
            }
            return null;
        }

        public net.hypixel.api.util.GameType getMostRecentGamePlayed(UUID uuid) {
            try {
                return net.hypixel.api.util.GameType.valueOf(API.getPlayerByUuid(uuid).get().getPlayer().get("mostRecentGameType").getAsString());
            } catch (Exception e) {
                LOGGER.atSevere().withCause(e).log();
            }
            return null;
        }

        public String getValue(UUID uuid, String... valuePath) {
            try {
                JsonObject obj = API.getPlayerByUuid(uuid).get().getPlayer();
                for (int i = 0; i < valuePath.length - 2; i++) {
                    obj = obj.getAsJsonObject(valuePath[i]);
                }
                return obj.get(valuePath[valuePath.length - 1]).getAsString();
            } catch (Exception e) {
                LOGGER.atSevere().withCause(e).log();
            }
            return null;
        }

        public UUID getAPIKey() {
            return API.getApiKey();
        }

        public API.Hypixel.Bedwars getBedwars() {
            return new Bedwars(this);
        }

        public static class Bedwars {
            private static final FluentLogger LOGGER = FluentLogger.forEnclosingClass();
            private static HypixelAPI API;

            public Bedwars(API.Hypixel api) {
                API = new HypixelAPI(api.getAPIKey());
            }

            public JsonObject getBedwarsStats(UUID uuid) {
                try {
                    return API.getPlayerByUuid(uuid).get().getPlayer().getAsJsonObject("stats").getAsJsonObject("Bedwars");
                } catch (Exception e) {
                    LOGGER.atSevere().withCause(e).log();
                }
                return null;
            }

            public long getBedwarsExp(UUID uuid) {
                try {
                    return this.getBedwarsStats(uuid).get("Experience").getAsLong();
                } catch (Exception e) {
                    LOGGER.atSevere().withCause(e).log();
                }
                return 0L;
            }

            public Map<PrivateGameSettings, Object> getPrivateGameSettings(UUID uuid) {
                try {
                    Map<PrivateGameSettings, Object> settings = new HashMap<>();
                    Json.getMapFromJsonObject(this.getBedwarsStats(uuid).getAsJsonObject("privategames")).forEach((option, setting) -> settings.put(PrivateGameSettings.valueOf(option.toUpperCase()), setting));
                    return settings;
                } catch (Exception e) {
                    LOGGER.atSevere().withCause(e).log();
                }
                return null;
            }

            public String getSelectedUltimate(UUID uuid) {
                try {
                    return this.getBedwarsStats(uuid).get("selected_ultimate").getAsString();
                } catch (Exception e) {
                    LOGGER.atSevere().withCause(e).log();
                }
                return null;
            }

            public int getItemPurchased(UUID uuid) {
                try {
                    return this.getBedwarsStats(uuid).get("_items_purchaced_bedwars").getAsInt();
                } catch (Exception e) {
                    LOGGER.atSevere().withCause(e).log();
                }
                return 0;
            }

            public int getBedsLost(UUID uuid) {
                try {
                    return this.getBedwarsStats(uuid).get("beds_lost_bedwars").getAsInt();
                } catch (Exception e) {
                    LOGGER.atSevere().withCause(e).log();
                }
                return 0;
            }

            public int getBedsBroken(UUID uuid) {
                try {
                    return this.getBedwarsStats(uuid).get("beds_broken_bedwars").getAsInt();
                } catch (Exception e) {
                    LOGGER.atSevere().withCause(e).log();
                }
                return 0;
            }

            public int getCoins(UUID uuid) {
                try {
                    return this.getBedwarsStats(uuid).get("coins").getAsInt();
                } catch (Exception e) {
                    LOGGER.atSevere().withCause(e).log();
                }
                return 0;
            }

            public int getDeaths(UUID uuid) {
                try {
                    return this.getBedwarsStats(uuid).get("deaths_bedwars").getAsInt();
                } catch (Exception e) {
                    LOGGER.atSevere().withCause(e).log();
                }
                return 0;
            }

            public int getDiamondsCollected(UUID uuid) {
                try {
                    return this.getBedwarsStats(uuid).get("diamond_resources_collected_bedwars").getAsInt();
                } catch (Exception e) {
                    LOGGER.atSevere().withCause(e).log();
                }
                return 0;
            }

            public int getEmeraldsCollected(UUID uuid) {
                try {
                    return this.getBedwarsStats(uuid).get("emerald_resources_collected_bedwars").getAsInt();
                } catch (Exception e) {
                    LOGGER.atSevere().withCause(e).log();
                }
                return 0;
            }

            public int getDeathsByEntity(UUID uuid) {
                try {
                    return this.getBedwarsStats(uuid).get("entity_attack_deaths_bedwars").getAsInt();
                } catch (Exception e) {
                    LOGGER.atSevere().withCause(e).log();
                }
                return 0;
            }

            public int getFinalDeathsByEntity(UUID uuid) {
                try {
                    return this.getBedwarsStats(uuid).get("entity_attack_final_deaths_bedwars").getAsInt();
                } catch (Exception e) {
                    LOGGER.atSevere().withCause(e).log();
                }
                return 0;
            }

            public int getKillsByEntity(UUID uuid) {
                try {
                    return this.getBedwarsStats(uuid).get("entity_attack_kills_bedwars").getAsInt();
                } catch (Exception e) {
                    LOGGER.atSevere().withCause(e).log();
                }
                return 0;
            }

            public int getFinalDeaths(UUID uuid) {
                try {
                    return this.getBedwarsStats(uuid).get("final_deaths_bedwars").getAsInt();
                } catch (Exception e) {
                    LOGGER.atSevere().withCause(e).log();
                }
                return 0;
            }

            public int getFinalKills(UUID uuid) {
                try {
                    return this.getBedwarsStats(uuid).get("final_kills_bedwars").getAsInt();
                } catch (Exception e) {
                    LOGGER.atSevere().withCause(e).log();
                }
                return 0;
            }

            public int getGamesPlayed(UUID uuid) {
                try {
                    return this.getBedwarsStats(uuid).get("games_played_bedwars").getAsInt();
                } catch (Exception e) {
                    LOGGER.atSevere().withCause(e).log();
                }
                return 0;
            }

            public int getGoldCollected(UUID uuid) {
                try {
                    return this.getBedwarsStats(uuid).get("gold_resourced_collected_bedwars").getAsInt();
                } catch (Exception e) {
                    LOGGER.atSevere().withCause(e).log();
                }
                return 0;
            }

            public int getIronCollected(UUID uuid) {
                try {
                    return this.getBedwarsStats(uuid).get("iron_resources_collected_bedwars").getAsInt();
                } catch (Exception e) {
                    LOGGER.atSevere().withCause(e).log();
                }
                return 0;
            }

            public int getItemsPurchaced(UUID uuid) {
                try {
                    return this.getBedwarsStats(uuid).get("items_purchaced_bedwars").getAsInt();
                } catch (Exception e) {
                    LOGGER.atSevere().withCause(e).log();
                }
                return 0;
            }

            public enum PrivateGameSettings {
                HEALTH_BUFF,
                LOW_GRAVITY,
                SPEED,
                RESPAWN_TIME,
                EVENT_TIME,
                DISABLE_BLOCK_PROTECTION,
                MAX_TEAM_UPGRADES,
                NO_DIAMONDS,
                NO_EMERALDS,
                BED_INSTABREAK,
                ONE_HIT_ONE_KILL;


                /**
                 * Returns the name of this enum constant, as contained in the
                 * declaration.  This method may be overridden, though it typically
                 * isn't necessary or desirable.  An enum type should override this
                 * method when a more "programmer-friendly" string form exists.
                 *
                 * @return the name of this enum constant
                 */
                @Override
                public String toString() {
                    return super.toString().toLowerCase();
                }
            }
        }

        public enum SocialMedia {
            DISCORD,
            YOUTUBE,
            TWITTER,
            INSTAGRAM,
            TWITCH,
            HYPIXEL_FOURMS
        }

        public enum Rank {
            NONE,
            VIP,
            VIP_PLUS,
            MVP,
            MVP_PLUS,
            MVP_PLUS_PLUS,
            YOUTUBE,
            HELPER,
            MOD,
            ADMIN,
            OWNER
        }

        public enum LeaderboardPrefix {
            OVERALL,
            WEEKLY,
            CURRENT;

            /**
             * Returns the name of this enum constant, as contained in the
             * declaration.  This method may be overridden, though it typically
             * isn't necessary or desirable.  An enum type should override this
             * method when a more "programmer-friendly" string form exists.
             *
             * @return the name of this enum constant
             */
            @Override
            public String toString() {
                switch (this) {
                    case WEEKLY:
                        return "Weekly";
                    case CURRENT:
                        return "Current";
                    case OVERALL:
                        return "Overall";
                }
                return "";
            }
        }

        public enum LeaderboardPaths {
            BEDWARS_LEVEL,
            WINS,
            WINS_1,
            FINAL_KILLS,
            FINAL_KILLS_1;

            /**
             * Returns the name of this enum constant, as contained in the
             * declaration.
             *
             * @return the name of this enum constant
             */
            @Override
            public String toString() {
                return super.toString().toLowerCase();
            }
        }

        public enum GameType {
            QUAKECRAFT,
            WALLS,
            PAINTBALL,
            BLITZ_SURVIVAL_GAMES,
            TNTGAMES,
            VAMPIREZ,
            MEGA_WALLS,
            ARCADE,
            ARENA,
            UHC,
            COPS_AND_CRIMS,
            WARLORDS,
            SMASH_HEROS,
            TURBO_KART_RACERS,
            HOUSING,
            SKYWARS,
            CRAZY_WALLS,
            SPEED_UHC,
            SKYCLASH, // Was removed from network
            CLASSIC_GAMES,
            PROTOTYPE,
            BEDWARS,
            MURDER_MYSTERY,
            BUILD_BATTLE,
            DUELS,
            SKYBLOCK,
            PIT;


            /**
             * Returns the name of this enum constant, as contained in the
             * declaration.
             *
             * @return the name of this enum constant
             */
            @Override
            public String toString() {
                switch (this) {
                    case BLITZ_SURVIVAL_GAMES:
                        return "SURVIVAL_GAMES";
                    case WARLORDS:
                        return "BATTLEGROUND";
                    case MEGA_WALLS:
                        return "WALLS3";
                    case CRAZY_WALLS:
                        return "TRUE_COMBAT";
                    case SMASH_HEROS:
                        return "SUPER_SMASH";
                    case CLASSIC_GAMES:
                        return "LEGACY";
                    case COPS_AND_CRIMS:
                        return "MCGO";
                    case TURBO_KART_RACERS:
                        return "GINGERBREAD";
                    default:
                        return super.toString();
                }
            }

            /**
             * Converts a {@code me.cyberdie22.apilibrary.API.Hypixel.GameType} object to a {@code net.hypixxel.api.util.GameType} object
             */
            public net.hypixel.api.util.GameType convert() {
                return net.hypixel.api.util.GameType.valueOf(this.toString());
            }
        }
    }

    public static class Mojang {

        private final Map<ServiceType, ServiceStatus> apiStatus;

        public Mojang() {
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
         *
         * @return {@link me.cyberdie22.apilibrary.API.Mojang} class
         */
        public API.Mojang connect() {
            JsonArray array = Json.getJSONArrayFromUrl("https://status.mojang.com/check");

            for (int i = 0; i <= 7; i++) {
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
        public static void clearCaches() {
            cachedUUIDs.clear();
            cachedSkinTextures.clear();
            cachedCapeTextures.clear();
            cachedNameHistory.clear();
            cachedPlayerProfiles.clear();
        }

        /**
         * Gets the status of a service
         *
         * @param service {@link me.cyberdie22.apilibrary.API.Mojang.ServiceType} to get {@link me.cyberdie22.apilibrary.API.Mojang.ServiceStatus} of
         * @return {@link me.cyberdie22.apilibrary.API.Mojang.ServiceStatus} of a service
         */
        public ServiceStatus getStatus(ServiceType service) {
            if(service == null) return ServiceStatus.UNKNOWN;
            return apiStatus.get(service);
        }

        /*
         * API Methods
         */

        /**
         * <p>Gets a players UUID from {@literal https://api.mojang.com/}</p>
         *
         * @param username Players name
         * @return Players UUID as a {@link java.lang.String}
         */
        public String getUuidOfPlayer(String username) {
            // Return Cached UUID
            if(cachedUUIDs.containsKey(username.toLowerCase())) return cachedUUIDs.get(username.toLowerCase());
            LOGGER.atInfo().log("Not getting UUID from cache!");
            // Return null if https://api.mojang.com/ is down
            if(!apiStatus.get(ServiceType.API_MOJANG_COM).equals(ServiceStatus.GREEN)) return null;
            String uuid = Json.getJSONObjectFromURL("https://api.mojang.com/users/profiles/minecraft/" + username).get("id").getAsString();
            if(uuid != null) cachedUUIDs.put(username.toLowerCase(), uuid);
            return uuid;
        }

        /**
         * Gets a players UUID from {@literal https://api.mojang.com} and fixes it to be a UUID object
         *
         * @param username Players name
         * @return Players UUID as a UUID object
         */
        public UUID getFixedUuidOfPlayer(String username) {
            // Return null if https://api.mojang.com/ is down
            if(!apiStatus.get(ServiceType.API_MOJANG_COM).equals(ServiceStatus.GREEN)) return null;
            return fixUUID(Json.getJSONObjectFromURL("https://api.mojang.com/users/profiles/minecraft/" + username).get("id").getAsString());
        }

        /**
         * <p>Gets a players UUID from {@literal https://api.mojang.com/}</p>
         *
         * @param username  Players name
         * @param timestamp The time for when to check for the name
         * @return Players UUID as a {@link java.lang.String}
         */
        public String getUuidOfPlayer(String username, long timestamp) {
            // Return null if https://api.mojang.com/ is down
            if(apiStatus.get(ServiceType.API_MOJANG_COM).equals(ServiceStatus.GREEN)) return null;
            return Json.getJSONObjectFromURL("https://api.mojang.com/users/profiles/minecraft/" + username + "?at=" + timestamp).get("id").getAsString();
        }

        /**
         * Gets the name history of the provided uuid from {@literal https://api.mojang.com/}
         *
         * @param uuid UUID to get name history of
         * @return Name history of provided UUID
         */
        public Map<String, Long> getNameHistoryOfPlayer(String uuid) {
            // Return cached name history if possible
            if(cachedNameHistory.containsKey(uuid)) return cachedNameHistory.get(uuid);
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
         *
         * @param uuid UUID to get player profile of
         * @return Player Profile of provided UUID using a {@link me.cyberdie22.apilibrary.API.PlayerProfile} (Only returns with Mojang data, does not return with Hypixel data)
         */
        public PlayerProfile getPlayerProfile(String uuid) {
            // Return cached player profile if possible
            if(cachedPlayerProfiles.containsKey(uuid)) return cachedPlayerProfiles.get(uuid);
            LOGGER.atInfo().log("Not getting Player Profile from cache!");
            // Return null if session server is down
            if(!(getStatus(ServiceType.SESSIONSERVER_MOJANG_COM) == ServiceStatus.GREEN)) return null;
            JsonObject obj = Json.getJSONObjectFromURL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);
            String name = obj.get("name").getAsString();
            Set<PlayerProfile.Property> properties = new HashSet<>();
            obj.get("properties").getAsJsonArray().forEach(o ->
            {
                PlayerProfile.Property property;
                JsonObject prop = (JsonObject) o;

                String propName = prop.get("name").getAsString();
                String propValue = prop.get("value").getAsString();
                if(propName.equals("textures")) {
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
                    try {
                        textureProperties.signatureRequired = tex.get("signatureRequired").getAsBoolean();
                    } catch (Exception ignored) {
                    }
                    URL skin = null;
                    URL cape = null;
                    try {
                        JsonObject textures = tex.getAsJsonObject("textures");
                        skin = new URL(textures.getAsJsonObject("SKIN").get("url").getAsString());
                        try {
                            cape = new URL(textures.getAsJsonObject("CAPE").get("url").getAsString());
                        } catch (Exception ignored) {
                        }
                    } catch (Exception e) {
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
                try {
                    property.signature = prop.get("signature").getAsString();
                } catch (Exception ignored) {
                }
                property.value = propValue;
                properties.add(property);
            });
            PlayerProfile playerProfile = new PlayerProfile(uuid, name, properties);
            cachedPlayerProfiles.put(uuid, playerProfile);
            return playerProfile;
        }

        public PlayerProfile getPlayerProfile(String uuid, PlayerProfile playerProfile) {
            PlayerProfile profile = getPlayerProfile(uuid);
            playerProfile.username = profile.username;
            playerProfile.uuid = profile.uuid;
            playerProfile.textures = profile.textures;
            playerProfile.properties = profile.properties;
            return profile;
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

        public enum ServiceStatus {
            RED,
            YELLOW,
            GREEN,
            UNKNOWN
        }

        /**
         * This enum represents the various portions of the Mojang API.
         */
        public enum ServiceType {
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
            public String toString() {
                return name().toLowerCase().replace("_", ".");
            }
        }

        /**
         * This enum represents the skin types "Alex" and "Steve".
         */
        public enum SkinType {
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
            public String toString() {
                return this == DEFAULT ? "" : "slim";
            }
        }
    }

    public static class PlayerProfile {

        private String uuid;
        private String username;
        private Set<Property> properties;
        private Optional<TexturesProperty> textures;
        private boolean online;
        private GameType game;
        private String gameMode;
        private String map;

        /**
         * Represents a property.
         */
        public static class Property {
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
            public String toString() {
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
         * @param uuid       the UUID of the player this object should represent
         * @param username   the username of said player (you may use {@code API.Mojang().connect().getNameHistoryOfPlayer(uuid)} to retrieve it).
         * @param properties the properties for that player. Depends on what you wish to do with the object
         */
        public PlayerProfile(String uuid, String username, Set<Property> properties) {
            this.uuid = uuid;
            this.username = username;
            this.properties = properties;
            this.textures = properties.stream().filter(p -> p.getName().equals("textures")).map(p -> (TexturesProperty) p).findAny();
        }

        /**
         * <p>Constructor for the class</p>
         * <p>You may use {@code new API.Hypixel().getPlayerProfile(uuid)} to retrieve the instance as it will verify the validity of the parameters</p>
         *
         * @param uuid     the UUID of the player this object should represent
         * @param username the username of said player (you may use {@code API.Mojang().connect().getNameHistoryOfPlayer(uuid)} to retrueve it)
         * @param online   whether or not the player is online on Hypixel
         * @param game     the game the player is playing
         * @param gameMode the game mode the player is playing
         * @param map      the map the player is on
         */
        public PlayerProfile(String uuid, String username, boolean online, GameType game, String gameMode, String map) {
            this.uuid = uuid;
            this.username = username;
            this.online = online;
            this.game = game;
            this.gameMode = gameMode;
            this.map = map;
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

        /**
         * <p>Returns whether or not the player is online on Hypixel</p>
         *
         * @return Returns whether or not the player is online on Hypixel
         */
        public boolean isOnline() {
            return online;
        }

        /**
         * <p>Returns the game the player is in if the player is online on Hypixel</p>
         *
         * @return The game the player is in
         */
        public GameType getGame() {
            return game;
        }

        /**
         * <p>Returns the game mode the player is in if the player is playing a game and is online on Hypixel</p>
         *
         * @return The game mode the player is in
         */
        public String getGameMode() {
            return gameMode;
        }

        /**
         * <p>Returns the map the player is on if the player is playing a game and is online on Hypixel</p>
         *
         * @return The map the player is on
         */
        public String getMap() {
            return map;
        }

        @Override
        public String toString() {
            return "PlayerProfile{" +
                "uuid='" + uuid + '\'' +
                ", username='" + username + '\'' +
                ", properties=" + properties.toString() +
                ", \ntextures=" + textures.toString() +
                '}';
        }
    }
}
