import cs2030s.fp.InfiniteList;
import cs2030s.fp.Maybe;
import cs2030s.fp.Transformer;
import java.util.HashSet;
import java.util.Set;

public class Wordle {
  private static class Status {
    private String word;
    private Maybe<String> message;

    private static Status of(String word) {
      return new Status(word, Maybe.none());
    }

    private Status(String word, Maybe<String> message) {
      this.word = word;
      this.message = message;
    }

    private Status map(Transformer<String, Maybe<String>> mapper) {
      return new Status(word, Maybe.of(message.orElseGet(() -> mapper.transform(word)
                                                                     .orElse(null))));
    }

    private String getMessage() {
      return message.orElse("");
    }
  }

  private final static int MAX_ATTEMPTS = 6;

  private final String targetWord;
  private final Set<String> alreadyGuessed;
  private boolean isGameWon;

  public Wordle(String targetWord) {
    this.targetWord = targetWord;
    this.alreadyGuessed = new HashSet<>();
    this.isGameWon = false;
  }

  public void run() {
    UserInterface ui = new UserInterface();
    ui.getInputs()
      .takeWhile(line -> !isGameOver(line))
      .map(w -> guess(w))
      .forEach(m -> ui.print(m));
    ui.print(String.format("The word was %s!", targetWord));
  }

  private boolean isGameOver(String line) {
    return isGameWon
      || alreadyGuessed.size() == MAX_ATTEMPTS
      || line.equals("/exit");
  }

  private String guess(String word) {
    Status status = Status.of(word)
                          .map(w -> validateLength(w))
                          .map(w -> validateAlreadyGuessed(w))
                          .map(w -> checkGameWin(w))
                          .map(w -> checkPositions(w));
    alreadyGuessed.add(word);
    return status.getMessage();
  }

  private Maybe<String> validateLength(String word) {
    return Maybe.of(word)
                .filter(w -> word.length() != targetWord.length())
                .map(_ -> String.format("Expected length %d", targetWord.length()));
  }

  private Maybe<String> validateAlreadyGuessed(String word) {
    Maybe<String> message = Maybe.of(word)
                                 .filter(w -> alreadyGuessed.contains(w))
                                 .map(_ -> String.format("%s already guessed"));
    alreadyGuessed.add(word);
    return message;
  }

  private Maybe<String> checkGameWin(String word) {
    Maybe<String> message = Maybe.of(word)
                                 .filter(w -> w.equals(targetWord))
                                 .map(_ -> String.format("Congrats! Solved in %d guesses", alreadyGuessed.size()));
    message.ifPresent(_ -> isGameWon = true);
    return message;
  }

  private Maybe<String> checkPositions(String word) {
    return Maybe.of(InfiniteList.iterate(0, i -> i + 1)
                                .limit(targetWord.length())
                                .map(i -> checkPosition(word.charAt(i), i))
                                .reduce("", (a, b) -> a + b));
  }

  private String checkPosition(char letter, int index) {
    return Maybe.of(letter)
      .filter(c -> targetWord.indexOf(c) != -1)
      .map(c -> Maybe.of(index)
                     .filter(i -> targetWord.charAt(i) == c)
                     .map(_ -> "O")
                     .orElse("?"))
      .orElse("X");
  }
}
