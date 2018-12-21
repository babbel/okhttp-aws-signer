package com.babbel.mobile.android.commons.okhttpawssigner.signing

import com.babbel.mobile.android.commons.okhttpawssigner.OkHttpAwsV4Signer
import com.babbel.mobile.android.commons.okhttpawssigner.internal.stringToSign
import com.babbel.mobile.android.commons.okhttpawssigner.testhelpers.*
import org.assertj.core.api.Assertions
import org.junit.Test

class GetQueryTimeStampSigningTest {
    @Test
    fun `string to sign with query parameters that need encoding`() {
        val request = request {
            url = "https://api.babbel-staging.io/gamma/v1.1.0/en/accounts/6874a00f78d738e0204277ff665df284/learn_languages/SPA/trainer_items/?since=2018-09-15T17:29:28Z&sort=last_reviewed_at&limit=2147483647"

            headers = mapOf(
                    "X-Amz-Date" to "20180915T173136Z",
                    "x-amz-security-token" to "FQoGZXIvYXdzEFsaDMhjCG8itvCbnoKhWCKEArgMEXhYYk3DghvFE3GO7P4f/yyV8WCUchv98M3Bwc6WjG/Y48AAvGSx4DkPZNvsKdC7/ujxOVDtJVa9EKMbduY/DcrHfQvr6LgZCyUZAK6NwEVZ0PnajjmuKrxctsaX9cANgsgGt5W4UAJQP319kjc/JBffwn+88ngTyN5+eH1Yat+bKJDsYlIk+hi1OLRRfU2/QEjjDtgCcCjSt/XMcGBGvgGRHxP8SxfyHg+W+ZaeKDdD2PbnLxjD+zXSHklzQkyjwmIgQjTayWzlLkW/OK2NrSNnFp8bnksNrtlOZZATG+SozDU/FuykFHXPsGyDJS4P9jXU19hYprb/YCItt8NQhSQgKPiC9dwF"
            )

            get()
        }

        val result = OkHttpAwsV4Signer("us-east-1", "service")
                .sign(request, "AKIDEXAMPLE", "wJalrXUtnFEMI/K7MDENG+bPxRfiCYEXAMPLEKEY")

        Assertions.assertThat(result.headers()["Authorization"])
                .isEqualTo(ResourceHelper.readResource("get-query-time-stamp.sreq").lineStartingWith("Authorization"))
    }
}