package com.example.contentprovider

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

data class ContentProviderDataItem(
    val id: Long,
    val name: String,
    val uri: Uri
)

class ContentProviderViewModel:ViewModel (){

    var imagesSate = mutableStateOf(emptyList<ContentProviderDataItem>())

      fun updateImages(image:List<ContentProviderDataItem>){
        imagesSate.value=image
    }
}