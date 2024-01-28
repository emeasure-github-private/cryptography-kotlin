/*
 * Copyright (c) 2023-2024 Oleg Yukhnevich. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.whyoleg.cryptography.providers.webcrypto.algorithms

import dev.whyoleg.cryptography.*
import dev.whyoleg.cryptography.algorithms.asymmetric.*
import dev.whyoleg.cryptography.algorithms.digest.*
import dev.whyoleg.cryptography.bigint.*
import dev.whyoleg.cryptography.materials.key.*
import dev.whyoleg.cryptography.operations.signature.*
import dev.whyoleg.cryptography.providers.webcrypto.internal.*
import dev.whyoleg.cryptography.providers.webcrypto.materials.*
import dev.whyoleg.cryptography.providers.webcrypto.operations.*

internal object WebCryptoRsaPkcs1 : RSA.PKCS1 {
    private val publicKeyFormat: (RSA.PublicKey.Format) -> String = {
        when (it) {
            RSA.PublicKey.Format.DER -> "spki"
            RSA.PublicKey.Format.PEM -> "pem-RSA-spki"
            RSA.PublicKey.Format.JWK -> "jwk"
            RSA.PublicKey.Format.DER_RSA,
            RSA.PublicKey.Format.PEM_RSA,
            -> error("$it format is not supported")
        }
    }
    private val privateKeyFormat: (RSA.PrivateKey.Format) -> String = {
        when (it) {
            RSA.PrivateKey.Format.DER -> "pkcs8"
            RSA.PrivateKey.Format.PEM -> "pem-RSA-pkcs8"
            RSA.PrivateKey.Format.JWK -> "jwk"
            RSA.PrivateKey.Format.DER_RSA,
            RSA.PrivateKey.Format.PEM_RSA,
            -> error("$it format is not supported")
        }
    }
    private val publicKeyWrapper: (CryptoKey) -> RSA.PKCS1.PublicKey = { key ->
        object : RSA.PKCS1.PublicKey, EncodableKey<RSA.PublicKey.Format> by WebCryptoEncodableKey(key, publicKeyFormat) {
            override fun signatureVerifier(): SignatureVerifier = WebCryptoSignatureVerifier(
                algorithm = Algorithm("RSASSA-PKCS1-v1_5"),
                key = key
            )
        }
    }
    private val privateKeyWrapper: (CryptoKey) -> RSA.PKCS1.PrivateKey = { key ->
        object : RSA.PKCS1.PrivateKey, EncodableKey<RSA.PrivateKey.Format> by WebCryptoEncodableKey(key, privateKeyFormat) {
            override fun signatureGenerator(): SignatureGenerator = WebCryptoSignatureGenerator(
                algorithm = Algorithm("RSASSA-PKCS1-v1_5"),
                key = key
            )
        }
    }
    private val keyPairWrapper: (CryptoKeyPair) -> RSA.PKCS1.KeyPair = { keyPair ->
        object : RSA.PKCS1.KeyPair {
            override val publicKey: RSA.PKCS1.PublicKey = publicKeyWrapper(keyPair.publicKey)
            override val privateKey: RSA.PKCS1.PrivateKey = privateKeyWrapper(keyPair.privateKey)
        }
    }

    override fun publicKeyDecoder(digest: CryptographyAlgorithmId<Digest>): KeyDecoder<RSA.PublicKey.Format, RSA.PKCS1.PublicKey> =
        WebCryptoKeyDecoder(
            RsaKeyImportAlgorithm("RSASSA-PKCS1-v1_5", digest.hashAlgorithmName()),
            arrayOf("verify"), publicKeyFormat, publicKeyWrapper
        )

    override fun privateKeyDecoder(digest: CryptographyAlgorithmId<Digest>): KeyDecoder<RSA.PrivateKey.Format, RSA.PKCS1.PrivateKey> =
        WebCryptoKeyDecoder(
            RsaKeyImportAlgorithm("RSASSA-PKCS1-v1_5", digest.hashAlgorithmName()),
            arrayOf("sign"), privateKeyFormat, privateKeyWrapper
        )

    override fun keyPairGenerator(
        keySize: BinarySize,
        digest: CryptographyAlgorithmId<Digest>,
        publicExponent: BigInt,
    ): KeyGenerator<RSA.PKCS1.KeyPair> = WebCryptoAsymmetricKeyGenerator(
        algorithm = RsaKeyGenerationAlgorithm(
            name = "RSASSA-PKCS1-v1_5",
            modulusLength = keySize.inBits,
            publicExponent = publicExponent.encodeToByteArray(),
            hash = digest.hashAlgorithmName()
        ),
        keyUsages = arrayOf("sign", "verify"),
        keyPairWrapper = keyPairWrapper
    )
}
