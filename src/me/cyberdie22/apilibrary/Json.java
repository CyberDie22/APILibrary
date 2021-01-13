package me.cyberdie22.apilibrary;

import com.google.common.flogger.FluentLogger;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Json {

    private static final FluentLogger LOGGER = FluentLogger.forEnclosingClass();

    public static Object getStringAsJsonObject(@NotNull String string) {
        try {
            return new JsonParser().parse(string).getAsJsonObject();
        } catch (Exception e) {
            LOGGER.atInfo().log("Inputted String %s is not a JsonObject, trying to get as a JsonArray", string);
            return new JsonParser().parse(string).getAsJsonArray();
        }
    }

    public static String getURLAsString(@NotNull URL URL) {
        try {
            LOGGER.atInfo().log("URL = %s", URL);
            HttpsURLConnection URLconnection = (HttpsURLConnection) URL.openConnection();
            URLconnection.setRequestMethod("GET");
            URLconnection.connect();
            int code = URLconnection.getResponseCode();
            if (code == 204) return "204";
            if (code == 400) return "400";
            InputStream inputStream = URLconnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            return bufferedReader.readLine();

        } catch (Exception ignored) {}

        return "";
    }

    public static Object getURLAsJsonObject(@NotNull URL URL)
    {
        return getStringAsJsonObject(getURLAsString(URL));
    }

    public static String convertMapToJsonObject(@NotNull Map<String, String> map)
    {
        JsonObject obj = new JsonObject();
        for (Map.Entry<String, String> entry : map.entrySet())
        {
            obj.addProperty(entry.getKey(), entry.getValue());
        }
        return obj.toString();
    }

    public static Map<String, String> convertJsonStringToMap(@NotNull String json)
    {
        JsonObject obj = (JsonObject) getStringAsJsonObject(json);
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : obj.entrySet())
        {
            map.put(entry.getKey(), entry.getValue().getAsString());
        }
        return map;
    }

}