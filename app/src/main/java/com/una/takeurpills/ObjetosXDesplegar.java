package com.una.takeurpills;

/**
 * Created by Esteban on 4/19/2017.
 */

public class ObjetosXDesplegar {
    private String atributo01;
    private String atributo02;
    private int NumDibujo;
    public ObjetosXDesplegar(String atributo01, String atributo02, int NumDibujo){
        super();
        this.atributo01 = atributo01;
        this.atributo02 = atributo02;
        this.NumDibujo = NumDibujo;
    }
    public String getAtributo01() {
        return atributo01;
    }
    public String getAtributo02() {
        return atributo02;
    }
    public int getNumDibujo() {
        return NumDibujo;
    }
    public void setAtributo01(String atributo01) {
        this.atributo01 = atributo01;
    }
}
