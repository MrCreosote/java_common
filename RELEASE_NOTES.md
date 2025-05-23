# JavaCommon repo

## OVERVIEW

Repo for code shared between Java services.

## VERSION: 0.3.1 (Release 4/21/25)

* Restored the JobState class, which was removed in version 0.1.0 is 2019. It was no longer
  used in this repo, but is still used in `kb_sdk` async Java clients.

## VERSION: 0.3.0 (Release 4/24/24)

* The GetMongoDB class has been removed.
* Classes in the `us.kbase.common.test` package have been moved to `us.kbase.test.common`
  for consistency with other repos.
* 3 test helper classes in `us.kbase.commmon.test` as well as the `MongoController` and
  `ControllerCommon` have been moved to the `java_test_utilities` repo.
* The build system was switched from Make / Ant to Gradle.

## VERSION: 0.2.0 (Release 2/7/24)

* MongoController is now compatible with Mongo versions 2 through 7.
* The ShockController was removed.

## VERSION: 0.1.1 (Release 12/21/22)

* MongoController will retry once if it encounters an IOError when deleting test files.

## VERSION: 0.1.0 (Release 11/12/19)

* JsonServerServlet method dispatch to the Narrative Job Service (via `*_async` and `*_check`)
  methods has been removed.
  * This includes removing the us.kbase.common.service.JobState class.
* JsonServerServlet automatic provenance generation has been removed.
* Java required version is now 1.8.
* JsonServerServlet now has a constructor more suited for embedding the servlet as a library.
* The unused us.kbase.common.service.GwtTransformer class was removed.
* The unused us.kbase.common.utils.JsonTreeGenerator class was removed.
* Deprecated us.kbase.common.mongo.GetMongoDB class.
* Fixed several bugs with token stream processing in JsonTokenStream.java.
* Updated Jackson jars to v2.9.

## VERSION: 0.0.25 (Release 11/15/2018)

* The SDK logger now reads an environment variable, `KB_SDK_LOGGER_TARGET_PACKAGE`,
  that determines what classes are included in logging and how error traces are trimmed.
  This variable must be set to a parent package of the generated SDK code packages or undefined
  behavior may result. It defaults to `us.kbase`.
* JsonServerSyslog.findCaller() is now private.
* It is now permissible to pass null for the `configFileParam` in the JsonServerSyslog
  constructor. In this case, no logging configuration will be loaded from an external file.

## VERSION: 0.0.24 (Release 5/25/2017)

UPDATED FEATURES / MAJOR BUG FIXES:
* The shock controller now accepts and requires an auth url to be used for Globus lookups. This is
  expected to be https://nexus.api.globusonline.org/ (soon to be deprecated) or 
  https://(ci.|next.|appdev.|)kbase.us/services/auth/api/legacy/globus.

## VERSION: 0.0.23 (Released 2/23/2017)

UPDATED FEATURES / MAJOR BUG FIXES:
* Some private methods in JsonServerServlet are made protected (necessary
  for coming switch to auth2 in kb-sdk and njs_wrapper).
* Old code was deleted (custom task queue support, AWE API client).

## VERSION: 0.0.22 (Released 9/15/2016)

UPDATED FEATURES / MAJOR BUG FIXES:
* Fixed a bug that would throw an IllegalStateError when configuration of the
  authentication client in JsonServerServlet failed rather than logging the
  error.

## VERSION: 0.0.21 (Released 8/26/2016)

NEW FEATURES:
* The Java servers now accept an 'auth-service-url-allow-insecure' parameter in
  the deployment configuration file. Set to 'true' if using an http url rather
  than https for the auth service.

## VERSION: 0.0.20 (Released 8/5/2016)

NEW FEATURES:
* None

UPDATED FEATURES / MAJOR BUG FIXES:
* Java servers now instantiate an auth client once on startup rather than
  every call (if an auth url is provided in the config). This prevents an
  extra call to the auth service for every server call.

ANTICIPATED FUTURE DEVELOPMENTS:
* None

## VERSION: 0.0.19 (Released 8/3/2016)

NEW FEATURES:
* None

UPDATED FEATURES / MAJOR BUG FIXES:
* Java clients no longer validate a token before making a call.

ANTICIPATED FUTURE DEVELOPMENTS:
* None

## VERSION: 0.0.18 (Released 7/31/2016)

NEW FEATURES:
* None

UPDATED FEATURES / MAJOR BUG FIXES:
* Updated java servers and clients to the latest auth library.

ANTICIPATED FUTURE DEVELOPMENTS:
* None

## VERSION: 0.0.17 (Released 6/14/2016)

NEW FEATURES:
* None

UPDATED FEATURES / MAJOR BUG FIXES:
* Support for dynamic Java client URL lookup is added to JsonClientCaller

ANTICIPATED FUTURE DEVELOPMENTS:
* None


## VERSION: 0.0.16 (Released 6/13/2016)

NEW FEATURES:
* The JsonServerSyslog class now logs SLF4J messages.

UPDATED FEATURES / MAJOR BUG FIXES:
* Fixed a bug where the server would stop logging after a syslog daemon
  restart.
* removed the UTCDateFormat class (use joda library classes instead).
* removed UTF8Utils, unused.

