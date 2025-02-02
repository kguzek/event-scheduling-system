package pl.papuda.ess.client.tools;

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
import pl.papuda.ess.client.services.StompClient;
import pl.papuda.ess.client.model.body.ErrorResponse;
import pl.papuda.ess.client.model.User;
import uk.guzek.sac.auth.JwtAuth;

public class Web {

    private static final boolean PRODUCTION_ENVIRONMENT = true;
    private static final String LOCAL_URL = "http://localhost:8080";
    private static final String PRODUCTION_URL = "https://ps8o4ow0cssswww408cs0wc4.konrad.s.solvro.pl";
    private static final String API_BASE = PRODUCTION_ENVIRONMENT ? PRODUCTION_URL : LOCAL_URL;
    private static final String PREFERRED_API_BASE = AppPreferences.read("apiUrl", API_BASE);
    private static final String API_URL = PREFERRED_API_BASE + "/api/v1";
    private static final String API_URL_WS = API_URL.replaceFirst("^http", "ws");

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static StompClient stompClient = null;
    private static final Map<String, Consumer<Object>> subscriptions = new HashMap<>();
    private static String accessToken = null;

    public static User user = null;

    public static String getApiUrl() {
        return PREFERRED_API_BASE;
    }
    
    public static boolean isCustomApiUrlSet() {
        return !PREFERRED_API_BASE.equals(API_BASE);
    }
    
    public static void unsetAccessToken(boolean clearStoredData) {
        unsetAccessToken();
        if (clearStoredData) {
            AppPreferences.unset("accessToken");
            AppPreferences.unset("tokenGenerationDate");
        }
    }

    public static void unsetAccessToken() {
        accessToken = null;
        user = null;
        if (stompClient != null && stompClient.isConnected()) {
            stompClient.close();
        }
    }

    public static void initialiseStompClient() {
        URI stompServerUri = URI.create(API_URL_WS + "/private/stomp");
        stompClient = new StompClient(stompServerUri, new JwtAuth(accessToken), API_URL);
        stompClient.connect();
    }

    public static void setAccessToken(String token, boolean remember) {
        accessToken = token;
        if (token != null && stompClient == null) {
            initialiseStompClient();
        }
        if (remember) {
            System.out.println("Saving access token to preferences");
            AppPreferences.set("accessToken", accessToken);
            String generationDate = new Date().getTime() + "";
            AppPreferences.set("tokenGenerationDate", generationDate);
        }
    }

    public static <T> T readResponseBody(HttpResponse<String> response, TypeReference<T> cls) {
        ObjectMapper objectMapper = new ObjectMapper();
        String body = response.body();
        T obj;
        try {
            obj = objectMapper.readValue(body, cls);
        } catch (JsonProcessingException ex) {
            System.err.println("Could not parse request body: " + body + " exception: " + ex.getMessage());
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
            System.err.println("JSON parse error: " + ex.getMessage());
            return "Unknown error (" + code + " response)";
        }
    }
    
    private static HttpRequest.Builder createRequest(String endpoint) {
        return createRequest(endpoint, null, null);
    }

    private static HttpRequest.Builder createRequest(String endpoint, String method, String json) {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(API_URL + endpoint));
        if (method != null && json != null){ 
            builder = builder.method(method, HttpRequest.BodyPublishers.ofString(json))
                .setHeader("Content-Type", "application/json");
        }
        if (endpoint.startsWith("/auth/")) {
            return builder;
        }
        return builder.setHeader("Authorization", "Bearer " + accessToken);
    }

    private static HttpResponse<String> sendRequest(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(String.format("Sending %s request to %s", request.method(), request.uri().toString()));
        int status = response.statusCode();
        if (status == 401) {
            System.err.println("401 response fetching " + request.uri().toString());
            unsetAccessToken();
        }
        return response;
    }

    public static HttpResponse<String> sendGetRequest(String endpoint) throws IOException, InterruptedException {
        HttpRequest request = createRequest(endpoint).GET().build();
        return sendRequest(request);
    }

    public static HttpResponse<String> sendPostRequest(String endpoint) throws IOException, InterruptedException {
        return sendPostRequest(endpoint, "{}");
    }

    public static HttpResponse<String> sendPostRequest(String endpoint, String json)
            throws IOException, InterruptedException {
        HttpRequest request = createRequest(endpoint, "POST", json).build();
        return sendRequest(request);
    }

    public static HttpResponse<String> sendPutRequest(String endpoint, String json)
            throws IOException, InterruptedException {
        HttpRequest request = createRequest(endpoint, "PUT", json).build();
        return sendRequest(request);
    }

    public static HttpResponse<String> sendDeleteRequest(String endpoint) throws IOException, InterruptedException {
        HttpRequest request = createRequest(endpoint).DELETE().build();
        return sendRequest(request);
    }
    
    public static HttpResponse<String> sendPatchRequest(String endpoint, String json) throws IOException, InterruptedException {
        HttpRequest request = createRequest(endpoint, "PATCH", json).build();
        return sendRequest(request);
    }

    private static void runWhenClientReady(Runnable callback) {
        new Thread(() -> {
            try {
                while (stompClient == null) {
                    Thread.sleep(100);
                }
                if (stompClient.wasClosed()) {
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
        if (stompClient != null && stompClient.isConnected()) {
            stompClient.subscribe(destination, handler);
        } else {
            System.out.println("Scheduling subscription to " + destination);
            subscriptions.put(destination, handler);
        }
    }
    
    public static void unsubscribeStompResource(String destination) {
        stompClient.unsubscribe(destination);
    }

    public static Set<Map.Entry<String, Consumer<Object>>> getSubscriptions() {
        return subscriptions.entrySet();
    }
}
