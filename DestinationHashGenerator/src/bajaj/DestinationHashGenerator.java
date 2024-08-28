package bajaj;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Random;

public class DestinationHashGenerator {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java -jar DestinationHashGenerator.jar <PRN> <JSON File Path>");
            return;
        }

        String prn = args[0].toLowerCase(); 
        String inputJsonPath = args[1];

        try {

            String content = new String(Files.readAllBytes(new File(inputJsonPath).toPath()));
            JSONObject jsonObject = new JSONObject(content);

            // Iterate through json file and find the first occurance of destination
            String destinationValue = getDestinationValue(jsonObject);
            if (destinationValue == null) {
                System.out.println("No 'destination' key found in the JSON file.");
                return;
            }

            // Generate random string
            String randomString = generateRandomString(8);

            // Concatenate PRN, destination value, and random string, and generate MD5 hash
            String concatenatedString = prn + destinationValue + randomString;
            String md5Hash = generateMD5Hash(concatenatedString);

            // Append the random string with ";" after the hash value as per question
            String output = md5Hash + ";" + randomString;

            // Print the value
            System.out.println(output);

        } catch (IOException e) {
            System.out.println("Failed to read the JSON file: " + e.getMessage());
        }
    }

    private static String getDestinationValue(JSONObject jsonObject) {
        Iterator<String> keys = jsonObject.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            Object value = jsonObject.get(key);

            if (key.equals("destination")) {
                return value.toString();
            }

            if (value instanceof JSONObject) {
                String result = getDestinationValue((JSONObject) value);
                if (result != null) 
                	return result;
            } else if (value instanceof JSONArray) {
                for (int i = 0; i < ((JSONArray) value).length(); i++) {
                    Object arrayElement = ((JSONArray) value).get(i);
                    if (arrayElement instanceof JSONObject) {
                        String result = getDestinationValue((JSONObject) arrayElement);
                        if (result != null) 
                        	return result;
                    }
                }
            }
        }

        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            result.append(characters.charAt(random.nextInt(characters.length())));
        }
        return result.toString();
    }

    private static String generateMD5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }
}
