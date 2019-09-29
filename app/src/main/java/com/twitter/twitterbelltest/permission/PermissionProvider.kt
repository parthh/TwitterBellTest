package com.twitter.twitterbelltest.permission


import android.app.Activity
import com.twitter.twitterbelltest.permission.model.PermissionRequest
import com.twitter.twitterbelltest.permission.model.PermissionResult


/**
 * <p>PermissionProvider - interface to provide permission handler</p>
 */
interface PermissionProvider {
    companion object {
        const val DEFAULT_PERMISSION_REQUEST_CODE = 1001
    }

    /**
     * check permission
     *
     */
    fun checkPermissions(
        activity: Activity,
        permissions: Array<out String>,
        onPermissionResult: ((PermissionResult) -> Unit)?,
        permissionRequestPreExecuteExplanation: PermissionRequest? = null,
        permissionRequestRetryExplanation: PermissionRequest? = null,
        requestCode: Int? = null
    ): Boolean


    /**
     * handle permission request result
     *
     */
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean?


}