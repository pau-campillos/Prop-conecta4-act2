package edu.epsevg.prop.lab.c4;

/**
 * Jugador Proposat per Pau i Pablo
 * @author Pau i Pablo
 */
public class Propossat extends Heuristica
  implements Jugador, IAuto
{
  private final String nom;
  private final int depth;
  boolean usaHeur;
  
  public Propossat(int profMaxima, boolean usaremHeur)
  {
    depth = profMaxima;
    usaHeur = usaremHeur;
    nom = "Propossat";
  }
  
  @Override
  public int moviment(Tauler t, int color)
  {
    int res = triaMillorMoviment(t, color, depth);
    System.out.println("Propossat tira a la columna: " + res);
    return res;
  }

  // s = successor
  private int triaMillorMoviment(Tauler t, int color, int depth)
  {
    int millorMoviment = -1;
    int valor = Integer.MIN_VALUE;
    for (int columna = 0; t.espotmoure() && columna < t.getMida(); columna++) {
        if (!t.movpossible(columna)) continue;
        
        Tauler s = new Tauler(t);
        s.afegeix(columna, color);

        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        int iniJugador = color;
        
        int candidat = Min_Valor(s, -color, columna, depth-1, iniJugador, alpha, beta);
        if(candidat > valor){ 
            valor = candidat;
            millorMoviment = columna;
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

  private int Min_Valor(Tauler t, int color, int col, int depth, int iniJugador, int alpha, int beta){
    // Mirar
    if (t.solucio(col, -color)) return Integer.MAX_VALUE; 
    else if (!t.espotmoure()) return 0;
    else if (depth == 0) return valorarEstat(t, color, iniJugador);
    
    int valor = Integer.MAX_VALUE;
    
    for (int columna = 0; columna < t.getMida(); columna++) {
      if (!t.movpossible(columna)) continue;

      Tauler s = new Tauler(t);
      s.afegeix(columna, color);

      valor = Math.min(valor, Max_Valor(s, -color, columna, depth-1, iniJugador, alpha, beta));
      if(valor <= alpha){
        //System.out.println("Poda alpha");
        return valor;
      } 
      beta = Math.min(beta, valor);
    }
 
    return valor;
  }

  private int Max_Valor(Tauler t, int color, int col, int depth, int iniJugador, int alpha, int beta){
    // Mirar
    if (t.solucio(col, -color)) return Integer.MIN_VALUE; 
    else if (!t.espotmoure()) return 0;
    else if (depth == 0) return valorarEstat(t, color, iniJugador);
    
    int valor = Integer.MIN_VALUE;
    for (int columna = 0; columna < t.getMida(); columna++) {
      if (!t.movpossible(columna)) continue;

      Tauler s = new Tauler(t);
      s.afegeix(columna, color);

      valor = Math.max(valor, Min_Valor(s, -color, columna, depth-1, iniJugador, alpha, beta));
      if(beta <= valor){
        //System.out.println("Poda beta");
        return valor;
      } 
      alpha = Math.max(alpha, valor);
    }
    return valor;
  }
  
  private int valorarEstat(Tauler t, int color, int iniJugador){
    int h = 0;
    if(usaHeur) h = evaluarEstat(t, color, iniJugador); 
    return h;
  }
  
  // Getter
  @Override
  public String nom()
  {
    return nom;
  }
  
}

