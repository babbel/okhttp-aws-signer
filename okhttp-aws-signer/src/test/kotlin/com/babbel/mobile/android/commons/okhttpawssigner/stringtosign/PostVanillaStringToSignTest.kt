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

class PostVanillaStringToSignTest {
    @Test
    fun `string to sign should allow vanilla post requests`() {
        val request = request {
            url = "http://example.amazonaws.com/"

            headers = mapOf(
                "X-Amz-Date" to "20150830T123600Z"
            )

            post(RequestBody.create(MediaType.parse("application/json"), ""))
        }

        val result = request.stringToSign("us-east-1", "service")

        assertThat(result).isEqualTo(ResourceHelper.readResource("post-vanilla.sts"))
    }
}
