package com.example.permsion1904

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.example.permsion1904.databinding.ActivityMain2Binding
import com.farimarwat.permissionmate.PMate
import com.farimarwat.permissionmate.PermissionMate
import com.farimarwat.permissionmate.PermissionMateListener
import android.Manifest
import android.os.Build
import android.util.Log
import com.farimarwat.permissionmate.TAG

class MainActivity2 : AppCompatActivity() {
    val binding by lazy {
        ActivityMain2Binding.inflate(layoutInflater)
    }
    lateinit var mContext: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        mContext = this

        val pm = PermissionMate.Builder(this)
            .setPermissions(
                mutableListOf(
                    PMate(
                        Manifest.permission.CALL_PHONE,
                        false, "Phone call permission is required to work app correctly"
                    ),
                    PMate(
                        Manifest.permission.CAMERA,
                        true, "Camera permission is required to work app correctly"
                    ),
                    PMate(
                        Manifest.permission.READ_CONTACTS,
                        false, "Read Contacts permission is required to work app correctly"
                    )
                ).also {
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                        it.add(
                            PMate(
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                true, "Storage permission is required to work app correctly"
                            )
                        )
                    }
                }
            )
            .setListener(object : PermissionMateListener {
                override fun onPermission(permission: String, granted: Boolean) {
                    super.onPermission(permission, granted)
                    Log.e(TAG, "Permission: $permission Granted: $granted")
                }

                override fun onError(error: String) {
                    super.onError(error)
                    Log.e(TAG, error)
                }

                override fun onComplete(permissionsgranted: List<PMate>) {
                    super.onComplete(permissionsgranted)
                    Log.e(TAG, "Permission Granted: ${permissionsgranted.size}")
                }
            })
            .build()
        binding.button.setOnClickListener {


            pm.start()
        }
    }
}