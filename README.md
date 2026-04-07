# Quiz Engine

A Java 17 console and REST API application that allows learners to prepare for tests with persistent scoring and leaderboards.

## Tech Stack

- **Language:** Java 17 (Records, Sealed Classes)
- **Framework:** Spring Boot 3.2
- **Build Tool:** Maven
- **Storage:** JSON files (Jackson)
- **Testing:** JUnit 5, Mockito, MockMvc

## Project Structure
src/
└── main/java/com/school/
├── api/                        # REST layer
│   ├── QuizController.java
│   ├── LeaderboardController.java
│   └── dto/
│       ├── ApiResponse.java
│       ├── QuestionView.java
│       ├── StartQuizRequest.java
│       └── SubmitAnswersRequest.java
├── cli/                        # Console layer
│   ├── MenuDisplay.java
│   └── QuizRunner.java
├── config/
│   └── AppConfig.java
├── exception/
│   └── QuizException.java
├── model/                      # Domain records
│   ├── Answer.java
│   ├── Question.java
│   ├── QuizResult.java
│   └── LeaderboardEntry.java
├── repository/                 # Persistence layer
│   ├── QuestionRepository.java
│   └── LeaderboardRepository.java
├── service/                    # Business logic
│   ├── QuizService.java
│   └── LeaderboardService.java
└── App.java

## Prerequisites

- Java 17+
- Maven 3.8+

## Getting Started

### Clone the repository
```bash
git clone https://github.com/YOUR_USERNAME/YOUR_REPO.git
cd YOUR_REPO
```

### Run the REST API
```bash
mvn spring-boot:run
```

The API will start on `http://localhost:8080`.

### Run the CLI
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--cli
```

### Run Tests
```bash
mvn test
```

## REST API Endpoints

### Quiz

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/quiz/start` | Start a new quiz |
| GET | `/api/quiz/start?category=java&limit=5` | Start a quiz by category and limit |
| GET | `/api/quiz/categories` | Get all available categories |
| POST | `/api/quiz/submit` | Submit answers and get score |

### Leaderboard

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/leaderboard` | Get top 10 scores |
| GET | `/api/leaderboard?top=5` | Get top N scores |
| GET | `/api/leaderboard/category?name=java` | Get top scores by category |

## Example API Usage

### Start a quiz
```bash
curl http://localhost:8080/api/quiz/start?category=java&limit=2
```

Response:
```json
{
  "success": true,
  "message": "Quiz ready",
  "data": [
    {
      "id": "q1",
      "text": "What is the default value of an int in Java?",
      "category": "java",
      "answerType": "multiple_choice",
      "options": ["0", "1", "null", "-1"]
    },
    {
      "id": "q2",
      "text": "Java is a compiled language.",
      "category": "java",
      "answerType": "true_false",
      "options": null
    }
  ]
}
```

### Submit answers
```bash
curl -X POST http://localhost:8080/api/quiz/submit \
  -H "Content-Type: application/json" \
  -d '{
    "playerName": "Alice",
    "category": "java",
    "questionIds": ["q1", "q2"],
    "answers": ["0", "true"]
  }'
```

Response:
```json
{
  "success": true,
  "message": "Quiz submitted successfully",
  "data": {
    "playerName": "Alice",
    "score": 2,
    "totalQuestions": 2,
    "percentage": 100.0,
    "category": "java"
  }
}
```

### Get leaderboard
```bash
curl http://localhost:8080/api/leaderboard
```

Response:
```json
{
  "success": true,
  "message": "OK",
  "data": [
    {
      "playerName": "Alice",
      "percentage": 100.0,
      "score": 2,
      "totalQuestions": 2,
      "category": "java",
      "completedAt": "2026-04-07T09:00:00Z"
    }
  ]
}
```

## Adding Questions

Edit `src/main/resources/questions.json`. Two answer types are supported:

### Multiple choice
```json
{
  "id": "q5",
  "text": "Which of these is not a Java primitive?",
  "category": "java",
  "answer": {
    "type": "multiple_choice",
    "options": ["int", "boolean", "String", "char"],
    "correctIndex": 2
  }
}
```

### True / False
```json
{
  "id": "q6",
  "text": "Java supports multiple inheritance through classes.",
  "category": "java",
  "answer": {
    "type": "true_false",
    "correctAnswer": false
  }
}
```

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `PORT` | `8080` | Server port |
| `QUESTIONS_PATH` | `src/main/resources/questions.json` | Path to questions file |
| `LEADERBOARD_PATH` | `leaderboard.json` | Path to leaderboard file |

## Deployment

This project is configured for deployment on [Railway](https://railway.app).

1. Push the project to GitHub
2. Create a new project on Railway and connect your repository
3. Railway auto-detects Maven and builds the project
4. Generate a public domain under **Settings → Networking**

## Architecture

The project follows a strict layered architecture where dependencies only flow downward: