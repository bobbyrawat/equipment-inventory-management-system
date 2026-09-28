# Workspace guidance

- Keep application code under `com.example.inventory` and preserve the controller/service/repository/entity layering.
- Use Java 17, Spring Boot, Maven, Spring Data JPA, MySQL, and Lombok as declared in `pom.xml`.
- Keep database credentials externalized through `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`.
- Do not add authentication/JWT, controllers, service implementations, or frontend code unless explicitly requested.
