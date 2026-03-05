package app.services;

import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

class WeatherServiceTest {

    ThreadService threadService = new ThreadService(4);
    WeatherService weatherService = new WeatherService();


    @Test
    void testWeatherService() throws Exception {

        Future<HttpResponse<String>> future = threadService.callAsync(() ->
            weatherService.getWeather("Odense")
        );

        HttpResponse<String> response = future.get();
        weatherService.printWeather(response.body());
        assertNotNull(response);
        assertEquals(200, response.statusCode());
    }



}