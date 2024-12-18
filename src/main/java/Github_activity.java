import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Github_activity {
    private final String user;

    public Github_activity(String user) {
        this.user = user;
    }
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("Usage: mvn excec:java -Dexec.args='<username>'");
            return;
        }

        Github_activity gitHubActivity = new Github_activity(args[0]);
        String json = gitHubActivity.getJson();
        gitHubActivity.ParseJson(json);

    }

    public String getUser() {
        return user;
    }

    public void ParseJson(String json) {
        System.out.println("Parsing JSON...");
        System.out.println("--------------------------------------");
        System.out.println("GitHub Events for " + getUser()+ "\n".toUpperCase());
        try (JsonReader reader = Json.createReader(new StringReader(json))) {
            JsonArray activities = reader.readArray();

            // Iterate through activities and process each event
            for (var activity : activities) {
                JsonObject event = activity.asJsonObject();
                String type = event.getString("type");
                String repo = event.getJsonObject("repo").getString("name");

                switch (type) {
                    case "PushEvent" -> {
                        var commits = event.getJsonObject("payload").getJsonArray("commits");
                        System.out.println("# Pushed " + commits.size() + " commits to " + repo);
                    }
                    case "IssuesEvent" -> {
                        event.getJsonObject("payload").getString("action");
                        System.out.println("# Opened a new issue in " + repo);
                    }
                    case "PullRequestEvent" -> {
                        String action = event.getJsonObject("payload").getString("action");
                        System.out.println("# Opened a pull request in " + repo);
                    }
                    default -> System.out.println("# " + type + " in " + repo);
                }
                System.out.println();
            }
        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
        }
    }

    // Returns JSON from GitHub API
    public String getJson() throws Exception {
        System.out.println("Getting user...");
        String username = getUser();
        String url = "https://api.github.com/users/" + username + "/events";

        try {
            System.out.println("\n\nFetching GitHub Events for " + username);
            System.out.println("Fetching JSON from " + url);
            HttpClient client = HttpClient.newHttpClient();
            System.out.println("Sending request...");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();
            System.out.println("Waiting for response...");
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response received.");
            return response.body();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }
}
