# PulseDesk

PulseDesk is a Spring Boot backend application for collecting user comments and automatically turning real customer issues into support tickets.

The application accepts comments, analyzes them using the Hugging Face Inference API, and creates a structured ticket when the comment represents a bug, billing issue, account issue, feature request, or other support problem.

## Features

- Submit user comments
- View all submitted comments
- Analyze comments using Hugging Face
- Automatically decide whether a comment should become a support ticket
- Generate ticket data:
  - title
  - category
  - priority
  - summary
- View all created tickets
- View a ticket by ID
- Store data using an in-memory H2 database
- Fallback keyword logic if the Hugging Face API call fails

## Tech Stack

- Java 21
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- H2 Database
- Hugging Face Inference API
- Maven

## API Endpoints

### Comments

| Method | Endpoint | Description |
|---|---|---|
| POST | `/comments` | Submit a new comment |
| GET | `/comments` | View all comments |

### Tickets

| Method | Endpoint | Description |
|---|---|---|
| GET | `/tickets` | View all created tickets |
| GET | `/tickets/{ticketId}` | View a ticket by ID |

## Setup Instructions

### 1. Clone the repository

```bash
git clone https://github.com/Pak1nas/pulsedesk.git
cd pulsedesk
```

### 2. Create a Hugging Face token

Create a Hugging Face access token with permission to call Inference Providers.

Required permission:

```text
Inference → Make calls to Inference Providers
```

### 3. Set the Hugging Face token

In PowerShell:

```powershell
$env:HF_TOKEN="your_hugging_face_token_here"
```

Do not commit your real token to GitHub.

The application reads the token from:

```properties
huggingface.api.token=${HF_TOKEN}
```

### 4. Run the application

```powershell
.\mvnw.cmd spring-boot:run
```

The application will start at:

```text
http://localhost:8080
```

## Example Requests

### Submit a comment that creates a ticket

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/comments" -Method POST -ContentType "application/json" -Body '{"text":"The app crashes every time I upload a PDF file."}'
```

Example result:

```text
ticketCreated : True
category      : bug
priority      : medium
```

### Submit a comment that does not create a ticket

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/comments" -Method POST -ContentType "application/json" -Body '{"text":"The new dashboard looks amazing!"}'
```

Example result:

```text
ticketCreated : False
```

### View all comments

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/comments" -Method GET
```

### View all tickets

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/tickets" -Method GET
```

### View one ticket

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/tickets/1" -Method GET
```

## H2 Database Console

The H2 console is available at:

```text
http://localhost:8080/h2-console
```

Use these settings:

```text
JDBC URL: jdbc:h2:mem:pulsedeskdb
Username: sa
Password: leave empty
```

## AI Behavior

PulseDesk asks Hugging Face to return structured JSON with:

```json
{
  "shouldCreateTicket": true,
  "title": "short ticket title",
  "category": "bug",
  "priority": "medium",
  "summary": "short summary"
}
```

Allowed categories:

```text
bug, feature, billing, account, other
```

Allowed priorities:

```text
low, medium, high
```

If the Hugging Face API request fails, the application uses fallback keyword logic so comments can still be analyzed during testing.

## Frontend

PulseDesk includes a simple frontend served directly by Spring Boot.

The frontend has two pages:

- `index.html` - main page for submitting user comments
- `dashboard.html` - dashboard page for viewing submitted comments and created tickets

When the application is running, open:

```text
http://localhost:8080

## Project Structure

```text
src/main/java/com/pulsedesk
├── ai
│   └── HuggingFaceClient.java
├── controller
│   ├── CommentController.java
│   └── TicketController.java
├── dto
│   ├── AiTicketResult.java
│   └── CreateCommentRequest.java
├── model
│   ├── Comment.java
│   └── Ticket.java
├── repository
│   ├── CommentRepository.java
│   └── TicketRepository.java
├── service
│   ├── CommentService.java
│   └── TicketAnalysisService.java
└── PulsedeskApplication.java
```

## Notes

- The application uses an in-memory H2 database, so data resets when the app restarts.
- The Hugging Face token must be provided through the `HF_TOKEN` environment variable.
- No API tokens should be committed to the repository.
