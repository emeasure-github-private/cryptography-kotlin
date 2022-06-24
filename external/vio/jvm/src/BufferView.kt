package dev.whyoleg.vio

import java.net.http.HttpClient
import java.nio.*

public actual sealed class BufferView {
    public actual abstract var readIndex: Int
    public actual abstract var writeIndex: Int
    public actual abstract val size: Int

    public actual companion object {
        public actual val Empty: BufferView = ByteArrayBufferView(ByteArray(0))
    }
}

public actual class ByteArrayBufferView actual constructor(
    public actual val array: ByteArray,
    public actual val arrayOffset: Int,
    public actual val arraySize: Int,
    public override var readIndex: Int,
    public override var writeIndex: Int
) : BufferView() {
    override val size: Int
        get() = arraySize - arrayOffset
}

public actual class PlatformBufferView(
    public val byteBuffer: ByteBuffer
) : BufferView() {
    override val size: Int
        get() = byteBuffer.capacity()

    override var readIndex: Int
        get() = byteBuffer.position()
        set(value) {
            byteBuffer.position(value)
        }

    override var writeIndex: Int
        get() = byteBuffer.limit()
        set(value) {
            byteBuffer.limit(value)
        }

}

public fun ByteBuffer.view(): PlatformBufferView = PlatformBufferView(this)

public inline fun <T> BufferView.read(
    block: (ByteBuffer) -> T
): T {
    return when (this) {
        is ByteArrayBufferView -> {
            val buffer = ByteBuffer.wrap(
                array,
                arrayOffset + readIndex,
                arraySize - arrayOffset - readIndex
            )
            val result = block(buffer)

            return result
        }

        is PlatformBufferView -> block(byteBuffer)
        else -> TODO()
    }
}

public inline fun <T> BufferView.write(
    block: (ByteBuffer) -> T
): T {
    return when (this) {
        is ByteArrayBufferView -> {
            val buffer = ByteBuffer.wrap(
                array,
                arrayOffset + readIndex,
                arraySize - arrayOffset - readIndex
            )
            val result = block(buffer)

            return result
        }

        is PlatformBufferView -> block(byteBuffer)
        else -> TODO()
    }
}