import cs2030s.fp.Maybe;
import java.util.List;
import java.util.Random;

public class Main {
  public static void main(String[] args) {
    String targetWord = Maybe.of(args.length)
                             .filter(n -> n > 0)
                             .map(i -> args[0])
                             .orElseGet(() -> chooseWord());

    Wordle wordle = new Wordle(targetWord);
    wordle.run();
  }

  private final static List<String> WORD_BANK = List.of("maybe", "class", "monad");

  private static String chooseWord() {
    int index = new Random().nextInt(WORD_BANK.size());
    return WORD_BANK.get(index);
  }
}
