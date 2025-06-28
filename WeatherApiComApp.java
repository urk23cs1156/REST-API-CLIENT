import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class WeatherApiComApp {

    private static final String API_KEY = "c4149fbe2a774152987142857252806";

    public static void main(String[] args) throws IOException, InterruptedException {
        String[] cities = {"London", "New York", "Tokyo", "Paris"};

        System.out.println("=== WeatherAPI.com Weather Report ===\n");

        for (String city : cities) {
            System.out.println("Fetching weather for: " + city);
            String response = fetchWeather(city);
            if (response != null) {
                parseAndDisplay(response);
            } else {
                System.out.println("Failed to get data for " + city);
            }
            System.out.println("-------------------------------\n");
        }
    }

    private static String fetchWeather(String city) throws IOException, InterruptedException {
        String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
        String url = String.format(
            "http://api.weatherapi.com/v1/current.json?key=%s&q=%s&aqi=no",
            API_KEY, encodedCity);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            System.out.println("Error: " + response.statusCode() + " for city " + city);
            return null;
        }
    }

    private static void parseAndDisplay(String json) {
        try {
            String city = extract(json, "\"name\":\"", "\"");
            String tempC = extract(json, "\"temp_c\":", ",");
            String condition = extract(json, "\"text\":\"", "\"");
            String humidity = extract(json, "\"humidity\":", ",");

            System.out.println("City       : " + city);
            System.out.println("Temperature: " + tempC + " °C");
            System.out.println("Condition  : " + condition);
            System.out.println("Humidity   : " + humidity + "%");
        } catch (Exception e) {
            System.out.println("Failed to parse JSON");
        }
    }

    private static String extract(String json, String startDelim, String endDelim) {
        int start = json.indexOf(startDelim);
        if (start == -1) return "";
        start += startDelim.length();
        int end = json.indexOf(endDelim, start);
        if (end == -1) return "";
        return json.substring(start, end);
    }
}
