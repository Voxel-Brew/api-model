# Model API instructions

- Own shared document, sketch, parameter, geometry, mesh and manufacturing values and their validation helpers.
- Keep this module under MIT with no dependency on engine, util or other AGPL application modules.
- Keep native evaluation, persistence codecs, registries and application services in engine.
- Preserve existing Java package names and value validation during extraction.
- Publish model-api with its MIT license in binary and source JARs.

## Standalone repository

- Use Java 21 and the committed Gradle wrapper. Preserve the MIT license and public Java packages.
- Run `./gradlew spotlessApply` (Windows: `.\gradlew.bat spotlessApply`) before every commit, then review changes.
- Run `check` and validate Maven POM dependencies when changing publication configuration.
- Never add application-module dependencies or commit generated output or credentials.
