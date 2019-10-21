package io.selja.permissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import io.selja.R

const val PERMISSION_REQUEST_CODE = 123
const val LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
const val CAMERA_PERMISSION = Manifest.permission.CAMERA

class PermissionManager {

    fun hasPermission(context: Context, permission: String) =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    private fun needsExplanation(context: Activity, permission: String) =
        ActivityCompat.shouldShowRequestPermissionRationale(context, permission)

    private fun displayExplanation(context: Activity, @StringRes explanation: Int) {
        MaterialDialog(context).show {
            message(explanation)
            positiveButton(R.string.open_settings) { dialog ->
                dialog.dismiss()
                ContextCompat.startActivity(context, Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), null)
            }
            negativeButton(R.string.cancel)
        }
    }

    fun requestCameraPermission(context: Activity) {
        requestPermission(context, CAMERA_PERMISSION, R.string.camera_permission_explanation)
    }

    fun requestLocationPermissions(context: Activity) {
        requestPermission(context, LOCATION_PERMISSION, R.string.permission_explanation)
    }

    private fun requestPermission(context: Activity, permission: String, @StringRes explanation: Int) {
        if (hasPermission(context, permission)) {
            return
        }

        if (needsExplanation(context, permission)) {
            displayExplanation(context, explanation)
        } else {
            ActivityCompat.requestPermissions(context, arrayOf(permission), PERMISSION_REQUEST_CODE)
        }
    }

    fun onResults(requestCode: Int, grantResults: IntArray, callback: (Boolean) -> Unit) {
        if (requestCode != PERMISSION_REQUEST_CODE) {
            return
        }

        callback.invoke((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED))
    }
}