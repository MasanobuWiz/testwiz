package Tetrix;

import java.util.Random;

/* --------------------------------------------------------------------------------
 |
 | 無駄のない詰め合わせを行いましょう ： TETRIX箱詰め問題
 |
 | tetrix4 1.0 1.2 28
 |
 ---------------------------------------------------------------------------------
*/


class POS
{
	public POS(int xa, int ya)
	{
		x = xa; y = ya;
	}
	
	int  x,y;
}

class PUNIT
{
	public PUNIT(int xa, int ya, int fa)
	{
		x = xa; y = ya; fig = fa;
	}
	
	int  x,y,fig;
}

public class Tetrix {

	static final int Tetra = 4;
	static final int Figures = 19;
	static final int Pieces  =  7;

	static final int Units = 230;
	static final int Tsize = ( ((Units*(Units-1))/2)+Units );
	static final int X_width = 5;
	static final int Y_hight = 6;
	static final int S_elements = 100;
	static final int P_elements = 60;

	static double[] unit = new double[Units];
	static double[] unitOut = new double[Units];
	static double Acoef,Bcoef;

	static int[] Ta = new int[Tsize];
	static int[] Tb = new int[Tsize];
	static double[] I = new double[Units];

	static int[][][]  Sxy = new int[X_width][Y_hight][S_elements];
	static int[][] Pa = new int[Pieces][P_elements];

	static int[][]  state = new int[X_width+1][Y_hight];

	
	static POS[][] figpos =
	{
		{ new POS(0, 0),new POS( 1, 0),new POS( 2, 0),new POS( 2, 1) },   /* Figure 1 */
		{ new POS(0, 0),new POS( 0, 1),new POS( 0, 2),new POS(-1, 2) },   /* Figure 2 */
	    { new POS(0, 0),new POS( 0, 1),new POS( 1, 1),new POS( 2, 1) },   /* Figure 3 */
	    { new POS(0, 0),new POS( 0, 1),new POS( 0, 2),new POS( 1, 0) },   /* Figure 4 */
	    { new POS(0, 0),new POS( 1, 0),new POS( 2, 0),new POS( 0, 1) },   /* Figure 5 */
	    { new POS(0, 0),new POS( 0, 1),new POS( 0, 2),new POS( 1, 2) },   /* Figure 6 */
	    { new POS(0, 0),new POS( 0, 1),new POS(-1, 1),new POS(-2, 1) },   /* Figure 7 */
	    { new POS(0, 0),new POS( 1, 0),new POS( 1, 1),new POS( 1, 2) },   /* Figure 8 */
	    { new POS(0, 0),new POS( 1, 0),new POS( 1, 1),new POS( 2, 1) },   /* Figure 9 */
	    { new POS(0, 0),new POS( 0, 1),new POS(-1, 1),new POS(-1, 2) },   /* Figure 10 */
	    { new POS(0, 0),new POS( 1, 0),new POS( 0, 1),new POS(-1, 1) },   /* Figure 11 */
	    { new POS(0, 0),new POS( 0, 1),new POS( 1, 1),new POS( 1, 2) },   /* Figure 12 */
	    { new POS(0, 0),new POS(-1, 1),new POS( 0, 1),new POS( 1, 1) },   /* Figure 13 */
	    { new POS(0, 0),new POS( 0, 1),new POS( 0, 2),new POS( 1, 1) },   /* Figure 14 */
	    { new POS(0, 0),new POS( 1, 0),new POS( 2, 0),new POS( 1, 1) },   /* Figure 15 */
	    { new POS(0, 0),new POS( 0, 1),new POS( 0, 2),new POS(-1, 1) },   /* Figure 16 */
	    { new POS(0, 0),new POS( 1, 0),new POS( 2, 0),new POS( 3, 0) },   /* Figure 17 */
	    { new POS(0, 0),new POS( 0, 1),new POS( 0, 2),new POS( 0, 3) },   /* Figure 18 */
	    { new POS(0, 0),new POS( 1, 0),new POS( 0, 1),new POS( 1, 1) }    /* Figure 19 */
	};

