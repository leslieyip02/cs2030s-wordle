public class Wordle {
  private final static int MAX_ATTEMPTS = 6;

  private final String targetWord;
  private boolean isGameWon;

  public Wordle(String targetWord) {
    this.targetWord = targetWord;
    this.isGameWon = false;
  }

  public void run() {
    UserInterface ui = new UserInterface();
    ui.getInputs(() -> !isGameOver())
      .takeWhile(line -> !isGameExit(line))
      .map(line -> guess(line))
      .reduce("", (accumulator, line) -> {
        // TODO: Task 1
        ui.print(line);
        return line;
      });
    ui.print(String.format("The word was %s!", targetWord));
  }

  private boolean isGameOver() {
    return isGameWon;
  }

  private boolean isGameExit(String line) {
    return line.equals("/exit");
  }
}
