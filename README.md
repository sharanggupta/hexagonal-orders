# Orderly

A learning project: moving from controller-service-repository habits to a hexagonal design, one failing test at a time.

> **Work in progress.** The domain is complete for now and the application layer has started. There is no REST API, database or Spring Boot application yet; they are planned and listed below.

The goal is to learn to tell business rules from coordination, ports from adapters, and dependency direction from call direction. The end state is a small order-management core that can run behind REST and PostgreSQL, yet compiles and tests with neither.

## The one rule the build enforces

```
compile-time dependencies   bootstrap → adapters → application → domain → (JDK)
runtime calls               REST adapter → service → OrderRepository port ← JPA adapter implements it
```

Each layer is a Maven module, and a module's POM lists only what it may depend on. An inward import from `domain` to anything else is a compile error, not a code-review comment. At runtime the service calls the repository, but at compile time the repository adapter depends on the core's interface. That reversal is the dependency inversion the exercise is about.

| In controller-service-repository terms | Hexagonal name | Who owns the interface |
|---|---|---|
| Controller | Driving adapter | Outside the core |
| Service | Application service behind an input port | Core |
| Repository interface | Output port, plain Java | Core, not Spring Data |
| JPA repository implementation | Driven adapter | Outside the core |
| Entity with getters and setters | Domain model with behavior | Core |
| Spring configuration | Bootstrap wiring | Outside the core |

## Where it stands

| Layer | Status | What exists |
|---|---|---|
| `domain` | Built | `Order` aggregate, `OrderItem`, `OrderStatus`. Depends on the JDK only. |
| `application` | In progress | `CreateOrderService` turns a request into an order. The `OrderRepository` output port is next. |
| adapters | Planned | In-memory repository, REST controller, JPA / PostgreSQL repository. |
| bootstrap | Planned | Spring Boot wiring and Docker Compose for PostgreSQL. |

The domain enforces: an order needs an ID, a customer reference, a creation time and at least one non-null item; items are copied and exposed read-only; the total uses exact decimal arithmetic; and an order can be cancelled once. It deliberately does not generate IDs, read the clock, remember orders, or know whether a customer exists.

The application layer lives on the `claude/keen-hopper-06uq9f` branch until it is merged.

## How it is built

Test-driven, one small increment per cycle, trunk-based:

1. Name the gap: what works, what is missing, and why this behavior comes next.
2. **Red**: the smallest failing test that demands the behavior.
3. **Green**: the minimum code that passes. A hidden dependency stays hidden until a test can observe it.
4. **Refactor**: behavior-preserving, in its own commit.
5. Run the focused test, then the whole build, before committing.

The commit history reads as the lesson: each `feat` commit adds one rule, each `refactor` commit changes no behavior.

A few decisions are recorded so they are not reopened every step. The service trusts the caller on customer identity. There is no input port or command object until something needs one. `Clock` will be injected next, while the UUID source stays hidden until a test cares what the ID is.

Review notes are in [`docs/refactoring`](docs/refactoring).

## Run it

Requires Java 21 and Maven.

```bash
mvn clean test
```

## Next

1. Creation time comes from an injected `Clock`.
2. The created order is saved through an `OrderRepository` output port, observed with a hand-written test double.
3. Get-order and cancel-order use cases.
4. An in-memory repository adapter, then a REST adapter and bootstrap wiring.
5. A PostgreSQL / JPA adapter, swapped in without touching the core.