	static PUNIT[] punit =
	{
		new PUNIT(1,0,0),new PUNIT(0,1,0),new PUNIT(1,1,0),new PUNIT(2,1,0),new PUNIT(0,2,0),new PUNIT(1,2,0),
	    new PUNIT(2,2,0),new PUNIT(0,3,0),new PUNIT(1,3,0),new PUNIT(2,3,0),new PUNIT(1,4,0),new PUNIT(2,4,0),       /* 12 */
	    new PUNIT(2,0,1),new PUNIT(3,0,1),new PUNIT(2,1,1),new PUNIT(3,1,1),new PUNIT(4,1,1),new PUNIT(1,2,1),
	    new PUNIT(2,2,1),new PUNIT(3,2,1),new PUNIT(4,2,1),new PUNIT(1,3,1),new PUNIT(2,3,1),new PUNIT(3,3,1),new PUNIT(4,3,1),   /* 13 */
	    new PUNIT(0,1,2),new PUNIT(1,1,2),new PUNIT(2,1,2),new PUNIT(0,2,2),new PUNIT(1,2,2),new PUNIT(2,2,2),
	    new PUNIT(0,3,2),new PUNIT(1,3,2),new PUNIT(2,3,2),new PUNIT(0,4,2),new PUNIT(1,4,2),new PUNIT(2,4,2),       /* 12 */
	    new PUNIT(1,0,3),new PUNIT(2,0,3),new PUNIT(0,1,3),new PUNIT(1,1,3),new PUNIT(2,1,3),new PUNIT(3,1,3),
	    new PUNIT(0,2,3),new PUNIT(1,2,3),new PUNIT(2,2,3),new PUNIT(3,2,3),new PUNIT(0,3,3),new PUNIT(1,3,3),new PUNIT(2,3,3),   /* 13 */
	    new PUNIT(1,0,4),new PUNIT(0,1,4),new PUNIT(2,1,4),new PUNIT(0,2,4),new PUNIT(1,2,4),new PUNIT(2,2,4),
	    new PUNIT(0,3,4),new PUNIT(1,3,4),new PUNIT(2,3,4),new PUNIT(0,4,4),new PUNIT(1,4,4),           /* 11 */
	    new PUNIT(1,0,5),new PUNIT(2,0,5),new PUNIT(0,1,5),new PUNIT(1,1,5),new PUNIT(2,1,5),new PUNIT(0,2,5),
	    new PUNIT(1,2,5),new PUNIT(2,2,5),new PUNIT(3,2,5),new PUNIT(0,3,5),new PUNIT(1,3,5),new PUNIT(2,3,5),new PUNIT(3,3,5),   /* 13 */
	    new PUNIT(2,1,6),new PUNIT(3,1,6),new PUNIT(4,1,6),new PUNIT(2,2,6),new PUNIT(3,2,6),new PUNIT(4,2,6),
	    new PUNIT(2,3,6),new PUNIT(3,3,6),new PUNIT(4,3,6),new PUNIT(2,4,6),new PUNIT(3,4,6),new PUNIT(4,4,6),       /* 12 */
	    new PUNIT(1,0,7),new PUNIT(2,0,7),new PUNIT(0,1,7),new PUNIT(1,1,7),new PUNIT(2,1,7),new PUNIT(3,1,7),
	    new PUNIT(0,2,7),new PUNIT(1,2,7),new PUNIT(2,2,7),new PUNIT(3,2,7),new PUNIT(1,3,7),new PUNIT(2,3,7),new PUNIT(3,3,7),   /* 13 */
	    new PUNIT(2,0,8),new PUNIT(0,1,8),new PUNIT(1,1,8),new PUNIT(0,2,8),new PUNIT(1,2,8),new PUNIT(2,2,8),
	    new PUNIT(0,3,8),new PUNIT(1,3,8),new PUNIT(2,3,8),new PUNIT(1,4,8),new PUNIT(2,4,8),           /* 11 */
	    new PUNIT(1,0,9),new PUNIT(3,0,9),new PUNIT(2,1,9),new PUNIT(3,1,9),new PUNIT(4,1,9),new PUNIT(1,2,9),
	    new PUNIT(2,2,9),new PUNIT(3,2,9),new PUNIT(4,2,9),new PUNIT(1,3,9),new PUNIT(2,3,9),new PUNIT(3,3,9),       /* 12 */
	    new PUNIT(1,0,10),new PUNIT(2,1,10),new PUNIT(3,1,10),new PUNIT(1,2,10),new PUNIT(2,2,10),new PUNIT(3,2,10),
	    new PUNIT(1,3,10),new PUNIT(2,3,10),new PUNIT(3,3,10),new PUNIT(1,4,10),new PUNIT(2,4,10),        /* 11 */
	    new PUNIT(1,0,11),new PUNIT(3,0,11),new PUNIT(0,1,11),new PUNIT(1,1,11),new PUNIT(2,1,11),new PUNIT(0,2,11),
	    new PUNIT(1,2,11),new PUNIT(2,2,11),new PUNIT(3,2,11),new PUNIT(1,3,11),new PUNIT(2,3,11),new PUNIT(3,3,11),   /* 12 */
	    new PUNIT(1,0,12),new PUNIT(3,0,12),new PUNIT(2,1,12),new PUNIT(1,2,12),new PUNIT(2,2,12),new PUNIT(3,2,12),
	    new PUNIT(1,3,12),new PUNIT(2,3,12),new PUNIT(3,3,12),new PUNIT(1,4,12),new PUNIT(2,4,12),new PUNIT(3,4,12),   /* 12 */
	    new PUNIT(1,0,13),new PUNIT(3,0,13),new PUNIT(0,1,13),new PUNIT(1,1,13),new PUNIT(2,1,13),new PUNIT(0,2,13),
	    new PUNIT(1,2,13),new PUNIT(2,2,13),new PUNIT(3,2,13),new PUNIT(0,3,13),new PUNIT(1,3,13),new PUNIT(2,3,13),   /* 12 */
	    new PUNIT(1,0,14),new PUNIT(0,1,14),new PUNIT(2,1,14),new PUNIT(0,2,14),new PUNIT(1,2,14),new PUNIT(2,2,14),
	    new PUNIT(0,3,14),new PUNIT(1,3,14),new PUNIT(2,3,14),new PUNIT(1,4,14),             /* 10 */
	    new PUNIT(1,0,15),new PUNIT(3,0,15),new PUNIT(2,1,15),new PUNIT(3,1,15),new PUNIT(4,1,15),new PUNIT(1,2,15),
	    new PUNIT(2,2,15),new PUNIT(3,2,15),new PUNIT(4,2,15),new PUNIT(2,3,15),new PUNIT(3,3,15),new PUNIT(4,3,15),   /* 12 */
	    new PUNIT(0,2,16),new PUNIT(1,2,16),new PUNIT(0,3,16),new PUNIT(1,3,16),new PUNIT(0,4,16),new PUNIT(1,4,16),
	    new PUNIT(0,5,16),new PUNIT(1,5,16),                     /*  8 */
	    new PUNIT(1,0,17),new PUNIT(2,0,17),new PUNIT(3,0,17),new PUNIT(0,1,17),new PUNIT(1,1,17),new PUNIT(2,1,17),new PUNIT(3,1,17),
	    new PUNIT(4,1,17),new PUNIT(0,2,17),new PUNIT(1,2,17),new PUNIT(2,2,17),new PUNIT(3,2,17),new PUNIT(4,2,17),   /* 13 */
	    new PUNIT(1,0,18),new PUNIT(2,0,18),new PUNIT(0,1,18),new PUNIT(1,1,18),new PUNIT(2,1,18),new PUNIT(3,1,18),
	    new PUNIT(0,2,18),new PUNIT(1,2,18),new PUNIT(2,2,18),new PUNIT(3,2,18),
	    new PUNIT(0,3,18),new PUNIT(1,3,18),new PUNIT(2,3,18),new PUNIT(3,3,18),             /* 18 */
	    new PUNIT(0,4,18),new PUNIT(1,4,18),new PUNIT(2,4,18),new PUNIT(3,4,18)
	};

