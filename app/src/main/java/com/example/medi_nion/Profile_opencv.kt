package com.example.medi_nion

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.googlecode.tesseract.android.TessBaseAPI
import kotlinx.android.synthetic.main.profile_opencv.*
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.io.*
import kotlin.math.max
import kotlin.math.sqrt

class Profile_opencv: AppCompatActivity() {
    lateinit var cameraPermission: ActivityResultLauncher<String>
    lateinit var storagePermission: ActivityResultLauncher<String>
    lateinit var cameraLauncher: ActivityResultLauncher<Uri>

    var photoUri: Uri? = null
    var image : String = "null"
    lateinit var bitmap: Bitmap

    lateinit var tess: TessBaseAPI //Tesseract API 객체 생성
    var dataPath: String = "" //데이터 경로 변수 선언

    init{
        OpenCVLoader.initDebug()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_opencv)

        OpenCVLoader.initDebug()

        var id = intent.getStringExtra("id")
        var userType = intent.getStringExtra("userType")
        var userDept = intent.getStringExtra("userDept")
        var userGrade = intent.getStringExtra("userGrade")
        var nickname = intent.getStringExtra("nickname")
        var button_opencv = findViewById<Button>(R.id.opencv_button)
        var identity_img = findViewById<ImageView>(R.id.identity_imageView)

