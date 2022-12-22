package dev.whyoleg.cryptography.webcrypto

import dev.whyoleg.cryptography.io.*
import dev.whyoleg.cryptography.operations.*
import dev.whyoleg.cryptography.operations.hash.*
import dev.whyoleg.cryptography.webcrypto.external.*

internal class WebCryptoHasherProvider(
    private val algorithm: String,
) : HasherProvider<CryptographyOperationParameters.Empty>(ENGINE_ID) {
    override fun provideOperation(parameters: CryptographyOperationParameters.Empty): Hasher = WebCryptoHasher(algorithm)
}

internal class WebCryptoHasher(
    private val algorithm: String,
) : Hasher {
    override val digestSize: Int
        get() = TODO("Not yet implemented")

    override suspend fun hash(dataInput: Buffer): Buffer {
        return WebCrypto.subtle.digest(algorithm, dataInput).await().toByteArray()
    }

    override suspend fun hash(dataInput: Buffer, digestOutput: Buffer): Buffer {
        return hash(dataInput).copyInto(digestOutput)
    }

    override fun hashBlocking(dataInput: Buffer): Buffer {
        TODO("Not yet implemented")
    }

    override fun hashBlocking(dataInput: Buffer, digestOutput: Buffer): Buffer {
        TODO("Not yet implemented")
    }

    override fun hashFunction(): HashFunction {
        TODO("Not yet implemented")
    }
}
