package me.cyberdie22.apilibrary;

import com.google.common.flogger.FluentLogger;

public class APILibrary {

    private static final FluentLogger LOGGER = FluentLogger.forEnclosingClass();

    public static void main(String[] args) {
        API.Mojang.retrieveCaches();
        if (API.Mojang.apiStatus() == Status.GREEN) LOGGER.atInfo().log("Mojang API is online");
        else LOGGER.atWarning().log("Mojang API is offline, cannot ensure that player will exist in Hypixel API Database");

        LOGGER.atInfo().log(API.Mojang.create().setPlayer("owaytt").getUUIDFromPlayer());
    }
}
