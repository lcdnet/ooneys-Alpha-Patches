package net.minecraft.src;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class CapeManager {
    private static final HashMap<String, String> capeCache = new HashMap<String, String>();
    private static boolean hasInitialized = false;

    /**
     * Called ONCE when Minecraft launches to fetch the local player's cape data.
     */
    public static void loadLocalPlayerCapeAtLaunch(final String username) {
        if (hasInitialized || username == null || username.trim().isEmpty()) return;
        hasInitialized = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Pre-fetching launch cape data for: " + username);

                    URL url = new URL("https://capeapi.onrender.com/api/cape/" + username.trim());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);

                    if (conn.getResponseCode() == 200) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String inputLine;

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        String json = response.toString();

                        if (json.contains("\"capeType\":\"")) {
                            String part = json.split("\"capeType\":\"")[1];
                            String capeType = part.split("\"")[0];

                            capeCache.put(username.toLowerCase().trim(), capeType);
                            System.out.println("Successfully cached launch cape: " + capeType);
                        }
                    } else {
                        capeCache.put(username.toLowerCase().trim(), "none");
                    }
                } catch (Exception e) {
                    System.out.println("Error fetching launch cape data.");
                    capeCache.put(username.toLowerCase().trim(), "none");
                }
            }
        }).start();
    }

    /**
     * Called frame-by-frame inside RenderPlayer.java.
     * Pure memory lookup—zero network requests or rate limits here.
     */
    public static String getPlayerCape(String username) {
        if (username == null) return "none";
        String lowerName = username.toLowerCase().trim();

        if (capeCache.containsKey(lowerName)) {
            return capeCache.get(lowerName);
        }
        return "none";
    }
}