package edu.epsevg.prop.lab.c4;


/**
 * Classe encarregada d'avaluar la heurística d'un tauler de Connecta 4.
 * <p>
 * La heurística analitza totes les possibles finestres de 4 fitxes en
 * horitzontal, vertical i les dues diagonals, assignant puntuacions segons
 * el nombre de fitxes del jugador que maximitza, el que minimitza i caselles buides.
 * </p>
 * 
 * <p>
 * Constants com PARTIDA_GUANYADA, POTENCIAL_DE_TRES, etc. defineixen la
 * importància (el pes) relativa de cada situació en una finestra de 4.
 * </p>
 * 
 * @author Pau Campillos
 * @author Pablo Martín
 */
class Heuristica {
    /**
     * Valor heurístic assignat quan es detecta una alineació completa de 4. En cas de que sigui del que minimitza,
     * és el mateix valor però en negatiu
     */
    private final int PARTIDA_GUANYADA = 100000000;

    /**
     * Valor assignat a una finestra amb 3 fitxes del jugador que maximitza i 1 buit. En cas de que sigui del que minimitza,
     * és el mateix valor però en negatiu
     */
    private final int POTENCIAL_DE_TRES = 1000;

    /**
     * Valor assignat a una finestra amb 2 fitxes del jugador que maximitza i 2 buits. En cas de que sigui del que minimitza,
     * és el mateix valor però en negatiu
     */
    private final int POTENCIAL_DE_DOS = 100;

    /**
     * Valor assignat a una finestra amb 1 fitxa del jugador que maximitza i 3 buits. En cas de que sigui del que minimitza,
     * és el mateix valor però en negatiu
     */
    private final int POTENCIAL_DE_UN = 10;  
    
    /**
     * Avalua l'estat actual del tauler per al jugador indicat.
     *
     * @param t           Tauler actual de la partida.
     * @param jugInicial  Color del jugador maximitzador (jugador principal de l'heurística).
     * @return            Valor heurístic de la posició.
     */
    public int evaluarEstat(Tauler t, int jugInicial) {
       int h = 0;
       h = evaluarTauler(t, jugInicial);
       return h;
    }

    /**
     * Avalua el tauler recorrent totes les finestres de mida 4 en les
     * quatre direccions possibles (horitzontal, vertical i diagonals).
     *
     * @param t           Tauler a analitzar.
     * @param jugInicial  Jugador que es vol maximitzar.
     * @return            Valor heurístic total del tauler.
     */
    private int evaluarTauler(Tauler t, int jugInicial) {
        int h = 0;
        int mida = t.getMida();
        
        /* Definim les 4 direccions: {direccioFila, direccioColumna}
            1. Horizontal (0, 1)
            2. Vertical (1, 0)
            3. Diagonal Descendent (1, 1)
            4. Diagonal Ascendent (-1, 1)
        */
        int[][] direccions = {{0, 1}, {1, 0}, {1, 1}, {-1, 1}};

        for (int[] dir : direccions) {
            int dirFila = dir[0];
            int dirCol = dir[1];

            // Calculem fila i columna inicial i finals per evitar desbordaments.
            int iniciFila = 0;
            if(dirFila == -1) iniciFila = 3;
           
            int finFila = mida; 
            if(dirFila == 1) finFila = mida - 3;
            
            int finCol = mida;
            if (dirCol == 1) finCol = mida - 3;

            // Recorregut del tauler
            for (int fila = iniciFila; fila < finFila; ++fila) {
                for (int col = 0; col < finCol; ++col) {
                    
                    int buits = 0, jugMax = 0, rival = 0;
                    
                    // Comprovem la finestra de 4 posicions.
                    for (int i = 0; i < 4; ++i) {
                        int valor = t.getColor(fila + (i * dirFila), col + (i * dirCol));
                        
                        if (valor == jugInicial) jugMax++;
                        else if (valor == 0) buits++;
                        else rival++;
                    }
                    h += calcularHeuristica(buits, jugMax, rival);
                }
            }
        }
        return h;
    }

    /**
     * Calcula el valor heurístic d'una finestra de 4 posicions en funció
     * del nombre de fitxes pròpies, rivals i caselles buides.
     *
     * @param buits   Nombre de caselles buides.
     * @param jugMax  Nombre de fitxes del jugador maximitzador.
     * @param rival   Nombre de fitxes del jugador rival.
     * @return        Valor heurístic assignat a aquesta finestra.
     */
    private int calcularHeuristica(int buits, int jugMax, int rival){
        int h = 0;
        
        // Cassos favorables (jugMax)
        if(jugMax == 4) h += PARTIDA_GUANYADA; 
        else if(jugMax == 3 && buits == 1) h += POTENCIAL_DE_TRES;
        else if(jugMax == 2 && buits == 2) h += POTENCIAL_DE_DOS; 
        else if(jugMax == 1 && buits == 3) h += POTENCIAL_DE_UN;
        
        // Cassos desfavorables (rival)
        else if(rival == 4) h -= PARTIDA_GUANYADA; 
        else if(rival == 3 && buits == 1) h -= POTENCIAL_DE_TRES; 
        else if(rival == 2 && buits == 2) h -= POTENCIAL_DE_DOS; 
        else if(rival == 1 && buits == 3) h -= POTENCIAL_DE_UN; 

        return h;
    }
}