	static int[] piefig = { 0,0,0,0, 1,1,1,1, 2,2, 3,3, 4,4,4,4, 5,5, 6 };

	//String[] pcolor = { "a","b","c","d","e","f","g" };

	public Tetrix() {
		super();
		
		// TODO 自動生成されたコンストラクター・スタブ
	}

	static Random randG = new Random();
	static double urand() { return Math.random(); }

	public static void main(String[] args)
	{
		int  no,i;
		int  seed;

		if ( args.length <= 1 )
		{
			System.out.printf( " Usage: tetrix4 Acoef Bcoef seed\n" );
		    return;
		}
		
		Acoef = Double.parseDouble( args[0] );
		Bcoef = Double.parseDouble( args[1] );
		seed  = Integer.parseInt( args[2] );

		randG.setSeed( seed );  // 乱数の種を指定
		initialize();
		displayState( 0,0 );

		while( true )
		{
		    for( i = 0; i < Units; i++ )
		      unitOut[i] = urand();
		    for( no = 1; no <= 20; no++ )
		    {
		    	updateState();
		        displayState( no,seed );
		        if ( goalCheck() )
		        {
		        	printGoal( no,seed );
		            return;
		        }
		    }
		    seed++;
		}
	}

	static void initialize()
	{
	  initializeMis();
	  System.out.printf( " Now, initializing T-matrix.\n" );
	  initializeT();
	}

