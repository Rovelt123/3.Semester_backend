package app.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class HolidayAPIService {


    // ________________________________________________________

    public Map<LocalDate, String> getHolidays(int year) {
        Map<LocalDate, String> holidays = new HashMap<>();
        try {

            String url = "https://date.nager.at/api/v3/PublicHolidays/" + year + "/DK";

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(response.body());

            for (JsonNode holiday : json) {

                LocalDate date = LocalDate.parse(holiday.get("date").asText());
                String name = holiday.get("localName").asText();

                holidays.put(date, name);
            }
            return holidays;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ________________________________________________________

    public String getHoliday(LocalDate date, Map<LocalDate, String> holidays) {
        return holidays.get(date);
    }

    // ________________________________________________________

    public boolean isHoliday(LocalDate date, Map<LocalDate, String> holidays) {
        return holidays.containsKey(date);
    }
}