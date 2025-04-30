[![Build Status](https://github.com/bigboxer23/utils/actions/workflows/unit-tests.yml/badge.svg)](https://github.com/bigboxer23/utils/actions/workflows/unit-tests.yml)
[![CodeQL](https://github.com/bigboxer23/utils/actions/workflows/codeql.yml/badge.svg)](https://github.com/bigboxer23/utils/actions/workflows/codeql.yml)

# utils

A collection of Java utility classes used across multiple projects. Includes functionality for:

- File-based persistence (`FilePersistedSet`, `FilePersistentIndex`, etc.)
- Retryable commands with logging (`RetryingCommand`)
- HTTP utilities using OkHttp
- Environment and logging helpers

## Usage

### Maven

```xml
<dependency>
  <groupId>com.bigboxer23</groupId>
  <artifactId>utils</artifactId>
  <version>2.2.4</version>
</dependency>
```

### Retry Command Example

```java
RetryingCommand.builder()
    .identifier("important-task")
    .numberOfRetriesBeforeFailure(3)
    .waitInSeconds(1)
    .buildAndExecute(() -> {
        // logic that might intermittently fail
        return null;
    });
```

### File Persisted Boolean

```java
FilePersistedBoolean persisted = new FilePersistedBoolean("my-flag");
persisted.set(true);
boolean currentValue = persisted.get();
```

### Testing

```
mvn test
```

