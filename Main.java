public class Main {
  public static void main(String[] args) {
    UserInterface ui = new UserInterface();
    ui.getInputs()
      .takeWhile(line -> !isExit(line))
      .reduce("", (a, b) -> {
        System.out.println(b);
        return a + b;
      });
  }

  private static boolean isExit(String line) {
    String normalized = line.strip().toLowerCase();
    return normalized.equals("/exit") || normalized.equals("/quit"); 
  }
}
