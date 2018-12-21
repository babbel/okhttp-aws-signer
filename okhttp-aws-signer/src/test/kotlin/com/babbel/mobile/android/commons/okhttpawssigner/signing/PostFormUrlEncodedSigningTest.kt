package com.babbel.mobile.android.commons.okhttpawssigner.signing

import com.babbel.mobile.android.commons.okhttpawssigner.OkHttpAwsV4Signer
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.ResourceHelper
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.headers
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.lineStartingWith
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.request
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.url
import okhttp3.MediaType
import okhttp3.RequestBody
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class PostFormUrlEncodedSigningTest {
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
            .isEqualTo(ResourceHelper.readResource("post-vanilla-empty-query-value.sreq").lineStartingWith("Authorization"))
    }
}
