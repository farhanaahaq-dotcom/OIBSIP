# Number Guessing Game

## Overview
This is a simple **Java Swing** application that lets you play a classic number‑guessing game. The goal is to guess a randomly generated number within a limited number of attempts. The game supports three difficulty levels (Easy, Medium, Hard) that change the range of the hidden number.

The project was built as a university assignment for a Java Development course. It demonstrates basic GUI programming, event handling, and simple game logic.

## Features
- **Three difficulty levels** – Easy (1‑50), Medium (1‑100), Hard (1‑200).
- **10 attempts per round** – you get a maximum of 10 guesses.
- **Score system** – points are awarded based on how quickly you find the number.
- **History panel** – keeps a log of all rounds played during the session.
- **Reset and play‑again buttons** – start a new round or reset the whole game.

## How to Run
The project uses **Maven** for dependency management and building.

1. **Open a terminal** in the project folder.
2. Compile the project:
   ```bash
   mvn clean compile
   ```
3. Run the application:
   ```bash
   mvn exec:java -Dexec.mainClass="NGM"
   ```

Alternatively, you can run it directly from an IDE that supports Maven (IntelliJ IDEA, Eclipse, VS Code). Just run the `NGM` class.

## Code Overview
The main class is `NGM.java` which extends `JFrame`. It contains:

- **Difficulty enum** – defines the three difficulty levels and their ranges.
- **Game state variables** – `secretNumber`, `attempts`, `roundNumber`, `wins`, `losses`, etc.
- **UI components** – labels, buttons, text fields, and a history text area.
- **Game logic** – `handleGuess()` processes a guess, updates the UI, and checks for win/loss.
- **Utility methods** – `startNewRound()`, `handleWin()`, `handleLoss()`, `updateInformation()`, etc.

The UI is built with Swing components (`JPanel`, `JLabel`, `JButton`, `JTextArea`, etc.) and uses a simple layout to keep the interface clean.

## How to Play
1. Choose a difficulty from the drop‑down menu.
2. Enter a number in the text field and press **Guess** or hit **Enter**.
3. The game will tell you if your guess is too high or too low.
4. You have 10 attempts to find the number. If you succeed, you win and receive points.
5. After each round, you can click **Play Again** to start a new round or **Reset Game** to clear scores and history.

Enjoy the challenge and try to beat your own high score!
