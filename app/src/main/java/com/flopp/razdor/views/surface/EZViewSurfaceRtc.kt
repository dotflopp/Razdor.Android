package com.flopp.razdor.views.surface

import android.content.Context
import com.flopp.razdor.EZApp
import com.flopp.razdor.model.EZModelUser
import org.webrtc.Camera2Enumerator
import org.webrtc.MediaStream
import org.webrtc.PeerConnectionFactory
import org.webrtc.SurfaceTextureHelper
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoSource
import org.webrtc.VideoTrack

class EZViewSurfaceRtc(
    context: Context
): SurfaceViewRenderer(
    context
) {

    private var mTextureHelper: SurfaceTextureHelper? = null

    private var mLocalTrack: VideoTrack? = null
    private var mLocalVideoSource: VideoSource? = null

    private val mVideoCapture = Camera2Enumerator(
        context
    ).run {
        deviceNames.find {
            isFrontFacing(it)
        }?.let {
            createCapturer(
                it,
                null
            )
        }
    }

    init {
        setMirror(false)
        setEnableHardwareScaler(true)
        init(
            EZApp.eglBaseContext,
            null
        )
    }

    fun switchCamera() {
        mVideoCapture?.switchCamera(
            null
        )
    }

    fun setupVideoSource(
        peerConnectionFactory: PeerConnectionFactory
    ) {
        mLocalVideoSource = peerConnectionFactory.createVideoSource(
            false
        )
    }

    fun captureCamera(
        factory: PeerConnectionFactory,
        trackId: String,
        stream: MediaStream
    ) {
        val localVideoSource = mLocalVideoSource
            ?: return

        mTextureHelper = SurfaceTextureHelper.create(
            Thread.currentThread().name,
            EZApp.eglBaseContext
        )

        mVideoCapture?.apply {
            initialize(
                mTextureHelper,
                context,
                localVideoSource.capturerObserver
            )

            startCapture(
                720,
                480,
                20
            )
        }

        mLocalTrack = factory.createVideoTrack(
            "${trackId}_video",
            localVideoSource
        ).apply {
            addSink(
                this@EZViewSurfaceRtc
            )
        }

        stream.addTrack(
            mLocalTrack
        )
    }

    fun dropCamera(
        stream: MediaStream
    ) {
        mVideoCapture?.dispose()
        mLocalTrack?.apply {
            removeSink(
                this@EZViewSurfaceRtc
            )

            clearImage()
            stream.removeTrack(
                mLocalTrack
            )
            dispose()
        }
    }

    override fun release() {
        mVideoCapture?.dispose()
        super.release()
    }

}