package com.babbel.mobile.android.commons.okhttpawssigner.stringtosign

import com.babbel.mobile.android.commons.okhttpawssigner.internal.stringToSign
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.ResourceHelper
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.headers
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.request
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.url
import okhttp3.MediaType
import okhttp3.RequestBody
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class PostVanillaEmptyQueryValueStringToSignTest {
    @Test
    fun `string to sign should allow requests with query strings`() {
        val request = request {
            url = "http://example.amazonaws.com/"

            headers = mapOf(
                "Content-Type" to "application/x-www-form-urlencoded",
                "X-Amz-Date" to "20150830T123600Z"
            )

            post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "Param1=value1"))
        }

        val result = request.stringToSign("us-east-1", "service")

        assertThat(result).isEqualTo(ResourceHelper.readResource("post-x-www-form-urlencoded.sts"))
    }
}
