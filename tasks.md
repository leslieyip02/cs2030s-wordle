# CS2030S PE2 Extra Practice: Wordle

## Background

Wordle is a game where you attempt to guess a target word. The game gives
feedback for each letter of a guess:

| Symbol | Meaning                                                               |
|--------|-----------------------------------------------------------------------|
| `X`    | The letter does not occur in the target word.                         |
| `?`    | The letter occurs in the target word, but is in a different position. |
| `O`    | The letter is in the correct position.                                |

For example, if the target is `hello` and the guess is `world`:

```text
? world
> X?XOX
```

Unlike the actual Wordle game, this exercise does not limit how many times a
letter may receive `?` or `O`. Check each position independently using the
rules above. You do not need a dictionary of valid words.

## Preliminary

`Main.java` chooses a target word and starts the game. `UserInterface.java`
reads, cleans, and prints input. `Wordle.java` contains the game starter code.
The classes in `cs2030s/fp/` provide the functional programming operations.

Edit only `Wordle.java` and `cs2030s/fp/InfiniteList.java`. Do not modify
`Main.java`, `UserInterface.java`, or the other classes in `cs2030s/fp/`.

## Guidelines

- Do not use `if`, `else`, `switch`, `for`, `while`, `do`, or the ternary
  operator in code you write (unless stated otherwise).
- Do not use a block inside a lambda expression. However, mutation through a
  one-line lambda is allowed, for example `foo.ifPresent(bar -> baz())`.

For example, use `Maybe` instead of a conditional expression:

```java
int x = Maybe.of(y)
             .filter(n -> n > 0)
             .map(n -> 1)
             .orElse(-1);
```

Use `InfiniteList` instead of a loop over positions:

```java
String result = InfiniteList.iterate(0, i -> i + 1)
    .limit(word.length())
    .map(i -> String.valueOf(word.charAt(i)))
    .reduce("", (a, b) -> a + b);
```

## Compilation

Make sure your work compiles before testing or submitting:

```sh
javac -Xlint:unchecked -Xlint:rawtypes Main.java UserInterface.java Wordle.java cs2030s/fp/*.java
```

## Testing

I'm too lazy to write a full test suite for each task. Anyways, it's good
practice for testing your own code. After compiling, you can run the game with
`java Main`, and test its behavior on different inputs.

I've also provided a small test script for the finished game:

```sh
bash ./test.sh
```

The script compiles your Java files, runs both inputs, and compares the
output with the corresponding `.expected` files.

The `win.in` case checks an invalid length, a repeated guess, and a win. Its
output should be:

```text
? > Expected length 5
? > X?XOX
? > world already guessed
? > Congrats! Solved in 2 guess(es)
> The word was hello!
```

The `lose.in` checks that six different valid, incorrect guesses end the
game without requesting a seventh line:

```text
? > X?XOX
? > XXXXX
? > XXXX?
? > XXXXX
? > ?XX?X
? > XX?XX
> The word was hello!
```

The `? ` prompt and `> ` response appear on the same line when input comes
from a file. These are examples rather than an exhaustive test suite. You
can play interactively with `java Main`. To try the supplied solution, use
a separate copy of the project so that your work is not overwritten.

You can also write test inputs and redirect them like so:

```sh
vim example.txt
java Main < example.txt
```

Passing a word as the first argument to `Main` fixes the target word:

```sh
java Main hello
```

## Task 1: Consume input

`InfiniteList` has methods such as `reduce` and `toList`, but no operation
that performs an action once for every value. Implement `InfiniteList.forEach(action)`.

| Method            | Behaviour                                                 |
|-------------------|-----------------------------------------------------------|
| `forEach(action)` | Apply `action` to each present element and return `void`. |

Filtered-out elements must not be passed to `action`. A sentinel or empty
list must perform no action. You may use a `while` loop for this task.

In `Wordle.run`, replace the temporary `reduce` call with `forEach` so that
each input line is printed once. After compiling, you can run `java Main`. You
should be able to submit arbitrary inputs. Use `/exit` to stop the game.

## Task 2: Validate input

When a user submits a guess, we need to perform various checks.

- Is the input valid?
- Is the input the target word?
- What letters are in their correct positions?

