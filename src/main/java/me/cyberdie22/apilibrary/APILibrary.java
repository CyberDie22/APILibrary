package me.cyberdie22.apilibrary;

import com.google.common.flogger.FluentLogger;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class APILibrary {

    private static final FluentLogger LOGGER = FluentLogger.forEnclosingClass();
    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private static API.Mojang mojang;
    private static API.Hypixel hypixel;

    public static void main(String[] args) {
        executorService.scheduleAtFixedRate(API.Mojang::clearCaches, 0, 1, TimeUnit.MINUTES);

        mojang = new API.Mojang().connect();
        /* Replace API key provided here with your own API key */
        hypixel = new API.Hypixel("6c7bbdac-e436-470f-8c9b-3087134a1376");
        if(mojang.getStatus(API.Mojang.ServiceType.API_MOJANG_COM) == API.Mojang.ServiceStatus.GREEN) {
            LOGGER.atInfo().log("Mojang API is online");
        } else {
            LOGGER.atWarning().log("Mojang API is offline, cannot ensure that player will exist in Hypixel API Database");
        }

        LOGGER.atInfo().log("%s", hypixel.getBedwars().getSelectedUltimate(mojang.getFixedUuidOfPlayer("Minecraaftt")));

        shutdown();
    }

    /**
     * Call this method when shutting down
     */
    public static void shutdown() {
        executorService.shutdown();
        System.exit(0);
    }
}
