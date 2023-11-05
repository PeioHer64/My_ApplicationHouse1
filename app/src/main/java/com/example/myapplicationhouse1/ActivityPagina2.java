package com.example.myapplicationhouse1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ActivityPagina2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagina2);

        TextView mensajePagina=findViewById(R.id.textViewMensaje);

        Intent recibirDatos = getIntent(); //Recoge los datos
        String textoRecibido = recibirDatos.getStringExtra("variableMensaje");
        mensajePagina.setText(textoRecibido);
    }
}