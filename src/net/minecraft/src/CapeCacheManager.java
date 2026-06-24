package net.minecraft.src;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64; // Java 8 Base64

public class CapeCacheManager {
    private static final Logger LOGGER = Logger.getLogger(CapeCacheManager.class.getName());
    private static final String CACHE_FILE_NAME = "cape_cache.properties";
    private static final int MAX_CACHE_SIZE = 1000;
    private static CapeCacheManager instance;
    private LinkedHashMap<String, String> capeCache;
    private final File cacheFile;


    private static final String cache = "cape_cache.props";
    private static final String a = "AES";

    private CapeCacheManager() {
        File configDir = new File(System.getProperty("user.dir"), "config");
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
        cacheFile = new File(configDir, CACHE_FILE_NAME);
        capeCache = new LinkedHashMap<String, String>(MAX_CACHE_SIZE + 1, .75F, true) {
            protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                return size() > MAX_CACHE_SIZE;
            }
        };
        loadCache();
    }

    public static synchronized CapeCacheManager getInstance() {
        if (instance == null) {
            instance = new CapeCacheManager();
        }
        return instance;
    }

    private Key generateKey() throws Exception {
        return new SecretKeySpec(cache.getBytes("UTF-8"), a);
    }

    private String u(String data) {
        try {
            Key key = generateKey();
            Cipher c = Cipher.getInstance(a);
            c.init(Cipher.ENCRYPT_MODE, key);
            byte[] encVal = c.doFinal(data.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(encVal);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error encrypting data", e);
            return null;
        }
    }

    private String e(String ed) {
        try {
            Key key = generateKey();
            Cipher c = Cipher.getInstance(a);
            c.init(Cipher.DECRYPT_MODE, key);
            byte[] decodedValue = Base64.getDecoder().decode(ed);
            byte[] decValue = c.doFinal(decodedValue);
            return new String(decValue, "UTF-8");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "How dare you..", e);

            return null;
        }
    }

    private void loadCache() {
        if (!cacheFile.exists()) {
            LOGGER.info("Cape cache file not found, creating new one.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(cacheFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                int separatorIndex = line.indexOf('=');
                if (separatorIndex > 0) {
                    String username = line.substring(0, separatorIndex).trim();
                    String ecu = line.substring(separatorIndex + 1).trim();
                    
                    String cu = e(ecu);
                    
                    if (cu != null && !username.isEmpty() && !cu.isEmpty()) {
                        capeCache.put(username, cu);
                    } else {
                        LOGGER.warning("Failure.");
                        if (!username.isEmpty() && !ecu.isEmpty()) {
                             capeCache.put(username, ecu);
                        }
                    }
                }
            }
            LOGGER.info("Loaded " + capeCache.size() + " entries from cape cache.");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading cape cache from " + cacheFile.getAbsolutePath(), e);
        }
    }

    public void saveCache() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(cacheFile))) {
            for (Map.Entry<String, String> entry : capeCache.entrySet()) {
                String encryptedCapeUrl = u(entry.getValue());
                if (encryptedCapeUrl != null) {
                    writer.write(entry.getKey() + "=" + encryptedCapeUrl);
                    writer.newLine();
                } else {
                    LOGGER.log(Level.WARNING, "Failuree.");
                }
            }
            LOGGER.info("Saved " + capeCache.size() + " entries to cape cache.");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving cape cache to " + cacheFile.getAbsolutePath(), e);
        }
    }

    public String getCapeUrl(String username) {
        return capeCache.get(username);
    }

    public void putCapeUrl(String username, String capeUrl) {
        if (username == null || username.isEmpty() || capeUrl == null || capeUrl.isEmpty()) {
            return;
        }
        capeCache.put(username, capeUrl);
        saveCache(); // Save cache immediately after adding a new entry or updating one.
    }
}