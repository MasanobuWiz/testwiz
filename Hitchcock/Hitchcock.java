package Hitchcock;

/* --------------------------------------------------------------------------------
 |
 | 効果的な配送を行いたい ： ヒッチコック問題
 |
 |  > hitchcok 0.55
 |
 ---------------------------------------------------------------------------------
*/

public class Hitchcock {

	static final int Mmax = 4;
	static final int Nmax = 5;
	static final int Qmax = 7;

	static double[][][] unitOut = new double[Mmax][Nmax][Qmax];
	static double[][]   bias = new double[Mmax][Nmax];

	static double Acoef = 29.0;
	static double Bcoef = 80.0;
	static double Ccoef = 80.0;
	static double Dcoef;

	static final double X0 = 0.1;

	static int[]  source = { 5, 3, 4, 6 };
	static int[]  destination = { 2, 7, 3, 2, 4 };

	static double[][] cost =
	{
		{ 5.0, 1.0, 7.0, 3.0, 3.0 },
	    { 2.0, 3.0, 6.0, 9.0, 5.0 },
	    { 6.0, 4.0, 8.0, 1.0, 4.0 },
	    { 3.0, 2.0, 2.0, 2.0, 4.0 }
	};
	
	public static void main( String[] args )
	{
		int    no;
		double en;
		
		if ( args.length <= 1 )
		{ 
			System.out.println( " Usage: hitchcok Dcoef" );
			//return;
		}
		Dcoef = Double.parseDouble( args[0] );

		initialize();
		
		for( no = 1; no < 30; no++ )
		{ 
			updateState();
			displayState( no );
			en = calcEnergy();
			System.out.printf( " Energy = %f\n\n",en );
			if ( en == 0.0 ) break;
		}
		
		displayState( 30 );
	}
	
	static void initialize()
	{
		int  x,y,i;

		for( x = 0; x < Mmax; x++ )
		  for( y = 0; y < Nmax; y++ )
		  {
			  for( i = 0; i < Qmax; i++ )
		          unitOut[x][y][i] = 0.5;
		      bias[x][y] = -2*Acoef + Bcoef*source[x] + Ccoef*destination[y];
		  }
	}
	
	static void updateState()
	{
		  int     x,y,i,xd,yd,id;
		  double  unitin,aterm,bterm,cterm,dterm;

		  for( x = 0; x < Mmax; x++ )
		    for( y = 0; y < Nmax; y++ )
		      for( i = 0; i < Qmax; i++ )
		        { aterm = 4*Acoef*unitOut[x][y][i];

		          bterm = 0.0;
		          for( yd = 0; yd < Nmax; yd++ )
		            for( id = 0; id < Qmax; id++ )
		              bterm += unitOut[x][yd][id];
		          bterm = Bcoef*bterm;

		          cterm = 0.0;
		          for( xd = 0; xd < Mmax; xd++ )
		            for( id = 0; id < Qmax; id++ )
		              cterm += unitOut[xd][y][id];
		          cterm = Ccoef*cterm;

		          dterm = 0.0;
		          for( xd = 0; xd < Mmax; xd++ )
		            for( yd = 0; yd < Nmax; yd++ )
		              for( id = 0; id < Qmax; id++ )
		                dterm += cost[xd][yd]*unitOut[xd][yd][id];
		          dterm = Dcoef*cost[x][y]*dterm;

		          unitin = ( aterm - bterm - cterm - dterm + bias[x][y] )/X0;

		          if ( unitin > 11000.0 )  unitin = 11000.0;
		          if ( unitin < -11000.0 ) unitin = -11000.0;
		          unitOut[x][y][i] = 0.5*( 1.0 + Math.tanh(unitin) );
		        }
		
	}
	
	static void displayState( int n )
	{
		int     x,y,i;
		double  fxy;
		  
		System.out.printf( "     Destination      ---- %d\n",n );
		System.out.printf( "Source   1    2    3    4    5\n" );
		for( x = 0; x < Mmax; x++ )
		{
			System.out.printf( "   %d  ",x+1 );
		    for( y = 0; y < Nmax; y++ )
		    {
		    	fxy = 0.0;
		        for( i = 0; i < Qmax; i++ ) fxy += unitOut[x][y][i];
		        System.out.printf( "%5.2f  ",fxy );
		    }
		    System.out.printf( "\n" );
		}
	}
	
	static double calcEnergy()
	{
		int     x,y,i;
		double  work,en1,en2,en3,en4;

		en1 = 0.0;
		for( x = 0; x < Mmax; x++ )
		    for( y = 0; y < Nmax; y++ )
		      for( i = 0; i < Qmax; i++ )
		        { work = 1.0 - 2.0*unitOut[x][y][i];
		          en1 += work*work;
		        }
		en1 = -0.5*Acoef*en1;

		en2 = 0.0;
		for( x = 0; x < Mmax; x++ )
		{ 
			work = 0.0;
		    for( y = 0; y < Nmax; y++ )
		        for( i = 0; i < Qmax; i++ )
		          work += unitOut[x][y][i];
		    work = source[x] - work;
		    en2 += work*work;
		}
		en2 = 0.5*Bcoef*en2;

		en3 = 0.0;
		for( y = 0; y < Nmax; y++ )
		{
			work = 0.0;
		    for( x = 0; x < Mmax; x++ )
		        for( i = 0; i < Qmax; i++ )
		          work += unitOut[x][y][i];
		      work = destination[y] - work;
		      en3 += work*work;
		}
		en3 = 0.5*Ccoef*en3;

		en4 = 0.0;
		for( x = 0; x < Mmax; x++ )
		    for( y = 0; y < Nmax; y++ )
		      for( i = 0; i < Qmax; i++ )
		        en4 += cost[x][y]*unitOut[x][y][i];
		    en4 = 0.5*Dcoef*en4*en4;

		return( en1 + en2 + en3 + en4 );
	}

}
