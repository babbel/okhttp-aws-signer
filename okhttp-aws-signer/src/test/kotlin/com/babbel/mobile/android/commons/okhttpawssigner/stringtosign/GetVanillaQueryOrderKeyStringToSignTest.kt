package com.babbel.mobile.android.commons.okhttpawssigner.stringtosign

import com.babbel.mobile.android.commons.okhttpawssigner.internal.stringToSign
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.ResourceHelper
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.headers
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.request
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.url
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class GetVanillaQueryOrderKeyStringToSignTest {
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
}
