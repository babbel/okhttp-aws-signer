package com.babbel.mobile.android.commons.okhttpawssigner.canonicalrequests

import com.babbel.mobile.android.commons.okhttpawssigner.internal.canonicalRequest
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.ResourceHelper
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.headers
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.request
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.url
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class GetSpaceCanonicalRequestTest {
    @Test
    fun `canonical request should remove double slashes from path everywhere`() {
        val request = request {
            url = "http://example.amazonaws.com/example space/"

            headers = mapOf(
                "X-Amz-Date" to "20150830T123600Z"
            )

            get()
        }

        val result = request.canonicalRequest()

        assertThat(result).isEqualTo(ResourceHelper.readResource("get-space.creq"))
    }
}
