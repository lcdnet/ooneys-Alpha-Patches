package net.minecraft.src;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CapeManager {
    private static final Logger LOGGER = Logger.getLogger(CapeManager.class.getName());
    // This cache now stores username -> capeType (e.g., "owner", "none")
    private static final HashMap<String, String> customCapeTypeCache = new HashMap<String, String>();
    private static boolean hasInitializedLocalPlayerCape = false;

    // Callback interface for asynchronous cape type fetching
    public interface CapeTypeCallback {
        void onCapeTypeFetched(String username, String capeType);
    }

    /**
     * Called ONCE when Minecraft launches to fetch the local player's cape data.
     * This method now uses the new asynchronous fetching logic.
     */
    public static void loadLocalPlayerCapeAtLaunch(final String username) {
        if (hasInitializedLocalPlayerCape || username == null || username.trim().isEmpty()) return;
        hasInitializedLocalPlayerCape = true;

        fetchAndCacheCapeTypeAsync(username, new CapeTypeCallback() {
            @Override
            public void onCapeTypeFetched(String username, String capeType) {
                // No direct action needed here for the local player, as EntityPlayer.updateCloak will handle it.
                // The type is already cached in customCapeTypeCache by fetchAndCacheCapeTypeAsync.
                LOGGER.info("Local player cape type pre-fetched for " + username + ": " + capeType);
            }
        });
    }

    /**
     * Fetches the custom cape type for a given username asynchronously from the database.
     * If a custom cape type is found, it is stored in the internal customCapeTypeCache.
     * The callback is invoked with the fetched type (or "none" if no custom cape).
     */
    public static void fetchAndCacheCapeTypeAsync(final String username, final CapeTypeCallback callback) {
        if (username == null || username.trim().isEmpty()) {
            if (callback != null) {
                callback.onCapeTypeFetched(username, "none");
            }
            return;
        }

        final String lowerName = username.toLowerCase().trim();

        // Check internal custom cape cache first to avoid redundant network requests
        if (customCapeTypeCache.containsKey(lowerName)) {
            if (callback != null) {
                callback.onCapeTypeFetched(username, customCapeTypeCache.get(lowerName));
            }
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                String fetchedCapeType = "none"; // Default to "none" if no custom cape is found
                try {
                    LOGGER.info("Attempting to fetch custom cape data for: " + username);

                    URL url = new URL("https://capeapi.onrender.com/api/cape/" + lowerName);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000); // 5 seconds timeout
                    conn.setReadTimeout(5000);    // 5 seconds timeout

                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
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

                            if (!"none".equalsIgnoreCase(capeType) && !capeType.isEmpty()) {
                                fetchedCapeType = capeType;
                                LOGGER.info("Successfully fetched custom cape type for " + username + ": " + fetchedCapeType);
                            }
                        }
                    } else {
                        LOGGER.warning("Failed to fetch custom cape type for " + username + ". HTTP response code: " + responseCode);
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error fetching custom cape data for " + username, e);
                } finally {
                    // Store the fetched type (even "none") in the internal customCapeTypeCache
                    customCapeTypeCache.put(lowerName, fetchedCapeType);
                    if (callback != null) {
                        callback.onCapeTypeFetched(username, fetchedCapeType);
                    }
                }
            }
        }).start();
    }

    /**
     * Pure memory lookup for custom cape types.
     * Returns the custom cape type if available in the internal cache, otherwise "none".
     */
    public static String getPlayerCape(String username) {
        if (username == null) return "none";
        String lowerName = username.toLowerCase().trim();
        return customCapeTypeCache.getOrDefault(lowerName, "none");
    }
}