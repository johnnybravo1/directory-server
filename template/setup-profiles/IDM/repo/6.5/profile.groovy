/*
 * Copyright 2018-2022 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */

def addJsonEqualityMatchingRule(String providerName, String name, String oid, Collection<String> indexedFields) {
    def arguments = [ "create-schema-provider",
                      "--type", "json-query-equality-matching-rule",
                      "--set", "enabled:true",
                      "--set", "case-sensitive-strings:false",
                      "--set", "ignore-white-space:true",
                      "--provider-name", providerName,
                      "--set", "matching-rule-name:" + name,
                      "--set", "matching-rule-oid:" + oid ]
    for (String indexedField in indexedFields) {
        arguments.add("--set")
        arguments.add("indexed-field:" + indexedField)
    }
    ds.config arguments
}

addJsonEqualityMatchingRule "IDM managed/user Json Schema",
                            "caseIgnoreJsonQueryMatchManagedUser",
                            "1.3.6.1.4.1.36733.2.3.4.1",
                            [ "userName", "givenName", "sn", "mail", "accountStatus"]

addJsonEqualityMatchingRule "IDM managed/role Json Schema",
                            "caseIgnoreJsonQueryMatchManagedRole",
                            "1.3.6.1.4.1.36733.2.3.4.2",
                            [ "condition/**", "temporalConstraints/**" ]

addJsonEqualityMatchingRule "IDM Relationship Json Schema",
                            "caseIgnoreJsonQueryMatchRelationship",
                            "1.3.6.1.4.1.36733.2.3.4.3",
                            [ "firstResourceCollection", "firstResourceId", "firstPropertyName",
                              "secondResourceCollection", "secondResourceId", "secondPropertyName" ]

addJsonEqualityMatchingRule "IDM Cluster Object Json Schema",
                            "caseIgnoreJsonQueryMatchClusterObject",
                            "1.3.6.1.4.1.36733.2.3.4.4",
                            [ "timestamp", "state"]

ds.addBackendWithDefaultUserIndexes backendName, "dc=openidm," + domain

ds.addSchemaFiles()

ds.addIndex "fr-idm-managed-user-json", "equality"
ds.addIndex "fr-idm-managed-role-json", "equality"
ds.addIndex "fr-idm-relationship-json", "equality"
ds.addIndex "fr-idm-cluster-json", "equality"
ds.addIndex "fr-idm-json", "equality"
ds.addIndex "fr-idm-link-type", "equality"
ds.addIndex "fr-idm-link-firstid", "equality"
ds.addIndex "fr-idm-link-secondid", "equality"
ds.addIndex "fr-idm-link-firstid-constraint", "equality"
ds.addIndex "fr-idm-link-secondid-constraint", "equality"
ds.addIndex "fr-idm-link-qualifier", "equality"
ds.addIndex "fr-idm-lock-nodeid", "equality"
ds.addIndex "fr-idm-syncqueue-mapping", "equality"
ds.addIndex "fr-idm-syncqueue-resourceid", "equality"
ds.addIndex "fr-idm-syncqueue-state", "equality"
ds.addIndex "fr-idm-syncqueue-remainingretries", "equality"
ds.addIndex "fr-idm-syncqueue-createdate", "equality"

// create vlv index on fr-idm-syncqueue-createdate to support sorting of potentially > 4000 entries
ds.config "create-backend-vlv-index",
        "--index-name", "syncqueue-by-create-date",
        "--backend-name", backendName,
        "--set", "base-dn:ou=queue,ou=sync,dc=openidm," + domain,
        "--set", "sort-order:+fr-idm-syncqueue-createdate",
        "--set", "scope:single-level",
        "--set", "filter:(&)"

ds.config "create-plugin",
        "--set", "enabled:true",
        "--set", "type:fr-idm-link-firstid-constraint",
        "--set", "base-dn:ou=links,dc=openidm," + domain,
        "--type", "unique-attribute",
        "--plugin-name", "FirstId Links Unique Attribute"

ds.config "create-plugin",
        "--set", "enabled:true",
        "--set", "type:fr-idm-link-secondid-constraint",
        "--set", "base-dn:ou=links,dc=openidm," + domain,
        "--type", "unique-attribute",
        "--plugin-name", "SecondId Links Unique Attribute"

ds.importLdifTemplate "base-entries.ldif"
