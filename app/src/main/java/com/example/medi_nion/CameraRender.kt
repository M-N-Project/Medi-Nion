package com.example.medi_nion

import android.hardware.Camera
import android.os.Build
import android.util.Log
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.CameraGLRendererBase
import org.opencv.android.CameraGLSurfaceView
import java.io.IOException


class CameraRenderer internal constructor(view: CameraGLSurfaceView?) :
    CameraGLRendererBase(view) {
    private var mCamera: Camera? = null
    private var mPreviewStarted = false

    @Synchronized
    override fun closeCamera() {
        Log.i(Companion.LOGTAG, "closeCamera")
        if (mCamera != null) {
            mCamera!!.stopPreview()
            mPreviewStarted = false
            mCamera!!.release()
            mCamera = null
        }
    }

    @Synchronized
    override fun openCamera(id: Int) {
        Log.i(Companion.LOGTAG, "openCamera")
        closeCamera()
        if (id == CameraBridgeViewBase.CAMERA_ID_ANY) {
            Log.d(Companion.LOGTAG, "Trying to open camera with old open()")
            try {
                mCamera = Camera.open()
            } catch (e: Exception) {
                Log.e(
                    Companion.LOGTAG,
                    "Camera is not available (in use or does not exist): " + e.localizedMessage
                )
            }
            if (mCamera == null) {
                var connected = false
                for (camIdx in 0 until Camera.getNumberOfCameras()) {
                    Log.d(
                        Companion.LOGTAG,
                        "Trying to open camera with new open($camIdx)"
                    )
                    try {
                        mCamera = Camera.open(camIdx)
                        connected = true
                    } catch (e: RuntimeException) {
                        Log.e(
                            Companion.LOGTAG,
                            "Camera #" + camIdx + "failed to open: " + e.localizedMessage
                        )
                    }
                    if (connected) break
                }
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                var localCameraIndex = mCameraIndex
                if (mCameraIndex == CameraBridgeViewBase.CAMERA_ID_BACK) {
                    Log.i(Companion.LOGTAG, "Trying to open BACK camera")
                    val cameraInfo = Camera.CameraInfo()
                    for (camIdx in 0 until Camera.getNumberOfCameras()) {
                        Camera.getCameraInfo(camIdx, cameraInfo)
                        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                            localCameraIndex = camIdx
                            break
                        }
                    }
                } else if (mCameraIndex == CameraBridgeViewBase.CAMERA_ID_FRONT) {
                    Log.i(Companion.LOGTAG, "Trying to open FRONT camera")
                    val cameraInfo = Camera.CameraInfo()
                    for (camIdx in 0 until Camera.getNumberOfCameras()) {
                        Camera.getCameraInfo(camIdx, cameraInfo)
                        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                            localCameraIndex = camIdx
                            break
                        }
                    }
                }
                if (localCameraIndex == CameraBridgeViewBase.CAMERA_ID_BACK) {
                    Log.e(Companion.LOGTAG, "Back camera not found!")
                } else if (localCameraIndex == CameraBridgeViewBase.CAMERA_ID_FRONT) {
                    Log.e(Companion.LOGTAG, "Front camera not found!")
                } else {
                    Log.d(
                        Companion.LOGTAG,
                        "Trying to open camera with new open($localCameraIndex)"
                    )
                    try {
                        mCamera = Camera.open(localCameraIndex)
                    } catch (e: RuntimeException) {
                        Log.e(
                            Companion.LOGTAG,
                            "Camera #" + localCameraIndex + "failed to open: " + e.localizedMessage
                        )
                    }
                }
            }
        }
        if (mCamera == null) {
            Log.e(Companion.LOGTAG, "Error: can't open camera")
            return
        }
        val params = mCamera!!.parameters
        val FocusModes = params.supportedFocusModes
        if (FocusModes != null && FocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            params.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO
        }
        mCamera!!.parameters = params
        try {
            mCamera!!.setPreviewTexture(mSTexture)
        } catch (ioe: IOException) {
            Log.e(Companion.LOGTAG, "setPreviewTexture() failed: " + ioe.message)
        }
    }

    @Synchronized
    public override fun setCameraPreviewSize(width: Int, height: Int) {
        var width = width
        var height = height
        Log.i(Companion.LOGTAG, "setCameraPreviewSize: " + width + "x" + height)
        if (mCamera == null) {
            Log.e(Companion.LOGTAG, "Camera isn't initialized!")
            return
        }
        if (mMaxCameraWidth > 0 && mMaxCameraWidth < width) width = mMaxCameraWidth
        if (mMaxCameraHeight > 0 && mMaxCameraHeight < height) height = mMaxCameraHeight
        val param = mCamera!!.parameters
        val psize = param.supportedPreviewSizes
        var bestWidth = 0
        var bestHeight = 0
        if (psize.size > 0) {
            val aspect = width.toFloat() / height
            for (size in psize) {
                val w = size.width
                val h = size.height
                Log.d(Companion.LOGTAG, "checking camera preview size: " + w + "x" + h)
                if (w <= width && h <= height && w >= bestWidth && h >= bestHeight && Math.abs(
                        aspect - w.toFloat() / h
                    ) < 0.2
                ) {
                    bestWidth = w
                    bestHeight = h
                }
            }
            if (bestWidth <= 0 || bestHeight <= 0) {
                bestWidth = psize[0].width
                bestHeight = psize[0].height
                Log.e(
                    Companion.LOGTAG,
                    "Error: best size was not selected, using $bestWidth x $bestHeight"
                )
            } else {
                Log.i(
                    Companion.LOGTAG,
                    "Selected best size: $bestWidth x $bestHeight"
                )
            }
            if (mPreviewStarted) {
                mCamera!!.stopPreview()
                mPreviewStarted = false
            }
            mCameraWidth = bestWidth
            mCameraHeight = bestHeight
            param.setPreviewSize(bestWidth, bestHeight)
        }
        param["orientation"] = "landscape"
        mCamera!!.parameters = param
        mCamera!!.startPreview()
        mPreviewStarted = true
    }

    companion object {
        const val LOGTAG = "CameraRenderer"
    }
}