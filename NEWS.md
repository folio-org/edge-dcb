## v2.0.0 -  2026-04-17

### Breaking changes
* Upgrade module to Spring Boot 4.0 and Spring Framework 7.0 ([EDGEDCB-60](https://folio-org.atlassian.net/browse/EDGEDCB-60))

### New APIs versions
* Requires `transactions v1.6` (was `v1.1`)
* Requires `dcb_refresh_shadow_locations v2.0` (new)

### Features
* Add `selfBorrowing` field in DcbTransaction for BORROWING_PICKUP role ([EDGEDCB-52](https://folio-org.atlassian.net/browse/EDGEDCB-52))
* Populate `locationCode` for DCB Transaction API in DcbItem ([EDGEDCB-53](https://folio-org.atlassian.net/browse/EDGEDCB-53))
* Add `localNames` field for DCB patron ([EDGEDCB-58](https://folio-org.atlassian.net/browse/EDGEDCB-58))
* Provide endpoints to toggle borrower role renewals on virtual items ([EDGEDCB-61](https://folio-org.atlassian.net/browse/EDGEDCB-61))
* Handle new status EXPIRED in DCB transaction ([EDGEDCB-65](https://folio-org.atlassian.net/browse/EDGEDCB-65))
* Proxy request to refresh shadow locations ([EDGEDCB-68](https://folio-org.atlassian.net/browse/EDGEDCB-68))

### Tech Debt
* Add Dependabot config, CODEOWNERS and PR template ([EDGEDCB-51](https://folio-org.atlassian.net/browse/EDGEDCB-51))
* Use GitHub Workflows for Maven ([EDGEDCB-71](https://folio-org.atlassian.net/browse/EDGEDCB-71))

### Dependencies
* Bump `spring-boot` from `3.4.3` to `4.0.5`
* Bump `edge-common-spring` from `3.0.0` to `4.0.0`
* Bump `edge-common` from `4.9.0` to `5.1.0`
* Bump `folio-spring-support` from `9.0.0` to `10.0.0`

---

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
