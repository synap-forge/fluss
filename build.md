JAVA_HOME="$HOME/.sdkman/candidates/java/21.0.8-graal" \
  mvn -B -ntp clean install -DskipTests

export JAVA_HOME="$HOME/.sdkman/candidates/java/21.0.8-graal"
export FLUSS_HOME="$HOME/Developments/fluss-java21/fluss-1.0-SNAPSHOT"

JAVA_HOME="$JAVA_HOME" "$FLUSS_HOME/bin/local-cluster.sh" start