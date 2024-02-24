import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class WeatherApp {

    // get weather data for given location
    public static JSONObject getWeatherData(String nameOfPlace) {
        // get coordinates using geolocation API
        JSONArray locationData = getLocationData(nameOfPlace);

        // extract lat/longitude
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        // API request URL with coordinates
        String urlLink = "https://api.open-meteo.com/v1/forecast?" +
        "latitude=" + latitude + "&longitude=" + longitude + 
        "&hourly=temperature_2m,relative_humidity_2m,weather_code,windspeed_10m&timezone=Europe%2FLondon";

        try {
            // call API and get response
            HttpURLConnection connection = fetchApiResponse(urlLink);
            
            if(connection.getResponseCode() != 200) {
                System.out.println("Error: could not connect to API");
                return null;
            }

            // store data result
            StringBuilder resultJson = new StringBuilder();
            Scanner keyboard = new Scanner(connection.getInputStream());
            
            while (keyboard.hasNext()) {
                resultJson.append(keyboard.nextLine());
            }

            keyboard.close();

            connection.disconnect();

            JSONParser parser = new JSONParser();
            JSONObject resultJsonObject = (JSONObject) parser.parse(String.valueOf(resultJson));


            // retrieve hourly data
            JSONObject hourly = (JSONObject) resultJsonObject.get("hourly");

            // get index of current hour
            JSONArray time = (JSONArray) hourly.get("time");
            int index = findCurrentTimeIndex(time);

            // get temperature
            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            // weather codes
            JSONArray weatherCode = (JSONArray) hourly.get("weather_code");
            String weatherCondition = convertWeatherCode((long) weatherCode.get(index));

            // get humidity
            JSONArray relativeHumidity = (JSONArray) hourly.get("relative_humidity_2m");
            long humidity = (long) relativeHumidity.get(index);

            // get windspeed
            JSONArray windspeedData = (JSONArray) hourly.get("windspeed_10m");
            double windspeed = (double) windspeedData.get(index);

            // build frontend json data object
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);
            
            return weatherData;

        } 
        catch (Exception exception) {
            exception.printStackTrace();
        }

        return null;
    }

    // retrieves coordinates for location
    public static JSONArray getLocationData(String nameOfPlace) {
        // formatting to API requirement
        nameOfPlace = nameOfPlace.replaceAll(" ", "+");

        // build API
        String urlLink = "https://geocoding-api.open-meteo.com/v1/search?name=" +
        nameOfPlace + "&count=10&language=en&format=json";

        // calling API
        try {
            HttpURLConnection connection = fetchApiResponse(urlLink);
            
            // check if response is successful
            if (connection.getResponseCode() != 200) {
                System.out.println("Error: could not connect to API");
                return null;
            }
            else {
                // storing API results
                StringBuilder resultJson = new StringBuilder();
                Scanner keyboard = new Scanner(connection.getInputStream());
                
                // reading and storing results
                while (keyboard.hasNext()) {
                    resultJson.append(keyboard.nextLine());
                }

                // closing connections
                keyboard.close();
                connection.disconnect();

                // parse String into obj
                JSONParser parser = new JSONParser();
                JSONObject resultJsonObject = (JSONObject) parser.parse(String.valueOf(resultJson));

                // get list of API generated location data
                JSONArray locationData = (JSONArray) resultJsonObject.get("results");
                return locationData;
            }
        } 
        catch(Exception exception) {
            exception.printStackTrace();
        }
        
        return null;
    }

    // aims to create connection 
    private static HttpURLConnection fetchApiResponse(String urlLink) {
        try {
            // create connection
            URL url = new URL(urlLink);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // set request method to get
            connection.setRequestMethod("GET");
            connection.connect();

            return connection;
        } 
        catch(IOException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    // loops through list to get matching time
    private static int findCurrentTimeIndex(JSONArray timeList) {
        String currentTime = getCurrentTime();

        for (int i = 0; i < timeList.size(); i++) {
            String time = (String) timeList.get(i);

            if (time.equalsIgnoreCase(currentTime)) {
                return i;
            }
        }

        return 0;
    }

    // gets current data and time
    private static String getCurrentTime() {
        LocalDateTime currentDateTime = LocalDateTime.now();

        // formatting data to API requirement
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");
        String formattedResult = currentDateTime.format(format);

        return formattedResult;
    }

    // convert code to readable format
    private static String convertWeatherCode(long weatherCode) {
        String weatherCondition = "";
        if (weatherCode == 0L) {
            weatherCondition = "Clear";
        }
        else if (weatherCode <= 3L && weatherCode > 0L) {
            weatherCondition = "Cloudy";
        }
        else if ((weatherCode >= 51L && weatherCode <= 67L) || (weatherCode >= 80L && weatherCode <= 99L)){
            weatherCondition = "Rain";
        }
        else if (weatherCode >= 71L && weatherCode <= 77L) {
            weatherCondition = "Snow";
        }

        return weatherCondition;
    }
}