	static void initializeMis()
	{
	  int  x,y,xp,yp,i,j,fig,sp,pie;

	  for( x = 0; x < X_width; x++ )
	    for( y = 0; y < Y_hight; y++ )
	      for( i = 0; i < S_elements; i++ ) Sxy[x][y][i] = -1;

	  for( i = 0; i < Units; i++ )
	    { x = punit[i].x; y = punit[i].y; fig = punit[i].fig;
	      for( j = 0; j < Tetra; j++ )
	        { xp = x + figpos[fig][j].x; yp = y + figpos[fig][j].y;
	          sp = 0;
	          while( Sxy[xp][yp][sp] != -1 ) sp++;
	          Sxy[xp][yp][sp] = i;
	        }
	    }

	  for( x = 0; x < Pieces; x++ )
	    for( y = 0; y < P_elements; y++ ) Pa[x][y] = -1;

	  for( i = 0; i < Units; i++ )
	    { pie = piefig[punit[i].fig]; sp = 0;
	      while( Pa[pie][sp] != -1 ) sp++;
	      Pa[pie][sp] = i;
	    }
	  //printf( "\x1b[2J" );
	}

	static void initializeT()
	{
	  int  x,y,ir,jr,i,j,bi,tp,ii;

	  for( i = 0; i < Tsize; i++ ) Ta[i] = Tb[i] = 0;
	  for( i = 0; i < Units; i++ ) I[i] = 0.0;

	  for( x = 0; x < X_width; x++ )
	    for( y = 0; y < Y_hight; y++ )
	      { ir = 0;
	        while( (i = Sxy[x][y][ir++]) != -1 )
	          { jr = 0; 
	            while( (j = Sxy[x][y][jr++]) != -1 )
	            if ( i <= j )
	              { bi = 0; for( ii = 0; ii <= i; ii++ ) bi += ii;
	                tp = i*Units + j - bi;
	                if ( i == j ) Ta[tp] -= 1;
	                else          Ta[tp] -= 2;
	              }
	          }
	      }

	  for( x = 0; x < Pieces; x++ )
	    { ir = 0;
	      while( (i = Pa[x][ir++]) != -1 )
	        { jr = 0; 
	          while( (j = Pa[x][jr++]) != -1 )
	          if ( i <= j )
	            { bi = 0; for( ii = 0; ii <= i; ii++ ) bi += ii;
	              tp = i*Units + j - bi;
	              if ( i == j ) Tb[tp] -= 1;
	              else          Tb[tp] -= 2;
	            }
	        }
	    }

	  for( x = 0; x < X_width; x++ )
	    for( y = 0; y < Y_hight; y++ )
	      { ir = 0;
	        while( (i = Sxy[x][y][ir++]) != -1 )
	          I[i] += Acoef;
	      }

	  for( x = 0; x < Pieces; x++ )
	    { ir = 0;
	      while( (i = Pa[x][ir++]) != -1 )
	        I[i] += Bcoef;
	    }
	}

	static void updateState()
	{
	  int  i,j,ir,jr,bi,ii,tp;
	  double aterm,ac,bc;

	  ac = 0.5*Acoef; bc = 0.5*Bcoef;

	  for( i = 0; i < Units; i++ )
	    { aterm = 0.0;
	      for( j = 0; j < Units; j++ )
	        { ir = i; jr = j; if ( i > j ) { ir = j; jr = i; }
	          bi = 0; for( ii = 0; ii <= ir; ii++ ) bi += ii;
	          tp = ir*Units + jr - bi;
	          aterm += ( ac*Ta[tp] + bc*Tb[tp] )*unitOut[j];
	        }
	      aterm += I[i];
	      unitOut[i] = 0.5*( 1.0 + Math.tanh( aterm/0.5 ) );
	    }
	}

	static void displayState( int n,int seed )
	{
	  int   k,d,dold,i,nf,x,y;
	  //static char  cfire[] = "\x1b[35m", cnormal[] = "\x1b[m";

	  System.out.printf( "Position  --->       1           2\n" );
	  System.out.printf( "Piece  1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0" );
	  System.out.printf( " 1 2 3 4 5 6 7 8   Cycle:%5d\n",n );
	  for( k = 0; k < Figures; k++ )
	    { System.out.printf( " %d %2d ",piefig[k]+1,k+1 ); dold = 6;
	      for( i = 0; i < Units; i++ )
	        if ( punit[i].fig == k )
	          { x = punit[i].x; y = punit[i].y;
	            d = 2*(5*y + x) + 4; dold = printTab( dold,d ) + 2;
	            //if ( unitOut[i] >= 0.5 ) printf( "%s",cfire );
	            nf = (int)(unitOut[i]*10.0); 
	            System.out.printf( " %1d",nf ); //printf( "%s",cnormal );
	          }
	      if ( k == 1 ) System.out.printf( "             Acoef:%5.2f",Acoef );
	      if ( k == 2 ) System.out.printf( "       Bcoef:%5.2f",Bcoef );
	      if ( k == 3 ) System.out.printf( "                 seed:%6d",seed );
	      System.out.printf( "\n" );
	    }
	}

