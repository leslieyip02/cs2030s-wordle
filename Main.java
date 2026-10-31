import java.util.List;
import java.util.Random;

public class Main {
  public static void main(String[] args) {
    Wordle wordle = new Wordle(chooseWord());
    wordle.run();
  }

  private final static List<String> WORD_BANK = List.of("maybe", "class", "monad");

  private static String chooseWord() {
    int index = new Random().nextInt(WORD_BANK.size());
    return WORD_BANK.get(index);
  }
}
