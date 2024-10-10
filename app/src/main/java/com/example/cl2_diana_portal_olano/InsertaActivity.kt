package com.example.cl2_diana_portal_olano

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cl2_diana_portal_olano.Interface.JsonPlaceHolderApi
import com.example.cl2_diana_portal_olano.Modelo.MensagePost
import com.example.cl2_diana_portal_olano.Modelo.Posts
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class InsertaActivity : AppCompatActivity() {

    private val ruta = "https://jsonplaceholder.typicode.com/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert)

        // Obtener referencias a los elementos de la interfaz
        val userIdInput = findViewById<EditText>(R.id.userIdInput)
        val titleInput = findViewById<EditText>(R.id.nameInput)
        val bodyInput = findViewById<EditText>(R.id.descriptionInput)
        val btnGrabar = findViewById<Button>(R.id.btnGrabar)
        val btnVolver = findViewById<Button>(R.id.btnVolver)

        // listener para el btnGrabar
        btnGrabar.setOnClickListener {
            // obtenemos los valores ingresados
            val userId = userIdInput.text.toString().toIntOrNull()
            val title = titleInput.text.toString()
            val body = bodyInput.text.toString()

            // validamos que los campos no estén vacíos
            if (userId != null && title.isNotEmpty() && body.isNotEmpty()) {
                // simulamos la inserción y envio de los datos de vuelta a MainActivity-- ya que el
                //el registro será ficticio
                val intent = Intent()
                intent.putExtra("userId", userId)
                intent.putExtra("title", title)
                intent.putExtra("body", body)
                setResult(RESULT_OK, intent)
                // mensaje de registro exitoso
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                finish()
            // Cerramos la actividad
            } else {
                Toast.makeText(this, "Completar todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // listener para el btnVolver
        btnVolver.setOnClickListener {
            finish()
        // Cierra la actividad
        }
    }

    private fun createPost(userId: Int, title: String, body: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(ruta)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi::class.java)

        val newPost = Posts().apply {
            setUserId(userId)
            setTitle(title)
            setBody(body)
        }

        val call = jsonPlaceHolderApi.createPost(newPost)

        call.enqueue(object : Callback<MensagePost> {
            override fun onResponse(call: Call<MensagePost>, response: Response<MensagePost>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@InsertaActivity, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK) //Inserción fue exitosa
                    finish()  //Volvemos a la actividad principal
                } else {
                    Toast.makeText(this@InsertaActivity, "Error en el registro", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MensagePost>, t: Throwable) {
                Toast.makeText(this@InsertaActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
