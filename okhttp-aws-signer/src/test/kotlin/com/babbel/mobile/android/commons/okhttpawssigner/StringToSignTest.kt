package com.babbel.mobile.android.commons.okhttpawssigner

import com.babbel.mobile.android.commons.okhttpawssigner.internal.stringToSign
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.ResourceHelper
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.headers
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.request
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.url
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class StringToSignTest {
    @Test
    fun `string to sign with simple get request and 1 header`() {
        val request = request {
            url = "http://example.amazonaws.com"

            headers = mapOf(
                "My-Header1" to "value4,value1,value3,value2",
                "X-Amz-Date" to "20150830T123600Z"
            )

            get()
        }

        val result = request.stringToSign("us-east-1", "service")

        assertThat(result).isEqualTo(ResourceHelper.readResource("get-header-value-order.sts"))
    }

    @Test
    fun `string to sign with headers that should not have multiple spaces`() {
        val request = request {
            url = "http://example.amazonaws.com"

            headers = mapOf(
                "My-Header1" to "value1",
                "My-Header2" to "\"a   b   c\"",
                "X-Amz-Date" to "20150830T123600Z"
            )

            get()
        }

        val result = request.stringToSign("us-east-1", "service")

        assertThat(result).isEqualTo(ResourceHelper.readResource("get-header-value-trim.sts"))
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

        val result = request.stringToSign("us-east-1", "service")

        assertThat(result).isEqualTo(ResourceHelper.readResource("get-query-empty.sts"))
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

        val result = request.stringToSign("us-east-1", "service")

        assertThat(result).isEqualTo(ResourceHelper.readResource("get-query-time-stamp.sts"))
    }

    @Test
    fun `string to sign with double slashes in the path`() {
        val request = request {
            url = "http://example.amazonaws.com//example//"

            headers = mapOf(
                "X-Amz-Date" to "20150830T123600Z"
            )

            get()
        }

        val result = request.stringToSign("us-east-1", "service")

        assertThat(result).isEqualTo(ResourceHelper.readResource("get-slashes.sts"))
    }

    @Test
    fun `string to sign with double slashes in the beginning of the path`() {
        val request = request {
            url = "http://example.amazonaws.com//"

            headers = mapOf(
                "X-Amz-Date" to "20150830T123600Z"
            )

            get()
        }

        val result = request.stringToSign("us-east-1", "service")

        assertThat(result).isEqualTo(ResourceHelper.readResource("get-slash.sts"))
    }

    @Test
    fun `string to sign with spaces in path`() {
        val request = request {
            url = "http://example.amazonaws.com/example space/"

            headers = mapOf(
                "X-Amz-Date" to "20150830T123600Z"
            )

            get()
        }

        val result = request.stringToSign("us-east-1", "service")

        assertThat(result).isEqualTo(ResourceHelper.readResource("get-space.sts"))
    }

    @Test
    fun `string to sign should correctly work with unreserved characters`() {
        val request = request {
            url = "http://example.amazonaws.com/-._~0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

            headers = mapOf(
                "X-Amz-Date" to "20150830T123600Z"
            )

            get()
        }

        val result = request.stringToSign("us-east-1", "service")

        assertThat(result).isEqualTo(ResourceHelper.readResource("get-unreserved.sts"))
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

        val result = request.stringToSign("us-east-1", "service")

        assertThat(result).isEqualTo(ResourceHelper.readResource("get-utf8.sts"))
    }

    @Test
    fun `string to sign should accept requests with simple query parameters`() {
        val request = request {
            url = "http://example.amazonaws.com/?Param1=value1"

            headers = mapOf(
                "X-Amz-Date" to "20150830T123600Z"
            )

            get()
        }

        val result = request.stringToSign("us-east-1", "service")

        assertThat(result).isEqualTo(ResourceHelper.readResource("get-vanilla-empty-query-key.sts"))
    }

    @Test
    fun `string to sign should work with unordered query parameters`() {
        val request = request {
            url = "http://example.amazonaws.com/?Param2=value2&Param1=value1"

            headers = mapOf(
                "X-Amz-Date" to "20150830T123600Z"
            )

            get()
        }

        val result = request.stringToSign("us-east-1", "service")

        assertThat(result).isEqualTo(ResourceHelper.readResource("get-vanilla-query-order-key-case.sts"))
    }

    @Test
    fun `canonical request should order query parameters`() {
        val request = request {
            url = "http://example.amazonaws.com/?Param1=value2&Param1=Value1"

            headers = mapOf(
                "X-Amz-Date" to "20150830T123600Z"
            )

            get()
        }

        val result = request.stringToSign("us-east-1", "service")

        assertThat(result).isEqualTo(ResourceHelper.readResource("get-vanilla-query-order-key.sts"))
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
    fun `string to sign should allow utf8 characters in query string`() {
        val request = request {
            url = "http://example.amazonaws.com/?ሴ=bar"

            headers = mapOf(
                "X-Amz-Date" to "20150830T123600Z"
            )

            get()
        }

        val result = request.stringToSign("us-east-1", "service")

        assertThat(result).isEqualTo(ResourceHelper.readResource("get-vanilla-utf8-query.sts"))
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

        val result = request.stringToSign("us-east-1", "service")

        assertThat(result).isEqualTo(ResourceHelper.readResource("get-vanilla.sts"))
    }

    @Test
    fun `canonical request should allow simple post requests`() {
        val request = request {
            url = "http://example.amazonaws.com/"

            headers = mapOf(
                "X-Amz-Date" to "20150830T123600Z"
            )
            post("".toRequestBody("application/json".toMediaType()))
        }

        val result = request.stringToSign("us-east-1", "service")

        assertThat(result).isEqualTo(ResourceHelper.readResource("post-header-key-case.sts"))
    }

    @Test
    fun `string to sign should allow simple post requests`() {
        val request = request {
            url = "http://example.amazonaws.com/"

            headers = mapOf(
                "My-Header1" to "VALUE1",
                "X-Amz-Date" to "20150830T123600Z"
            )
            post("".toRequestBody("application/json".toMediaType()))
        }

        val result = request.stringToSign("us-east-1", "service")

        assertThat(result).isEqualTo(ResourceHelper.readResource("post-header-value-case.sts"))
    }

    @Test
    fun `string to sign should allow requests with parameters in the content`() {
        val request = request {
            url = "http://example.amazonaws.com/"

            headers = mapOf(
                "Content-Type" to "application/x-www-form-urlencoded",
                "X-Amz-Date" to "20150830T123600Z"
            )
            post("Param1=value1".toRequestBody("application/x-www-form-urlencoded".toMediaType()))
        }

        val result = request.stringToSign("us-east-1", "service")

        assertThat(result).isEqualTo(ResourceHelper.readResource("post-x-www-form-urlencoded.sts"))
    }

    @Test
    fun `string to sign should allow requests with query strings`() {
        val request = request {
            url = "http://example.amazonaws.com/?Param1=value1"

            headers = mapOf(
                "X-Amz-Date" to "20150830T123600Z"
            )
            post("".toRequestBody("application/json".toMediaType()))
        }

        val result = request.stringToSign("us-east-1", "service")

        assertThat(result).isEqualTo(ResourceHelper.readResource("post-vanilla-empty-query-value.sts"))
    }

    @Test
    fun `string to sign should allow vanilla post requests`() {
        val request = request {
            url = "http://example.amazonaws.com/"

            headers = mapOf(
                "X-Amz-Date" to "20150830T123600Z"
            )
            post("".toRequestBody("application/json".toMediaType()))
        }

        val result = request.stringToSign("us-east-1", "service")

        assertThat(result).isEqualTo(ResourceHelper.readResource("post-vanilla.sts"))
    }
}
