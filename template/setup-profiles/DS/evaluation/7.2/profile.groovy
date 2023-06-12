/*
 * Copyright 2018-2021 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
import org.forgerock.opendj.ldap.Dn

ds.config "set-global-configuration-prop",
          "--reset", "unauthenticated-requests-policy"

baseDn = Dn.valueOf("dc=example,dc=com")
ds.addBackendWithDefaultUserIndexes "dsEvaluation", baseDn
ds.addSchemaFiles()

// Enable the Rest2ldap /api HTTP endpoint
ds.config "set-http-endpoint-prop",
        "--endpoint-name", "/api",
        "--set", "enabled:true"

// Enable a custom index for an attribute that holds an OAuth 2.0-style token
ds.config "create-schema-provider",
        "--provider-name", "Custom JSON Query Matching Rule",
        "--type", "json-query-equality-matching-rule",
        "--set", "enabled:true",
        "--set", "case-sensitive-strings:false",
        "--set", "ignore-white-space:true",
        "--set", "matching-rule-name:caseIgnoreOAuth2TokenQueryMatch",
        "--set", "matching-rule-oid:1.3.6.1.4.1.36733.2.1.4.1.1",
        "--set", "indexed-field:access_token",
        "--set", "indexed-field:refresh_token"

// Enable a custom index for a JSON attribute where values with the same "id" are equal
ds.config "create-schema-provider",
        "--provider-name", "Custom JSON Token ID Matching Rule",
        "--type", "json-equality-matching-rule",
        "--set", "enabled:true",
        "--set", "case-sensitive-strings:false",
        "--set", "ignore-white-space:true",
        "--set", "matching-rule-name:caseIgnoreJsonTokenIDMatch",
        "--set", "matching-rule-oid:1.3.6.1.4.1.36733.2.1.4.4.1",
        "--set", "json-keys:id"

ds.addIndex "json", "equality"
ds.addIndex "oauth2Token", "equality"
ds.addIndex "jsonToken", "equality"

ds.importLdifWithSampleEntries baseDn, generatedUsers, "base-entries.ldif"
