package com.example.permsion1904


import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import coil.load
import coil.transform.CircleCropTransformation
import com.example.permsion1904.databinding.ActivityMainBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val CAMERA_REQUEST_CODE = 1
    private val GALLERY_REQUEST_CODE = 2


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //  val message = intent?.getStringExtra(NOTIFICATION_MESSAGE_TAG)





        binding.btnCamera.setOnClickListener {
            cameraCheckPermission()
        }

        binding.btnGallery.setOnClickListener {
            galleryCheckPermission()
        }
        binding.btnNext1.setOnClickListener {
            /*  when {
                  ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                          PackageManager.PERMISSION_GRANTED -> {
                      Log.e(TAG, "User accepted the notifications!")
                      sendNotification(this)
                  }
                  shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                      Snackbar.make(
                          findViewById(R.id.parent_layout),
                          "The user denied the notifications ):",
                          Snackbar.LENGTH_LONG
                      )
                          .setAction("Settings") {
                              val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                              intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                              val uri: Uri =
                                  Uri.fromParts("\"package:$packageName\"", packageName, null)
                              intent.data = uri
                              startActivity(intent)
                          }
                          .show()
                  }
                  else -> {
                      requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                  }
              }*/

            postNotificationPermission()
        }

        @SuppressLint("SimpleDateFormat")
        fun getOutputMediaFile(): File? {
            return try {
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmm ss").format(Date())
                val imageFileName = "JPEG_" + timeStamp + "_"
                val storageDir: File =applicationContext.cacheDir
                File.createTempFile(
                    imageFileName,  /* prefix */
                    ".JPEG",  /* suffix */
                    storageDir /* directory */
                )
            } catch (e: IOException) {
                null
            }
        }
        fun saveBitmap(bitmap: Bitmap){
            var fileOutputStream: FileOutputStream? = null
            try {
                fileOutputStream =  FileOutputStream(getOutputMediaFile())
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream) // save bitmap image
            } catch (e: IOException) {
                e.printStackTrace()
            }finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }


        //when you click on the image
        binding.imageView.setOnClickListener {
            val pictureDialog = AlertDialog.Builder(this)
            pictureDialog.setTitle("Select Action")
            val pictureDialogItem = arrayOf(
                "Select photo from Gallery",
                "Capture photo from Camera"
            )
            pictureDialog.setItems(pictureDialogItem) { dialog, which ->

                when (which) {
                    0 -> gallery()
                    1 -> camera()
                }
            }

            pictureDialog.show()
        }


    }

    /*  @RequiresApi(Build.VERSION_CODES.TIRAMISU)
      private fun postNotificationPermission() {
          Dexter.withContext(this).withPermission(
              android.Manifest.permission.POST_NOTIFICATIONS,


          ).withListener(object : PermissionListener {
              override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                  notification()
              }

              override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                  Toast.makeText(
                      this@MainActivity,
                      "You have denied the storage permission to select image",
                      Toast.LENGTH_SHORT
                  ).show()
                  showRotationalDialogForPermission()
              }

              override fun onPermissionRationaleShouldBeShown(
                  p0: PermissionRequest?,
                  p1: PermissionToken?
              ) {
                  showRotationalDialogForPermission()
              }
          }
          ).onSameThread().check()

      }*/


    /*   companion object {
           const val TAG = "MainActivity"
           const val NOTIFICATION_MESSAGE_TAG = "message from notification"
           fun newIntent(context: Context) = Intent(context, MainActivity::class.java).apply {
               putExtra(
                   NOTIFICATION_MESSAGE_TAG, "Hi ☕\uD83C\uDF77\uD83C\uDF70"
               )
           }
       }*/

    /* private val requestPermissionLauncher = registerForActivityResult(
         ActivityResultContracts.RequestPermission()
     ) { isGranted: Boolean ->
         if (isGranted) {
             // Permission is granted. Continue the action or workflow in your
             // app.
             sendNotification(this)
         } else {
             // Explain to the user that the feature is unavailable because the
             // features requires a permission that the user has denied. At the
             // same time, respect the user's decision. Don't link to system
             // settings in an effort to convince the user to change their
             // decision.
         }
     }*/

    private fun galleryCheckPermission() {

        Dexter.withContext(this).withPermission(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,

            ).withListener(object : PermissionListener {
            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                gallery()
            }

            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                Toast.makeText(
                    this@MainActivity,
                    "You have denied the storage permission to select image",
                    Toast.LENGTH_SHORT
                ).show()
                showRotationalDialogForPermission()
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: PermissionRequest?, p1: PermissionToken?
            ) {
                showRotationalDialogForPermission()
            }
        }).onSameThread().check()
    }

    private fun gallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun notification() {
        val intent = Intent(Context.NOTIFICATION_SERVICE)
        intent.data = Uri.parse("package:$packageName")
        startActivity(intent)
        /* val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
         intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
         val uri: Uri =
             Uri.fromParts("\"package:$packageName\"", packageName, null)
         intent.data = uri
         startActivity(intent)*/
    }


    private fun cameraCheckPermission() {

        Dexter.withContext(this)
            .withPermissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA
            ).withListener(

                object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report?.let {

                            if (report.areAllPermissionsGranted()) {
                                camera()
                            }

                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<PermissionRequest>?,
                        p1: PermissionToken?
                    ) {
                        showRotationalDialogForPermission()
                    }

                }
            ).onSameThread().check()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun postNotificationPermission() {
        Dexter.withContext(this)
            .withPermissions(
                android.Manifest.permission.POST_NOTIFICATIONS,
            ).withListener(

                object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report?.let {

                            if (report.areAllPermissionsGranted()) {
                                sendNotification(applicationContext)
                            }
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<PermissionRequest>?,
                        p1: PermissionToken?
                    ) {
                        showRotationalDialogForPermission()
                    }
                }
            ).onSameThread().check()
    }

    private fun camera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }



    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

            when (requestCode) {

                CAMERA_REQUEST_CODE -> {

                    val bitmap = data?.extras?.get("data") as Bitmap

                    //we are using coroutine image loader (coil)
                    binding.imageView.load(bitmap) {
                        crossfade(true)
                        crossfade(1000)
                        transformations(CircleCropTransformation())
                    }
                }

                GALLERY_REQUEST_CODE -> {

                    binding.imageView.load(data?.data) {
                        crossfade(true)
                        crossfade(1000)
                        transformations(CircleCropTransformation())
                      /*  val storageDir: String = this@MainActivity.cacheDir.path
                        val root = Environment.getExternalStorageDirectory().absolutePath
                        getDirectories(root)*/


                    }


                }


            }

        }

    }

    private fun getDirectories(root: String) {

    }


    private fun showRotationalDialogForPermission() {
        AlertDialog.Builder(this)
            .setMessage(
                "It looks like you have turned off permissions"
                        + "required for this feature. It can be enable under App settings!!!"
            )

            .setPositiveButton("Go TO SETTINGS") { _, _ ->

                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)

                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }

            .setNegativeButton("CANCEL") { dialog, _ ->
                dialog.dismiss()
            }.show()


    }


}