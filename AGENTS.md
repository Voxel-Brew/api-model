# Repository Guidelines

## Project Structure & Module Organization

This repository publishes the Java 21 `dev.voxelbrew.api:model` library. Production contracts live under `src/main/java/dev/voxelbrew/api/model`, grouped by domain (`model`, `printing`, `geometry`, `mesh`, `parameters`, and related packages). Version metadata is in `src/main/resources`. Unit tests use the repository-specific path `src/test/test/java`; optional integration and end-to-end suites belong in `src/test/integrationTest/java` and `src/test/e2eTest/java`. Gradle configuration is split between `build.gradle.kts` and reusable scripts in `gradle/`; static-analysis configuration lives in `config/quality`.

## Build, Test, and Development Commands

- `./gradlew check` runs unit and integration tests, formatting checks, SpotBugs, and API dependency validation. This is the main pre-PR command.
- `./gradlew test` runs the JUnit unit suite only.
- `./gradlew integrationTest` or `./gradlew e2eTest` runs a specific extended suite.
- `./gradlew spotlessApply` formats Java, Kotlin Gradle scripts, Markdown, YAML, and text files.
- `./gradlew build` compiles Java 21 sources and creates binary, source, and Javadoc JARs.
- `./gradlew publishToMavenLocal -PuseMavenLocal=true` publishes a local development artifact. Publish the BOM first when using unpublished BOM versions.

Use the committed wrapper (`gradlew.bat` on Windows); do not depend on a system Gradle installation.

Run `./gradlew checkBinaryCompatibility` to compare the built JAR with the latest stable Maven
Central release in the same major at or below `projectVersion` (ignoring `-SNAPSHOT`). The
`check` task includes this comparison on pull requests and pushes to master and release branches.

## Coding Style & Naming Conventions

Spotless enforces Palantir Java Format and ktfmt; run it instead of hand-aligning code. Java compilation enables all lint warnings and treats warnings as errors. Use four-space Java indentation, `UpperCamelCase` types, `lowerCamelCase` members, and package names beneath `dev.voxelbrew.api.model`. Keep public API values immutable where practical, following the existing records and defensive collection copies. Add concise Javadoc when a public contract or invariant is not self-evident.

Run `./gradlew checkJavaMemberOrder` to check Java source declaration order. The `check` and
`build` tasks include it. Within each class, start with static final fields, then other static fields,
final instance fields, other instance fields, static nested types, inner classes, static methods,
constructors, and instance methods. Preserve Java initialization order when moving fields.

## Testing Guidelines

Tests use JUnit 5. Name test classes `*Test` and methods as behavior statements, such as `documentIdentifiersRemainMonotonicAndRejectOverflow`. Cover valid behavior and boundary/error cases. No numeric coverage threshold is configured; every public contract change should include focused regression tests.

## Pre-commit SpotBugs

The build resolves VoxelBrew artifacts through Maven and fetches shared quality policy
from the public BOM master branch.

Before committing, run `./gradlew spotbugsMain` from this API repository or
`./gradlew :model:spotbugsMain` from the composite workspace.
Use `.\gradlew.bat` in place of `./gradlew` on Windows.

## Commit & Pull Request Guidelines

Prefer **Rebase and merge** for pull requests. Use **Squash and merge** only as an exception.
The **Create a merge commit** option is blocked.

History favors short, imperative subjects (`Use BOM versions...`) and occasionally scoped Conventional Commit prefixes (`build:`, `ci:`, `docs:`, `feat(printing):`). Keep each commit focused. PRs should explain the API impact, identify BOM or version changes, link relevant issues, and report `./gradlew check` results. Avoid unrelated generated files, credentials, or build output. Treat published coordinates and versions as immutable.

## Security & Configuration

Never commit package tokens or signing keys. Supply GitHub Packages and Maven Central credentials through documented environment variables or private Gradle properties. Release workflow changes require particular care because tags and published artifacts cannot safely be replaced.

## Lombok in Java code

Use Lombok where it removes routine Java boilerplate without obscuring a public contract. Apply these annotations in Java-bearing repositories and modules; the website has no Java production code.

- `@Getter` / `@Setter`: Generate straightforward accessors for fields that should be exposed or mutable. Prefer field-level annotations when only some fields qualify; retain custom accessors that enforce invariants or make defensive copies.
- `@RequiredArgsConstructor` / `@NoArgsConstructor`: Generate constructors for required final or `@NonNull` fields, or a no-argument constructor when a framework or serializer needs one. Keep explicit constructors when they validate, transform, or document special initialization behavior.
- `@NonNull`: Mark required fields, parameters, and other supported elements to express and enforce non-null inputs. Replace equivalent `Objects.requireNonNull` checks in those places; keep explicit checks when they provide distinct behavior or messages.
- `@ToString`: Generate useful diagnostic string representations for value or state classes. Exclude secrets, large graphs, and recursive relationships.
- `@Data`: Use for mutable data holders that genuinely need getters, setters, equality, hashing, and string representation together. Prefer narrower annotations when only some behavior is appropriate.
- `@Value`: Use for immutable value types when its generated constructor, accessors, equality, hashing, and string representation fit the contract. Preserve defensive copies for mutable components.
- `@EqualsAndHashCode`: Generate value equality for classes whose identity is defined by selected fields. Choose included fields and superclass handling deliberately; avoid mutable fields that would break hash collections.
- `@Slf4j`: Add a logger to classes that emit operational diagnostics. Use parameterized logging and keep credentials and other sensitive data out of logs.
- `@Builder`: Use for objects with many optional or named construction inputs when it improves call-site clarity. Keep required-field validation and invariants explicit.
- `@UtilityClass`: Use for stateless collections of static helpers that must not be instantiated. Do not use it for classes that may need instances, inheritance, or dependency injection.

## AssertJ in Java tests

Use AssertJ (org.assertj:assertj-core) for assertions in Java unit, integration, and end-to-end tests. Keep JUnit Jupiter for test lifecycle, parameterization, and execution. Declare AssertJ without a version in test dependencies; the BOM manages its version. Prefer ssertThat, ssertThatThrownBy, and ssertThatCode with descriptive failure messages and appropriate numeric tolerances. Do not add new JUnit Assertions calls.

## Service injection

Resolve services by their interfaces at the wiring point and pass them to consumers through
constructors. Provider classes must not expose static service getters or downcast discovered
services to their implementations.

## Release workflows

Dispatch **Prepare release** on `master` to cut `release/A.B.x` and open the
next-minor snapshot pull request. Merge that PR through normal branch protection.
Dispatch `release.yml` on the release branch, first with the default dry run,
then with the dry run disabled to sign, reserve annotated tag `vA.B.C`, publish
to Maven Central, create the GitHub release and advance the patch snapshot.

Release-branch pushes and completed publications run `release-merge-back.yml`.
It proposes release fixes in a PR against master, retaining master's
`projectVersion`. Resolve other conflicts through normal review. To retry,
dispatch merge-back on master with the release branch name.

Allow GitHub Actions to create pull requests, or configure `RELEASE_PR_TOKEN`
with contents and pull-request write access for the automation branches. Use a
dedicated token when branch protection requires CI triggered by PR updates.
