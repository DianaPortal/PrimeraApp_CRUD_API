package com.example.cl2_diana_portal_olano.Interface;

import com.example.cl2_diana_portal_olano.Modelo.MensagePost;
import com.example.cl2_diana_portal_olano.Modelo.Posts;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface JsonPlaceHolderApi {
    @GET("posts") // El nombre post es el mismo nombre de  la parte final de la URL
    Call<List<Posts>> getPosts();
    @GET("posts/{id}")
    Call<Posts> getPostById(@Path("id") int id);


    // CREATE: Insertar un nuevo post (POST)
    @POST("posts")
    Call<MensagePost> createPost(@Body Posts post);

    // UPDATE: Actualizar un post completo (PUT)
    @PUT("posts/{id}")
    Call<Posts> updatePost(@Path("id") int id, @Body Posts post);

    // PATCH: Actualizar parcialmente un post (PATCH)
    @PATCH("posts/{id}")
    Call<Posts> patchPost(@Path("id") int id, @Body Posts post);

    // DELETE: Eliminar un post (DELETE)
    @DELETE("posts/{id}")
    Call<Void> deletePost(@Path("id") int id);
}
