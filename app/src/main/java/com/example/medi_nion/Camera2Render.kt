package com.example.medi_nion

import android.annotation.TargetApi
import android.content.Context
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.view.Surface
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.CameraGLRendererBase
import org.opencv.android.CameraGLSurfaceView
import java.util.*
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit


class Camera2Renderer internal constructor(view: CameraGLSurfaceView?) :
    CameraGLRendererBase(view) {
    protected val LOGTAG = "Camera2Renderer"
    private var mCameraDevice: CameraDevice? = null
    private var mCaptureSession: CameraCaptureSession? = null
    private var mPreviewRequestBuilder: CaptureRequest.Builder? = null
    private var mCameraID: String? = null
    private var mPreviewSize = Size(-1, -1)
    private var mBackgroundThread: HandlerThread? = null
    private var mBackgroundHandler: Handler? = null
    private val mCameraOpenCloseLock = Semaphore(1)
    override fun doStart() {
        Log.d(LOGTAG, "doStart")
        startBackgroundThread()
        super.doStart()
    }

    override fun doStop() {
        Log.d(LOGTAG, "doStop")
        super.doStop()
        stopBackgroundThread()
    }

    fun cacPreviewSize(width: Int, height: Int): Boolean {
        Log.i(LOGTAG, "cacPreviewSize: " + width + "x" + height)
        if (mCameraID == null) {
            Log.e(LOGTAG, "Camera isn't initialized!")
            return false
        }
        val manager = mView.context
            .getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            val characteristics = manager
                .getCameraCharacteristics(mCameraID!!)
            val map = characteristics
                .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            var bestWidth = 0
            var bestHeight = 0
            val aspect = width.toFloat() / height
            for (psize in map!!.getOutputSizes(
                SurfaceTexture::class.java
            )) {
                val w = psize.width
                val h = psize.height
                Log.d(LOGTAG, "trying size: " + w + "x" + h)
                if (width >= w && height >= h && bestWidth <= w && bestHeight <= h && Math.abs(
                        aspect - w.toFloat() / h
                    ) < 0.2
                ) {
                    bestWidth = w
                    bestHeight = h
                }
            }
            Log.i(LOGTAG, "best size: " + bestWidth + "x" + bestHeight)
            return if (bestWidth == 0 || bestHeight == 0 || mPreviewSize.width == bestWidth &&
                mPreviewSize.height == bestHeight
            ) false else {
                mPreviewSize = Size(bestWidth, bestHeight)
                true
            }
        } catch (e: CameraAccessException) {
            Log.e(LOGTAG, "cacPreviewSize - Camera Access Exception")
        } catch (e: IllegalArgumentException) {
            Log.e(LOGTAG, "cacPreviewSize - Illegal Argument Exception")
        } catch (e: SecurityException) {
            Log.e(LOGTAG, "cacPreviewSize - Security Exception")
        }
        return false
    }

    override fun openCamera(id: Int) {
        Log.i(LOGTAG, "openCamera")
        val manager = mView.context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            val camList = manager.cameraIdList
            if (camList.size == 0) {
                Log.e(LOGTAG, "Error: camera isn't detected.")
                return
            }
            if (id == CameraBridgeViewBase.CAMERA_ID_ANY) {
                mCameraID = camList[0]
            } else {
                for (cameraID in camList) {
                    val characteristics = manager.getCameraCharacteristics(
                        cameraID!!
                    )
                    if (id == CameraBridgeViewBase.CAMERA_ID_BACK &&
                        characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK ||
                        id == CameraBridgeViewBase.CAMERA_ID_FRONT &&
                        characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT
                    ) {
                        mCameraID = cameraID
                        break
                    }
                }
            }
            if (mCameraID != null) {
                if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                    throw RuntimeException(
                        "Time out waiting to lock camera opening."
                    )
                }
                Log.i(LOGTAG, "Opening camera: $mCameraID")
                manager.openCamera(mCameraID!!, mStateCallback, mBackgroundHandler)
            }
        } catch (e: CameraAccessException) {
            Log.e(LOGTAG, "OpenCamera - Camera Access Exception")
        } catch (e: IllegalArgumentException) {
            Log.e(LOGTAG, "OpenCamera - Illegal Argument Exception")
        } catch (e: SecurityException) {
            Log.e(LOGTAG, "OpenCamera - Security Exception")
        } catch (e: InterruptedException) {
            Log.e(LOGTAG, "OpenCamera - Interrupted Exception")
        }
    }

    override fun closeCamera() {
        Log.i(LOGTAG, "closeCamera")
        try {
            mCameraOpenCloseLock.acquire()
            if (null != mCaptureSession) {
                mCaptureSession!!.close()
                mCaptureSession = null
            }
            if (null != mCameraDevice) {
                mCameraDevice!!.close()
                mCameraDevice = null
            }
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera closing.", e)
        } finally {
            mCameraOpenCloseLock.release()
        }
    }

    private val mStateCallback: CameraDevice.StateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(cameraDevice: CameraDevice) {
            mCameraDevice = cameraDevice
            mCameraOpenCloseLock.release()
            createCameraPreviewSession()
        }

        override fun onDisconnected(cameraDevice: CameraDevice) {
            cameraDevice.close()
            mCameraDevice = null
            mCameraOpenCloseLock.release()
        }

        override fun onError(cameraDevice: CameraDevice, error: Int) {
            cameraDevice.close()
            mCameraDevice = null
            mCameraOpenCloseLock.release()
        }
    }

    private fun createCameraPreviewSession() {
        val w = mPreviewSize.width
        val h = mPreviewSize.height
        Log.i(LOGTAG, "createCameraPreviewSession(" + w + "x" + h + ")")
        if (w < 0 || h < 0) return
        try {
            mCameraOpenCloseLock.acquire()
            if (null == mCameraDevice) {
                mCameraOpenCloseLock.release()
                Log.e(LOGTAG, "createCameraPreviewSession: camera isn't opened")
                return
            }
            if (null != mCaptureSession) {
                mCameraOpenCloseLock.release()
                Log.e(LOGTAG, "createCameraPreviewSession: mCaptureSession is already started")
                return
            }
            if (null == mSTexture) {
                mCameraOpenCloseLock.release()
                Log.e(LOGTAG, "createCameraPreviewSession: preview SurfaceTexture is null")
                return
            }
            mSTexture.setDefaultBufferSize(w, h)
            val surface = Surface(mSTexture)
            mPreviewRequestBuilder = mCameraDevice!!
                .createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            mPreviewRequestBuilder!!.addTarget(surface)
            mCameraDevice!!.createCaptureSession(
                Arrays.asList(surface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                        mCaptureSession = cameraCaptureSession
                        try {
                            mPreviewRequestBuilder!!.set(
                                CaptureRequest.CONTROL_AF_MODE,
                                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                            )
                            mPreviewRequestBuilder!!.set(
                                CaptureRequest.CONTROL_AE_MODE,
                                CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH
                            )
                            mCaptureSession!!.setRepeatingRequest(
                                mPreviewRequestBuilder!!.build(),
                                null,
                                mBackgroundHandler
                            )
                            Log.i(LOGTAG, "CameraPreviewSession has been started")
                        } catch (e: CameraAccessException) {
                            Log.e(LOGTAG, "createCaptureSession failed")
                        }
                        mCameraOpenCloseLock.release()
                    }

                    override fun onConfigureFailed(
                        cameraCaptureSession: CameraCaptureSession
                    ) {
                        Log.e(LOGTAG, "createCameraPreviewSession failed")
                        mCameraOpenCloseLock.release()
                    }
                }, mBackgroundHandler
            )
        } catch (e: CameraAccessException) {
            Log.e(LOGTAG, "createCameraPreviewSession")
        } catch (e: InterruptedException) {
            throw RuntimeException(
                "Interrupted while createCameraPreviewSession", e
            )
        } finally {
            //mCameraOpenCloseLock.release();
        }
    }

    private fun startBackgroundThread() {
        Log.i(LOGTAG, "startBackgroundThread")
        stopBackgroundThread()
        mBackgroundThread = HandlerThread("CameraBackground")
        mBackgroundThread!!.start()
        mBackgroundHandler = Handler(mBackgroundThread!!.looper)
    }

    private fun stopBackgroundThread() {
        Log.i(LOGTAG, "stopBackgroundThread")
        if (mBackgroundThread == null) return
        mBackgroundThread!!.quitSafely()
        try {
            mBackgroundThread!!.join()
            mBackgroundThread = null
            mBackgroundHandler = null
        } catch (e: InterruptedException) {
            Log.e(LOGTAG, "stopBackgroundThread")
        }
    }

    override fun setCameraPreviewSize(width: Int, height: Int) {
        var width = width
        var height = height
        Log.i(LOGTAG, "setCameraPreviewSize(" + width + "x" + height + ")")
        if (mMaxCameraWidth > 0 && mMaxCameraWidth < width) width = mMaxCameraWidth
        if (mMaxCameraHeight > 0 && mMaxCameraHeight < height) height = mMaxCameraHeight
        try {
            mCameraOpenCloseLock.acquire()
            val needReconfig = cacPreviewSize(width, height)
            mCameraWidth = mPreviewSize.width
            mCameraHeight = mPreviewSize.height
            if (!needReconfig) {
                mCameraOpenCloseLock.release()
                return
            }
            if (null != mCaptureSession) {
                Log.d(LOGTAG, "closing existing previewSession")
                mCaptureSession!!.close()
                mCaptureSession = null
            }
            mCameraOpenCloseLock.release()
            createCameraPreviewSession()
        } catch (e: InterruptedException) {
            mCameraOpenCloseLock.release()
            throw RuntimeException("Interrupted while setCameraPreviewSize.", e)
        }
    }
}