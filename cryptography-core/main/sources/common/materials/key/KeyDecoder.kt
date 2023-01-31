package dev.whyoleg.cryptography.materials.key

import dev.whyoleg.cryptography.provider.*

@SubclassOptInRequired(CryptographyProviderApi::class)
public interface KeyDecoder<KF : KeyFormat, K : Key> {
    public suspend fun decodeFrom(format: KF, input: ByteArray): K = decodeFromBlocking(format, input)
    public fun decodeFromBlocking(format: KF, input: ByteArray): K
}