	static int printTab( int xold,int x )
	{
	  int   i;

	  for( i = x - xold; i > 0; i-- ) System.out.printf( " " );
	  return( x );
	}

	static boolean goalCheck()
	{
	  int  k,i,j,x,y,xp,yp;
	  int[] pused = new int[Pieces];

	  for( x = 0; x < X_width; x++ )
	    for( y = 0; y < Y_hight; y++ ) state[x][y] = 0;
	  state[0][0] = state[4][0] = 1;
	  for( i = 0; i < Pieces; i++ ) pused[i] = 0;

	  for( i = 0; i < Units; i++ )
	    if ( unitOut[i] >= 0.5 )
	      { x = punit[i].x; y = punit[i].y; k = punit[i].fig;
	        for( j = 0; j < Tetra; j++ )
	          { xp = x + figpos[k][j].x; yp = y + figpos[k][j].y;
	            state[xp][yp] += 1;
	          }
	        pused[piefig[k]] = 1;
	      }

	  for( x = 0; x < X_width; x++ )
	    for( y = 0; y < Y_hight; y++ )
	      if ( state[x][y] != 1 ) return( false );
	  for( i = 0; i < Pieces; i++ )
	    if ( pused[i] == 0 ) return( false );

	  return( true );
	}

	static void printGoal( int cycle,int seed )
	{
	  int  k,i,j,x,y,xp,yp;
	  int[][] pos = new int[Pieces][3];

	  System.out.printf( " Find a solution !   Press any key. " );// getch();
	  //printf( "\x1b[2J" );

	  state[0][0] = state[4][0] = -1;
	  for( i = 1; i <= 5; i++ ) state[5][i] = -1;

	  for( i = 0; i < Units; i++ )
	    if ( unitOut[i] >= 0.5 )
	      { x = punit[i].x; y = punit[i].y; k = punit[i].fig;
	        pos[piefig[k]][0] = x; pos[piefig[k]][1] = y;
	        pos[piefig[k]][2] = k+1;
	        for( j = 0; j < Tetra; j++ )
	          { xp = x + figpos[k][j].x; yp = y + figpos[k][j].y;
	            state[xp][yp] = piefig[k];
	          }
	      }
	 
	  System.out.printf( "\n ---- TETRIX     One of the solution ----\n\n" );
	  System.out.printf( "      +---+---+---+     " );
	  System.out.printf( "Acoef:%5.2f   Bcoef:%5.2f\n      |",Acoef,Bcoef );
	  for( i = 1; i <= 3; i++ )
	    { k = state[i][0];
	      //printf( " %s%1d%s ",pcolor[k],k+1,pcolor[6] );
	    System.out.printf( " %1d ",k+1 );
	      if ( k == state[i+1][0] ) System.out.printf( " " ); else System.out.printf( "|" );
	    }
	  System.out.printf( "     seed:%6d   Cycle:%5d\n",seed,cycle );
	  for( j = 1; j <= 5; j++ )
	    { System.out.printf( "  +" );
	      for( i = 0; i < 5; i++ )
	        if ( state[i][j-1] == state[i][j] ) System.out.printf( "   +" );
	        else                System.out.printf( "---+" );
	      System.out.printf( "\n  |" );
	      for( i = 0; i < 5; i++ )
	        { k = state[i][j];
	          //printf( " %s%1d%s ",pcolor[k],k+1,pcolor[6] );
	        System.out.printf( " %1d ",k+1 );
	          if ( k == state[i+1][j] ) System.out.printf( " " ); else System.out.printf( "|" );
	        }
	      System.out.printf( "\n" );
	    }
	  System.out.printf( "  +---+---+---+---+---+\n\n  " );

	  for( i = 0; i < Pieces; i++ )
	    { System.out.printf( "Figure %2d--(%d,%d)   ",pos[i][2],pos[i][0],pos[i][1] );
	      if ( i == 3 ) System.out.printf( "\n  " );
	    }
	  System.out.printf( "\n" );
	}

}
