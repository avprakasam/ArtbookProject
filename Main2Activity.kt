@file:Suppress("DEPRECATION")

package com.prakasam.mypracticeartbookapplication

import android.Manifest
import android.Manifest.*
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import kotlinx.android.synthetic.main.activity_main2.*
import java.lang.Exception
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore.*
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.ContextCompat.startActivity
import java.io.ByteArrayOutputStream


@Suppress("DEPRECATION")
class Main2Activivty : AppCompatActivity() {
        var selectedImage : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        val intent = intent
        val info = intent.getStringExtra("info")



    if (info.equals("new")) {

            val background =
            BitmapFactory.decodeResource(applicationContext.resources, R.drawable.select)
            imageView.setImageBitmap(background)
            button.visibility = View.VISIBLE
            editText.setText("")

        } else {

            val name = intent.getStringExtra("name")
            editText.setText(name)
            val chosen = Globals.Chosen
            val bitmap = chosen.returnImage()
            imageView.setImageBitmap(bitmap)
            button.visibility = View.INVISIBLE
        }
    }



   fun select(view: View) {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 2)
        } else {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 1)
        }
    }


        override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
            if (requestCode == 2) {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(intent, 1)
                }
            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


            if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
                val image = data.data
                try {

                    selectedImage =
                        MediaStore.Images.Media.getBitmap(this.contentResolver, image)
                    imageView.setImageBitmap(selectedImage)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
            super.onActivityResult(requestCode, resultCode, data)
        }


       fun save(view: View) {
            val artName = editText.text.toString()
            val outputStream = ByteArrayOutputStream()
            selectedImage?.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
            val byteArray = outputStream.toByteArray()

            try {

                val database = this.openOrCreateDatabase("Arts", Context.MODE_PRIVATE, null)
                database.execSQL("CREATE TABLE IF NOT EXISTS aarts (artname VARCHAR, image BLOB)")
                val sqlString = "INSERT INTO aarts (artname, image) VALUES (?, ?)"
                val statement = database.compileStatement(sqlString)
                statement.bindString(1, artName)
                statement.bindBlob(2, byteArray)
                statement.execute()

        //        database.close()

            } catch (e: Exception) {
                e.printStackTrace()
            }


            val intent = Intent(applicationContext,MainActivity::class.java)
            startActivity(intent)


        }
    }






