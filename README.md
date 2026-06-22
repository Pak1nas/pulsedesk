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
