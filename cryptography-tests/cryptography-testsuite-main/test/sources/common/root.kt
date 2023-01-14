package dev.whyoleg.cryptography.testcase.main

import dev.whyoleg.cryptography.io.*
import dev.whyoleg.cryptography.materials.key.*
import dev.whyoleg.cryptography.test.api.*

suspend fun <KF : KeyFormat> EncodableKey<KF>.encodeToIf(supported: Boolean, format: KF): Buffer? {
    return if (supported) encodeTo(format) else null
}

suspend fun <KF : KeyFormat, K : Key> KeyDecoder<KF, K>.decodeFromIf(supported: Boolean, format: KF, input: Buffer): K? {
    return if (supported) decodeFrom(format, input) else null
}

suspend fun <KF : KeyFormat> EncodableKey<KF>.encodeTo(format: KF?): Buffer? {
    return format?.let { encodeTo(it) }
}

suspend fun <KF : KeyFormat, K : Key> KeyDecoder<KF, K>.decodeFrom(format: KF?, input: Buffer): K? {
    return format?.let { decodeFrom(it, input) }
}

fun ByteArray.assertContentEquals(expected: Buffer) {
    kotlin.test.assertContentEquals(expected, this)
}

fun Boolean.assertTrue() {
    kotlin.test.assertTrue(this)
}

inline fun EncodedKey(block: MutableMap<String, ByteArray>.() -> Unit): KeyData = KeyData(buildMap(block))