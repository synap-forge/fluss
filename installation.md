# Run Fluss Locally with Flink 2.3 on JDK 21

This guide uses the Java 21 build produced from this repository and the existing local Flink installation at `/Users/anasiri2/Developments/flink`.

## 1. Set the Java 21 Environment

Run all Fluss and Maven commands with the same JDK as Flink:

```bash
export JAVA_HOME="$HOME/.sdkman/candidates/java/21.0.8-graal"
export PATH="$JAVA_HOME/bin:$PATH"
java -version
```SELECT * FROM pos_transaction_lines;

The reported version must be Java 21.

## 2. Build and Install Fluss

From the repository root:

```bash
mvn -B -ntp clean install -DskipTests
```

The runnable distribution is created at:

```text
fluss-dist/target/fluss-1.0-SNAPSHOT-bin.tgz
```

## 3. Create a Local Runtime Directory

Keep the running service separate from Maven build output:

```bash
mkdir -p "$HOME/Developments/fluss-java21"
tar -xzf fluss-dist/target/fluss-1.0-SNAPSHOT-bin.tgz \
	-C "$HOME/Developments/fluss-java21"
export FLUSS_HOME="$HOME/Developments/fluss-java21/fluss-1.0-SNAPSHOT"
```

## 4. Configure Local Fluss Storage

The bundled configuration uses these local defaults:

```yaml
zookeeper.address: localhost:2181
data.dir: /tmp/fluss-data
remote.data.dir: /tmp/fluss-remote-data
bind.listeners: FLUSS://localhost:9123
tablet-server.id: 0
```

For a clean local start, remove only previous local Fluss data after stopping Fluss:

```bash
rm -rf /tmp/fluss-data /tmp/fluss-remote-data /tmp/zookeeper
```

Do not remove these directories while Fluss is running. Removing `/tmp/zookeeper` clears stale TabletServer registrations from prior local clusters.

## 5. Start Fluss

The local-cluster script starts ZooKeeper, one Coordinator, and one TabletServer:

```bash
export JAVA_HOME="$HOME/.sdkman/candidates/java/21.0.8-graal"
export FLUSS_HOME="$HOME/Developments/fluss-java21/fluss-1.0-SNAPSHOT"
"$FLUSS_HOME/bin/local-cluster.sh" start
```

Verify the processes and inspect logs:

```bash
pgrep -af 'fluss|QuorumPeerMain'
tail -n 100 "$FLUSS_HOME/log"/*
```

The Coordinator listens at `localhost:9123`. The TabletServer uses a random local port to avoid colliding with the Coordinator.

## 6. Verify the Flink Cluster

The local Flink 2.3 REST endpoint is:

```bash
curl -fsS http://localhost:8081/overview
```

Expected output includes `"flink-version":"2.3.0"` and at least one TaskManager.

## 7. Install the Matching Flink Connector

Use only the Flink 2.3 connector from this Java 21 build:

```bash
cp fluss-flink/fluss-flink-2.3/target/fluss-flink-2.3-1.0-SNAPSHOT.jar \
	/Users/anasiri2/Developments/flink/lib/
```

Restart Flink after replacing the connector so its JobManager and TaskManager load the new JAR:

```bash
/Users/anasiri2/Developments/flink/bin/stop-cluster.sh
/Users/anasiri2/Developments/flink/bin/start-cluster.sh
curl -fsS http://localhost:8081/overview
```

## 8. Connect Flink SQL to Fluss

Open the Flink SQL client:

```bash
/Users/anasiri2/Developments/flink/bin/sql-client.sh
```

Create and use a Fluss catalog:

```sql
CREATE CATALOG fluss_catalog WITH (
	'type' = 'fluss',
	'bootstrap.servers' = 'localhost:9123'
);

USE CATALOG fluss_catalog;
CREATE DATABASE IF NOT EXISTS demo;
USE demo;

CREATE TABLE orders (
	order_id BIGINT,
	customer_name STRING,
	amount DECIMAL(10, 2),
	PRIMARY KEY (order_id) NOT ENFORCED
);

INSERT INTO orders VALUES
	(1, 'Ada', 25.00),
	(2, 'Lin', 42.50);

SELECT * FROM orders;
```

## 9. Stop Local Fluss

Stop Fluss before changing its JDK, configuration, connector artifacts, or local data directories:

```bash
"$FLUSS_HOME/bin/local-cluster.sh" stop
```

To stop Flink:

```bash
/Users/anasiri2/Developments/flink/bin/stop-cluster.sh
```
