#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# Base image for all Fluss quickstart scenarios. The Flink distribution is prepared from a
# Java 25 build by prepare_build.sh so the image does not depend on a stock Java 17 Flink image.
FROM eclipse-temurin:25-jre-noble

RUN set -ex; \
  apt-get update; \
  apt-get -y install bash tini; \
  rm -rf /var/lib/apt/lists/*

ENV FLINK_HOME=/opt/flink
ENV PATH=$FLINK_HOME/bin:$PATH

# The init scripts copy optional lakehouse jars into /opt/flink/lib at runtime,
# so keep the quickstart container running as root like the previous image.
USER root

# Copy the Java 25 Flink distribution prepared by prepare_build.sh.
COPY flink/ /opt/flink/

# Put the SQL helper into a dedicated directory. The helper is used by the
# "Real-Time Analytics with Flink" quickstart to pre-load demo source tables.
COPY bin/ /opt/sql-client/
COPY sql/ /opt/sql-client/sql/

# Keep the base Fluss quickstart dependencies in /opt/flink/lib so the standard
# Flink quickstart works out of the box with no extra container setup.
COPY lib/ /opt/flink/lib/

# Keep optional lakehouse dependencies out of /opt/flink/lib by default.
# The init_paimon.sh / init_iceberg.sh scripts activate them explicitly when a
# Paimon or Iceberg quickstart container starts.
COPY paimon/ /opt/flink/paimon/
COPY iceberg/ /opt/flink/iceberg/
COPY opt/ /opt/flink/opt/
COPY bin/init_paimon.sh /opt/flink/init_paimon.sh
COPY bin/init_iceberg.sh /opt/flink/init_iceberg.sh
COPY bin/docker-entrypoint.sh /docker-entrypoint.sh

WORKDIR /opt/sql-client
ENV SQL_CLIENT_HOME=/opt/sql-client

RUN chmod +x \
    /opt/flink/bin/*.sh \
    /opt/sql-client/sql-client \
    /opt/flink/init_paimon.sh \
    /opt/flink/init_iceberg.sh \
    /docker-entrypoint.sh

ENTRYPOINT ["/usr/bin/tini", "--", "/docker-entrypoint.sh"]
