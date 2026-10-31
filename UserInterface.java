import cs2030s.fp.InfiniteList;
import cs2030s.fp.Maybe;
import java.util.Scanner;

public class UserInterface {
  private final InfiniteList<String> inputs;

  public UserInterface() {
    Scanner scanner = new Scanner(System.in);
    this.inputs = InfiniteList
      .generate(() -> scanner.nextLine())
      .map(line -> line.strip())
      .filter(line -> line.length() > 0);
  }

  public InfiniteList<String> getInputs() {
    return inputs;
  }
}
