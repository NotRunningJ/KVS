package lvc.cds;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.*;

/**
 * Data structure used to store Strings as keys and JSONObjects as values.
 */
public class KVS {
    private HashMap<String, JSONObject> kvs; // Backing HashMap

    /**
     * Creates a KVS object.
     */
    public KVS() {
        kvs = new HashMap<>();
    }

    /**
     * Adds a key/value pair to the KVS
     * 
     * @param key   String, the key you are adding
     * @param value JSONObject, the value you are adding
     * @return If an existing key is passed then the previous value gets returned.
     *         If a new pair is passed, then NULL is returned.
     */
    public JSONObject put(String key, JSONObject value) {
        return kvs.put(key, value);
    }

    /**
     * Used to retrieve a JSONObject associated with a key
     * 
     * @param key The key you are trying to look up a JSONObject for
     * @return Returns a JSONObject
     */
    public JSONObject get(Object key) {
        return kvs.get(key);
    }

    /**
     * Get the size of the KVS
     * @return an int that is the size.
     */
    public int size() {
        return kvs.size();
    }

    /**
     * Used to lookup a value associated with a key in a JSONObject
     * 
     * @param key     The KVS key for the JSONObject
     * @param JSONkey The key for the field you are looking for
     * @return Return type is dependent on the JSON field
     */
    public Object getJSON(String key, String JSONkey) {
        if (kvs.get(key) != null) {
            JSONObject json = kvs.get(key);
            return json.get(JSONkey);
        }
        return null;
    }

    /**
     * Removes a key value pair from the KVS
     * 
     * @param key The key for the pair to be removed
     * @return The value that was mapped to the key, null if the key did not exists
     */
    public JSONObject remove(Object key) {
        return kvs.remove(key);
    }

    /**
     * Update a key/value pair in the JSONObject. If the value is null, then the key
     * will be removed from the JSONObject if it is present.
     * 
     * @param key      The KVS key
     * @param field    The JSON key
     * @param newValue The updated value
     * @return the updated JSONObject, null if not present
     */
    public JSONObject updateJSONField(String key, String field, Object newValue) {
        JSONObject jsono = kvs.get(key);

        return jsono.put(field, newValue);
    }

    /**
     * Clear the KVS in memory
     */
    public void clear() {
        kvs.clear();
    }

    /**
     * Check if the KVS contains a key
     * 
     * @param str The key to be checked for
     * @return True if key is present, false if not
     */
    public boolean containsKeyString(String str) {
        return kvs.containsKey(str);
    }

    /**
     * Used to create a Set of the KVS.
     * 
     * @return A Set containing the same elements as the KVS
     */
    public Set<Map.Entry<String, JSONObject>> entrySet() {
        return kvs.entrySet();
    }

    /**
     * Searches through the KVS using a regex
     * 
     * @return A KVS whose keys matched the regex.
     */
    public KVS search(String regex) {
        KVS results = new KVS();

        Pattern p = Pattern.compile(regex);

        Set<String> keys = kvs.keySet();

        for (String s : keys) {
            Matcher m = p.matcher(s);
            if (m.find()) {
                results.put(s, kvs.get(s));
            }
        }

        return results;
    }

    /**
     * @return A SortedMap of the KVS.
     */
    public SortedMap<String, JSONObject> getSortedMap() {
        TreeMap<String, JSONObject> sorted = new TreeMap<>();
        sorted.putAll(kvs);
        return sorted;
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        for (Map.Entry<String, JSONObject> entry : this.entrySet()) {
            text.append(entry.getKey() + "\n" + entry.getValue().toString(4) + "\n");
            text.append("\n");
        }
        return text.toString();
    }

    /**
     * Used to create a text file of the KVS in the current working directory
     * 
     * @param name The name that will be given to the new file
     */
    public void write(String name) {
        this.write(null, name);
    }

    /**
     * Writes the KVS to a text file.
     * 
     * @param name The name that will be given to the new file
     * @param path The path the new file will be written to
     */
    public void write(String name, String path) {
        File file = new File(name, path);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {

            for (Map.Entry<String, JSONObject> entry : kvs.entrySet()) {
                bw.write(entry.getKey() + "\n" + entry.getValue().toString(4) + "\n");
                bw.newLine();
            }

            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a KVS from a text file.
     * 
     * @param path The path for the text file.
     * @return The KVS described in the text file.
     */
    public void read(String path) {
        String tempKey;
        JSONObject tempJSON;
        File file = new File(path);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = null;

            while ((line = br.readLine()) != null) {
                tempKey = line;
                StringBuilder jsonText = new StringBuilder();
                while (!(line = br.readLine()).trim().isEmpty()) {
                    jsonText.append(line);
                }
                String text = jsonText.toString();
                tempJSON = new JSONObject(text);
                this.put(tempKey, tempJSON);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}