package com.babbel.mobile.android.commons.okhttpawssigner

import com.babbel.mobile.android.commons.okhttpawssigner.internal.signed
import okhttp3.Request

/**
 * Signer for okhttp that signs the requests with the AWS V4 algorithm
 *
 * More details [here](https://docs.aws.amazon.com/general/latest/gr/sigv4_signing.html)
 */
class OkHttpAwsV4Signer(
    private val region: String,
    private val service: String
) {
    /**
     * Sign the given request with the given credentials.
     *
     * When one of the credentials is null, then no signature is produced
     */
    fun sign(
        request: Request,
        accessKeyId: String?,
        accessKey: String?
    ): Request =
        if (accessKey == null || accessKeyId == null)
            request
        else
            request.signed(accessKeyId, accessKey, region, service)
}
