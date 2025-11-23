package edu.epsevg.prop.lab.c4;

public class Jugador1
  implements Jugador, IAuto
{
  private String nom;
  private int profunditat;
  
  public Jugador1(int profunditat)
  {
    nom = "MiniMaxPlayer";
    this.profunditat = profunditat;
  }
  
  /**
   * 
   * @param t
   * @param color
   * @return 
   */
  public int moviment(Tauler t, int color) {
    int valor = Integer.MIN_VALUE;
    int millorMoviment = -1;
    for (int i = 0; i < t.getMida(); i++){
      if (t.movpossible(i)){
        Tauler taulerMov = new Tauler(t);
        taulerMov.afegeix(i, color);
        int prof = profunditat;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        int jugadorIni = color;
        int candidat = MinValor(taulerMov, i, color*-1, alpha, beta, prof-1, jugadorIni);
        if (valor < candidat){
          valor = candidat;
          millorMoviment = i;
        } 
      }    
    }
    System.out.println("LidiaDani tira a la columna: " + millorMoviment);
    return millorMoviment;
  }
  
  /**
   * 
   * @return 
   */
  public String nom()
  {
    return nom;
  }


  private int MinValor(Tauler t, int column, int color, int alpha, int beta, int prof, int jugadorIni) {
    if (t.solucio(column, color*-1) || prof==0){
      return evaluaEstat(t, color, jugadorIni);
      //if (t.solucio(column, color*-1)) return 1000000;
      //if (prof==0) return 0;
    } 
    int valor = Integer.MAX_VALUE;
    for (int i = 0; i < t.getMida(); i++){
      if (t.movpossible(i)){
        Tauler taulerMov = new Tauler(t);
        taulerMov.afegeix(i, color);
        valor = Math.min(valor, MaxValor(taulerMov, i, color*-1, alpha, beta, prof-1, jugadorIni));
        if ( valor <= alpha ) { // fem la poda
          return valor;
        }
        beta = Math.min(beta, valor); // actualitzem beta
      } 
    } 
    return valor;
  }


  private int MaxValor (Tauler t, int column, int color, int alpha, int beta, int prof, int jugadorIni) {
    if (t.solucio(column, color*-1) || prof==0){
      return evaluaEstat(t, color, jugadorIni);
      //if (t.solucio(column, color*-1)) return -1000000;
      //if (prof==0) return 0;
    } 
    int valor = Integer.MIN_VALUE;
    for (int i = 0; i < t.getMida(); i++){
      if (t.movpossible(i)){
        Tauler taulerMov = new Tauler(t);
        taulerMov.afegeix(i, color);
        valor = Math.max(valor, MinValor(taulerMov, i, color*-1, alpha, beta, prof-1, jugadorIni));
        if ( beta <= valor ) { // fem la poda
          return valor;
        }
        alpha = Math.max(alpha, valor); // actualitzem alpha
      } 
    } 
    return valor;
  }
/* 
  private int evaluaEstat(Tauler t, int color) {
    int h = 0;
    for (int fila = 0; fila < t.getMida(); fila++) {
      for (int col = 0; col < t.getMida(); col++) {
        
        int colorFitxa = t.getColor(fila, col);

        if (colorFitxa == color) {
          if (col==0 || col==7) h += 0;
          else if (col==1 || col==6) h += 2;
          else if (col==2 || col==5) h += 5;
          else if (col==3 || col==4) h += 15;
        } 
        
        if (colorFitxa != color){

          int rival = color*-1;
          if (col + 1 < (t.getMida()-1) && t.getColor(fila, col + 1) == rival && col != 0 ) { // mirar filesssss + prioritzar 3 fitxes del rival seguides

            boolean foratEsq = (col - 1 >= 0 && t.getColor(fila, col - 1) == 0);

            boolean foratDer = (col + 2 < t.getMida() && t.getColor(fila, col + 2) == 0);

            if (foratEsq && foratDer) {
              h -= 200;
            }
            
            else if (foratEsq || foratDer) {
              h -= 80;   
            }
          }
        }
      }
    }
    return h;
  }
}
*/

// fer classe auxiliar per fer la heuristica 

/* 
if (i==0 || i==7) h = 0;
else if (i==1 || i==6) h = 2;
else if (i==2 || i==5) h = 5;
else if (i==3 || i==4) h = 10;

if (amenaça || diagonal || forçar resposta || evites triple amenaça) 
else {
  if (altura == 0 || altura == 7) h = 10;
  else if (altura == 1 || altura == 6) h = 5;
  else if (altura == 2 || altura == 5) h = 2;
  else if (altura == 3 || altura == 4) h = 0;
}  
*/


// 1- Prioritzar columnes centrals
// 2- No continuar apilant fitxes en columnes on ja no tens hueco 
// 3- Prioritzar bloquejar doble amenaces del rival
// 4- Prioritzar crear dobles amenaces ( guanyar des de dues columnes diferents ) 
// 5- Prioritzar guanyar, no tapar al rival ( ja es té en compte en principi amb el minimax )
// 6- Idea feliç: intentar fer trampes pel rival ( posar una fitxa que li faci pensar que pot guanyar en un lloc, i després guanyar per un altre lloc )


  private int calculaHeuristica4(int pos0, int pos1, int pos2, int pos3) {
    //0:buit; -1:rival; 1:jugador
    int h = 0; 
  
    int buits = 0;
    int rival = 0;
    int jugador = 0;
    
    if(pos0==0) ++buits;
    if(pos1==0) ++buits;
    if(pos2==0) ++buits;
    if(pos3==0) ++buits;
    
    if(pos0==-1) ++rival;
    if(pos1==-1) ++rival;
    if(pos2==-1) ++rival;
    if(pos3==-1) ++rival;
    
    if(pos0==1) ++jugador;
    if(pos1==1) ++jugador;
    if(pos2==1) ++jugador;
    if(pos3==1) ++jugador;
    
    // Jugador
    if (jugador == 4) return 1000000;
    if (jugador == 3 && buits == 1) return 100;
    if (jugador == 2 && buits == 2) return 10;
    if (jugador == 1 && buits == 3) return 1;

    // Rival
    if (rival == 4) return -1000000;
    if (rival == 3 && buits == 1) return -100;
    if (rival == 2 && buits == 2) return -10;
    if (rival == 1 && buits == 3) return -1;
        
    return h;
  }
  
  private int evaluaEstat(Tauler t, int color, int jugadorIni) {
    int h = 0;
    
    //horitzontals
    for (int fila = 0; fila < t.getMida(); fila++) {
      for (int col = 0; col < t.getMida()-3; col++) {
          
        int pos0=0, pos1=0, pos2=0, pos3=0;
        
        if (jugadorIni == t.getColor(fila, col)) pos0 = 1;
        else if (jugadorIni*-1 == t.getColor(fila, col)) pos0 = -1;
        
        if (jugadorIni == t.getColor(fila, col+1)) pos1 = 1;
        else if (jugadorIni*-1 == t.getColor(fila, col+1)) pos1 = -1;
        
        if (jugadorIni == t.getColor(fila, col+2)) pos2 = 1;
        else if (jugadorIni*-1 == t.getColor(fila, col+2)) pos2 = -1;
        
        if (jugadorIni == t.getColor(fila, col+3)) pos3 = 1;
        else if (jugadorIni*-1 == t.getColor(fila, col+3)) pos3 = -1;
        
        h += calculaHeuristica4(pos0, pos1, pos2, pos3);
      }  
    }
    
    //verticals
    for (int col = 0; col < t.getMida(); col++) {
      for (int fila = 0; fila < t.getMida()-3; fila++) {
        
        int pos0=0, pos1=0, pos2=0, pos3=0;
        
        if (jugadorIni == t.getColor(fila, col)) pos0 = 1;
        else if (jugadorIni*-1 == t.getColor(fila, col)) pos0 = -1;
        
        if (jugadorIni == t.getColor(fila+1, col)) pos1 = 1;
        else if (jugadorIni*-1 == t.getColor(fila+1, col)) pos1 = -1;
        
        if (jugadorIni == t.getColor(fila+2, col)) pos2 = 1;
        else if (jugadorIni*-1 == t.getColor(fila+2, col)) pos2 = -1;
        
        if (jugadorIni == t.getColor(fila+3, col)) pos3 = 1;
        else if (jugadorIni*-1 == t.getColor(fila+3, col)) pos3 = -1;
        
        h += calculaHeuristica4(pos0, pos1, pos2, pos3);
      }  
    }

    //diagonals adalt - dreta
    for (int col = 0; col < t.getMida()-3; col++) {
      for (int fila = 0; fila < t.getMida()-3; fila++) {
        
        int pos0=0, pos1=0, pos2=0, pos3=0;
        
        if (jugadorIni == t.getColor(fila, col)) pos0 = 1;
        else if (jugadorIni*-1 == t.getColor(fila, col)) pos0 = -1;
        
        if (jugadorIni == t.getColor(fila+1, col+1)) pos1 = 1;
        else if (jugadorIni*-1 == t.getColor(fila+1, col+1)) pos1 = -1;
        
        if (jugadorIni == t.getColor(fila+2, col+2)) pos2 = 1;
        else if (jugadorIni*-1 == t.getColor(fila+2, col+2)) pos2 = -1;
        
        if (jugadorIni == t.getColor(fila+3, col+3)) pos3 = 1;
        else if (jugadorIni*-1 == t.getColor(fila+3, col+3)) pos3 = -1;
        
        h += calculaHeuristica4(pos0, pos1, pos2, pos3);
      }      
    }

    // diagonals adalt - esquerra
    for (int col = 3; col < t.getMida(); col++) {
      for (int fila = 0; fila < t.getMida()-3; fila++) {
        
        int pos0=0, pos1=0, pos2=0, pos3=0;
        
        if (jugadorIni == t.getColor(fila, col)) pos0 = 1;
        else if (jugadorIni*-1 == t.getColor(fila, col)) pos0 = -1;
        
        if (jugadorIni == t.getColor(fila+1, col-1)) pos1 = 1;
        else if (jugadorIni*-1 == t.getColor(fila+1, col-1)) pos1 = -1;
        
        if (jugadorIni == t.getColor(fila+2, col-2)) pos2 = 1;
        else if (jugadorIni*-1 == t.getColor(fila+2, col-2)) pos2 = -1;
        
        if (jugadorIni == t.getColor(fila+3, col-3)) pos3 = 1;
        else if (jugadorIni*-1 == t.getColor(fila+3, col-3)) pos3 = -1;
        
        h += calculaHeuristica4(pos0, pos1, pos2, pos3);
      }      
    }
    return h;
  }
   
   /* 
  private int calculaHeuristica4(int pos0, int pos1, int pos2, int pos3, int profunditatBuits) {
    //0:buit; -1:rival; 1:jugador
    int h = 0; 
  
    int buits = 0;
    int rival = 0;
    int jugador = 0;
    
    if(pos0==0) ++buits;  
    if(pos1==0) ++buits;
    if(pos2==0) ++buits;
    if(pos3==0) ++buits;
        
    if(pos0==-1) ++rival;
    if(pos1==-1) ++rival;
    if(pos2==-1) ++rival;
    if(pos3==-1) ++rival;
    
    if(pos0==1) ++jugador;
    if(pos1==1) ++jugador;
    if(pos2==1) ++jugador;
    if(pos3==1) ++jugador;
    
   // Jugador
    if (jugador == 4) return 1000000;
    if (jugador == 3 && buits == 1) return (int)(100 / (1 + 0.5 * profunditatBuits));
    if (jugador == 2 && buits == 2) return (int)(10 / (1 + 0.5 * profunditatBuits));
    if (jugador == 1 && buits == 3) return (int)(1 / (1 + 0.5 * profunditatBuits));

    // Rival
    if (rival == 4) return -1000000;
    if (rival == 3 && buits == 1) return (int)(-100 / (1 + 0.5 * profunditatBuits));
    if (rival == 2 && buits == 2) return (int)(-10 / (1 + 0.5 * profunditatBuits));
    if (rival == 1 && buits == 3) return (int)(-1 / (1 + 0.5 * profunditatBuits));

    return h;
  }

  //pre: la casella (fila,columna) està buida
  private int jugabilitatCasellaBuida(Tauler t, int fila, int columna) {
    int buits = -1;
    while (fila >= 0 && t.getColor(fila, columna) == 0) {
      --fila;
      ++buits;
    }
    return buits;
  }
  
  private int evaluaEstat(Tauler t, int color, int jugadorIni) {
    int h = 0;
    
    //horitzontals
    for (int fila = 0; fila < t.getMida(); fila++) {
      for (int col = 0; col < t.getMida()-3; col++) {
          
        int pos0=1, pos1=1, pos2=1, pos3=1;
        int profunditatBuits = 0;;
        
        if (0 == t.getColor(fila, col)) {
          pos0 = 0;
          profunditatBuits += jugabilitatCasellaBuida(t, fila, col);
        }
        else if (jugadorIni*-1 == t.getColor(fila, col)) pos0 = -1;
        
        if (0 == t.getColor(fila, col+1)) {
          pos1 = 0;
          profunditatBuits += jugabilitatCasellaBuida(t, fila, col+1);
        }
        else if (jugadorIni*-1 == t.getColor(fila, col+1)) pos1 = -1;
        
        if (0 == t.getColor(fila, col+2)) {
          pos2 = 0;
          profunditatBuits += jugabilitatCasellaBuida(t, fila, col+2);
        }
        else if (jugadorIni*-1 == t.getColor(fila, col+2)) pos2 = -1;
        
        if (0 == t.getColor(fila, col+3)) {
          pos3 = 0;
          profunditatBuits += jugabilitatCasellaBuida(t, fila, col+3);
        }
        else if (jugadorIni*-1 == t.getColor(fila, col+3)) pos3 = -1;
        
        h += calculaHeuristica4(pos0, pos1, pos2, pos3, profunditatBuits);
      }  
    }
    
    //verticals
    for (int col = 0; col < t.getMida(); col++) {
      for (int fila = 0; fila < t.getMida()-3; fila++) {
        
        int pos0=0, pos1=0, pos2=0, pos3=0;
        int profunditatBuits = 0;
        
        if (jugadorIni == t.getColor(fila, col)) pos0 = 1;
        else if (jugadorIni*-1 == t.getColor(fila, col)) pos0 = -1;
        
        if (jugadorIni == t.getColor(fila+1, col)) pos1 = 1;
        else if (jugadorIni*-1 == t.getColor(fila+1, col)) pos1 = -1;
        
        if (jugadorIni == t.getColor(fila+2, col)) pos2 = 1;
        else if (jugadorIni*-1 == t.getColor(fila+2, col)) pos2 = -1;
        
        if (jugadorIni == t.getColor(fila+3, col)) pos3 = 1;
        else if (jugadorIni*-1 == t.getColor(fila+3, col)) pos3 = -1;
        
        h += calculaHeuristica4(pos0, pos1, pos2, pos3, profunditatBuits);
      }  
    }

    //diagonals adalt - dreta
    for (int col = 0; col < t.getMida()-3; col++) {
      for (int fila = 0; fila < t.getMida()-3; fila++) {
        
        int pos0=1, pos1=1, pos2=1, pos3=1;
        int profunditatBuits = 0;
        
        if (0 == t.getColor(fila, col)) {
          pos0 = 0;
          profunditatBuits += jugabilitatCasellaBuida(t, fila, col);
        }
        else if (jugadorIni*-1 == t.getColor(fila, col)) pos0 = -1;
        
        if (0 == t.getColor(fila+1, col+1)) {
          pos1 = 0;
          profunditatBuits += jugabilitatCasellaBuida(t, fila+1, col+1);
        }
        else if (jugadorIni*-1 == t.getColor(fila+1, col+1)) pos1 = -1;
        
        if (0 == t.getColor(fila+2, col+2)) {
          pos2 = 0;
          profunditatBuits += jugabilitatCasellaBuida(t, fila+2, col+2);
        }
        else if (jugadorIni*-1 == t.getColor(fila+2, col+2)) pos2 = -1;
        
        if (0 == t.getColor(fila+3, col+3)) {
          pos3 = 0;
          profunditatBuits += jugabilitatCasellaBuida(t, fila+3, col+3);
        }
        else if (jugadorIni*-1 == t.getColor(fila+3, col+3)) pos3 = -1;
        
        h += calculaHeuristica4(pos0, pos1, pos2, pos3, profunditatBuits);
      }      
    }

    // diagonals adalt - esquerra
    for (int col = 3; col < t.getMida(); col++) {
      for (int fila = 0; fila < t.getMida()-3; fila++) {
        
        int pos0=1, pos1=1, pos2=1, pos3=1;
        int profunditatBuits = 0;
        
        if (0 == t.getColor(fila, col)) {
          pos0 = 0;
          profunditatBuits += jugabilitatCasellaBuida(t, fila, col);
        }
        else if (jugadorIni*-1 == t.getColor(fila, col)) pos0 = -1;
        
        if (0 == t.getColor(fila+1, col-1)) {
          pos1 = 0;
          profunditatBuits += jugabilitatCasellaBuida(t, fila+1, col-1);
        }
        else if (jugadorIni*-1 == t.getColor(fila+1, col-1)) pos1 = -1;
        
        if (0 == t.getColor(fila+2, col-2)) {
          pos2 = 0;
          profunditatBuits += jugabilitatCasellaBuida(t, fila+2, col-2);
        }
        else if (jugadorIni*-1 == t.getColor(fila+2, col-2)) pos2 = -1;
        
        if (0 == t.getColor(fila+3, col-3)) {
          pos3 = 0;
          profunditatBuits += jugabilitatCasellaBuida(t, fila+3, col-3);
        }
        else if (jugadorIni*-1 == t.getColor(fila+3, col-3)) pos3 = -1;
        
        h += calculaHeuristica4(pos0, pos1, pos2, pos3, profunditatBuits);
      }      
    }
    return h;
  }
    */
}
