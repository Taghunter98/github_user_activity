# GitHub Activity Tracker

**GitHub Activity Tracker** is a Java-based command-line application that fetches, parses, and processes public GitHub activity for a specified user. The tool categorizes activities into types such as `Push Events`, `Issues`, and `Pull Requests`, making it an invaluable utility for monitoring user activity on GitHub.

## Features

- Fetches public activity from the GitHub API for a given username.
- Processes and categorizes activities into types:
    - **Push Events** – Logs pushed commits.
    - **Issues Events** – Tracks actions performed on issues in repositories.
    - **Pull Request Events** – Tracks actions related to pull requests.
- Handles unknown event types gracefully with logging.
- Displays activity details in the console for easy analysis.

---

## Getting Started

These instructions will guide you on how to clone, build, and run this project on your local machine.

### Prerequisites

- **Java 17** or higher is required.
- Apache Maven for build automation.
- Internet connection to fetch data from the GitHub API.

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/Taghunter98/github_user_activity.git
   ```

2. Navigate to the project directory:
   ```bash
   cd github_user_activity
   ```

3. Build the project using Maven:
   ```bash
   mvn clean install
   ```

---

## Usage

To use the application, ensure you pass a GitHub username as a command-line argument.

### Example Command:

```bash
mvn exec:java -Dexec.args='<github-username>'
```

Replace `<github-username>` with the desired GitHub user whose activity you want to fetch.

### Output Example:

The application will display the processed GitHub activity in the console:

```
GITHUB ACTIVITY FOR TAGHUNTER98:

# Pushed 1 commits to Taghunter98/github_user_activity
#
# Opened a new issue in Taghunter98/Task_cli with action: opened
```