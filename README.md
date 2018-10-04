# Okhttp AWS Signer

This is a kotlin library that implements the AWS V4 signing algorithm described [here](https://docs.aws.amazon.com/general/latest/gr/sigv4_signing.html) for the [OkHttp](https://square.github.io/okhttp/) requests. It has no dependency on the Amazon sdks.

## Getting the library 

Currently the easiest way to get the library is by using [JitPack](https://jitpack.io/)

```groovy
dependencies {
    implementation 'com.github.babbel:okhttp-aws-signer:<tag>'
}
```

You can also create a fat JAR by executing the gradle task `shadowJar`. This task comes from the [shadow](https://github.com/johnrengelman/shadow) plugin.

## Usage

You'll need to first setup the signer object:

```kotlin
val signer = OkHttpAwsV4Signer(region, serviceName)
```

`region` is the region you have your services running, i.e., `eu-west-1`.

`serviceName` should be the name of the service your calling, i.e., `execute-api`.

Once you have a request you can easily sign it with:

```kotlin
val newRequest = signer.sign(request, accessKeyId, accessKey)
```

The signing algorithm requires that the `host` and the `x-amz-date` headers be present in the request prior to signing. Please make sure to include them. The `x-amz-date` header must be formatted with the pattern `"yyyyMMdd'T'HHmmss'Z'"`.

## Developing

All changes are welcomed in the form of PRs. Please explain the change in the PR's description. We'll make our best to be swift in getting it through.

The gradle wrapper is not versioned under the repo, but can be easily downloaded by executing the task `wrapper` inside the file `wrapper.gradle`. A typical execution of this looks like:

```bash
gradle wrapper -b wrapper.gradle
```

To run the tests, please execute:

```bash
./gradlew clean test 
```
