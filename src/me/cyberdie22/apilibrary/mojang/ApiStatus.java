package me.cyberdie22.apilibrary.mojang;

import com.google.gson.JsonArray;
import me.cyberdie22.apilibrary.Json;

import java.net.URL;

public class ApiStatus
{

    public static boolean isOnline()
    {
        try {
            URL statusURL = new URL("https://status.mojang.com/check");
            JsonArray array = (JsonArray) Json.getURLAsJsonObject(statusURL);
            String element = array.get(5).getAsJsonObject().get("api.mojang.com").getAsString();
            Status apistatus = Status.fromString(element);
            return apistatus == Status.GREEN;
        } catch (Exception ignored){}
        return false;
    }
}
