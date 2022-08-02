package com.babbel.mobile.android.commons.okhttpawssigner

import com.babbel.mobile.android.commons.okhttpawssigner.internal.stringToSign
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.ResourceHelper
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.headers
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.lineStartingWith
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.request
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.url
import okhttp3.MediaType
import okhttp3.RequestBody
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class SigningTest {
    @Test
    fun `signing simple get request and 1 header`() {
        val request = request {
            url = "http://example.amazonaws.com"

            headers = mapOf(
                "My-Header1" to "value4,value1,value3,value2",
                "X-Amz-Date" to "20150830T123600Z"
            )

            get()
        }

        val result = OkHttpAwsV4Signer("us-east-1", "service")
            .sign(request, "AKIDEXAMPLE", "wJalrXUtnFEMI/K7MDENG+bPxRfiCYEXAMPLEKEY")

        assertThat(result.headers()["Authorization"])
            .isEqualTo(ResourceHelper.readResource("get-header-value-order.sreq").lineStartingWith("Authorization"))
    }

    @Test
    fun `signing request should only allow one Authorization header`() {
        val request = request {
            url = "http://example.amazonaws.com"

            headers = mapOf(
                "My-Header1" to "value4,value1,value3,value2",
                "X-Amz-Date" to "20150830T123600Z"
            )

            get()
        }

        val signer = OkHttpAwsV4Signer("us-east-1", "service")
        val result = signer.sign(request, "AKIDEXAMPLE", "wJalrXUtnFEMI/K7MDENG+bPxRfiCYEXAMPLEKEY")
        val secondResult = signer.sign(result, "AKIDEXAMPLE", "wJalrXUtnFEMI/K7MDENG+bPxRfiCYEXAMPLEKEY")

        assertThat(secondResult.headers().toString().lines().filter { it.startsWith("Authorization") }.size)
            .isEqualTo(1)
    }

    @Test
    fun `signing request with headers that should not have multiple spaces`() {
        val request = request {
            url = "http://example.amazonaws.com"

            headers = mapOf(
                "My-Header1" to "value1",
                "My-Header2" to "\"a   b   c\"",
                "X-Amz-Date" to "20150830T123600Z"
            )

            get()
        }

        val result = OkHttpAwsV4Signer("us-east-1", "service")
            .sign(request, "AKIDEXAMPLE", "wJalrXUtnFEMI/K7MDENG+bPxRfiCYEXAMPLEKEY")

        assertThat(result.headers()["Authorization"])
            .isEqualTo(ResourceHelper.readResource("get-header-value-trim.sreq").lineStartingWith("Authorization"))
    }

    @Test
    fun `string to sign with empty query paramters`() {
        val request = request {
            url =
                "https://api.babbel-staging.io/gamma/v1.1.0/en/accounts/6874a00f78d738e0204277ff665df284/learn_languages/SPA/trainer_items/?since=&sort=last_reviewed_at&limit=2147483647"

            headers = mapOf(
                "X-Amz-Date" to "20180915T172928Z",
                "x-amz-security-token" to "FQoGZXIvYXdzEFsaDMhjCG8itvCbnoKhWCKEArgMEXhYYk3DghvFE3GO7P4f/yyV8WCUchv98M3Bwc6WjG/Y48AAvGSx4DkPZNvsKdC7/ujxOVDtJVa9EKMbduY/DcrHfQvr6LgZCyUZAK6NwEVZ0PnajjmuKrxctsaX9cANgsgGt5W4UAJQP319kjc/JBffwn+88ngTyN5+eH1Yat+bKJDsYlIk+hi1OLRRfU2/QEjjDtgCcCjSt/XMcGBGvgGRHxP8SxfyHg+W+ZaeKDdD2PbnLxjD+zXSHklzQkyjwmIgQjTayWzlLkW/OK2NrSNnFp8bnksNrtlOZZATG+SozDU/FuykFHXPsGyDJS4P9jXU19hYprb/YCItt8NQhSQgKPiC9dwF"
            )

            get()
        }

        val result = OkHttpAwsV4Signer("us-east-1", "service")
            .sign(request, "AKIDEXAMPLE", "wJalrXUtnFEMI/K7MDENG+bPxRfiCYEXAMPLEKEY")

        assertThat(result.headers()["Authorization"])
            .isEqualTo(ResourceHelper.readResource("get-query-empty.sreq").lineStartingWith("Authorization"))
    }

    @Test
    fun `string to sign with query parameters that need encoding`() {
        val request = request {
            url =
                "https://api.babbel-staging.io/gamma/v1.1.0/en/accounts/6874a00f78d738e0204277ff665df284/learn_languages/SPA/trainer_items/?since=2018-09-15T17:29:28Z&sort=last_reviewed_at&limit=2147483647"

            headers = mapOf(
                "X-Amz-Date" to "20180915T173136Z",
                "x-amz-security-token" to "FQoGZXIvYXdzEFsaDMhjCG8itvCbnoKhWCKEArgMEXhYYk3DghvFE3GO7P4f/yyV8WCUchv98M3Bwc6WjG/Y48AAvGSx4DkPZNvsKdC7/ujxOVDtJVa9EKMbduY/DcrHfQvr6LgZCyUZAK6NwEVZ0PnajjmuKrxctsaX9cANgsgGt5W4UAJQP319kjc/JBffwn+88ngTyN5+eH1Yat+bKJDsYlIk+hi1OLRRfU2/QEjjDtgCcCjSt/XMcGBGvgGRHxP8SxfyHg+W+ZaeKDdD2PbnLxjD+zXSHklzQkyjwmIgQjTayWzlLkW/OK2NrSNnFp8bnksNrtlOZZATG+SozDU/FuykFHXPsGyDJS4P9jXU19hYprb/YCItt8NQhSQgKPiC9dwF"
            )

            get()
        }

        val result = OkHttpAwsV4Signer("us-east-1", "service")
            .sign(request, "AKIDEXAMPLE", "wJalrXUtnFEMI/K7MDENG+bPxRfiCYEXAMPLEKEY")

        assertThat(result.headers()["Authorization"])
            .isEqualTo(ResourceHelper.readResource("get-query-time-stamp.sreq").lineStartingWith("Authorization"))
    }

    @Test
    fun `signing request with double slashes in the path`() {
        val request = request {
            url = "http://example.amazonaws.com//example//"

            headers = mapOf(
                "X-Amz-Date" to "20150830T123600Z"
            )

            get()
        }

        val result = OkHttpAwsV4Signer("us-east-1", "service")
            .sign(request, "AKIDEXAMPLE", "wJalrXUtnFEMI/K7MDENG+bPxRfiCYEXAMPLEKEY")

        assertThat(result.headers()["Authorization"])
            .isEqualTo(ResourceHelper.readResource("get-slashes.sreq").lineStartingWith("Authorization"))
    }

    @Test
    fun `signing request with double slashes in the beginning of the path`() {
        val request = request {
            url = "http://example.amazonaws.com//"

            headers = mapOf(
                "X-Amz-Date" to "20150830T123600Z"
            )

            get()
        }

        val result = OkHttpAwsV4Signer("us-east-1", "service")
            .sign(request, "AKIDEXAMPLE", "wJalrXUtnFEMI/K7MDENG+bPxRfiCYEXAMPLEKEY")

        assertThat(result.headers()["Authorization"])
            .isEqualTo(ResourceHelper.readResource("get-slash.sreq").lineStartingWith("Authorization"))
    }

    @Test
    fun `signing request with spaces in the path`() {
        val request = request {
            url = "http://example.amazonaws.com/example space/"

            headers = mapOf(
                "X-Amz-Date" to "20150830T123600Z"
            )

            get()
        }

        val result = OkHttpAwsV4Signer("us-east-1", "service")
            .sign(request, "AKIDEXAMPLE", "wJalrXUtnFEMI/K7MDENG+bPxRfiCYEXAMPLEKEY")

        assertThat(result.headers()["Authorization"])
            .isEqualTo(ResourceHelper.readResource("get-space.sreq").lineStartingWith("Authorization"))
    }

    @Test
    fun `signing request with unreserved characters`() {
        val request = request {
            url = "http://example.amazonaws.com/-._~0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

            headers = mapOf(
                "X-Amz-Date" to "20150830T123600Z"
            )

            get()
        }

        val result = OkHttpAwsV4Signer("us-east-1", "service")
            .sign(request, "AKIDEXAMPLE", "wJalrXUtnFEMI/K7MDENG+bPxRfiCYEXAMPLEKEY")

        assertThat(result.headers()["Authorization"])
            .isEqualTo(ResourceHelper.readResource("get-unreserved.sreq").lineStartingWith("Authorization"))
    }

    @Test
    fun `canonical request should accept utf8 characters`() {
        val request = request {
            url = "http://example.amazonaws.com/ሴ"

            headers = mapOf(
                "X-Amz-Date" to "20150830T123600Z"
            )

            get()
        }

        val result = OkHttpAwsV4Signer("us-east-1", "service")
            .sign(request, "AKIDEXAMPLE", "wJalrXUtnFEMI/K7MDENG+bPxRfiCYEXAMPLEKEY")

        assertThat(result.headers()["Authorization"])
            .isEqualTo(ResourceHelper.readResource("get-utf8.sreq").lineStartingWith("Authorization"))
    }

    @Test
    fun `signing should accept requests with simple query parameters`() {
        val request = request {
            url = "http://example.amazonaws.com/?Param1=value1"

            headers = mapOf(
                "X-Amz-Date" to "20150830T123600Z"
            )

            get()
        }

        val result = OkHttpAwsV4Signer("us-east-1", "service")
            .sign(request, "AKIDEXAMPLE", "wJalrXUtnFEMI/K7MDENG+bPxRfiCYEXAMPLEKEY")

        assertThat(result.headers()["Authorization"])
            .isEqualTo(
                ResourceHelper.readResource("get-vanilla-empty-query-key.sreq").lineStartingWith("Authorization")
            )
    }

    @Test
    fun `signing should work with unordered query parameters`() {
        val request = request {
            url = "http://example.amazonaws.com/?Param2=value2&Param1=value1"

            headers = mapOf(
                "X-Amz-Date" to "20150830T123600Z"
            )

            get()
        }

        val result = OkHttpAwsV4Signer("us-east-1", "service")
            .sign(request, "AKIDEXAMPLE", "wJalrXUtnFEMI/K7MDENG+bPxRfiCYEXAMPLEKEY")

        assertThat(result.headers()["Authorization"])
            .isEqualTo(
                ResourceHelper.readResource("get-vanilla-query-order-key-case.sreq").lineStartingWith("Authorization")
            )
    }

    @Test
    fun `signing request with unordered query parameters`() {
        val request = request {
            url = "http://example.amazonaws.com/?Param1=value2&Param1=Value1"

            headers = mapOf(
                "X-Amz-Date" to "20150830T123600Z"
            )

            get()
        }
        val result = OkHttpAwsV4Signer("us-east-1", "service")
            .sign(request, "AKIDEXAMPLE", "wJalrXUtnFEMI/K7MDENG+bPxRfiCYEXAMPLEKEY")

        assertThat(result.headers()["Authorization"])
            .isEqualTo(
                ResourceHelper.readResource("get-vanilla-query-order-key.sreq").lineStartingWith("Authorization")
            )
    }

    @Test
    fun `string to sign should allow unreserved characters in query string`() {
        val request = request {
            url =
                "http://example.amazonaws.com/?-._~0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz=-._~0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

            headers = mapOf(
                "X-Amz-Date" to "20150830T123600Z"
            )

            get()
        }

        val result = request.stringToSign("us-east-1", "service")

        assertThat(result).isEqualTo(ResourceHelper.readResource("get-vanilla-query-unreserved.sts"))
    }

    @Test
    fun `signing request with utf8 characters in query string`() {
        val request = request {
            url = "http://example.amazonaws.com/?ሴ=bar"

            headers = mapOf(
                "X-Amz-Date" to "20150830T123600Z"
            )

            get()
        }

        val result = OkHttpAwsV4Signer("us-east-1", "service")
            .sign(request, "AKIDEXAMPLE", "wJalrXUtnFEMI/K7MDENG+bPxRfiCYEXAMPLEKEY")

        assertThat(result.headers()["Authorization"])
            .isEqualTo(ResourceHelper.readResource("get-vanilla-utf8-query.sreq").lineStartingWith("Authorization"))
    }

    @Test
    fun `string to sign for simple requests`() {
        val request = request {
            url = "http://example.amazonaws.com/"

            headers = mapOf(
                "X-Amz-Date" to "20150830T123600Z"
            )

            get()
        }

        val result = OkHttpAwsV4Signer("us-east-1", "service")
            .sign(request, "AKIDEXAMPLE", "wJalrXUtnFEMI/K7MDENG+bPxRfiCYEXAMPLEKEY")

        assertThat(result.headers()["Authorization"])
            .isEqualTo(ResourceHelper.readResource("get-vanilla.sreq").lineStartingWith("Authorization"))
    }

    @Test
    fun `signing requests with query strings`() {
        val request = request {
            url = "http://example.amazonaws.com/?Param1=value1"

            headers = mapOf(
                "X-Amz-Date" to "20150830T123600Z"
            )

            post(RequestBody.create(MediaType.parse("application/json"), ""))
        }

        val result = OkHttpAwsV4Signer("us-east-1", "service")
            .sign(request, "AKIDEXAMPLE", "wJalrXUtnFEMI/K7MDENG+bPxRfiCYEXAMPLEKEY")

        assertThat(result.headers()["Authorization"])
            .isEqualTo(
                ResourceHelper.readResource("post-vanilla-empty-query-value.sreq").lineStartingWith("Authorization")
            )
    }

    @Test
    fun `canonical request should allow simple post requests`() {
        val request = request {
            url = "http://example.amazonaws.com/"

            headers = mapOf(
                "X-Amz-Date" to "20150830T123600Z"
            )

            post(RequestBody.create(MediaType.parse("application/json"), ""))
        }

        val result = OkHttpAwsV4Signer("us-east-1", "service")
            .sign(request, "AKIDEXAMPLE", "wJalrXUtnFEMI/K7MDENG+bPxRfiCYEXAMPLEKEY")

        assertThat(result.headers()["Authorization"])
            .isEqualTo(ResourceHelper.readResource("post-header-key-case.sreq").lineStartingWith("Authorization"))
    }

    @Test
    fun `signing simple post requests`() {
        val request = request {
            url = "http://example.amazonaws.com/"

            headers = mapOf(
                "My-Header1" to "VALUE1",
                "X-Amz-Date" to "20150830T123600Z"
            )

            post(RequestBody.create(MediaType.parse("application/json"), ""))
        }

        val result = OkHttpAwsV4Signer("us-east-1", "service")
            .sign(request, "AKIDEXAMPLE", "wJalrXUtnFEMI/K7MDENG+bPxRfiCYEXAMPLEKEY")

        assertThat(result.headers()["Authorization"])
            .isEqualTo(ResourceHelper.readResource("post-header-value-case.sreq").lineStartingWith("Authorization"))
    }

    @Test
    fun `signing post requests with query strings`() {
        val request = request {
            url = "http://example.amazonaws.com/"

            headers = mapOf(
                "Content-Type" to "application/x-www-form-urlencoded",
                "X-Amz-Date" to "20150830T123600Z"
            )

            post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "Param1=value1"))
        }

        val result = OkHttpAwsV4Signer("us-east-1", "service")
            .sign(request, "AKIDEXAMPLE", "wJalrXUtnFEMI/K7MDENG+bPxRfiCYEXAMPLEKEY")

        assertThat(result.headers()["Authorization"])
            .isEqualTo(ResourceHelper.readResource("post-x-www-form-urlencoded.sreq").lineStartingWith("Authorization"))
    }

    @Test
    fun `signing vanilla post requests`() {
        val request = request {
            url = "http://example.amazonaws.com/"

            headers = mapOf(
                "X-Amz-Date" to "20150830T123600Z"
            )

            post(RequestBody.create(MediaType.parse("application/json"), ""))
        }

        val result = OkHttpAwsV4Signer("us-east-1", "service")
            .sign(request, "AKIDEXAMPLE", "wJalrXUtnFEMI/K7MDENG+bPxRfiCYEXAMPLEKEY")

        assertThat(result.headers()["Authorization"])
            .isEqualTo(ResourceHelper.readResource("post-vanilla.sreq").lineStartingWith("Authorization"))
    }
}
