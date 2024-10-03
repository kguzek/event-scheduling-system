
package pl.papuda.ess.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import pl.papuda.ess.client.model.body.ErrorResponse;
import pl.papuda.ess.client.model.User;

public class Web {
    private static final boolean PRODUCTION_ENVIRONMENT = true;

    private static final String API_URL = PRODUCTION_ENVIRONMENT
            ? "https://event-scheduling-system.onrender.com"
            : "http://localhost:8080";

    private static final HttpClient httpClient = HttpClient.newHttpClient();

    private static String accessToken = null;

    public static User user = null;

    static final Preferences prefs = Preferences.userNodeForPackage(pl.papuda.ess.client.MainWindow.class);

    static void unsetAccessToken() {
        accessToken = null;
        user = null;
    }

    public static void logException(Exception ex) {
        Logger.getLogger(Web.class.getName()).log(Level.SEVERE, null, ex);
    }

    static void setAccessToken(String token, boolean remember) {
        accessToken = token;
        if (remember) {
            System.out.println("Saving access token to preferences");
            prefs.put("accessToken", accessToken);
            String generationDate = new Date().getTime() + "";
            prefs.put("tokenGenerationDate", generationDate);
        }
    }

    public static <T> T readResponseBody(HttpResponse<String> response, TypeReference<T> cls) {
        ObjectMapper objectMapper = new ObjectMapper();
        String body = response.body();
        T obj;
        try {
            obj = objectMapper.readValue(body, cls);
        } catch (JsonProcessingException ex) {
            logException(ex);
            System.out.println("Could not parse request body: " + body);
            return null;
        }
        return obj;
    }

    public static String getErrorMessage(HttpResponse<String> response) {
        ObjectMapper objectMapper = new ObjectMapper();
        String body = response.body();
        System.err.println("Received " + response.statusCode() + " response : " + body);
        try {
            ErrorResponse error = objectMapper.readValue(body, ErrorResponse.class);
            return error.getMessage();
        } catch (JsonProcessingException ex) {
            logException(ex);
            return "Non-OK response with unparsable body";
        }
    }

    private static HttpRequest.Builder createRequest(String endpoint) {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(API_URL + "/api/v1" + endpoint));
        if (endpoint.startsWith("/auth/")) {
            return builder;
        }
        return builder.setHeader("Authorization", "Bearer " + accessToken);
    }

    private static HttpResponse<String> sendRequest(HttpRequest request) throws IOException, InterruptedException {
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static HttpResponse<String> sendGetRequest(String endpoint) throws IOException, InterruptedException {
        HttpRequest request = createRequest(endpoint).GET().build();
        return sendRequest(request);
    }

    public static HttpResponse<String> sendPostRequest(String endpoint) throws IOException, InterruptedException {
        return sendPostRequest(endpoint, "{}");
    }

    public static HttpResponse<String> sendPostRequest(String endpoint, String json) throws IOException, InterruptedException {
        HttpRequest request = createRequest(endpoint)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .setHeader("Content-Type", "application/json")
                .build();
        return sendRequest(request);
    }

    public static HttpResponse<String> sendPutRequest(String endpoint, String json) throws IOException, InterruptedException {
        HttpRequest request = createRequest(endpoint)
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .setHeader("Content-Type", "application/json")
                .build();
        return sendRequest(request);
    }

    public static HttpResponse<String> sendDeleteRequest(String endpoint) throws IOException, InterruptedException {
        HttpRequest request = createRequest(endpoint).DELETE().build();
        return sendRequest(request);
    }
}
