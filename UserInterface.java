import cs2030s.fp.InfiniteList;
import cs2030s.fp.Producer;
import java.util.Scanner;

public class UserInterface {
  private final Scanner scanner;

  public UserInterface() {
    this.scanner = new Scanner(System.in);
  }

  public InfiniteList<String> getInputs(Producer<Boolean> shouldContinue) {
    return InfiniteList.generate(shouldContinue)
      .takeWhile(condition -> condition)
      .map(line -> clean(read()))
      .filter(line -> line.length() > 0);
  }

  private String clean(String line) {
    return line.strip().toLowerCase();
  }

  private String read() {
    System.out.print("? ");
    return scanner.nextLine();
  }

  public void print(String line) {
    System.out.println(String.format("> %s", line));
  }
}
