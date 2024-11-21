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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import pl.papuda.ess.client.model.body.ErrorResponse;
import pl.papuda.ess.client.model.User;
import uk.guzek.sac.AuthType;

public class Web {

    private static final boolean PRODUCTION_ENVIRONMENT = true;

    private static final String API_BASE = (PRODUCTION_ENVIRONMENT
            ? "s://event-scheduling-system.onrender.com"
            : "://localhost:8080") + "/api/v1";
    private static final String API_URL = "http" + API_BASE;

    private static final String API_URL_WS = "ws" + API_BASE;

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static StompClient stompClient = null;
    private static final Map<String, Consumer<Object>> subscriptions = new HashMap<>();

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

    public static void initialiseStompClient() {
        URI stompServerUri = URI.create(API_URL_WS + "/private/stomp");
        stompClient = new StompClient(stompServerUri, AuthType.jwt(accessToken), API_URL);
        stompClient.connect();
    }

    static void setAccessToken(String token, boolean remember) {
        accessToken = token;
        if (token != null && stompClient == null) {
            initialiseStompClient();
        }
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
        int code = response.statusCode();
        System.err.println("Received " + code + " response: " + body);
        try {
            ErrorResponse error = objectMapper.readValue(body, ErrorResponse.class);
            return error.getMessage();
        } catch (JsonProcessingException ex) {
            logException(ex);
            return "Unknown error (" + code + " response)";
        }
    }

    private static HttpRequest.Builder createRequest(String endpoint) {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(API_URL + endpoint));
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

    private static void runWhenClientReady(Runnable callback) {
        new Thread(() -> {
            try {
                while (stompClient == null) {
                    Thread.sleep(100);
                }
                if (stompClient.isDisconnected()) {
                    System.out.println("Reconnecting STOMP client");
                    initialiseStompClient();
                }
                while (!stompClient.isConnected()) {
                    Thread.sleep(100);
                }
            } catch (InterruptedException ex) {
                System.err.println("Client sleep interrupted, cancelling callback");
                return;
            }
            callback.run();
        }).start();
    }

    public static void sendStompText(String destination, String text) {
        runWhenClientReady(() -> {
            stompClient.sendText(text, "/app" + destination);
        });
    }

    public static void sendStompJson(String destination, Object object) {
        runWhenClientReady(() -> {
            try {
                stompClient.sendJson(object, "/app" + destination);
            } catch (JsonProcessingException ex) {
                System.err.println("Error sending message to: " + destination);
            }
        });
    }

    public static void subscribeStompResource(String destination, Consumer<Object> handler) {
        subscriptions.put(destination, handler);
        System.out.println("Scheduling subscription to " + destination);
    }
    
    public static Set<Map.Entry<String, Consumer<Object>>> getSubscriptions() {
        return subscriptions.entrySet();
    }
}
