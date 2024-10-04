if [[ -d ./mvnw ]]; then
  ./mvnw clean compile exec:java
else
  if mvn --version; then
    mvn clean compile exec:java
  else
    cp -r ../server/mvnw ../server/.mvn .
    ./mvnw clean compile exec:java
  fi
fi
