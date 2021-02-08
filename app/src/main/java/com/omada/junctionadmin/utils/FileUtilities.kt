package com.omada.junctionadmin.utils;

import android.net.Uri
import android.util.Log
import androidx.annotation.NonNull
import com.omada.junctionadmin.application.JunctionAdminApplication
import java.io.File

open class FileUtilities {

    companion object {

        fun deleteFile(@NonNull uri: Uri): Boolean {

            if (uri.path == null) {
                return false
            }
            val fileToDelete = File(uri.path)
            if (fileToDelete.exists()) {
                if (fileToDelete.delete()) {
                    if (fileToDelete.exists()) {
                        fileToDelete.canonicalFile.delete()
                        if (fileToDelete.exists()) {
                            JunctionAdminApplication.getContext().deleteFile(fileToDelete.name)
                        }
                    }
                    Log.e("", "File Deleted " + uri.path)
                    return true
                } else {
                    Log.e("", "File not Deleted " + uri.path)
                    return false
                }
            }
            return false
        }
    }

}