To do so, we can store the guessed word in a wrapper class. The wrapper class
can also store a status message. If the wrapper class already has a status
message, we can simply skip the check.

Since checks may or may not result in a message, one possible design is for
each check method to return a `Maybe<String>`. For example:

```java
private Maybe<String> validateLength(String word) {
  // Returns a message if the word length is incorrect, and none otherwise
  ...
}
```

Create a private nested `Status` class in `Wordle`. It keeps the original
guessed word and a `Maybe<String>` message. A missing message means that no
check has produced feedback yet.

For `map(check)`, `check` should be a `Transformer<String, Maybe<String>>`.

| Method            | Behaviour                                                  |
|-------------------|------------------------------------------------------------|
| `Status.of(word)` | Create a status for `word` with no message.                |
| `map(check)`      | Apply `check` only when there is no message.               |
|                   | Keep the first message produced and return a new `Status`. |
| `getMessage()`    | Return the message, or `""` when there is none.            |

Once `Status` is implemented, you can add validation checks to `Wordle.guess`.
It should look something like this:

```java
private String guess(String word) {
  return Status.of(word)
               .map(w -> checkFoo(w))
               .map(w -> checkBar(w))
               .getMessage();
}
```

If `checkFoo` has a message, we can skip running `checkBar`. If `checkFoo` has
no message, then `checkBar` will run. This allows checks to be composed through
chaining.

There are 2 main checks to implement:

- Is the guess the correct length?
- Has the guess already been guessed? (We don't want to waste turns)

| Method                         | Behaviour                                                  |
|--------------------------------|------------------------------------------------------------|
| `validateLength(word)`         | Checks the length of `word` equals the `targetWord`.       |
|                                | If not, the message should be `"Expected length <n>"`,     |
|                                | where `n` is the length of the `targetWord`                |
| `validateAlreadyGuessed(word)` | Checks if the word has already been guessed.               |
|                                | If so, the message should be `<word> already guessed`.     |

For a target of `hello`:

```text
? hi
> Expected length 5
? world
> 
? world
> world already guessed
```

For `validateAlreadyGuessed`, you probably want to use a `HashSet`:

```java
import java.util.HashSet;
import java.util.Set;

Set<String> alreadyGuessed = new HashSet<>();
alreadyGuessed.add("hello");
alreadyGuessed.contains("hello"); // true
alreadyGuessed.contains("world"); // false
```

## Task 3: Check guesses

Implement `checkPosition(letter, index)` and `checkPositions(word)`:

- `checkPosition` returns `"O"` when `letter` matches the target at `index`,
  `"?"` when the letter occurs elsewhere in the target, and `"X"` otherwise.
- `checkPositions` combines one symbol for each position, in the same order
  as the guess, and returns it as `Maybe<String>`.

| Method                         | Behaviour                                                  |
|--------------------------------|------------------------------------------------------------|
| `checkPosition(letter, index)` | Returns `"O"` when `letter` matches the target at `index`, |
|                                | `"?"` when the letter occurs elsewhere in the target,      |
|                                | and `"X"` otherwise.                                       |
| `checkPositions(word)`         | Returns the result for each position of `word`,            |
|                                | in the same order.                                         |

For target `hello`:

| Guess   | Result  |
|---------|---------|
| `world` | `X?XOX` |
| `hello` | `OOOOO` |
| `xxxxx` | `XXXXX` |


## Task 4: Finish the game

Great! Now we can (sort of) play the game.

The last step would be to add checks for whether the game is over. Implement
the methods below and chain them in `Wordle.guess`:

| Method               | Behaviour                                     |
|----------------------|-----------------------------------------------|
| `checkGameWin(word)` | Checks if the player has the correct word.    |
|                      | If the word is correct, the message should be |
|                      | `"Congrats! Solved in <n> guess(es)"`         |
| `isGameOver()`       | The game is over once the word is guessed,    |
|                      | or if the player has reached `MAX_ATTEMPTS`   |

`isGameOver` is already implemented, but the `isGameWon` boolean is never set.
Make sure to set `isGameWon` inside `checkGameWin` so that the input loop stops
polling for more inputs.

For a target of `hello`:

```text
? hi
> Expected length 5
? hello
> Congrats! Solved in 1 guess(es)
```
