package edu.epsevg.prop.lab.c4;

/**
 * Jugador Proposat per Pau i Pablo
 * @author Pau i Pablo
 */
public class Propossat
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
    return triaMillorMoviment(t, color, depth);
  }

  // s = successor
  public int triaMillorMoviment(Tauler t, int color, int depth)
  {
    int millorMoviment = -1;
    int valor = Integer.MIN_VALUE;
    for (int columna = 0; t.espotmoure() && columna < t.getMida(); columna++) {
      
        if (!t.movpossible(columna)) continue;

        Tauler s = new Tauler(t);
        s.afegeix(columna, color);

        int candidat = Min_Valor(s, -color, columna, depth-1);
        if(candidat > valor){ //Deberia de tener un =
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
  
  private int Min_Valor(Tauler t, int color, int col, int depth){
    if (t.solucio(col, -color)) return Integer.MAX_VALUE; // Guany l' adversari.
    else if (!t.espotmoure()) return 0;
    
    int valor = Integer.MAX_VALUE;
    if(depth > 0){
      for (int columna = 0; t.espotmoure() && columna < t.getMida(); columna++) {
        if (!t.movpossible(columna)) continue;
        
        Tauler s = new Tauler(t);
        s.afegeix(columna, color);
      
        valor = Math.min(valor, Max_Valor(s,-color,columna,depth-1));
      }
    }
    return valor;
  }


  private int Max_Valor(Tauler t, int color, int col, int depth){
    if (t.solucio(col, -color)) return Integer.MIN_VALUE; // Hem guanyat
    else if (!t.espotmoure()) return 0;
    
    int valor = Integer.MIN_VALUE;
    
    if (depth > 0) {
      for (int columna = 0; t.espotmoure() && columna < t.getMida(); columna++) {
        if (!t.movpossible(columna)) continue;
        Tauler s = new Tauler(t);
        s.afegeix(columna, color);
        valor = Math.max(valor, Min_Valor(s,-color,columna,depth-1));
      }
    }
    return valor;
  }

  // Getter
  @Override
  public String nom()
  {
    return nom;
  }
  
}


