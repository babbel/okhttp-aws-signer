package com.babbel.mobile.android.commons.okhttpawssigner.signing

import com.babbel.mobile.android.commons.okhttpawssigner.internal.stringToSign
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.ResourceHelper
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.headers
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.request
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.url
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class GetVanillaQueryUnreservedSigningTest {
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
}
