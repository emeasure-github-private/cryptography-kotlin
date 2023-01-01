package dev.whyoleg.cryptography.apple.algorithms

import dev.whyoleg.cryptography.*
import dev.whyoleg.cryptography.algorithms.*
import dev.whyoleg.cryptography.algorithms.digest.*
import dev.whyoleg.cryptography.algorithms.symmetric.mac.*
import dev.whyoleg.cryptography.apple.*
import dev.whyoleg.cryptography.io.*
import dev.whyoleg.cryptography.materials.key.*
import dev.whyoleg.cryptography.operations.signature.*
import kotlinx.cinterop.*
import platform.CoreCrypto.*

internal class CCHmac(
    private val state: AppleState,
) : HMAC {
    override fun keyDecoder(digest: CryptographyAlgorithmId<Digest>): KeyDecoder<HMAC.Key.Format, HMAC.Key> {
        TODO("Not yet implemented")
    }

    override fun keyGenerator(digest: CryptographyAlgorithmId<Digest>): KeyGenerator<HMAC.Key> {
        val (hmacAlgorithm, keySize) = when (digest) {
            SHA1   -> kCCHmacAlgSHA1 to CC_SHA1_DIGEST_LENGTH
            SHA256 -> kCCHmacAlgSHA256 to CC_SHA256_DIGEST_LENGTH
            SHA384 -> kCCHmacAlgSHA384 to CC_SHA384_DIGEST_LENGTH
            SHA512 -> kCCHmacAlgSHA512 to CC_SHA512_DIGEST_LENGTH
            else   -> throw CryptographyException("Unsupported hash algorithm: $digest")
        }
        return HmacKeyGenerator(state, keySize, hmacAlgorithm)
    }
}

private class HmacKeyGenerator(
    private val state: AppleState,
    private val keySizeBytes: Int,
    private val hmacAlgorithm: CCHmacAlgorithm,
) : KeyGenerator<HMAC.Key> {
    override fun generateKeyBlocking(): HMAC.Key {
        val key = randomBytes(keySizeBytes)
        return object : HMAC.Key {
            private val signature = HmacSignature(state, key, hmacAlgorithm)
            override fun signatureGenerator(): SignatureGenerator = signature
            override fun signatureVerifier(): SignatureVerifier = signature

            override suspend fun encodeTo(format: HMAC.Key.Format): Buffer {
                TODO("Not yet implemented")
            }

            override suspend fun encodeTo(format: HMAC.Key.Format, output: Buffer): Buffer {
                TODO("Not yet implemented")
            }

            override fun encodeToBlocking(format: HMAC.Key.Format): Buffer {
                TODO("Not yet implemented")
            }

            override fun encodeToBlocking(format: HMAC.Key.Format, output: Buffer): Buffer {
                TODO("Not yet implemented")
            }
        }
    }

    override suspend fun generateKey(): HMAC.Key {
        return state.execute { generateKeyBlocking() }
    }
}

private class HmacSignature(
    private val state: AppleState,
    private val key: Buffer,
    private val hmacAlgorithm: CCHmacAlgorithm,
) : SignatureGenerator, SignatureVerifier {
    override val signatureSize: Int
        get() = TODO("Not yet implemented")

    override fun generateSignatureBlocking(dataInput: Buffer): Buffer {
        val macOutput = ByteArray(signatureSize) //TODO: size!!!
        val result = CCHmac(
            algorithm = hmacAlgorithm,
            key = key.refTo(0),
            keyLength = key.size.convert(),
            data = dataInput.refTo(0),
            dataLength = dataInput.size.convert(),
            macOut = macOutput.refTo(0)
        )
        //TODO: check error
        return macOutput
    }

    override fun generateSignatureBlocking(dataInput: Buffer, signatureOutput: Buffer): Buffer {
        TODO("Not yet implemented")
    }

    override fun verifySignatureBlocking(dataInput: Buffer, signatureInput: Buffer): Boolean {
        return generateSignatureBlocking(dataInput).contentEquals(signatureInput)
    }

    override suspend fun generateSignature(dataInput: Buffer): Buffer {
        return state.execute { generateSignatureBlocking(dataInput) }
    }

    override suspend fun generateSignature(dataInput: Buffer, signatureOutput: Buffer): Buffer {
        return state.execute { generateSignatureBlocking(dataInput, signatureOutput) }
    }

    override suspend fun verifySignature(dataInput: Buffer, signatureInput: Buffer): Boolean {
        return state.execute { verifySignatureBlocking(dataInput, signatureInput) }
    }
}
