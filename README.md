# Tail Call Counter

A small program that reads a restricted Scheme function definition and counts how many times it properly tail-recursively calls itself.

## Assumptions

This solution follows the task statement and supports a restricted Scheme:
- there is exactly one top-level `define`
- `if` is the only special form
- every other list form is treated as a function call
- the program counts syntactic occurrences of proper tail self-calls

## How to run

Provide the Scheme definition on standard input (input is read until EOF - e.g. Ctrl+D on Linux/macOS or Ctrl+Z then Enter on Windows).

## Design notes

The solution has three stages:
1. Parse the input into a generic S-expression tree.
2. Convert it into a smaller AST for the restricted language.
3. Traverse the AST while tracking whether the current expression is in tail position.

Tail-position rules used:
- in `(if cond then else)`, only `then` and `else` may be in tail position
- `cond` is never in tail position
- function call arguments are never in tail position
- a call counts if it is both:
  - in tail position
  - a call to the same function being defined

## Testing

The test suite covers:
- the example from the task
- direct tail self-calls
- self-calls in arguments
- self-calls in `if` conditions
- nested `if`s
- classic non-tail-recursive factorial
- tail-recursive factorial
- malformed input
