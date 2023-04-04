package com.sk.superlock.util


/*
class CornerAnalyzer(private val listener: CornersListener) : ImageAnalysis.Analyzer {

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()    // Rewind the buffer to zero
        val data = ByteArray(remaining())
        get(data)   // Copy the buffer into a byte array
        return data // Return the byte array
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        if (!isOffline) {
            listener()
        }
        imageProxy.close() // important! if it is not closed it will only run once
    }

}*/
