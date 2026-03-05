package app.services;

import app.exceptions.ApiException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WeatherService {

    public HttpResponse<String> getWeather(String city) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://api.weatherapi.com/v1/current.json?key=3899689ceee948f08e9163840262002&q="+city))
                    .GET()
                    .build();

            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new ApiException(500, "Error fetching weather");
        }
    }

    public void printWeather(String body) {
        try {
            String name = body.split("\"name\":\"")[1].split("\"")[0];
            String region = body.split("\"region\":\"")[1].split("\"")[0];
            String temp = body.split("\"temp_c\":")[1].split(",")[0];
            String feelsLike = body.split("\"feelslike_c\":")[1].split(",")[0];
            String condition = body.split("\"text\":\"")[1].split("\"")[0];
            String windDir = body.split("\"wind_dir\":\"")[1].split("\"")[0];
            String windKph = body.split("\"wind_kph\":")[1].split(",")[0];

            System.out.println("====================================");
            System.out.println("Viser vejret for " + name + ", " + region);
            System.out.println("Temperatur lige nu: " + temp + "°C");
            System.out.println("Føles som: " + feelsLike + "°C");
            System.out.println("Vejr type: " + condition);
            System.out.println("Vind: " + windDir + " " + windKph + " km/t");
            System.out.println("====================================");
        } catch (Exception e) {
            System.out.println("Fejl i WeatherSerivce printWeather()");
        }
    }
}