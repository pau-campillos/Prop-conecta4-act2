/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.epsevg.prop.lab.c4;

/**
 *
 * @author Pau Campillos, Pablo Martín
 */
class Heuristica {
    private final int PARTIDA_GUANYADA = Integer.MAX_VALUE;
    private final int POTENCIAL_DE_TRES = 1000; 
    private final int POTENCIAL_DE_DOS = 100;   
    private final int POTENCIAL_DE_UN = 10;     
    private final int[] COLUMNES_CENTRALS = {1, 2, 3, 4, 4, 3, 2, 1};
    
    public int evaluarEstat(Tauler t, int color, int jugInicial) {
       int h = 0;
        
       // Evaluem el tauler actual
       
       h += evaluarLiniesHorizontals(t, jugInicial);
       h += evaluarLiniesVerticals(t, jugInicial);
       h += evaluarDiagonalsDescendents(t, jugInicial);
       h += evaluarDiagonalsAscendents(t, jugInicial);
       /*
       h += evaluarControlCentre(t, jugInicial);
       h += evaluarDoblesAmenaces(t, jugInicial);
       */
       //Mirar si no hi ha cap diagonal que no capiga en una columna
        
       return h;
    }

    private int evaluarLiniesHorizontals(Tauler t, int jugInicial) {
        int h = 0;
        int mida = t.getMida();
        
        //Evaluem horitzontals
        for (int fila = 0; fila < mida; ++fila) {
            for (int columna = 0; columna <= mida - 4; ++columna) {
                int buits = 0, jugMax = 0, rival = 0;
                for (int i = 0; i < 4; ++i) {
                    int valor = t.getColor(fila, columna + i);
                    if (valor == jugInicial) jugMax++;
                    else if (valor == 0) buits++;
                    else rival++;
                }
                h += calcularHeuristica(buits, jugMax, rival);
            }
        }
        return h;
    }

    private int evaluarLiniesVerticals(Tauler t, int jugInicial) {
        int h = 0;
        int mida = t.getMida();
        
        //Evaluem verticals
        for (int columna = 0; columna < mida; ++columna) {
            for (int fila = 0; fila < mida - 3; ++fila) {
                int buits = 0, jugMax = 0, rival = 0;
                for (int i = 0; i < 4; ++i) {
                    int valor = t.getColor(fila + i, columna);
                    if (valor == jugInicial) jugMax++;
                    else if (valor == 0) buits++;
                    else rival++;
                }
                h += calcularHeuristica(buits, jugMax, rival);
            }
        }
        
        return h;
    }

    private int evaluarDiagonalsDescendents(Tauler t, int jugInicial) {
        int h = 0;
        int mida = t.getMida();
        
        for (int fila = 0; fila < mida - 3; fila++) {
            for (int col = 0; col < mida - 3; col++) {
                int buits = 0, jugMax = 0, rival = 0;
                
                for (int i = 0; i < 4; i++) {
                    int valor = t.getColor(fila - i, col + i); 
                    if (valor == jugInicial) jugMax++;
                    else if (valor == 0) buits++;
                    else rival++;
                }
                h += calcularHeuristica(buits, jugMax, rival);
            }
        }
        return h;
    }

    private int evaluarDiagonalsAscendents(Tauler t, int jugInicial) {
        int h = 0;
        int mida = t.getMida();
        
        for (int fila = 3; fila < mida; fila++) {
            for (int col = 0; col < mida - 3; col++) {
                int buits = 0, jugMax = 0, rival = 0;
                
                for (int i = 0; i < 4; i++) {
                    int valor = t.getColor(fila + i, col + i); 
                    if (valor == jugInicial) jugMax++;
                    else if (valor == 0) buits++;
                    else rival++;
                }
                h += calcularHeuristica(buits, jugMax, rival);
            }
        }
        return h;
    }

    private int calcularHeuristica(int buits, int jugMax, int rival){
        int h = 0;
        
        // Cassos favorables (jugMax)
        if(jugMax == 4) h += PARTIDA_GUANYADA; 
        else if(jugMax == 3 && buits == 1) h += POTENCIAL_DE_TRES;
        else if(jugMax == 2 && buits == 2) h += POTENCIAL_DE_DOS; 
        else if(jugMax == 1 && buits == 3) h += POTENCIAL_DE_UN;
        
        // ChatGPT --> Parlar amb el Pau

        // Cassos de bloqueig

        //else if(rival == 3 && buits == 1) h += 50; // ¡BLOQUEO URGENTE!
        //else if(rival == 2 && buits == 2) h += 5; // bloqueo preventivo
        
        // Cassos desfavorables (rival)
        else if(rival == 4) h -= PARTIDA_GUANYADA; 
        else if(rival == 3 && buits == 1) h -= POTENCIAL_DE_TRES; 
        else if(rival == 2 && buits == 2) h -= POTENCIAL_DE_DOS; 
        else if(rival == 1 && buits == 3) h -= POTENCIAL_DE_UN; 

        return h;
    }
}

