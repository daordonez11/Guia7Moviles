package com.moviles.domiciliosmoviles.rest;

import com.moviles.domiciliosmoviles.entities.Pedido;
import com.moviles.domiciliosmoviles.entities.Plato;
import com.moviles.domiciliosmoviles.entities.ResponseMessage;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Maca on 9/02/17.
 */

public interface ApiService {

    @GET("/platos")
    Call<List<Plato>> getPlatos();

    @POST("/pedidos")
    Call<ResponseMessage> createPedido(@Body Pedido pedido);

}
