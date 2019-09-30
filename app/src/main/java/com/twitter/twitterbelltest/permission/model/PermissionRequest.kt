package com.twitter.twitterbelltest.permission.model

/**
 * This class is user to create custom permission title and message for run time permission
 * @param title title for permission
 * @param message message to explain users what is this permission user for
 */
data class PermissionRequest(
    val title: String = "Permission Title",
    val message: String = "Permission Message"
)