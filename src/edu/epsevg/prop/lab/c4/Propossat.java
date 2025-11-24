package edu.epsevg.prop.lab.c4;

import edu.epsevg.prop.lab.c4.IAuto;
import edu.epsevg.prop.lab.c4.Jugador;
import edu.epsevg.prop.lab.c4.Tauler;

/**
 * Implementació del jugador "Propossat", basat en Minimax amb poda Alpha-Beta.
 * <p>
 * Aquest jugador pot utilitzar o no heurística i poda Alpha-Beta en funció
 * dels paràmetres rebuts al constructor. Extén {@link Heuristica} i implementa
 * les interfícies {@link Jugador} i {@link IAuto}.
 * </p>
 *
 * <p>
 * El jugador explora l'arbre de joc fins a una profunditat determinada i
 * selecciona la millor columna possible segons les funcions Max_Valor i
 * Min_Valor, aplicant la poda Alpha-Beta si està habilitada.
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
     * la funció Max_Valor (primer moviment del jugador).
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

        for (int columna = 0; t.espotmoure() && columna < t.getMida(); columna++) {
            if (!t.movpossible(columna)) continue;

            Tauler s = new Tauler(t);
            s.afegeix(columna, color);

            int iniJugador = color;

            int candidat = Min_Valor(s, -color, columna, depth-1, iniJugador, alpha, beta);
            if(candidat > valor){
                valor = candidat;
                millorMoviment = columna;
            }
            // Actualització d'alpha a nivell 0 per tal que els següents Min_Valor puguin podar contra el millor resultat fins ara
            if (usaPoda) {
                alpha = Math.max(alpha, valor);
            }
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
     * Representa el torn del rival (minimitzador).
     *
     * @param t      Tauler actual.
     * @param color    Color del jugador que mou en aquest nivell (rival).
     * @param col     Última columna jugada.
     * @param depth    Profunditat restant.
     * @param iniJugador Color del jugador principal (maximitzador).
     * @param alpha    Valor alfa de la poda (millor opció coneguda per al MAX).
     * @param beta    Valor beta de la poda (millor opció coneguda per al MIN).
     * @return      Valor mínim que pot retornar aquest node.
     */
    private int Min_Valor(Tauler t, int color, int col, int depth, int iniJugador, int alpha, int beta){
        // Avaluació d'estats terminals: Victòria/Derrota o profunditat màxima
        if (t.solucio(col, -color)) return Integer.MAX_VALUE; // MAX va guanyar l'últim moviment (derrota del MIN actual)
        else if (!t.espotmoure()) return 0; // Tauler ple (empat)
        else if (depth == 0) return valorarEstat(t, color, iniJugador); // Avaluació heurística

        int valor = Integer.MAX_VALUE;

        for (int columna = 0; columna < t.getMida(); columna++) {
            if (!t.movpossible(columna)) continue;

            Tauler s = new Tauler(t);
            s.afegeix(columna, color);

            valor = Math.min(valor, Max_Valor(s, -color, columna, depth-1, iniJugador, alpha, beta));

            // Poda Alpha: Si el valor MIN és més petit o igual que alpha, la branca es poda
            if(usaPoda && valor <= alpha){
                //System.out.println("Poda alpha");
                return valor;
            }
            beta = Math.min(beta, valor);
        }

        return valor;
    }

    /**
     * Funció Max del Minimax amb poda Alpha-Beta.
     * Representa el torn del jugador principal (maximitzador).
     *
     * @param t      Tauler actual.
     * @param color    Color del jugador que mou en aquest nivell (principal).
     * @param col     Última columna jugada.
     * @param depth    Profunditat restant.
     * @param iniJugador Color del jugador principal.
     * @param alpha    Valor alfa de la poda (millor opció coneguda per al MAX).
     * @param beta    Valor beta de la poda (millor opció coneguda per al MIN).
     * @return      Valor màxim que pot retornar aquest node.
     */
    private int Max_Valor(Tauler t, int color, int col, int depth, int iniJugador, int alpha, int beta){
        // Avaluació d'estats terminals: Victòria/Derrota o profunditat màxima
        if (t.solucio(col, -color)) return Integer.MIN_VALUE; // MIN va guanyar l'últim moviment (derrota del MAX actual)
        else if (!t.espotmoure()) return 0; // Tauler ple (empat)
        else if (depth == 0) return valorarEstat(t, color, iniJugador); // Avaluació heurística

        int valor = Integer.MIN_VALUE;
        for (int columna = 0; columna < t.getMida(); columna++) {
            if (!t.movpossible(columna)) continue;

            Tauler s = new Tauler(t);
            s.afegeix(columna, color);

            valor = Math.max(valor, Min_Valor(s, -color, columna, depth-1, iniJugador, alpha, beta));

            // Poda Beta: Si el valor MAX és més gran o igual que beta, la branca es poda
            if(usaPoda && beta <= valor){
                //System.out.println("Poda beta");
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
     * @param color    Color del jugador que avalua.
     * @param iniJugador Color del jugador principal.
     * @return      Valor heurístic o 0 si no s'utilitza heurística.
     */
    private int valorarEstat(Tauler t, int color, int iniJugador){
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