package com.babbel.mobile.android.commons.okhttpawssigner.canonicalrequests

import com.babbel.mobile.android.commons.okhttpawssigner.internal.canonicalRequest
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.ResourceHelper
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.headers
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.request
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.url
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class GetHeaderHeaderValueOrderCanonicalRequestTest {
    @Test
    fun `canonical request should work with headers with several values`() {
        val request = request {
            url = "http://example.amazonaws.com"

            headers = mapOf(
                "My-Header1" to "value4,value1,value3,value2",
                "X-Amz-Date" to "20150830T123600Z"
            )

            get()
        }

        val result = request.canonicalRequest()

        assertThat(result).isEqualTo(ResourceHelper.readResource("get-header-value-order.creq"))
    }
}
