package me.cyberdie22.apilibrary;

import com.google.common.flogger.FluentLogger;
import me.cyberdie22.apilibrary.mojang.ApiStatus;
import me.cyberdie22.apilibrary.mojang.PlayerUUID;

public class APILibrary {

    private static final FluentLogger LOGGER = FluentLogger.forEnclosingClass();

    public static void main(String[] args) {
        PlayerUUID.retrieveCaches();
        if (ApiStatus.isOnline()) LOGGER.atInfo().log("Mojang API is online");
        else LOGGER.atWarning().log("Mojang API is offline, cannot ensure that player will exist in Hypixel API Database");
    }
}
