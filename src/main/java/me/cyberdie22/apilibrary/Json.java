package me.cyberdie22.apilibrary;

import com.google.common.flogger.FluentLogger;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.http.ParseException;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class Json {

    private static final FluentLogger LOGGER = FluentLogger.forEnclosingClass();

    public static Object getUrlAsJsonObject(@NotNull String url) {
        try {
            return getJSONObjectFromURL(url);
        } catch (Exception e) {
            LOGGER.atInfo().log("Inputted URL: %s\ndoes not contain a JsonObject, trying to get as a JsonArray", url);
            return getJSONArrayFromUrl(url);
        }
    }

    public static Object getStringAsJsonObject(@NotNull String string) {
        try {
            return getJSONObjectFromString(string);
        } catch (Exception e) {
            LOGGER.atInfo().log("Inputted String: %s\ndoes not contain a JsonObject, trying to get as a JsonArray", string);
            return getJSONArrayFromString(string);
        }
    }

    public static JsonObject getJSONObjectFromURL(String url) {
        LOGGER.atInfo().log("Conecting to: %s", url);
        JsonObject obj;

        try {
            obj = (JsonObject) JsonParser.parseString(Unirest.get(url).asString().getBody());
            Object err = obj.get("error");
            if(err != null) {
                err = ((JsonElement) err).getAsString();
                if("IllegalArgumentException".equals(err)) {
                    throw new IllegalArgumentException(obj.get("errorMessage").getAsString());
                }
                throw new RuntimeException((String) err);
            }
        } catch (ParseException | UnirestException e) {
            throw new RuntimeException(e);
        }

        return obj;
    }

    public static JsonObject getJSONObjectFromString(String string) {
        JsonObject obj;

        try {
            obj = (JsonObject) JsonParser.parseString(string);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return obj;
    }

    public static JsonArray getJSONArrayFromUrl(String url) {
        LOGGER.atFiner().log("Conecting to: %s", url);
        JsonArray arr;

        try {
            arr = (JsonArray) JsonParser.parseString(Unirest.get(url).asString().getBody());
        } catch (ParseException | UnirestException e) {
            throw new RuntimeException(e);
        }

        return arr;
    }

    public static JsonArray getJSONArrayFromString(String string) {
        JsonArray arr;

        try {
            arr = (JsonArray) JsonParser.parseString(string);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return arr;
    }

    public static String getURLAsString(@NotNull URL URL) {
        try {
            LOGGER.atInfo().log("URL = %s", URL);
            HttpsURLConnection URLconnection = (HttpsURLConnection) URL.openConnection();
            URLconnection.setRequestMethod("GET");
            URLconnection.connect();
            int code = URLconnection.getResponseCode();
            LOGGER.atInfo().log("%s", code);
            if(code == 204) return "204";
            if(code == 400) return "400";
            InputStream inputStream = URLconnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            Stream<String> jsonMultiLine = bufferedReader.lines();
            StringBuilder json = new StringBuilder();
            jsonMultiLine.forEach(json::append);
            return json.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static Map<String, String> getMapFromJsonObject(JsonObject obj) {
        Map<String, String> result = new HashMap<>();
        obj.entrySet().forEach(entry -> result.put(entry.getKey(), entry.getValue().getAsString()));
        return result;
    }

    public static Object getURLAsJsonObject(@NotNull URL URL) {
        return getStringAsJsonObject(getURLAsString(URL));
    }

}