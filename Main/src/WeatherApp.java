import java.util.Scanner;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;


/**
 * The WeatherApp program gets a city from the user and displays current weather information from that place.
 *
 * @author  Aidan Stacey
 * @version 1.0
 * @since   2023-01-25
 */
public class WeatherApp {

    /**
     * This is the main method which introduces the program to the user.
     * @param args Unused.
     * @return Nothing.
     */
    public static void main(String[] args) {
        System.out.println("\nThank you for using our weather app.");

        String city = getCity();
        city = format(city);
        getJson(city);

        while (repeat()) {
            city = getCity();
            getJson(city);
        }

        System.out.println("\nHave a nice day.");
    }

    /**
     * This method is used to format the input from the user into an acceptable form for the URL.
     * @param city The unformatted version of the users input.
     * @return String The formatted version of the users input.
     */
    public static String format(String city) {
        char[] cityArray = city.toCharArray();
        String formattedCity = "";
        boolean isNewWord = true;
        for (int i = 0; i < cityArray.length; i++) {
            char character = cityArray[i];
            if (character == ' ') {
                formattedCity += "%20";
                isNewWord = true;
            } else if (isNewWord) {
                formattedCity += Character.toString(Character.toUpperCase(character));
                isNewWord = false;
            } else {
                formattedCity += Character.toString(Character.toLowerCase(character));
            }
        }
        return formattedCity;
    }

    /**
     * This method gets the users input.
     * @return String The city chosen by the user.
     */
    public static String getCity() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Which city would you like to know about? ");
        String city = scanner.nextLine();

        return city;
    }

    /**
     * This method requests the Json data from the API.
     * @param city The formatted version of the users input.
     * @return Nothing.
     */
    public static void getJson(String city) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(
                        "http://api.weatherapi.com/v1/current.json?key=c1b9f634b682414b88813421232501&q=" + city + "&aqi=no"))
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body).thenApply(WeatherApp::parse).join();
    }

    /**
     * This method parses the Json data into attributes and prints the information to the terminal.
     * @param responseBody The Json data.
     * @return Nothing.
     */
    public static String parse(String responseBody) {
        JSONObject data = new JSONObject(responseBody);
        if (data.has("error")) {
            System.out.println("That city is not in the database.");
            return null;
        }
        JSONObject location = data.getJSONObject("location");
        JSONObject current = data.getJSONObject("current");

        double humidity = current.getDouble("humidity");
        String place = location.getString("name") + ", " + location.getString("region");
        double precipIn = current.getDouble("precip_in");
        double tempF = current.getDouble("temp_f");
        double visMiles = current.getDouble("vis_miles");
        String windDir = current.getString("wind_dir");
        double windMph = current.getDouble("wind_mph");

        System.out.println("\nShowing results for " + place + ":\nTemperature(F) - " + tempF + "\nHumidity(%) - " +
                humidity + "\nWind Speed(MPH) - " + windMph + "\nWind Direction - " + windDir +
                "\nPrecipitation(In) - " + precipIn + "\nVisibility(Miles) - " + visMiles);

        return null;
    }

    /**
     * This method asks the users if they would like to input another city.
     * @return Boolean If the program should repeat.
     */
    public static boolean repeat() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nWould you like to look at another city(Y or N)? ");
        char answer = Character.toUpperCase(scanner.nextLine().charAt(0));
        if (answer == 'Y') {
            return true;
        } else {
            return false;
        }
    }
}
