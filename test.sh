#!/usr/bin/env bash
set -o pipefail

cd "$(dirname "$0")" || exit 1

javac -Xlint:unchecked -Xlint:rawtypes Main.java UserInterface.java Wordle.java cs2030s/fp/*.java || exit 1

result=0
for name in win lose; do
  if java Main hello < "tests/${name}.in" | diff -u "tests/${name}.out" -; then
    printf '%s: \e[1;32mok\e[0m\n' "$name"
  else
    printf '%s: \e[1;31mfail\e[0m\n' "$name"
    result=1
  fi
done

exit "$result"
