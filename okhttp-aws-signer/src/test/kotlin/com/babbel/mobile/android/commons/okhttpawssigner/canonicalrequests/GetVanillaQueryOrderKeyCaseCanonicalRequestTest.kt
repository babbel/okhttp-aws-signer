package com.babbel.mobile.android.commons.okhttpawssigner.canonicalrequests

import com.babbel.mobile.android.commons.okhttpawssigner.internal.canonicalRequest
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.ResourceHelper
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.headers
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.request
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.url
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class GetVanillaQueryOrderKeyCaseCanonicalRequestTest {
    @Test
    fun `canonical request should order query parameters and apply correct casing`() {
        val request = request {
            url = "http://example.amazonaws.com/?Param2=value2&Param1=value1"

            headers = mapOf(
                "X-Amz-Date" to "20150830T123600Z"
            )

            get()
        }

        val result = request.canonicalRequest()

        assertThat(result).isEqualTo(ResourceHelper.readResource("get-vanilla-query-order-key-case.creq"))
    }
}
