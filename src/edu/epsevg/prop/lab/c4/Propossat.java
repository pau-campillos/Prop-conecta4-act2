package edu.epsevg.prop.lab.c4;

import edu.epsevg.prop.lab.c4.IAuto;
import edu.epsevg.prop.lab.c4.Jugador;
import edu.epsevg.prop.lab.c4.Tauler;

/**
 * Implementació del jugador "Propossat", basat en l'algorisme de cerca 
 * Minimax amb poda Alpha-Beta.
 * <p>
 * Aquest jugador pot utilitzar o no heurística i poda Alpha-Beta en funció
 * dels paràmetres rebuts al constructor, oferint més opcions en l'estratègia 
 * de joc. Extén {@link Heuristica} i implementa
 * les interfícies {@link Jugador} i {@link IAuto}.
 * </p>
 *
 * <p>
 * L'algorisme explora l'arbre de joc fins a la {@link #depth profunditat màxima} 
 * i selecciona la millor columna possible segons les funcions {@link #Max_Valor Max_Valor} 
 * i {@link #Min_Valor Min_Valor}, aplicant la poda Alpha-Beta si {@link #usaPoda està habilitada}.
 * </p>
 *
 * @author Pau Campillos
 * @author Pablo Martín
 */
public class Propossat extends Heuristica
    implements Jugador, IAuto
{
    /** Nom identificatiu del jugador. */
    private final String nom;

    /** Profunditat màxima de cerca per al Minimax. */
    private final int depth;

    /** Indica si s'ha d'utilitzar heurística quan s'arriba a la profunditat 0. */
    boolean usaHeur;

    /** Indica si s'ha d'utilitzar la poda Alpha-Beta. */
    boolean usaPoda;

    /** Comptador total de les avaluacions d'estats terminals (crides a la funció heurística). */
    private int jugadesFinalsTotals; 

    /** Comptador de les avaluacions d'estats terminals realitzades en el torn actual. */
    private int jugadesFinalsEnCadaTorn; 


    /**
     * Constructora del jugador Propossat.
     * Inicialitza els paràmetres de cerca Minimax i d'optimització.
     *
     * @param profMaxima  Profunditat màxima de la cerca Minimax.
     * @param usaremHeur  Si s'utilitzarà heurística en la valoració d'estats terminals.
     * @param usaremPoda  Si s'utilitzarà la poda Alpha-Beta.
     */
    public Propossat(int profMaxima, boolean usaremHeur, boolean usaremPoda)
    {
        depth = profMaxima;
        usaHeur = usaremHeur;
        usaPoda = usaremPoda; 
        nom = "Propossat";
        jugadesFinalsTotals = 0;
        jugadesFinalsEnCadaTorn = 0;
    }

    /**
     * Assigna el moviment del jugador segons l'algorisme Minimax.
     * Aquest mètode inicialitza el comptador de jugades finals del torn
     * i crida a la funció {@link #triaMillorMoviment triaMillorMoviment} per 
     * determinar la millor columna. 
     * * També mostra per consola les estadístiques de cerca del torn.
     *
     * @param t   Tauler actual.
     * @param color Color (fitxa) del jugador que mou.
     * @return    Columna on jugar.
     */
    @Override
    public int moviment(Tauler t, int color)
    {
        jugadesFinalsEnCadaTorn = 0;
        int res = triaMillorMoviment(t, color, depth);
        System.out.println("# Jugades analitzades finals en aquest torn: " + jugadesFinalsEnCadaTorn);
        System.out.println("# Jugades analitzades finals des de l'inici de la partida: " + jugadesFinalsTotals);
        return res;
    }

     /**
     * Selecciona el millor moviment possible per al jugador utilitzant
     * la funció {@link #Max_Valor Max_Valor} (primer moviment del jugador).
     * 
     * Si es el primer moviment de la partida (independentment del jugador), 
     * reinicia el contador jugadesFinalsTotals, per si l'usuari fes diverses 
     * partides seguides.
     * 
     * * Aquest mètode opera a nivell 0 de l'arbre i determina la columna amb la 
     * màxima puntuació Minimax/Alpha-Beta.
     * 
     * @param t     Tauler actual.
     * @param color   Color del jugador que mou.
     * @param depth   Profunditat restant.
     * @return     La millor columna a jugar.
     */
    private int triaMillorMoviment(Tauler t, int color, int depth)
    {
        int millorMoviment = -1;
        int valor = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        int contador = 0;

        for (int columna = 0; t.espotmoure() && columna < t.getMida(); columna++) {
            if (!t.movpossible(columna)) continue;

            if (contador < 2 && t.getColor(0,columna) != 0){
              if (t.getColor(1, columna) != 0) contador+=2;
              else contador++; 
            }
            
            Tauler s = new Tauler(t);
            s.afegeix(columna, color);

            int iniJugador = color;

            int candidat = Min_Valor(s, -color, columna, depth-1, iniJugador, alpha, beta);
            if(candidat > valor){
                valor = candidat;
                millorMoviment = columna;
            }
            
            if (usaPoda) {
                alpha = Math.max(alpha, valor);
            }
        }

        if (contador < 2){
          jugadesFinalsTotals = jugadesFinalsEnCadaTorn;
        }

        if (millorMoviment == -1) {
            for(int columna = 0; columna < t.getMida(); ++columna){
                if (t.movpossible(columna)){
                    millorMoviment = columna;
                    break;
                }
            }
        }
        return millorMoviment;
    }

    /**
     * Funció Min del Minimax amb poda Alpha-Beta.
     * Representa el torn del rival (minimitzador) buscant el moviment 
     * que minimitza el valor màxim potencial del jugador principal.
     *
     * @param t         Tauler actual.
     * @param color     Color del jugador que mou en aquest nivell (rival, minimitzador).
     * @param col       Última columna jugada (per comprovar l'estat de solució).
     * @param depth     Profunditat restant.
     * @param iniJugador Color del jugador principal (maximitzador).
     * @param alpha     Valor alfa de la poda (millor opció coneguda per al MAX).
     * @param beta      Valor beta de la poda (millor opció coneguda per al MIN).
     * @return          Valor mínim que pot retornar aquest node. Retorna {@code Integer.MAX_VALUE} si 
     * el jugador principal guanya en el moviment anterior, {@code Integer.MIN_VALUE}
     * si el rival guanya en el moviment actual, un valor heurístic, o 0 si és empat.
     */ 
    private int Min_Valor(Tauler t, int color, int col, int depth, int iniJugador, int alpha, int beta){
        // Avaluació d'estats terminals: Victòria/Derrota o profunditat màxima
        if (t.solucio(col, -color)) return Integer.MAX_VALUE; // MAX va guanyar l'últim moviment (derrota del MIN actual)
        else if (!t.espotmoure()) return 0; // Tauler ple (empat)
        else if (depth == 0) return valorarEstat(t, iniJugador); // Avaluació heurística

        int valor = Integer.MAX_VALUE;

        for (int columna = 0; columna < t.getMida(); columna++) {
            if (!t.movpossible(columna)) continue;

            Tauler s = new Tauler(t);
            s.afegeix(columna, color);

            valor = Math.min(valor, Max_Valor(s, -color, columna, depth-1, iniJugador, alpha, beta));

            // Poda Alpha: Si el valor MIN és més petit o igual que alpha, la branca es poda.
            if(usaPoda && valor <= alpha){
                return valor;
            }
            beta = Math.min(beta, valor);
        }

        return valor;
    }

    /**
     * Funció Max del Minimax amb poda Alpha-Beta.
     * Representa el torn del jugador principal (maximitzador) buscant el moviment 
     * que maximitza el valor mínim potencial del rival.
     *
     * @param t         Tauler actual.
     * @param color     Color del jugador que mou en aquest nivell (principal, maximitzador).
     * @param col       Última columna jugada (per comprovar l'estat de solució).
     * @param depth     Profunditat restant.
     * @param iniJugador Color del jugador principal.
     * @param alpha     Valor alfa de la poda (millor opció coneguda per al MAX).
     * @param beta      Valor beta de la poda (millor opció coneguda per al MIN).
     * @return          Valor màxim que pot retornar aquest node. Retorna {@code Integer.MIN_VALUE} si 
     * el rival guanya en el moviment anterior, {@code Integer.MAX_VALUE}
     * si el jugador principal guanya en el moviment actual, un valor heurístic, o 0 si és empat.
     */ 
    private int Max_Valor(Tauler t, int color, int col, int depth, int iniJugador, int alpha, int beta){
        // Avaluació d'estats terminals: Victòria/Derrota o profunditat màxima
        if (t.solucio(col, -color)) return Integer.MIN_VALUE; // MIN va guanyar l'últim moviment (derrota del MAX actual)
        else if (!t.espotmoure()) return 0; // Tauler ple (empat)
        else if (depth == 0) return valorarEstat(t, iniJugador); // Avaluació heurística

        int valor = Integer.MIN_VALUE;
        for (int columna = 0; columna < t.getMida(); columna++) {
            if (!t.movpossible(columna)) continue;

            Tauler s = new Tauler(t);
            s.afegeix(columna, color);

            valor = Math.max(valor, Min_Valor(s, -color, columna, depth-1, iniJugador, alpha, beta));

            // Poda Beta: Si el valor MAX és més gran o igual que beta, la branca es poda.
            if(usaPoda && beta <= valor){
                return valor;
            }
            alpha = Math.max(alpha, valor);
        }
        return valor;
    }
    
    /**
     * Avalua un estat quan s'arriba a profunditat 0.
     * Si usaHeur és false, retorna 0. A més, incrementa en 1 el número de
     * jugades finals (# vegades calculada la heurística).
     *
     * @param t      Tauler actual.
     * @param iniJugador Color del jugador principal.
     * @return      Valor heurístic o 0 si no s'utilitza heurística.
     */
    private int valorarEstat(Tauler t, int iniJugador){
        int h = 0;
        jugadesFinalsTotals++;
        jugadesFinalsEnCadaTorn++;
        if(usaHeur) h = evaluarEstat(t, iniJugador);
        return h;
    }

    /**
     * Getter: Retorna el nom del jugador.
     *
     * @return Nom del jugador "Propossat".
     */
    @Override
    public String nom()
    {
        return nom;
    }

}