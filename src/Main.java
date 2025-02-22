import org.json.simple.JSONObject;

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
                System.out.println("==========================");
                System.out.println("Enter your city ('exit' to quit) > ");
                city = sc.nextLine();

                if (city.equalsIgnoreCase("exit")) break;

                JSONObject cityLocationData = (JSONObject) getLocationData(city);
                double latitude = (double) cityLocationData.get("latitude");
                double longitude = (double) cityLocationData.get("longitude");


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

            //2. Check for the response status. 200 means that the connection was successful
            if (apiConnection.getResponseCode() != 200){
                System.out.println("Error: Could not connect to the API");
                return null;
            }



        }
        catch(){}
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
}
