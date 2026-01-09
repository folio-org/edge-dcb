## 1.3.6 - 2026-01-09

* EDGEDCB-69 - Spring Boot 3.4.13 fixing vulns

## 1.3.5 - 2025-12-10

* EDGEDCB-53 - Populate locationCode for DCB Transaction API
* EDGEDCB-68 - Add request body for `POST /dcb/shadow-locations/refresh`

## v1.3.4 - 2025-12-09

* EDGEDCB-65 - Add new transaction status: EXPIRED

## v1.3.3 - 2025-11-18

* EDGEDCB-61 - Add hold count field to transaction status response and renewal block toggle for virtual items

## v1.3.2 - 2025-10-29

* EDGEDCB-58 - Add localNames field for DCB patron

## v1.3.1 - 2025-06-18

* EDGEDCB-52 - Allow patrons to request items from own library via DCB

## v1.3.0 - 2025-03-14

* EDGEDCB-45 - Added renewalPolicy fields.
* EDGEDCB-41 - Create PUT /transaction/{dcbTransactionId}/renew API to allow the DCB hub to perform the renew operation
* FOLIO-4228 - Java and spring upgrade for Sunflower

## v1.2.2 - 2025-01-17

* EDGEDCB-34 - Implement Put API for re-request

## v1.2.1 - 2024-11-20

* Changes relate to TLS support

## v1.2.0 - 2024-10-31

* EDGEDCB-20 - Implement GET API for transaction updates
* EDGEDCB-21 - Enhance DcbClient TLS Configuration for Secure Connections to OKAPI
* EDGEDCB-22 - Enhance HTTP Endpoint Security with TLS and FIPS-140-2 Compliant Cryptography
* EDGEDCB-25 - Spring Boot 3.2.6, edge-common-spring 2.4.4 fixing vulns
* EDGEDCB-26 - edge-common-spring 2.4.5: AwsParamStore to support FIPS-approved crypto modules
* EDGEDCB-30 - Update pom.xml and interface dependencies for ramsons

## v1.1.0 - 2024-03-21

* EDGEDCB-2-added endoints
* EDGEDCB-3-Refactoring of yaml files and include pickup related objects
* EDGEDCB-4-remove loanType from Item object in edge-dcb module
* EDGEDCB-13 Adding exception handling
* EDGEDCB-12: TC module submission include: ${ACTUATOR_EXPOSURE:health,info,loggers}
* EDGEDCB-17 upgrading dependencies for Quesnelia 
* EDGEDCB-19 Add required permission in readme.md file
* EDGEDCB-16 - Tenant ID test_edge_dcb should not contain underscore
* EDGEDCB-15 - Adding details about test tenant to make karate features run
* EDGEDCB-14 400 error code should be thrown for Invalid UUID
* EDGEDCB-11 Adding new properties required for secure store

## v1.0.0 - Draft
Initial release of `edge-dcb`
