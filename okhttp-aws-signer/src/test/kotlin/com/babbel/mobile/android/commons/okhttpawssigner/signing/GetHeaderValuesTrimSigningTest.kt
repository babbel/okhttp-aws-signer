package com.babbel.mobile.android.commons.okhttpawssigner.signing

import com.babbel.mobile.android.commons.okhttpawssigner.OkHttpAwsV4Signer
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.ResourceHelper
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.headers
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.lineStartingWith
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.request
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.url
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class GetHeaderValuesTrimSigningTest {
    @Test
    fun `signing request with headers that should not have multiple spaces`() {
        val request = request {
            url = "http://example.amazonaws.com"

            headers = mapOf(
                "My-Header1" to "value1",
                "My-Header2" to "\"a   b   c\"",
                "X-Amz-Date" to "20150830T123600Z"
            )

            get()
        }

        val result = OkHttpAwsV4Signer("us-east-1", "service")
            .sign(request, "AKIDEXAMPLE", "wJalrXUtnFEMI/K7MDENG+bPxRfiCYEXAMPLEKEY")

        assertThat(result.headers()["Authorization"])
            .isEqualTo(ResourceHelper.readResource("get-header-value-trim.sreq").lineStartingWith("Authorization"))
    }
}
