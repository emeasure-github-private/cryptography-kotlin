package dev.whyoleg.cryptography.jdk.aes

import dev.whyoleg.cryptography.*
import dev.whyoleg.cryptography.algorithms.symmetric.*
import dev.whyoleg.cryptography.jdk.*
import dev.whyoleg.cryptography.key.*
import javax.crypto.KeyGenerator as JdkKeyGenerator

internal class AesGcmKeyGeneratorProvider(
    private val state: JdkCryptographyState,
) : KeyGeneratorProvider<SymmetricKeyParameters, AES.GCM.Key>(ENGINE_ID) {
    override fun provideOperation(parameters: SymmetricKeyParameters): KeyGenerator<AES.GCM.Key> =
        AesGcmKeyGenerator(state, parameters.size.value.bits)
}

internal class AesGcmKeyGenerator(
    private val state: JdkCryptographyState,
    private val keySizeBits: Int,
) : KeyGenerator<AES.GCM.Key> {
    private val keyGenerator: ThreadLocal<JdkKeyGenerator> = threadLocal {
        state.provider.keyGenerator("AES").apply {
            init(keySizeBits, state.secureRandom)
        }
    }

    override fun generateKeyBlocking(): AES.GCM.Key {
        val key = keyGenerator.get().generateKey()
        return AES.GCM.Key(
            AesGcmCipherProvider(state, key),
            NotSupportedProvider(ENGINE_ID)
        )
    }

    override suspend fun generateKey(): AES.GCM.Key {
        return state.execute { generateKeyBlocking() }
    }
}