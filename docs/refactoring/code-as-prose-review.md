# Clean Code and Code-as-Prose evaluation

Reviewed: 21 September 2026

Project: `/Users/sharang/Code/hexagonal-orders`, commit `0f2a346`.

Scope: four production Java classes, seven test classes, one test-support class, and both Maven POMs. Evaluation only; no repository files changed.

Method: review against the installed Clean Code (Robert C. Martin) and Code-as-Prose (Grady Booch) skills, independent code-guardian audit, full Maven test run, and whitespace checks. These skills supply practical review categories; this is not a claim to enumerate every statement in either author's work. No Checkstyle, PMD, or other static-analysis tool was installed or run.

## Verdict

The domain is small, readable, and correctly separated from frameworks. No Critical or Major issues were found. All 41 tests pass. The best next improvements concern test narrative and declaration order, not additional architecture.

## Findings, ordered by usefulness

### 1. Minor: exception tests hide the actual owner of validation

Locations: `OrderIdentityTest.java:38–40`, `OrderCustomerTest.java:38–40`, and `OrderTestSupport.java:17–18` under `domain/src/test/java/com/example/orders/domain/`.

The test reads:

```java
() -> new Order(creation(null, CUSTOMER_ID), items)
```

Java evaluates the constructor arguments first. Here `creation(...)` constructs an `OrderCreation`, which throws before the `Order` constructor runs. The expected invariant is tested, but the visible action points the reader at the wrong constructor. This is a narrative and ownership problem, not a missing-validation bug.

Recommended change: directly test `new OrderCreation(null, CUSTOMER_ID, CREATED_AT)` for missing identity, and the equivalent call for missing customer. Put these invariant tests with the creation-facts tests. Retain order-level tests showing that identity and customer association survive cancellation. Keep fixture timestamps fixed; do not use the clock.

TDD implication: this cleanup preserves existing assertions and behavior. It is a refactor with green tests throughout, not a reason to manufacture a new failing requirement.

### 2. Minor/style: Order's declarations do not tell a consistent story

Location: `domain/src/main/java/com/example/orders/domain/Order.java:23–55`.

The reader encounters creation time, customer ID, order ID, status, cancellation, total, then items. The creation record declares ID, customer ID, time. The order class reverses that sequence and separates its items accessor from other state accessors.

Recommended change: pick one coherent order, for example constructor → id/customerId/createdAt/items/status → total → cancel. Another consistent behavior-first ordering is also defensible. The goal is predictable navigation, not enforcement of a universal method-order rule.

### 3. Minor diagnostic weakness: a null item has no domain explanation

Location: `domain/src/main/java/com/example/orders/domain/Order.java:17`; corresponding test `OrderItemsTest.java:40–43`.

`List.copyOf(items)` correctly rejects null entries, but its exception does not identify the domain rule in a useful message. Other constructor failures do provide clear messages.

Recommended future behavior: reject a null item with a clear message such as “Order items must not contain null.” This changes observable exception details. Write the message expectation first, observe RED, then implement without weakening the immutable copy. It is separate from the prose-only refactor.

### 4. Optional: make creation-facts terminology explicit

Location: `domain/src/main/java/com/example/orders/domain/OrderCreation.java:7`.

`OrderCreation` contains facts; it does not perform creation. Its name could be mistaken for an action or application command when those are introduced. `OrderCreationFacts` would be more literal, but longer. Keeping the current name is defensible at this size.

The record was introduced to honor the requested three-argument limit. It groups immutable creation facts coherently, but the extra type and forwarding accessors impose a real navigation cost. Do not claim it was necessary for hexagonal architecture, and do not add more wrappers merely to satisfy a number.

## Code-as-Prose: each principle

| Principle | Assessment |
|---|---|
| 1. Code communicates intent | Good: `cancel()` states its guard and transition directly. The nested exception-test fixture is the principal exception; see finding 1. |
| 2. Simplicity | Good: no builders, service wrappers, or speculative interfaces. Keep the simple loop and standard exceptions. |
| 3. Flow and rhythm | Improve declaration order in Order. Tests have clear setup/action/assertion paragraphs. |
| 4. Naming | Domain nouns and operations are clear. Creation-facts terminology is an optional refinement. |
| 5. Low cognitive load | Small focused tests help. Nested construction can hide failure ownership. Shared defaults are appropriate only when irrelevant to the behavior under test. |
| 6. Visual clarity | Consistent indentation; Java lines are at most 106 characters. A trailing blank line in OrderItemsTest is cosmetic, not a meaningful defect. |
| 7. Domain context | Orders, item subtotals, cancellation, customer association, and creation timestamps are explicit. No persistence terminology leaks in. |
| 8. Clarity over compression | `total = total.add(item.subtotal())` is easy to follow. A stream is not inherently clearer. |
| 9. Precise, consistent words | `unitPrice`, `subtotal`, and `total` distinguish useful concepts. `OrderCreation` deserves attention as the application layer grows. |
| 10. Narrative structure | Focused test classes and local data providers work well. Direct exception targets and grouped accessors would improve the top-to-bottom story. |

## Clean Code: each principle

| Principle | Assessment |
|---|---|
| 1. Meaningful names | Pass with the optional creation-facts naming caveat. No cryptic production names. |
| 2. Small, single-purpose functions | Pass. Constructors establish invariants; subtotal multiplies; total sums; cancellation enforces one transition. |
| 3. Few arguments | Pass: no more than three arguments, no flags, no output arguments. Recognize the cost of the parameter record. |
| 4. DRY | Pass. Arithmetic is owned once; narrowly scoped fixtures remove repetition. Some repeated assertions intentionally document unchanged state. |
| 5. Error handling | Appropriate types and mostly useful messages. Null-item diagnostics are the remaining improvement. No broad catches or swallowed failures. |
| 6. Comments | Pass. No redundant comments, dead commented-out code, or prose that restates obvious implementation. |
| 7. Formatting | Consistent. Improve conceptual ordering rather than introducing formatting machinery. |
| 8. Objects and data structures | Order protects behavior and state; immutable records express values. Both forms can coexist. Immutable item exposure is safe. |
| 9. Error boundaries | Standard unchecked exceptions fit this domain. External exception translation remains in future adapters. |
| 10. Unit tests | Fast, deterministic, and behavior-focused; direct exception targets would make ownership clearer. Multiple assertions for one transition are appropriate. |
| 11. Concurrency | No concurrency contract exists. Do not call mutable Order thread-safe; do not add locking without a requirement. |
| 12. Class design | Four small cohesive production types. Focused tests remain useful; do not keep splitting until navigation becomes harder than reading. |

No extra predicate helpers are needed for `quantity <= 0` or `unitPrice.signum() < 0`: these express simple mathematical boundaries. No custom exceptions are needed solely to satisfy a checklist. No interface is needed around Order or OrderItem.

## Verification and current architecture

`mvn -o test` passed on GraalVM Java 21.0.7: 41 tests, zero failures, zero errors, zero skipped. `git diff --check` passed and the working tree remained clean. Passing tests support behavior, not a proof of perfect style or exhaustive correctness.

| Class | Responsibility | Hexagonal placement |
|---|---|---|
| Order | Protects item invariants and lifecycle, calculates total, exposes creation facts | Domain |
| OrderCreation | Validates and holds immutable identity, customer ID, and creation time | Domain |
| OrderItem | Validates quantity/price and calculates subtotal | Domain |
| OrderStatus | Names allowed business states | Domain |

No classes were added or changed during this evaluation. There are no application services, ports, adapters, or bootstrap module yet. The next feature remains the create-order application use case; readability improvements can be completed first as a separate refactor commit.


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
