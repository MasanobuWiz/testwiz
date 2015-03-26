package Equation;

/* --------------------------------------------------------------------------------------------
| 連立方程式の解法
| 　相互結合型ネットワークを連立方程式の解法に適用する．
|   ◆ プログラムタイプ　Javaアプリケーション
|   ◆ 構成
|      　NN部　：Equation.java
| 
*/

public class Equation {

	static final int Number = 5;
	static final int Dimension = 2;

	static double[][] unitOut = new double[Dimension][Number];
	static double Acoef = 1.0;
	static double Bcoef = 1.0;

	/* Equation 1 (Dimension = 2)    Answer  x = 2.0, y = 1.0 
	static double[][] a = { { 0.5, -1.0 }, { 3.0, 1.0 } };
	static double[]   b = { 0.0, 7.0 };
	*/
	/* Equation 2 (Dimension = 2)    Answer  x = 3.0, y = 2.0  */
	static double[][] a = { { 1.0, -1.0 }, { 1.0, 2.0 } };
	static double[]   b = { 1.0, 7.0 };

	public static void main( String[] args )
	{
		int    no;
		double en;
		
		if ( args.length <= 1 )
		{ 
			System.out.println( " Usage: equation Acoef Bcoef" );
			//return;
		}
		//Acoef = Double.parseDouble( args[1] );
		//Bcoef = Double.parseDouble( args[2] );

		initialize();
		
		for( no = 1; no < 10000; no++ )
		{ 
			updateState();
			displayState( no );
			en = calcEnergy();
			System.out.printf( " Energy = %f\n\n",en );
			if ( en == 0.0 ) break;
		}
		
		displayAnswer();
	}
	
	static void initialize()
	{
		int    i,j;
		
		for( i = 0; i < Dimension; i++ )
			for( j = 0; j < Number; j++ )
			{ unitOut[i][j] = 0.0;
			}
	}
	
	static void updateState()
	{
		int    i,j,jd,m,md;
		double un,unitin;
		double aterm,bterm;
		
		
		for( j = 0; j < Dimension; j++ )
			for( m = 0; m < Number; m++ ) 
			{ 
				unitin = 0.0;
				for( jd = 0; jd < Dimension; jd++ )
					for( md = 0; md < Number; md++ )
					{ 
						un = 0.0;
						for( i = 0; i < Dimension; i++ )
							un += a[i][j]*a[i][jd];
						unitin += un*unitOut[jd][md];
					}
			
				aterm = -2.0*Acoef*unitin;
			
				bterm = 0.0;
				for( i = 0; i < Dimension; i++ )
					bterm += b[i]*a[i][j];
				bterm = 2.0*Bcoef*bterm;
			
				unitin = aterm + bterm;
				unitOut[j][m] = 0.5*( 1.0 + Math.tanh( unitin/0.5 ) );
			}
		
	}
	
	static void displayState( int n )
	{
		int   i,j;
		
		System.out.printf( "           Units              Cycle : %d\n",n );
		System.out.printf( "vector " );
		for( i = 0; i < Number; i++ )
			System.out.printf( "%5d  ",i+1 );
		System.out.printf( "\n" );
		for( i = 0; i < Dimension; i++ )
		{ 
			System.out.printf( "  X%1d   ",i+1 );
			for( j = 0; j < Number; j++ )
			{ 
				System.out.printf( "%5.2f  ",unitOut[i][j] );
			}
			System.out.printf( "\n" );
		}
	}
	
	static void displayAnswer()
	{
		int   i,j;
		double  x;
		
		for( i = 0; i < Dimension; i++ )
		{
			x = 0.0;
			for( j = 0; j < Number; j++ )
				if ( unitOut[i][j] >= 0.5 ) x += 1.0;
			System.out.printf( "    X%d = %3.1f",i+1,x );
		}
		System.out.printf( ".\n" );
	}
	
	static double calcEnergy()
	{
		int    i,j,m;
		double uw,work,term1,term2,term3;
		
		term1 = term2 = term3 = 0.0;
		for( i = 0; i < Dimension; i++ ) term1 += b[i]*b[i];
		
		for( i = 0; i < Dimension; i++ )
		{ 
			work = 0.0;
			for( j = 0; j < Dimension; j++ )
				for( m = 0; m < Number; m++ )
				{ 
					if ( unitOut[j][m] >= 0.5 ) uw = 1.0; else uw = 0.0;
					work += a[i][j]*uw;
				}
			term2 += b[i]*work;
		}
		term2 = -2.0*term2;
		
		for( i = 0; i < Dimension; i++ )
		{ 
			work = 0.0;
			for( j = 0; j < Dimension; j++ )
				for( m = 0; m < Number; m++ )
				{ 
					if ( unitOut[j][m] >= 0.5 ) uw = 1.0; else uw = 0.0;
					work += a[i][j]*uw;
				}
			term3 += work*work;
		}
		
		return( term1 + term2 + term3 );
	}
}
