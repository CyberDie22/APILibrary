package me.cyberdie22.apilibrary;

import com.google.common.flogger.FluentLogger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class APILibrary {

    private static final FluentLogger LOGGER = FluentLogger.forEnclosingClass();
    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private static API.Mojang mojang;

    public static void main(String[] args) {
        executorService.scheduleAtFixedRate(API.Mojang::clearCaches, 0, 1, TimeUnit.MINUTES);

        mojang = new API.Mojang().connect();
        if (mojang.getStatus(API.Mojang.ServiceType.API_MOJANG_COM) == API.Mojang.ServiceStatus.GREEN) LOGGER.atInfo().log("Mojang API is online");
        else LOGGER.atWarning().log("Mojang API is offline, cannot ensure that player will exist in Hypixel API Database");
        
        LOGGER.atInfo().log("%s", mojang.getPlayerProfile(mojang.getUuidOfPlayer("Minecraaftt")).toString());
        mojang.getNameHistoryOfPlayer(mojang.getUuidOfPlayer("Minecraaftt"));
        LOGGER.atInfo().log("%s", mojang.getPlayerProfile(mojang.getUuidOfPlayer("Minecraaftt")).toString());
        try
        {
            Thread.sleep(60 * 1000);
        } catch (InterruptedException ignored) {}
        LOGGER.atInfo().log("%s", mojang.getPlayerProfile(mojang.getUuidOfPlayer("Minecraaftt")).toString());
        shutdown();
    }

    /**
     * Call this method when shutting down
     */
    public static void shutdown()
    {
        executorService.shutdown();
    }
}
