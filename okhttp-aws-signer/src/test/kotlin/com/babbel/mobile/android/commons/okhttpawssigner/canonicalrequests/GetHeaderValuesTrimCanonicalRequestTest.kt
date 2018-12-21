package com.babbel.mobile.android.commons.okhttpawssigner.canonicalrequests

import com.babbel.mobile.android.commons.okhttpawssigner.internal.canonicalRequest
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.ResourceHelper
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.headers
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.request
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.url
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class GetHeaderValuesTrimCanonicalRequestTest {
    @Test
    fun `canonical request should remove multiple spaces in headers`() {
        val request = request {
            url = "http://example.amazonaws.com"

            headers = mapOf(
                "My-Header1" to "value1",
                "My-Header2" to "\"a   b   c\"",
                "X-Amz-Date" to "20150830T123600Z"
            )

            get()
        }

        val result = request.canonicalRequest()

        assertThat(result).isEqualTo(ResourceHelper.readResource("get-header-value-trim.creq"))
    }
}
