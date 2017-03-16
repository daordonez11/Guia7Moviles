package com.moviles.domiciliosmoviles.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Maca on 9/02/17.
 */

public class Pedido {

    private int idPlato;
    private String cliente;
    private String lugar;

    public Pedido(String cliente, String lugar, int idPlato){
        this.cliente = cliente;
        this.idPlato = idPlato;
        this.lugar = lugar;
    }

    public int getIdPlato() { return idPlato; }

    public String getCliente() { return cliente; }

    public String getLugar() { return lugar; }

}
