package com.babbel.mobile.android.commons.okhttpawssigner.internal

import okhttp3.Headers
import okhttp3.Request
import okio.Buffer
import java.net.URLEncoder
import java.util.Locale

internal const val SIGNING_ALGORITHM = "AWS4-HMAC-SHA256"

/**
 * Sign the request with the aws signature needed
 */
internal fun Request.signed(accessKeyId: String, accessKey: String, region: String, service: String) =
    newBuilder()
        .addHeader("Authorization", awsAuthorizationHeader(accessKeyId, accessKey, region, service))
        .build()!!

internal fun Request.awsAuthorizationHeader(accesKeyId: String, accessKey: String, region: String, service: String) =
    "$SIGNING_ALGORITHM Credential=$accesKeyId/${credentialScope(
        region,
        service
    )}, SignedHeaders=${signedHeaders()}, Signature=${signature(
        accessKey,
        region,
        service
    )}"

/**
 * Calculate the signature according to [this](https://docs.aws.amazon.com/general/latest/gr/sigv4-calculate-signature.html)
 */
internal fun Request.signature(accessKey: String, region: String, service: String) =
    hmacSha256(
        hmacSha256(
            hmacSha256(
                hmacSha256(hmacSha256("AWS4$accessKey", amazonDateHeaderShort()), region),
                service
            ), "aws4_request"
        ),
        stringToSign(region, service)
    ).toHexString()

/**
 * Create a string to sign as described [here](https://docs.aws.amazon.com/general/latest/gr/sigv4-create-string-to-sign.html)
 */
internal fun Request.stringToSign(region: String, service: String) =
    """
    |$SIGNING_ALGORITHM
    |${amazonDateHeader()}
    |${credentialScope(region, service)}
    |${hash(canonicalRequest())}
    """.trimMargin("|")

/**
 * Create a canonical request as described [here](https://docs.aws.amazon.com/general/latest/gr/sigv4-create-canonical-request.html)
 */
internal fun Request.canonicalRequest() =
    """
    |${method()}
    |${canonicalUri()}
    |${canonicalQueryString()}
    |${canonicalHeaders()}
    |
    |${signedHeaders()}
    |${bodyDigest()}
    """.trimMargin("|")

private fun Request.canonicalUri() =
    url().encodedPath().replace(Regex("/+"), "/")

private fun Request.canonicalQueryString() =
    url().queryParameterNames().sorted()
        .takeIf { !it.isEmpty() }
        ?.flatMap { name ->
            url().queryParameterValues(name).sorted()
                .map { value ->
                    Pair(name.rfc3986Encode(), value.rfc3986Encode())
                }
        }
        ?.joinToString("&") { (name, value) ->
            "$name=$value"
        }
        ?: ""

private fun Request.canonicalHeaders() = headers().canonicalHeaders()

private fun Request.signedHeaders() =
    headers().names()
        .map { it.trim().toLowerCase(Locale.ENGLISH) }
        .sorted()
        .joinToString(";")

private fun Request.bodyDigest() =
    hash(bodyAsString()).toLowerCase(Locale.ENGLISH)

/**
 * Get the amazon header with date.
 *
 * @throws NoSuchFieldException When header is not found
 */
private fun Request.amazonDateHeader() =
    header("x-amz-date")
        ?: throw NoSuchFieldException("Request cannot be signed without having the x-amz-date header")

/**
 * Get the amazon header with only date.
 *
 * @throws NoSuchFieldException When header is not found
 */
private fun Request.amazonDateHeaderShort() =
    header("x-amz-date")?.substring(0..7)
        ?: throw NoSuchFieldException("Request cannot be signed without having the x-amz-date header")

/**
 * Read the request body without changing the current one.
 * Returns empty string on empty body.
 */
private fun Request.bodyAsString() =
    body()?.let {
        val buffer = Buffer()
        this.newBuilder().build().body()!!.writeTo(buffer)
        buffer.readUtf8()
    } ?: ""

private fun Headers.canonicalHeaders() =
    names().joinToString("\n") {
        "${it.toLowerCase(Locale.ENGLISH)}:${values(it).trimmedAndJoined()}"
    }

/**
 * Trims the trailing and leading spaces and replaces multiple spaces for only one
 */
private fun String.trimAll() = trim().replace(Regex("\\s+"), " ")

/**
 * Trims all the values and joins them with commas
 */
private fun List<String>.trimmedAndJoined() = joinToString(",") { it.trimAll() }

/**
 * Encode the given string with RFC3986
 */
private fun String.rfc3986Encode() =
    URLEncoder.encode(this, "utf8")
        .replace("+", "%20")
        .replace("*", "%2A")
        .replace("%7E", "~")

private fun Request.credentialScope(region: String, service: String) =
    "${amazonDateHeaderShort()}/$region/$service/aws4_request"
