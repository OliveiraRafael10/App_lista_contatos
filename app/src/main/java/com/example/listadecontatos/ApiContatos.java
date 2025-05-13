package com.example.listadecontatos;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiContatos {
    @GET("contacts")
    Call<List<Contato>> getContatos();

    @POST("contacts")
    Call<Contato> criarContato(@Body Contato contato);

    @DELETE("contacts/{id}")
    Call<Void> deletarContato(@Path("id") int id);

    @PUT("contacts/{id}")
    Call<Void> atualizarContato(@Path("id") int id, @Body Contato contato);
}