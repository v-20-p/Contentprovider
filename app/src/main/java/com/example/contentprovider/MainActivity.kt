package com.example.contentprovider

import android.content.ContentUris
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.contentprovider.ui.theme.ContentProviderTheme
import java.util.Calendar

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<ContentProviderViewModel>()
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_MEDIA_IMAGES
            ),
            0
        )
        super.onCreate(savedInstanceState)
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,

        )
        val  dataDuration=Calendar.getInstance()
            .apply {
                add(Calendar.DAY_OF_YEAR,-10)
            }.timeInMillis
        val selection = "${MediaStore.Images.Media.DATE_TAKEN} >= ?"
        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            arrayOf(dataDuration.toString()),
            "${MediaStore.Images.Media.DATE_TAKEN} DESC"
        )?.use {
            val idColumn= it.getColumnIndex((MediaStore.Images.Media._ID))
            val displayNameColumn =it.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
            val images = mutableListOf<ContentProviderDataItem>()
            while (it.moveToNext()){
                val  id =it.getLong(idColumn)
                val displayName= it.getString(displayNameColumn)
                val uri =ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,id
                )
                images.add(ContentProviderDataItem(id,displayName,uri))


            }
            viewModel.updateImages(images)


        }

        setContent {
            ContentProviderTheme {
                LazyColumn(Modifier.fillMaxSize()){
                    items(viewModel.imagesSate.value){ image ->
                        Column(Modifier.fillMaxWidth()){
                            AsyncImage(model = image.uri, contentDescription = "image")
                            Text(text = image.name)
                        }
                    }
                }
            }
        }
    }
}

