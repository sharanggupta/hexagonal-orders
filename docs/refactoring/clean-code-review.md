# Clean Code review and refactoring cycle

Project: `hexagonal-orders`
Review date: 20 September 2026
Baseline: `18a585c`
Scope: all five baseline Java files, both Maven POMs, and test organization.

This review uses the twelve categories in the installed `clean-code-martin` skill, with additional checks for SOLID, FIRST, command/query separation, and simple design. These are review categories, not an exhaustive numbered list from the book. Findings are contextual: a guideline is not automatically a defect.

No Critical or Major findings. The main issue is the cohesion of the 161-line `OrderTest`, not excessive production complexity. Original file references below refer to baseline `18a585c`.

## Principle-by-principle findings

| # | Principle | Original finding | Disposition |
|---|---|---|---|
| 1 | Meaningful names | Minor: `OrderTest.java:127` names preservation but also verifies the cancellation transition. Total scenarios are anonymous argument rows. Other domain names communicate their purpose. | Name cancellation around both outcomes and give arithmetic cases descriptive display names. Keep idiomatic `items()`, `status()`, and `total()` accessors. |
| 2 | Small functions that do one thing, at one abstraction level | Minor: `Order.total()` at `Order.java:30` both sums lines and knows how an individual line is priced. | Move multiplication into package-private `OrderItem.subtotal()`; `Order.total()` sums those subtotals. Constructor guards already form one coherent responsibility. A long data table does not warrant arbitrary helper extraction merely to meet a line limit. |
| 3 | Few arguments; no flags or output parameters | Pass: constructors have one or two arguments; no boolean flags or modified input parameters. | Preserve small signatures. No parameter-object abstraction needed. |
| 4 | Avoid duplication (DRY) | Minor: the same two-item setup appears in both cancellation tests, `OrderTest.java:128` and `:142`. Verbose decimal construction obscures total examples. | Share the cancellation fixture locally and simplify arithmetic fixtures where useful. Do not centralize unrelated domain exception messages or Maven parent coordinates. |
| 5 | Explicit errors with useful context | Mostly pass: standard unchecked exceptions correctly express invalid arguments and illegal state. Minor diagnostic gap: `List.copyOf` rejects null entries without a domain-specific message. | Preserve exception types, messages, and validation order. Record null-entry diagnostics as a possible later behavior change, not part of this refactor. Custom exceptions add no present value. |
| 6 | Useful comments; no redundant or stale comments | Pass: no commented-out code or comments repeating implementation. | Keep the Java code self-explanatory. No new explanatory comments needed. |
| 7 | Consistent formatting and vertical locality | Minor/style: provider imports are inconsistently ordered; the total-data provider interrupts progression into lifecycle tests in `OrderTest.java:89–117`. | Group related tests and providers in focused files and normalize imports. Existing indentation is consistent. |
| 8 | Encapsulation; appropriate objects and data structures | Minor: item arithmetic belongs with the item's quantity and price (same underlying finding as #2). Immutable item records and defensive copies already protect state. | Extract subtotal without expanding the public API. Retain immutable item-list exposure. Combining a behavior-rich Order with an immutable value record is appropriate; no new getters, setters, or wrappers. |
| 9 | Clean error boundaries | Pass internally; external boundaries not applicable. No HTTP, database, or third-party service boundary exists yet. | Keep unchecked domain errors. Add translation at future adapters when needed, rather than introducing speculative wrappers or checked exceptions. |
| 10 | Clean, focused, independent tests | Minor: one 161-line class mixes item invariants, arithmetic, and lifecycle. Cancellation's numeric equality assertions have weak failure output. | Split into `OrderItemsTest`, `OrderTotalTest`, and `OrderCancellationTest`; preserve all cases and assertions, with useful decimal diagnostics. Multiple assertions are appropriate when they describe a single behavior and its unchanged state. |
| 11 | Isolated concurrency concerns | Not applicable: there is no concurrent mutation contract. `Order` is mutable and is not claimed to be thread-safe. | Do not add synchronization or volatile speculatively. Reassess with an actual concurrent application/persistence design. |
| 12 | Small, cohesive classes with one reason to change | Minor: test-class cohesion is weak. Production classes are already small and focused. | Divide tests by behavior. Keep item invariants, totals, and lifecycle inside the Order domain model; do not introduce services just to reduce line counts. |

Several categories identify the same underlying problem. They are not twelve independent violations.

## Further Clean Code checks

| Check | Assessment |
|---|---|
| SRP | Test responsibilities are separated; each production class retains its domain responsibility. |
| Open/closed principle | No demonstrated alternative pricing or cancellation policies exist. Strategy interfaces would be premature. |
| Liskov substitution | No inheritance hierarchy to evaluate. |
| Interface segregation | No broad interfaces exist. Nothing to split. |
| Dependency inversion | Domain production code uses only Java APIs. JUnit is test-scoped. No infrastructure dependency leaks inward. |
| FIRST: fast | In-memory tests require no server, database, or Spring context. |
| FIRST: independent | Each test creates its own objects; no shared mutable test state. |
| FIRST: repeatable | Inputs are fixed and no clock, random generator, or network is involved. |
| FIRST: self-validating | Assertions check results and failures without manual log inspection. |
| FIRST: timely | Prior commits document observed RED then GREEN results. This cycle refactors existing tested behavior. |
| Command/query separation | `cancel()` changes status and returns no value. Accessors and `total()` do not mutate state. |
| Hidden side effects | Input lists are copied; returned items cannot be modified. Existing tests retain these guarantees. |
| Magic numbers | Zero is the direct mathematical boundary in quantity/price validation. Prices and counts in tests are explicit examples, not unexplained configuration. No constants needed merely to rename zero or one. |
| Minimal design | No added production interfaces, services, inheritance, or dependencies. Keep the readable aggregation loop. |
| Tests as documentation | Grouping by domain behavior and naming arithmetic cases improves navigation without deleting coverage. |

