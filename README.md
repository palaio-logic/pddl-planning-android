# PDDL Planning for Android

![](https://img.shields.io/maven-central/v/eu.palaio/pddl-planning "latest version on Maven Central")
![](https://img.shields.io/github/workflow/status/palaio-logic/pddl-planning-android/Android%20CI "master build state")

This project provides a library to interface third-party PDDL planners on Android.

It includes utilities
[to parse and manipulate PDDL strings](pddl-planning/src/main/java/eu/palaio/pddlplanning/PDDL.kt)
and
[represent PDDL entities in Kotlin](pddl-planning/src/main/java/eu/palaio/pddlplanning/BaseOntology.kt).

It provides various helpers to perform planning around the
[`PlanSearchFunction` interface](pddl-planning/src/main/java/eu/palaio/pddlplanning/Planning.kt)
that abstracts away the planner implementation.

It also provides a
[service interface](pddl-planning/src/main/java/eu/palaio/pddlplanning/PDDLPlannerServiceClient.kt)
to allow third-party implementations to be deployed as stand-alone applications.
It relies this [AIDL definition](pddl-planning/src/main/aidl/eu/palaio/pddlplanning/IPDDLPlannerService.aidl).

## Usage

This library is publicly available on Maven Central, but **not** in JCenter.
The `build.gradle` at the root of your project should mention:

```groovy
allprojects {
    repositories {
        mavenCentral()
    }
}
```

Then add the following dependency in your module's `build.gradle`:

```groovy
implementation 'eu.palaio.pddl:pddl-planning:1.5.0'
```

The `PlanSearchFunction` should be provided by another library implementing a planner,
by a service or locally within the client's code.

See the [example tests](pddl-planning-test/src/androidTest/java/eu/palaio/pddlplanning/test/ExamplePlanningInstrumentedTest.kt).
It shows how to build a domain and a problem,
and pass it to a mocked `PlanSearchFunction`.

## Usage around a service

The service must implement the [AIDL interface](pddl-planning/src/main/aidl/eu/palaio/pddlplanning/IPDDLPlannerService.aidl)
and declare the permission `eu.palaio.pddlplanning.permission.SEARCH_PLANS` in the manifest.

```xml
<permission
    android:name="eu.palaio.pddlplanning.permission.SEARCH_PLANS"
    android:protectionLevel="normal" />
```

> A custom permission name can be used.
> Make sure to use specify it properly on the client side too.

Then require that permission to bind to the service,
by adding this attribute to the `service` declaration in the manifest:
```xml
android:permission="eu.palaio.pddlplanning.permission.SEARCH_PLANS"
```

We recommend the service should respond to the intent
`eu.palaio.pddlplanning.action.SEARCH_PLANS_FROM_PDDL`.
In the manifest, it means adding this to the `service` declaration:

```xml
<intent-filter>
    <action android:name="eu.palaio.pddlplanning.action.SEARCH_PLANS_FROM_PDDL" />
    <category android:name="android.intent.category.DEFAULT" />
</intent-filter>
```

See the [service app example](service-app-example/src/main/java/eu/palaio/pddlplanning/example/service/ExamplePDDLPlannerService.kt).

The client should then use
[`createPlanSearchFunctionFromService`](pddl-planning/src/main/java/eu/palaio/pddlplanning/PDDLPlannerServiceClient.kt)
to get a `PlanSearchFunction` that binds the service and calls it remotely.

On Android 30 or higher,
the client manifest should also declare its need to communicate with the service by adding
the following in the `manifest` node:

```xml
<!-- To be allowed to bind the planner service -->
<uses-permission android:name="eu.palaio.pddlplanning.permission.SEARCH_PLANS" />

<!-- To see the planner service -->
<queries>
    <package android:name="com.commonsware.android.r.embed.server" />
    <intent>
        <action android:name="eu.palaio.pddlplanning.action.SEARCH_PLANS_FROM_PDDL" />
    </intent>
    <package android:name="eu.palaio.pddlplanning.example.service" />
</queries>
```

See the [client app example](client-app-example/src/main/java/eu/palaio/pddlplanning/example/client/ExampleClientActivity.kt).

## Testing

This project also provides
[helpers to check expected plans for given PDDL problems](pddl-planning-test/src/main/java/eu/palaio/pddlplanning/test/PlanningTestUtil.kt).
It also includes some PDDL tests that you can run with a third-party planner.
Add the following test dependency in your module's `build.gradle`:

```groovy
androidTestImplementation 'eu.palaio.pddl:pddl-planning-test:1.5.0'
```

Then, let a test extend the interface
[`PlanningInstrumentedTest`](pddl-planning-test/src/main/java/eu/palaio/pddlplanning/test/PlanningInstrumentedTest.kt),
to provide the included test units.

See the [example tests](pddl-planning-test/src/androidTest/java/eu/palaio/pddlplanning/test/ExamplePlanningInstrumentedTest.kt).

You can also get only the non-instrumented tests by defining the following dependency:

```groovy
testImplementation 'eu.palaio.pddl:pddl-planning-test:1.5.0'
```

Then, let a test extend the interface
[`PlanningTestUtil`](pddl-planning-test/src/main/java/eu/palaio/pddlplanning/test/PlanningTestUtil.kt),
to provide the included test units.
Note that there are much fewer tests with this interface.

## License

This project is distributed under the [BSD-3 license](LICENSE).