ANTICIPATED FUTURE DEVELOPMENTS:
* None


## VERSION: 0.0.15 (Released 1/24/2015)

NEW FEATURES:
* None

UPDATED FEATURES / MAJOR BUG FIXES:
* Added workarounds to the ShockController for starting the server without
  human intervention and adding an admin that works on the first startup

ANTICIPATED FUTURE DEVELOPMENTS:
* None


## VERSION: 0.0.14 (Released 1/4/2015)

NEW FEATURES:
* None

UPDATED FEATURES / MAJOR BUG FIXES:

* Made public static methods in JsonServerServlet for getting the
  server configuration, getting the IP address to log, and setting up
  response headers.

ANTICIPATED FUTURE DEVELOPMENTS:
* None


## VERSION: 0.0.13 (Released 12/14/2015)

NEW FEATURES:
* Support for auto-generated provenance was added in JsonServerServlet.

UPDATED FEATURES / MAJOR BUG FIXES:
* None

ANTICIPATED FUTURE DEVELOPMENTS:
* None


## VERSION: 0.0.12 (Released 10/26/2015)

NEW FEATURES:
* N/A

UPDATED FEATURES / MAJOR BUG FIXES:
* Bug was fixed in Java Client Caller related to recent backward-incompatible changes 
  in signatures of public methods used by old java generated JSON RPC clients.

ANTICIPATED FUTURE DEVELOPMENTS:
* None


## VERSION: 0.0.11 (Released 10/21/2015)

NEW FEATURES:
* N/A

UPDATED FEATURES / MAJOR BUG FIXES:
* Java Server Servlet was redesigned in order to support async server calls

ANTICIPATED FUTURE DEVELOPMENTS:
* None


## VERSION: 0.0.10 (Released 4/10/2015)

NEW FEATURES:
* N/A

UPDATED FEATURES / MAJOR BUG FIXES:
* Add method to close all mongo connections to GetMongoDB class.

ANTICIPATED FUTURE DEVELOPMENTS:
* None


## VERSION: 0.0.9 (Released 3/27/2015)

NEW FEATURES:
* N/A

UPDATED FEATURES / MAJOR BUG FIXES:
* Changes was added into makefile in order to be compatible with AWE deployment process.
* Reference to logger was removed from GetMongoDB (possible cause of permgen errors with glassfish).
* Fix for a bug in tuple deserializer related to treating non-array data as null instead of throwing an exception.
* Fix for a bug in JsonServerServlet related to wrapping returned null by array in case of void methods.

ANTICIPATED FUTURE DEVELOPMENTS:
* None


## VERSION: 0.0.8 (Released 11/10/2014)

NEW FEATURES:
* AWE task holder was added.

UPDATED FEATURES / MAJOR BUG FIXES:
* N/A

ANTICIPATED FUTURE DEVELOPMENTS:
* None


## VERSION: 0.0.7 (Released 10/20/2014)

NEW FEATURES:
* JsonClientCaller now allows configuring whether data should be streamed to
  the server or not. Default is off, as many servers do not support this mode.

UPDATED FEATURES / MAJOR BUG FIXES:
* N/A

ANTICIPATED FUTURE DEVELOPMENTS:
* None


## VERSION: 0.0.6 (Released 8/31/2014)

NEW FEATURES:
* The Java server parent class now handles X-Forwarded-For and X-Real-IP
  headers. Add dont_trust_x_ip_headers=true to deploy.cfg to ignore them.

UPDATED FEATURES / MAJOR BUG FIXES:
* N/A

ANTICIPATED FUTURE DEVELOPMENTS:
* None


## VERSION: 0.0.5 (Released 8/13/2014)

NEW FEATURES:
* test controllers for mongo, mysql, shock
* print out RPC error when writing to socket fails

UPDATED FEATURES / MAJOR BUG FIXES:
* N/A

ANTICIPATED FUTURE DEVELOPMENTS:
* None


## VERSION: 0.0.4 (Released 7/18/2014)

NEW FEATURES:
* minor string checking fn

UPDATED FEATURES / MAJOR BUG FIXES:
* N/A

ANTICIPATED FUTURE DEVELOPMENTS:
* None


## VERSION: 0.0.3 (Released 7/7/2014)

NEW FEATURES:
* Client code allows trusting unsigned certs.
* Configurable retries when establishing a connection to MongoDB.

UPDATED FEATURES / MAJOR BUG FIXES:
* N/A

ANTICIPATED FUTURE DEVELOPMENTS:
* None


## VERSION: 0.0.2 (Released 5/16/2014)

NEW FEATURES:
* Common files from the java type generator were moved here
* Added two JSON sorter implementations. Generally either faster than Jackson
  or uses far less memory
* Various compbio utilities
* Add a simple AWE client (experimental)
* Add a task queue system (alpha, may be removed)

UPDATED FEATURES / MAJOR BUG FIXES:
* N/A

ANTICIPATED FUTURE DEVELOPMENTS:
* None


## VERSION: 0.0.1 (Released 2/24/2014)

NEW FEATURES:
* First release.

UPDATED FEATURES / MAJOR BUG FIXES:
* N/A

ANTICIPATED FUTURE DEVELOPMENTS:
* None