        button_opencv.setOnClickListener {
                val cameraPermissionCheck = ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.CAMERA
                )
                if (cameraPermissionCheck != PackageManager.PERMISSION_GRANTED) { // 권한이 없는 경우
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.CAMERA),
                        1000
                    )
                } else { //권한이 있는 경우
                    openCamera()
                }
        }

        storagePermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                setViews()
            } else {
            }
        }

        cameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                Log.d("01503", "oepn Cmaera")
                openCamera()
            } else {
                Toast.makeText(this, "권한을 승인해야 카메라를 사용할 수 있습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {
            val identity_img = findViewById<ImageView>(R.id.identity_imageView)
            identity_img.visibility = View.VISIBLE

            if (it) {
                identity_img.setImageURI(photoUri)

                val matrix = ColorMatrix()
                matrix.setSaturation(0F)

                val filter = ColorMatrixColorFilter(matrix)
                identity_img.colorFilter = filter
                identity_img.setImageURI(photoUri)

                doOCR()

            }
            Log.d("phto", "uri")
        }
        storagePermission.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun doOCR() {
        try {
            bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(
                    contentResolver,
                    photoUri!!
                )

                ImageDecoder.decodeBitmap(
                    source
                ) { decoder: ImageDecoder, _: ImageDecoder.ImageInfo?, _: ImageDecoder.Source? ->
                    decoder.isMutableRequired = true
                }
            } else {
                MediaStore.Images.Media.getBitmap(contentResolver, photoUri)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat)

        //흑백 영상으로 전환
        val graySrc = Mat()
        Imgproc.cvtColor(mat, graySrc, Imgproc.COLOR_RGB2GRAY)

        //2.이진화
        val binarySrc = Mat()
        Imgproc.threshold(graySrc, binarySrc, 0.0, 255.0, Imgproc.THRESH_OTSU)
        Log.d("binary", binarySrc.toString())

        //3. 윤곽선 검출
        // 윤곽선 찾기
        val contours = ArrayList<MatOfPoint>()
        val hierarchy = Mat()
        Imgproc.findContours(
            binarySrc,
            contours,
            hierarchy,
            Imgproc.RETR_EXTERNAL,
            Imgproc.CHAIN_APPROX_NONE
        )

        //3-1 가장 면적이 큰 윤곽선 검충 (검출하고자 하는 문서가 가장 큰 피사체)
        // 가장 면적이 큰 윤곽선 찾기
        var biggestContour: MatOfPoint? = null
        var biggestContourArea: Double = 0.0
        for (contour in contours) {
            val area = Imgproc.contourArea(contour)
            if (area > biggestContourArea) {
                biggestContour = contour
                biggestContourArea = area
            }
        }

        if (biggestContour == null) {
            throw IllegalArgumentException("No Contour")
        }
        // 너무 작아도 안됨
        if (biggestContourArea < 400) {
            throw IllegalArgumentException("too small")
        }

        //3-2 근사화하기 (도형의 꼭짓점을 더 명확하게 찾기)
        val candidate2f = MatOfPoint2f(*biggestContour.toArray())
        val approxCandidate = MatOfPoint2f()
        Imgproc.approxPolyDP(
            candidate2f,
            approxCandidate,
            Imgproc.arcLength(candidate2f, true) * 0.02,
            true
        )

        //3-3 사각형인지 판별 -> 신분증은 사각형이므로..
        // 사각형 판별
//        if (approxCandidate.rows() != 4) {
//            Toast.makeText(applicationContext, "신분증 인식이 명확하지 않습니다\n다시 시도해주세요", Toast.LENGTH_SHORT).show()
//            openCamera()
//        }
//        // 컨벡스(볼록한 도형)인지 판별
//        if (!Imgproc.isContourConvex(MatOfPoint(*approxCandidate.toArray()))) {
//            Toast.makeText(applicationContext, "신분증 인식이 명확하지 않습니다\n다시 시도해주세요", Toast.LENGTH_SHORT).show()
//            openCamera()
//        }

        //4. 투시변환
        // 좌상단부터 시계 반대 방향으로 정점을 정렬한다.
        val points = arrayListOf(
            Point(approxCandidate.get(0, 0)[0],
                approxCandidate.get(0, 0)[1]
            ),
            Point(approxCandidate.get(1, 0)[0],
                approxCandidate.get(1, 0)[1]
            ),
            Point(approxCandidate.get(2, 0)[0],
                approxCandidate.get(2, 0)[1]
            ),
            Point(approxCandidate.get(3, 0)[0],
                approxCandidate.get(3, 0)[1]
            ),
        )
        points.sortBy { it.x } // x좌표 기준으로 먼저 정렬

        if (points[0].y > points[1].y) {
            val temp = points[0]
            points[0] = points[1]
            points[1] = temp
        }

        if (points[2].y < points[3].y) {
            val temp = points[2]
            points[2] = points[3]
            points[3] = temp
        }
        // 원본 영상 내 정점들
        val srcQuad = MatOfPoint2f().apply { fromList(points) }

        val maxSize = calculateMaxWidthHeight(
            tl = points[0],
            bl = points[1],
            br = points[2],
            tr = points[3]
        )
        val dw = maxSize.width
        val dh = dw * maxSize.height/maxSize.width
        val dstQuad = MatOfPoint2f(
            Point(0.0, 0.0),
            Point(0.0, dh),
            Point(dw, dh),
            Point(dw, 0.0)
        )

        // 투시변환 매트릭스 구하기
        val perspectiveTransform = Imgproc.getPerspectiveTransform(srcQuad, dstQuad)

        // 투시변환 된 결과 영상 얻기
        val dst = Mat()
        Imgproc.warpPerspective(mat, dst, perspectiveTransform, Size(dw, dh))

        Toast.makeText(this, "신분증 인식에 2~3분정도\n소요됩니다. 잠시만 기다려주세요.", Toast.LENGTH_SHORT).show()
        printOCRResult(dst)

    }

    // 사각형 꼭짓점 정보로 사각형 최대 사이즈 구하기
// 평면상 두 점 사이의 거리는 직각삼각형의 빗변길이 구하기와 동일
    private fun calculateMaxWidthHeight(
        tl: Point,
        tr: Point,
        br: Point,
        bl: Point,
    ): Size {
        // Calculate width
        val widthA = sqrt(((tl.x - tr.x) * (tl.x - tr.x) + (tl.y - tr.y) * (tl.y - tr.y)))
        val widthB = sqrt(((bl.x - br.x) * (bl.x - br.x) + (bl.y - br.y) * (bl.y - br.y)))
        val maxWidth = max(widthA, widthB)
        // Calculate height
        val heightA = sqrt(((tl.x - bl.x) * (tl.x - bl.x) + (tl.y - bl.y) * (tl.y - bl.y)))
        val heightB = sqrt(((tr.x - br.x) * (tr.x - br.x) + (tr.y - br.y) * (tr.y - br.y)))
        val maxHeight = max(heightA, heightB)
        return Size(maxWidth, maxHeight)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun printOCRResult(src: Mat) {
        with(TessBaseAPI()) {
//            val result = findViewById<TextView>(R.id.text_result)
            dataPath = "$filesDir/tesseracts/"
            checkFile(File(dataPath + "tessdata/"), "kor") //사용할 언어파일의 이름 지정
            checkFile(File(dataPath + "tessdata/"), "eng")
            init(dataPath, "kor+eng")

            // Improve image quality by adjusting brightness and contrast
            val contrast = 1.5
            val brightness = 20.0
            Core.addWeighted(src, contrast, Mat.zeros(src.size(), src.type()), 0.0, brightness, src)

            // Apply image preprocessing techniques
            Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY)
            Imgproc.GaussianBlur(src, src, Size(5.0, 5.0), 0.0)
            Imgproc.threshold(src, src, 0.0, 255.0, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU)
            Imgproc.medianBlur(src, src, 3)

            // Set page segmentation mode to treat the image as a single block of text
            pageSegMode = TessBaseAPI.PageSegMode.PSM_SINGLE_BLOCK

            // Set OCR engine mode to use LSTM-based OCR engine for better accuracy
            //val ocrEngineMode = TessBaseAPI.PageSegMode.PSM_SINGLE_BLOCK
            val ocrEngineMode = TessBaseAPI.PageSegMode.PSM_SINGLE_COLUMN
            pageSegMode = ocrEngineMode
            val tessdataDir = File("$filesDir/tessdata/")
            val engTrainedData = File(tessdataDir, "eng.trained")
            val korTrainedData = File(tessdataDir, "kor.trained")
            if (engTrainedData.exists() && korTrainedData.exists()) {
                val lang = "kor+eng"
                setVariable(
                    TessBaseAPI.VAR_CHAR_WHITELIST,
                    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
                )
                init(filesDir.absolutePath + "/tessdata/", lang)
            } else {
                Log.e("printOCRResult", "Trained data files are missing")
            }

            setImage(bitmap)
            bitmap = resize(bitmap)!!
            image = BitMapToString(bitmap)

            Log.d("imagegae", image.length.toString())

            updateIdentity()
            Log.d("update????", "update????")
        }
    }


    fun updateIdentity() {
        var id = intent.getStringExtra("id").toString()
        val updateidentityurl = "http://seonho.dothome.co.kr/identity_update.php"
        var img1 : String = ""
        var img2 : String = ""

        if(image != "null") {
            Log.d("imagegae11", image.length.toString())
            img1 = image.substring(0,image.length/2+1)
            img2 = image.substring(image.length/2+1,image.length)
        }

        val request = Login_Request(
            Request.Method.POST,
            updateidentityurl,
            { response ->
                Log.d("idendkd", response.toString())
                if(!response.equals("updateIdentity fail")) {
                    Log.d("123456", "789")

//                    var profileFragment = ProfileFragment()
//                    var bundle = Bundle()
//                    bundle.putString("id", id)
//                    bundle.putString("userType", userType)
//                    bundle.putString("userDept", userDept)
//                    bundle.putString("userGrade", userGrade)
//                    bundle.putString("nickname", nickname)
//                    profileFragment.arguments = bundle

                } else {
                    Toast.makeText(this, "신분증을 다시 촬영해주세요.", Toast.LENGTH_SHORT).show()
                }
            },
            { Log.d("failed", "error......${error(this)}") },
            hashMapOf(
                "id" to id,
                "identity_image1" to img1,
                "identity_image2" to img2
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)

    }

    private fun copyFile(lang: String) {
        try {
            //언어데이타파일의 위치
            val filePath: String = "$dataPath/tessdata/$lang.traineddata"

            //AssetManager를 사용하기 위한 객체 생성
            val assetManager: AssetManager = assets;

            //byte 스트림을 읽기 쓰기용으로 열기
            val inputStream: InputStream = assetManager.open("tessdata/$lang.traineddata")
            val outStream: OutputStream = FileOutputStream(filePath)

            //위에 적어둔 파일 경로쪽으로 해당 바이트코드 파일을 복사한다.
            val buffer = ByteArray(1024)

            var read: Int = 0
            read = inputStream.read(buffer)
            while (read != -1) {
                outStream.write(buffer, 0, read)
                read = inputStream.read(buffer)
            }
            outStream.flush()
            outStream.close()
            inputStream.close()

        } catch (e: FileNotFoundException) {
            Log.v("오류발생", e.toString())
        } catch (e: IOException) {
            Log.v("오류발생", e.toString())
        }

    }

    /***
     *  언어 데이터 파일이 기기에 존재하는지 체크하는 기능
     *  @param dir: 언어 데이터 파일 경로
     *  @param lang: 언어 종류 데이터 파일
     *
     *  -> 파일 없으면 파일 생성
     *  -> 있으면 언어 종류 데이터 파일 복사
     */
    private fun checkFile(dir: File, lang: String) {

        //파일의 존재여부 확인 후 내부로 복사
        if (!dir.exists() && dir.mkdirs()) {
            copyFile(lang)
        }

        if (dir.exists()) {
            val datafilePath = "$dataPath/tessdata/$lang.traineddata"
            val dataFile = File(datafilePath)
            if (!dataFile.exists()) {
                copyFile(lang)
            }
        }

    }

    //카메라 권한 요청
    fun setViews() {
        val opencv_button = findViewById<Button>(R.id.opencv_button)
        opencv_button.setOnClickListener {
            cameraPermission.launch(android.Manifest.permission.CAMERA)
        }
    }

    private fun openCamera() {
        Log.d("123", "456")

        val photoFile = File.createTempFile(
            "IMG_",
            ".jpg",
            this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        )
        photoUri = this.let {
            FileProvider.getUriForFile(
                it, "${applicationContext?.packageName}.provider", photoFile
            )
        }
        cameraLauncher.launch(photoUri)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun BitMapToString(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos) //bitmap compress
        val arr = baos.toByteArray()
        val base64Image = Base64.encodeToString(arr, Base64.DEFAULT)
        var temp = ""
        try {
            //temp = URLEncoder.encode(image, "utf-8")
        } catch (e: Exception) {
            Log.e("exception", e.toString())
        }
        return base64Image
    }

    private fun resize(bitmap: Bitmap): Bitmap? {
        var bitmap: Bitmap? = bitmap

        bitmap = Bitmap.createScaledBitmap(bitmap!!, 240, 480, true)
        return bitmap
    }
}