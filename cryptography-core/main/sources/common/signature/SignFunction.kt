package dev.whyoleg.cryptography.signature

import dev.whyoleg.cryptography.*

public interface SignFunction : Closeable {
    public val signatureSize: Int

    public fun update(dataInput: Buffer)

    public fun finish(): Buffer
    public fun finish(signatureOutput: Buffer): Buffer
}