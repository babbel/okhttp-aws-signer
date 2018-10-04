package com.babbel.mobile.android.commons.okhttpawssigner.signing

import com.babbel.mobile.android.commons.okhttpawssigner.OkHttpAwsV4Signer
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.ResourceHelper
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.headers
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.lineStartingWith
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.request
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.url
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class GetVanillaEmptyQueryKeySigningTest {
    @Test
    fun `signing should accept requests with simple query parameters`() {
        val request = request {
            url = "http://example.amazonaws.com/?Param1=value1"

            headers = mapOf(
                "X-Amz-Date" to "20150830T123600Z"
            )

            get()
        }

        val result = OkHttpAwsV4Signer("us-east-1", "service")
            .sign(request, "AKIDEXAMPLE", "wJalrXUtnFEMI/K7MDENG+bPxRfiCYEXAMPLEKEY")

        assertThat(result.headers()["Authorization"])
            .isEqualTo(ResourceHelper.readResource("get-vanilla-empty-query-key.sreq").lineStartingWith("Authorization"))
    }
}
