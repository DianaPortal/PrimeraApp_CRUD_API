package com.example.cl2_diana_portal_olano

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cl2_diana_portal_olano.Interface.JsonPlaceHolderApi
import com.example.cl2_diana_portal_olano.Modelo.Posts
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PostsAdapter
    private val postsList = mutableListOf<Posts>()
    private val ruta = "https://jsonplaceholder.typicode.com/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializamos RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PostsAdapter(postsList) { post ->
            showOptionsDialog(post)
        }
        recyclerView.adapter = adapter

        // Botones
        val btnBuscar = findViewById<Button>(R.id.btnBuscar)
        val btnInsertar = findViewById<Button>(R.id.btnInsertar)
        val btnEliminar = findViewById<Button>(R.id.btnEliminar)
        val btnActualizar = findViewById<Button>(R.id.btnActualizar)

        // Listener de cada botón
        btnBuscar.setOnClickListener { showSearchDialog() }
        btnInsertar.setOnClickListener {
            // Lanzar nueva actividad para insertar
            val intent = Intent(this, InsertaActivity::class.java)
            startActivityForResult(intent, 1)
        }
        btnEliminar.setOnClickListener { showDeleteDialog() }
        btnActualizar.setOnClickListener { showUpdateDialog() }

        // Obtener todos los posts de la api al inicio
        getPosts()
    }
    //Actualizar la lista despues de insertar un posts
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val userId = data?.getIntExtra("userId", 0)
            val title = data?.getStringExtra("title")
            val body = data?.getStringExtra("body")

            // Crear un nuevo post localmente y agregarlo a la lista
            if (userId != null && title != null && body != null) {
                val newPost = Posts().apply {
                    setUserId(userId)
                    setTitle(title)
                    setBody(body)
                    setId(postsList.size + 1)
                // se agrega un id falso ya que el recurso no se registrara en el servidor
                }
                postsList.add(newPost)
                adapter.notifyDataSetChanged()
            // Se actualiza el RecyclerView
            }
        }
    }

    // Mostramos todos los posts usando RecyclerView

    //obtener la lista
    private fun getPosts() {
        val retrofit = Retrofit.Builder()
            .baseUrl(ruta)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val jsonPlaceHolderApi: JsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi::class.java)
        val call: Call<List<Posts>> = jsonPlaceHolderApi.getPosts()

        call.enqueue(object : Callback<List<Posts>> {
            override fun onResponse(call: Call<List<Posts>>, response: Response<List<Posts>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        postsList.clear()
                        postsList.addAll(it)
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Error en la solicitud", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Posts>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

/* ****************************INICIO BUSCAR POR ID********************************************** */
    // AlertDialog para la BUSCAR por el id
    private fun showSearchDialog() {
        val input = EditText(this)
        input.hint = "Ingrese el id"//digitar el id a buscar

        val dialog = AlertDialog.Builder(this)
            .setTitle("BUSCAR ID")//Titulo del AlertDialog
            .setView(input)
            .setPositiveButton("Buscar") { _, _ ->
                val id = input.text.toString().toIntOrNull()
                if (id != null) getPostById(id) else Toast.makeText(this, "Id inválido", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }

    // Busqueda de un post por id
    private fun getPostById(id: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl(ruta)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi::class.java)
        val call: Call<Posts> = jsonPlaceHolderApi.getPostById(id)

        call.enqueue(object : Callback<Posts> {
            override fun onResponse(call: Call<Posts>, response: Response<Posts>) {
                if (response.isSuccessful) {
                    val post = response.body()
                    if (post != null) {
                        postsList.clear()
                        postsList.add(post)
                        adapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this@MainActivity, "Post no encontrado", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Posts>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /* *************************FIN BUSCAR POR ID********************************************* */

    private fun showOptionsDialog(post: Posts) {
        val options = arrayOf("Actualizar", "Eliminar")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Seleccionar acción para el post")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showUpdateDialog()
                    1 -> showDeleteDialog()
                }
            }
        builder.create().show()
    }

    /* ************************INICIO DELETE********************************************** */
    // AlertDialog para ELIMINAR

    private fun showDeleteDialog() {
        val input = EditText(this)
        input.hint = "Ingrese el Id"

        val dialog = AlertDialog.Builder(this)
            .setTitle("Eliminar por Id")
            .setView(input)
            .setPositiveButton("Eliminar") { _, _ ->
                val id = input.text.toString().toIntOrNull()
                if (id != null) deletePost(id) else Toast.makeText(this, "Id inválido", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }


    // Eliminar post por id
    private fun deletePost(id: Int) {
        // Verificamos si el post es local o real, ya que existen los que se estan registrando de manera ficticia
        // y los que ya estan registrados en la api
        val post = postsList.find { it.getId() == id }
        if (post == null) {
            Toast.makeText(this@MainActivity, "Post no encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        // Si el Id es ficticio, solo es el que se creo en el momento de insertar
        if (id > postsList.size) {
            // eliminamos localmente
            postsList.removeAll { it.getId() == id }
            adapter.notifyDataSetChanged()
            Toast.makeText(this@MainActivity, "Se elimino correctamente", Toast.LENGTH_SHORT).show()
        } else {
            // Si el Id está en el servidor, realizamos la solicitud a la api
            val retrofit = Retrofit.Builder()
                .baseUrl(ruta)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi::class.java)
            val call = jsonPlaceHolderApi.deletePost(id)

            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        postsList.removeAll { it.getId() == id }
                        adapter.notifyDataSetChanged()
                        Toast.makeText(this@MainActivity, "Eliminado correctamente ", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MainActivity, "Error al eliminar", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    /* ***********************FIN DELETE*********************************************** */

/* ***********************INICIO ACTUALIZAR*********************************************** */
    // AlertDialog para ACTUALIZAR
    private fun showUpdateDialog() {
        val input = EditText(this)
        input.hint = "Ingrese el Id"

        val dialog = AlertDialog.Builder(this)
            .setTitle("Actualizar por Id")
            .setView(input)
            .setPositiveButton("Actualizar") { _, _ ->
                val id = input.text.toString().toIntOrNull()
                if (id != null) {
                    getPostByIdForUpdate(id) // Obtener el post actual por ID
                } else {
                    Toast.makeText(this, "Id inválido", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }

    // Obtener un post por ID para mostrar en el formulario para realizar la actualización
    private fun getPostByIdForUpdate(id: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl(ruta)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi::class.java)
        val call: Call<Posts> = jsonPlaceHolderApi.getPostById(id)

        call.enqueue(object : Callback<Posts> {
            override fun onResponse(call: Call<Posts>, response: Response<Posts>) {
                if (response.isSuccessful) {
                    val post = response.body()
                    if (post != null) {
                        // Muestra el form con los datos actuales del id seleccionado
                        showUpdateForm(post)
                    } else {
                        Toast.makeText(this@MainActivity, "Post no encontrado", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Error al obtener el post", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Posts>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Mostrar el formulario de actualización
    private fun showUpdateForm(post: Posts) {
        // Crear el layout para el AlertDialog
        val view = layoutInflater.inflate(R.layout.dialog_actualizar, null)
        val titleInput = view.findViewById<EditText>(R.id.titleInput)
        val bodyInput = view.findViewById<EditText>(R.id.bodyInput)

        // Completar los campos para la actualización
        titleInput.setText(post.getTitle())
        bodyInput.setText(post.getBody())

        val dialog = AlertDialog.Builder(this)
            .setTitle("ACTUALIZAR")
            .setView(view)
            .setPositiveButton("GUARDAR") { _, _ ->
                val updatedTitle = titleInput.text.toString()
                val updatedBody = bodyInput.text.toString()
                updatePost(post.getId(), updatedTitle, updatedBody) // Llama al método para actualizar el post
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }

    // Método para actualizar el post
    private fun updatePost(id: Int, title: String, body: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(ruta)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi::class.java)

        val updatedPost = Posts().apply {
            setId(id)
            setUserId(1)
            setTitle(title)
            setBody(body)
        }

        val call = jsonPlaceHolderApi.updatePost(id, updatedPost)

        call.enqueue(object : Callback<Posts> {
            override fun onResponse(call: Call<Posts>, response: Response<Posts>) {
                if (response.isSuccessful) {
                    val postResponse = response.body()
                    postResponse?.let {
                        val index = postsList.indexOfFirst { it.getId() == id }
                        if (index >= 0) {
                            postsList[index] = it
                            // se notifica al adaptador
                            adapter.notifyItemChanged(index)
                        }
                    }
                    Toast.makeText(this@MainActivity, "Actualización correcta", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Posts>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /* ***********************FIN ACTUALIZAR*********************************************** */

}
