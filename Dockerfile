#
# The contents of this file are subject to the terms of the Common Development and
# Distribution License (the License). You may not use this file except in compliance with the
# License.
#
# You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
# specific language governing permission and limitations under the License.
#
# When distributing Covered Software, include this CDDL Header Notice in each file and include
# the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
# Header, with the fields enclosed by brackets [] replaced by your own identifying
# information: "Portions Copyright [year] [name of copyright owner]".
#
# Copyright 2020-2021 ForgeRock AS.
#
FROM gcr.io/forgerock-io/java-11:latest

RUN apt-get update && apt-get install -y bash-completion && rm -rf /var/lib/apt/lists/*
COPY --chown=forgerock:root . /opt/opendj/

USER 11111
WORKDIR /opt/opendj
ENV PATH $PATH:/opt/jdk/bin:/opt/opendj/bin

EXPOSE 4444
EXPOSE 1389
EXPOSE 1636
EXPOSE 8080
EXPOSE 8443
EXPOSE 8989

ENTRYPOINT ["/opt/opendj/docker-entrypoint.sh"]
