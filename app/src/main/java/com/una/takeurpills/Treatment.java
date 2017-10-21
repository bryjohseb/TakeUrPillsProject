package com.una.takeurpills;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by BryJohSeb on 5/26/2017.
 */

public class Treatment implements Serializable {
    private String titulo;
    private int dosis;
    private int cantidadRestante;
    private int vecesDiarias;
    private String Unidad;
    private int Reminder;
    private boolean dia1;
    private boolean dia2;
    private boolean dia3;
    private boolean dia4;
    private boolean dia5;
    private boolean dia6;
    private boolean dia7;
    private ArrayList<String> horas;

    public Treatment(String titulo, int dosis, int cantidadRestante, int vecesDiarias, String unidad, int reminder, boolean dia1, boolean dia2, boolean dia3, boolean dia4, boolean dia5, boolean dia6, boolean dia7, ArrayList<String> horas) {
        this.titulo = titulo;
        this.dosis = dosis;
        this.cantidadRestante = cantidadRestante;
        this.vecesDiarias = vecesDiarias;
        Unidad = unidad;
        Reminder = reminder;
        this.dia1 = dia1;
        this.dia2 = dia2;
        this.dia3 = dia3;
        this.dia4 = dia4;
        this.dia5 = dia5;
        this.dia6 = dia6;
        this.dia7 = dia7;
        this.horas = horas;
    }

    public Treatment() {
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getDosis() {
        return dosis;
    }

    public void setDosis(int dosis) {
        this.dosis = dosis;
    }

    public int getCantidadRestante() {
        return cantidadRestante;
    }

    public void setCantidadRestante(int cantidadRestante) {
        this.cantidadRestante = cantidadRestante;
    }

    public int getVecesDiarias() {
        return vecesDiarias;
    }

    public void setVecesDiarias(int vecesDiarias) {
        this.vecesDiarias = vecesDiarias;
    }

    public String getUnidad() {
        return Unidad;
    }

    public void setUnidad(String unidad) {
        Unidad = unidad;
    }

    public int getReminder() {
        return Reminder;
    }

    public void setReminder(int reminder) {
        Reminder = reminder;
    }

    public boolean isDia1() {
        return dia1;
    }

    public void setDia1(boolean dia1) {
        this.dia1 = dia1;
    }

    public boolean isDia2() {
        return dia2;
    }

    public void setDia2(boolean dia2) {
        this.dia2 = dia2;
    }

    public boolean isDia3() {
        return dia3;
    }

    public void setDia3(boolean dia3) {
        this.dia3 = dia3;
    }

    public boolean isDia4() {
        return dia4;
    }

    public void setDia4(boolean dia4) {
        this.dia4 = dia4;
    }

    public boolean isDia5() {
        return dia5;
    }

    public void setDia5(boolean dia5) {
        this.dia5 = dia5;
    }

    public boolean isDia6() {
        return dia6;
    }

    public void setDia6(boolean dia6) {
        this.dia6 = dia6;
    }

    public boolean isDia7() {
        return dia7;
    }

    public void setDia7(boolean dia7) {
        this.dia7 = dia7;
    }

    public ArrayList<String> getHoras() {
        return horas;
    }

    public void setHoras(ArrayList<String> horas) {
        this.horas = horas;
    }
}

