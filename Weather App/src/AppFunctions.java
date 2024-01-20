import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class AppFunctions {

    public static JSONObject getWeatherData(String locName){
        JSONArray locData = getLocationData(locName);

        // lat and long data retrieval
        JSONObject location = (JSONObject) locData.get(0);
        double latitude = (double) location.get("latitude");
        double longitute = (double) location.get("longitude");

        String URL = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude + "&longitude=" + longitute + "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=America%2FNew_York";

        try{

            HttpURLConnection conn = fetchApiResponse(URL);

            if(conn.getResponseCode() != 200){
                System.out.println("Error: API couldn't connect");
                return null;
            }
            // Stores result of api
            StringBuilder resultJSON = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());

            // Reads + Stores the resulting api data into string builder
            while(scanner.hasNext()){
                resultJSON.append(scanner.nextLine());
            }

            scanner.close();
            conn.disconnect();

            // Parses json string to json obj
            JSONParser parser = new JSONParser();
            JSONObject resultJSONObj = (JSONObject) parser.parse(String.valueOf(resultJSON));

            JSONObject hourly = (JSONObject) resultJSONObj.get("hourly");

            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            // Get Temp
            JSONArray tempData = (JSONArray) hourly.get("temperature_2m");
            double temp = (double) tempData.get(index);

            // Get Weather
            JSONArray weatherCode = (JSONArray) hourly.get("weather_code");
            String weatherCondition = convertWeatherCode((long) weatherCode.get(index));

            // Get Humidity
            JSONArray humData = (JSONArray) hourly.get("relative_humidity_2m");
            long humidity = (long) humData.get(index);

            // Get Windspeed
            JSONArray windData = (JSONArray) hourly.get("wind_speed_10m");
            double windspeed = (double) windData.get(index);

            JSONObject weatherData = new JSONObject();
            weatherData.put("temp", temp);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);

            return weatherData;

        }
        catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }


    //Receives Geographical Data
    public static JSONArray getLocationData(String locName){
        //Replaces White Space with +
        locName = locName.replaceAll(" ", "+");

        String URL = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locName + "&count=10&language=en&format=json";

        try{

            HttpURLConnection conn = fetchApiResponse(URL);


            if(conn.getResponseCode() != 200){
                System.out.println("Error: API couldn't connect");
                return null;
            }else{
                // Stores result of api
                StringBuilder JSONresult = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());

                // Reads + Stores the resulting api data into string builder
                while(scanner.hasNext()){
                    JSONresult.append(scanner.nextLine());
                }

                scanner.close();
                conn.disconnect();

                // Parses json string to json obj
                JSONParser parser = new JSONParser();
                JSONObject jsonResObj = (JSONObject) parser.parse(String.valueOf(JSONresult));

                // Creates list of locations from the api
                JSONArray locationData = (JSONArray) jsonResObj.get("results");
                return locationData;



            }
        }
        catch(Exception e){
                e.printStackTrace();
        }

        return null;

    }

    private static HttpURLConnection fetchApiResponse(String urlSTR){
        // Tries to make connection with api
        try{

            URL url = new URL(urlSTR);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            conn.connect();
            return conn;

        }catch (IOException e){
            e.printStackTrace();
        }

        // If unsuccessful returns null
        return null;

    }

    private static int findIndexOfCurrentTime(JSONArray timeList){
        String currentTime = getCurrentTime();

        for(int i =0; i<timeList.size(); i++){
            String time = (String) timeList.get(i);
            if(time.equalsIgnoreCase(currentTime)){
                return i;
            }
        }

        return 0;
    }

    private static String getCurrentTime(){
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Formatting Date
        DateTimeFormatter formatNew = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        String formattedDateTime = currentDateTime.format(formatNew);

        return formattedDateTime;


    }

    private static String convertWeatherCode(long code) {
        String weatherCondition = "";
        if(code == 0L){
            // clear
            weatherCondition = "Clear";
        }else if(code > 0L && code <= 3L){
            // cloudy
            weatherCondition = "Cloudy";
        }else if((code >= 51L && code <= 67L)
                || (code >= 80L && code <= 99L)){
            // rain
            weatherCondition = "Rain";
        }else if(code >= 71L && code <= 77L){
            // snow
            weatherCondition = "Snow";
        }

        return weatherCondition;
    }

}