## Refactoring plan and verification

Planning followed readability → complexity → responsibilities → abstraction/design checks. Only small improvements were justified. Apply edits as one coherent batch; verify the full suite afterward. No new behavior or RED test is required for a behavior-preserving refactor.

Expected case preservation: 15 item-validation cases + 7 order-item cases + 6 total cases + 3 cancellation cases = 31. The arithmetic cases include all-free and mixed orders, fractional decimals, and large quantities/prices. Existing exception assertions and item immutability checks remain.

Git change frequency is only a prioritization hint in this small repository: `Order.java` and `OrderTest.java` each changed in three commits; `OrderItem.java` and `OrderItemTest.java` each changed in two. This short history does not establish long-term risk.

## Completed changes and measured result

| Measure | Before | After |
|---|---:|---:|
| Largest test class | 161 lines | 74 lines |
| Order-related test responsibilities per class | 3 | 1 |
| Total test-source lines, including support | 223 | 253 |
| Executed test cases | 31 | 31 |
| Production framework dependencies | 0 | 0 |

Total test-source lines increased because each focused class has its own imports and declaration. The improvement is easier navigation and higher cohesion, not a claim of fewer lines overall.

| Final file | Role | Lines | Test cases |
|---|---|---:|---:|
| `OrderItemsTest.java` | Construction invariants and item-list protection | 74 | 7 |
| `OrderTotalTest.java` | Exact totals with six named examples | 45 | 6 |
| `OrderCancellationTest.java` | Initial status, cancellation, and repeat rejection | 52 | 3 |
| `OrderItemTest.java` | Quantity and price validation, unchanged | 62 | 15 |
| `OrderTestSupport.java` | Two small helpers for decimal fixtures/assertions | 20 | 0 |

Production change: multiplication moved to package-private `OrderItem.subtotal()`; `Order.total()` retains its loop and exact arithmetic. Public signatures, rounding behavior, validation order, exception behavior, item immutability, and cancellation behavior are unchanged. Both POMs and `OrderStatus` are unchanged.

Verification: `mvn -o clean test` on GraalVM Java 21.0.7 completed successfully with **31 tests, zero failures, zero errors, zero skipped**. Cleaning first removed the old compiled `OrderTest`, so this count comes exclusively from the new test layout. `git diff --check` also passed.

### Deferred improvement

A null element still produces the existing `NullPointerException` from `List.copyOf`. A domain-specific error message would improve diagnostics, but would change observable behavior. Address it in a separate RED → GREEN cycle if desired.

Final code-guardian review: **approved**. All original cases, input values, decimal scales, and assertions were checked against baseline `18a585c`; no Critical, Major, or Minor regressions were found.


## Resolution — 21 September 2026

All actionable findings from the Clean Code and Code-as-Prose review are addressed:

| Finding | Resolution |
|---|---|
| Validation hidden inside fixtures | OrderCreationFactsTest directly invokes the record constructor for missing ID, customer ID, and timestamp. |
| Unclear creation name | Renamed OrderCreation to OrderCreationFacts; aligned field, parameter, and fixture names. |
| Mixed declaration narrative | Order now presents identity, customer, timestamp, items, status, total, then cancellation. |
| Mixed test ownership | OrderCreationFactsTest owns record invariants; OrderCreationTest owns order-level creation behavior. |
| Inconsistent identity-test fixtures | Identity tests use the same item helper as neighboring order tests. |
| Formatting noise | Removed the trailing blank line in OrderItemsTest. |
| Missing null-item diagnostic | Explicitly reject null entries with NullPointerException and “Order items must not contain null”; preserve defensive copying. |

The readability refactor preserves all 41 cases. The existing null-item test was strengthened, not duplicated: it first failed because the message was null, then passed after the guard was implemented. A final `mvn -o clean test` on Java 21 passed all 41 cases with no failures, errors, or skipped tests. No additional production interfaces or dependencies were introduced.

Items marked Pass or Not applicable in the original principle tables require no artificial changes. The original findings above are historical evidence; this resolution supersedes all optional/deferred wording relating to them.


## Design correction — 22 September 2026

The user prefers four explicit Order constructor arguments over a parameter wrapper. Readability and meaningful domain cohesion take precedence over a numerical argument limit. Order now owns ID, customer ID, creation time and items directly; OrderCreationFacts and its fixture helper were removed. Their constructor guards now live in Order with the same messages.

The three missing-value tests now invoke Order directly in OrderCreationTest. The sole removed test checked a null wrapper that no longer exists. All 40 remaining cases pass after `mvn -o clean test`; no business rule or behavior assertion was dropped. This is a behavior-preserving domain refactor with an intentional construction API change, not a new business feature. Historical recommendations to retain the wrapper are superseded.
