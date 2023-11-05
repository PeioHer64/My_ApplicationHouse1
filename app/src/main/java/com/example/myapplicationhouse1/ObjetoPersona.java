package com.example.myapplicationhouse1;

import java.util.Date;

public class ObjetoPersona {

    private String nombreObjeto;
    private String apellidoObjeto;

    public ObjetoPersona(){}
    public ObjetoPersona(String nombreObjeto,String apellidoObjeto){
        nombreObjeto=this.nombreObjeto;
        apellidoObjeto=this.apellidoObjeto;
    }


    public String getNombreObjeto() {
        return nombreObjeto;
    }

    public void setNombreObjeto(String nombreObjeto) {
        this.nombreObjeto = nombreObjeto;
    }

    public String getApellidoObjeto() {
        return apellidoObjeto;
    }

    public void setApellidoObjeto(String apellidoObjeto) {
        this.apellidoObjeto = apellidoObjeto;
    }
}
