# edge-dcb

Copyright (C) 2021-2023 The Open Library Foundation

This software is distributed under the terms of the Apache License,
Version 2.0. See the file "[LICENSE](LICENSE)" for more information.

## Introduction
The purpose of this edge API is to bridge the gap between DCB provider and FOLIO.

## Additional information

### API Details
API provides the following URLs for working with dcb :
TBD

### Deployment information

1. Dcb connection should be established from the Dcb edge Folio module. Therefore Dcb edge module needs to know the name of all the tenants, which has Dcb connection. For the ephemeral configuration these names locate in the `ephemeral.properties` (key `tenants`). In order to provide it before the deployment the list of tenant names (e.g. ids) should be put to AWS parameters store (as String). The tenant names list separated by coma (e.g. diku, someothertenantname) should be stored in AWS param store in the variable with key: `DcbClient_tenants` by default or could be provided its own key through `caia_soft_tenants` parameter of starting module.
2. For each tenant using Dcb the corresponding user should be added to the AWS parameter store with key in the following format `{{username}}_{{tenant}}_{{username}}` (where salt and username are the same - `{{username}}`) with value of corresponding `{{password}}` (as Secured String). This user should work as ordinary edge institutional user with the only one difference - his username and salt name are same. By default the value of `{{username}}` is `DcbClient`. It could be changed through `caia_soft_client` parameter of starting module.
3. User with name `{{username}}`, password `{{password}}`, remote-storage.all permissions should be created on FOLIO. After that apikey can be generated for making calls to Edge Dcb API.

#### Rancher and kubernetes deployment
1. Check that mod-dcb has been installed and has been registered to okapi.
2. Create a new user named `DcbClient` in FOLIO. You may also use `diku_admin` for testing and avoid this step.
3. Create a secret in the rancher cluster. Make the key of this secret `ephemeral.properties` and the value similar to `secureStore.type=Ephemeral tenants=diku diku=diku_admin,admin`.
4. Add this secret as a volume mount to the workload for the edge module container. Set the mount point of this volume to `\etc\edge`.
5. Set the `JAVA_OPTIONS` environment variable for the workload to something similar to `-Dsecure_store_props=/etc/edge/ephemeral.properties -Dokapi_url=http://okapi:9130 -Dlog_level=DEBUG -Dcaia_soft_client=diku_admin` . 
6. Redeploy the container. This will make the container aware of the new secret and volume mount.

##### Other rancher considerations
If you are deploying using a FOLIO helm chart, you may want to take adavantage of overriding the chart's yml with answer keys and values to enable the ingress. Here is an example:

| Key | Value |
|---|---|
|ingress.annotations.external-dns\.alpha\.kubernetes\.io/target|f2b6996c-kubesystem-albing-accc-1096161577.us-west-2.elb.amazonaws.com|
|ingress.enabled|true|
|ingress.hosts[0].host|core-platform-edge-orders.ci.folio.org|
|ingress.hosts[0].paths[0]|/|


### Required Permissions
Institutional users should be granted the following permissions in order to use this edge API:
- TBD

### Configuration
Please refer to the [Configuration](https://github.com/folio-org/edge-common/blob/master/README.md#configuration) section in the [edge-common](https://github.com/folio-org/edge-common/blob/master/README.md) documentation to see all available system properties and their default values.

### Issue tracker
See project [EDGEDCB](https://issues.folio.org/browse/EDGEDCB)
at the [FOLIO issue tracker](https://dev.folio.org/guidelines/issue-tracker).

### Other documentation
Other [modules](https://dev.folio.org/source-code/#server-side) are described,
with further FOLIO Developer documentation at
[dev.folio.org](https://dev.folio.org/)
