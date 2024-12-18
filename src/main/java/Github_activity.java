import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.function.Consumer;

/**
 * The Github_activity class fetches, parses, and processes public GitHub activity
 * for a specified user. It categorizes activities into types such as push events,
 * pull requests, and issues.
 */
public class Github_activity {
    private final String user;
    private List<String> pushes = new ArrayList<>();
    private List<String> requests = new ArrayList<>();
    private List<String> issues = new ArrayList<>();

    public Github_activity(String user) {
        this.user = user;
    }

    /**
     * The entry point for the application, which fetches and processes GitHub activity
     * for a given user. The application retrieves user activity data from the GitHub API,
     * parses the response, and processes the activities to output various event details.
     *
     * @param args The command-line arguments. The first element in the array is expected
     *             to be the GitHub username. For proper usage, a username must be provided
     *             as an argument, otherwise the program will terminate with an error message.
     * @throws Exception If there are any errors during the fetching, parsing, or processing
     *                   of the GitHub activity data.
     */
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("Usage: mvn exec:java -Dexec.args='<username>'");
            return;
        }

        Github_activity githubActivity = new Github_activity(args[0]);
        String json = githubActivity.getJson();
        JsonArray activities = githubActivity.parseJson(json);
        githubActivity.processActivities(activities);
    }

    // Getter method for user
    public String getUser() {
        return user;
    }

    /**
     * Fetches the GitHub user activity in JSON format by making a request to the
     * GitHub API. The method builds the request URL using the username, sends the
     * HTTP request, and returns the response as a raw JSON string.
     *
     * @return A JSON-formatted string containing the user's activity data from the GitHub API.
     * @throws Exception If an error occurs during the HTTP request or response handling process.
     */
    public String getJson() throws Exception {
        System.out.println("\n\nFetching activity for " + user + "...\n");
        String url = "https://api.github.com/users/" + user + "/events";
        HttpClient client = HttpClient.newHttpClient();
        System.out.println("Requesting..." + url);
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Received " + response.statusCode() + " status code");
        return response.body();
    }

    /**
     * Parses a JSON-formatted string and returns it as a JsonArray.
     *
     * @param json The JSON string to be parsed.
     * @return A JsonArray representation of the provided JSON string.
     * @throws Exception If an error occurs during the parsing process.
     */
    public JsonArray parseJson(String json) throws Exception {
        try (JsonReader reader = Json.createReader(new StringReader(json))) {
            System.out.println("\nParsing JSON...");
            return reader.readArray();
        } catch (Exception e) {
            throw new Exception("Error parsing JSON: " + e.getMessage());
        }
    }

    /**
     * Processes a collection of GitHub activities and dispatches them to the appropriate event handlers
     * based on their type. Supported event types include PushEvent, IssuesEvent, and PullRequestEvent.
     * Unrecognized event types are handled by a default handler, which logs the unknown event type and repository.
     *
     * @param activities A JsonArray containing GitHub activity data, where each element represents a specific
     *                   activity in JSON format. Each activity must include the "type" and "repo" fields.
     */
    public void processActivities(JsonArray activities) {
        System.out.println("\nProcessing " + activities.size() + " activities...\n");
        System.out.println("GitHub activity for ".toUpperCase() + user.toUpperCase() + ":\n");
        // Map event types to their respective handlers
        Map<String, Consumer<JsonObject>> handlers = Map.of(
                "PushEvent", this::handlePushEvent,
                "IssuesEvent", this::handleIssuesEvent,
                "PullRequestEvent", this::handlePullRequestEvent
        );

        for (var activity : activities) {
            JsonObject event = activity.asJsonObject();
            String type = event.getString("type");
            String repo = event.getJsonObject("repo").getString("name");

            handlers.getOrDefault(type, e -> handleUnknownEvent(e, type, repo)).accept(event);
            System.out.println("#");
        }
    }

    /**
     * Handles a PushEvent by extracting the relevant repository name and the number of commits pushed.
     * Logs the details to the console and stores the commit information in the `pushes` collection.
     *
     * @param event A JsonObject representing the PushEvent. It must include a "repo" field specifying
     *              the repository details and a "payload" field containing the commit information.
     */
    private void handlePushEvent(JsonObject event) {
        String repo = event.getJsonObject("repo").getString("name");
        var commits = event.getJsonObject("payload").getJsonArray("commits");
        System.out.println("# Pushed " + commits.size() + " commits to " + repo);
        pushes.add(commits.toString());
    }

    /**
     * Handles an IssuesEvent by extracting the repository name and action associated
     * with the issue, logging the details to the console, and storing the issue event data.
     *
     * @param event A JsonObject representing the IssuesEvent. It must include a "repo" field
     *              specifying the repository details and a "payload" field containing the action performed.
     */
    private void handleIssuesEvent(JsonObject event) {
        String repo = event.getJsonObject("repo").getString("name");
        String action = event.getJsonObject("payload").getString("action");
        System.out.println("# Opened a new issue in " + repo + " with action: " + action);
        issues.add(event.toString());
    }

    /**
     * Handles a PullRequestEvent by extracting the repository name and the action performed
     * on the pull request, logging the details to the console, and storing the pull request
     * event data.
     *
     * @param event A JsonObject representing the PullRequestEvent. It must include a "repo"
     *              field specifying the repository details and a "payload" field containing
     *              the action performed.
     */
    private void handlePullRequestEvent(JsonObject event) {
        String repo = event.getJsonObject("repo").getString("name");
        String action = event.getJsonObject("payload").getString("action");
        System.out.println("# Opened a pull request in " + repo + " with action: " + action);
        requests.add(event.toString());
    }

    /**
     * Logs an unrecognized event type and its associated repository.
     *
     * @param event A JsonObject representing the unknown event. It must include relevant information
     *              about the event and its associated repository.
     * @param type  A String indicating the type of the event that was not recognized.
     * @param repo  A String specifying the repository associated with the unknown event.
     */
    private void handleUnknownEvent(JsonObject event, String type, String repo) {
        System.out.println("# Unknown event type: " + type + " in " + repo);
    }
}
