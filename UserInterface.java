import cs2030s.fp.InfiniteList;
import cs2030s.fp.Maybe;
import java.util.Scanner;

public class UserInterface {
  private final Scanner scanner;
  private final InfiniteList<String> inputs;

  public UserInterface() {
    this.scanner = new Scanner(System.in);
    this.inputs = InfiniteList
      .generate(() -> read()) 
      .map(line -> clean(line))
      .filter(line -> line.length() > 0);
  }

  public InfiniteList<String> getInputs() {
    return inputs;
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
