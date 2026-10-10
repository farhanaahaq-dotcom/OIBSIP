# Online Reservation System

## Project Overview
This project is a **Java desktop application** that allows users to book, view, and cancel reservations for a hypothetical service (e.g., a hotel, a car rental, or a restaurant). It was built as a university assignment for the *Java Development* course.

The application uses a simple Swing GUI and a plain‑text file (`Database.java`) to store reservation data. No external database or web server is required – everything runs locally.

## Features
- **Login** – Users can log in with a username and password.
- **View Reservations** – After logging in, the user can see a list of all current reservations.
- **Create Reservation** – The user can add a new reservation by entering a name, date, and time.
- **Cancel Reservation** – Existing reservations can be cancelled.
- **Persistence** – All data is written to a file so that it survives application restarts.

## Project Structure
```
online-reservation-system/
├─ pom.xml
├─ src/
│  └─ main/
│     └─ java/
│        └─ com/oasisinfobyte/
│           ├─ CancellationFrame.java
│           ├─ DashboardFrame.java
│           ├─ Database.java
│           ├─ LoginFrame.java
│           ├─ Main.java
│           ├─ Reservation.java
│           └─ ReservationFrame.java
└─ test/
   └─ java/
      └─ com/oasisinfobyte/
```

## How It Works (Simplified)
1. **Login** – `LoginFrame` shows a simple login form. When the user clicks *Login*, the program checks the credentials against a hard‑coded list.
2. **Dashboard** – After a successful login, `DashboardFrame` appears. It contains two buttons: *View Reservations* and *Create Reservation*.
3. **Reservation List** – Clicking *View Reservations* opens `ReservationFrame`, which reads the data from `Database.java` and displays it in a table.
4. **Adding a Reservation** – The user can click *Create Reservation*, fill in the details, and the new reservation is appended to the file via `Database.addReservation()`.
5. **Cancellation** – In the reservation list, selecting a row and pressing *Cancel* will remove that entry from the file.

## Running the Application
1. **Prerequisites** – Java 17+ and Maven installed.
2. **Build** – Open a terminal in the project root and run:
   ```bash
   mvn clean package
   ```
3. **Run** – After the build succeeds, start the application with:
   ```bash
   java -cp target/online-reservation-system-1.0-SNAPSHOT.jar com.oasisinfobyte.Main
   ```

## Example Code Snippet
```java
// Adding a reservation
Reservation newRes = new Reservation("John Doe", "2026-10-15", "18:00");
Database.addReservation(newRes);
```

## What I Learned
- How to build a simple Swing UI.
- Basic file I/O for persistence.
- Using Maven for dependency management and packaging.




