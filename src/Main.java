import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try{
            Scanner sc = new Scanner(System.in);
            String city;
            do{
                System.out.println("====================================");
                System.out.print("Enter your city ('exit' to quit) > ");
                city = sc.nextLine();

                if (city.equalsIgnoreCase("exit")) break;

                JSONObject cityLocationData;

                do{
                    cityLocationData = (JSONObject) getLocationData(city);
                    if (cityLocationData == null) {
                        System.out.print("Invalid name! Enter your city again ('exit' to quit) > ");
                        city = sc.nextLine();
                        if (city.equalsIgnoreCase("exit"))
                            return;
                    }
                }while (cityLocationData == null);

                double latitude = (double) cityLocationData.get("latitude");
                double longitude = (double) cityLocationData.get("longitude");

                displayWeatherData(latitude, longitude, city);
            }while(!city.equalsIgnoreCase("exit"));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private static JSONObject getLocationData(String city){
        city = city.replaceAll(" ", "+");

        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name="
            + city + "&count=1&language=en&format=json";

        try{
            //1. Fetch the API response based on API Link
            HttpURLConnection apiConnection = fetchApiResponse(urlString);

            //Check for the response status. 200 means that the connection was successful
            if (apiConnection.getResponseCode() != 200){
                System.out.println("Error: Could not connect to the API");
                return null;
            }

            //2. Read the response and convert store String type
            String jsonResponse = readApiResponse(apiConnection);

            //3. Parse the string into a JSON Object
            JSONParser parser = new JSONParser();
            JSONObject resultsJsonObj = (JSONObject) parser.parse(jsonResponse);

            //4. Retrieve Location Data
            JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
            return (JSONObject) locationData.getFirst();

        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString){
        try{
            //first, attempt to create connection
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //set request method to get
            conn.setRequestMethod("GET");

            return conn;
        }
        catch(IOException e){
            e.printStackTrace();
        }

        return null; //could not make the connection
    }

    private static String readApiResponse(HttpURLConnection apiConnection){
        try{
            //StringBuilder to store the resulting JSON data
            StringBuilder resultJson = new StringBuilder();

            //scanner to read from the InputStream of the HttpURLConnection
            Scanner sc = new Scanner(apiConnection.getInputStream());

            //iterate through each line of the Json data and append that to Json StringBuilder
            while(sc.hasNext()){
                resultJson.append(sc.nextLine());
            }
            sc.close();

            //return Json data as a string
            return resultJson.toString();
        }
        catch(IOException e){
            e.printStackTrace();
        }

        return null;
    }

    private static void displayWeatherData(double latitude, double longitude, String city){
        try{
            //1. Fetch the API response based on API link
            String url = "https://api.open-meteo.com/v1/forecast?latitude="
                        + latitude + "&longitude="
                        + longitude + "&current=temperature_2m,relative_humidity_2m,wind_speed_10m";

            HttpURLConnection apiConnection = fetchApiResponse(url);

            //Check for the response status. 200 means that the connection was successful
            if (apiConnection.getResponseCode() != 200){
                System.out.println("Error: Could not connect to the API");
                return;
            }

            //2. Read the response and convert store String type
            String jsonResponse = readApiResponse(apiConnection);

            //3. Parse the string into a JSON Object
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonResponse);
            JSONObject currentWeatherJson = (JSONObject) jsonObject.get("current");

            //4. Store the data from json into their corresponding data type
            String time = (String) currentWeatherJson.get("time");
            double temperature = (double) currentWeatherJson.get("temperature_2m");
            long relativeHumidity = (long) currentWeatherJson.get("relative_humidity_2m");
            double windSpeed = (double) currentWeatherJson.get("wind_speed_10m");

            //5. Display the data
            System.out.println("City: " + city);
            System.out.println("Current time: " + time);
            System.out.println("Current temperature: " + temperature + "ºC");
            System.out.println("Relative humidity: " + relativeHumidity + "%");
            System.out.println("Wind Speed: " + windSpeed + " km/h");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

}
