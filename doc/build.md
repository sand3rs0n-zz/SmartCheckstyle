# Developer Document

## Build & Run

1. How to generate build file:
   (gradle installation && download dependencies && generate build file).
   ```bash
    sudo apt install gradle 
    ./gradlew build
    gradle genJar
   ```

2. Help (to list all arguements availables)
   ```bash
    java -jar build/libs/SmartCheckstyle-all-1.0-SNAPSHOT.jar -h
   ```

3. Output should be find *.java files in the input directory and prints out the errors sorted by package, file name, and line number.

## Test

Developer can runs all tests found from the src/test/java directory by using the following command at command prompt:

```bash 
gradle clean test
```