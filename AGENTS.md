# Model API instructions

- Own shared document, sketch, parameter, geometry, mesh and manufacturing values and their validation helpers.
- Keep this module under MIT with no dependency on engine, util or other AGPL application modules.
- Keep native evaluation, persistence codecs, registries and application services in engine.
- Preserve existing `dev.voxelbrew.engine.*` package names and validation;
  those names do not imply an engine implementation dependency.
- Publish `dev.voxelbrew.api:model` with its MIT license in binary and source JARs.

## Standalone repository

- Use Java 21 and the committed Gradle wrapper. Preserve the MIT license and public Java packages.
- Run `./gradlew spotlessApply` (Windows: `.\gradlew.bat spotlessApply`) before every commit, then review changes.
- Run `check` and validate Maven POM dependencies when changing publication configuration.
- Never add application-module dependencies or commit generated output or credentials.

## Development and verification

- Read `README.md` and the build before changing contracts or publication.
- Unit tests live in `src/test/test`; `integrationTest` covers service integration.
  `check` also runs formatting, dependency checks, SpotBugs and release-script
  tests; it does not automatically run the separate `e2eTest` task.
- API versions are independent of the application. Preserve version properties;
  local workspace composite builds substitute sibling API sources. Standalone
  consumers opt into Maven local with `-PuseMavenLocal=true` after publishing
  required dependencies.
- Instruction-only edits need Spotless and diff review. In a dirty checkout,
  scope formatting with `-PspotlessIdeHook=<comma-separated absolute paths>`.
- Remote publication is separate from validation; do not publish or change release
  versions merely to test a change.
