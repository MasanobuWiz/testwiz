package TSP;

import java.util.Random;
/* -------------------------------------------------------------------------------
| DPOINTクラス
| 座標表現用の補助クラス．その点の名前も含んでいる．
*/
class DPOINT
{
	double  x,y;
	String   name;
}


/* -------------------------------------------------------------------------------
| TSubjectクラス
| 
*/
public class TSP
{
	public static final int MaxNumber = 20;
	public static final int MaxDimension = 5;
	public static final int INum = 5;
	public static final int IDim = 2;
	
	public static final int CityNo = 10;

	public static final double ACOEF   = 0.72;
	public static final double BCOEF   = 0.76;
	public static final double DCOEF   = 2.96;
	public static final double MAXACOEF = 4.0;
	public static final double MAXBCOEF = 4.0;
	public static final double MAXDCOEF = 4.0;
	
	int    cityNo;
	double [][] distance;
	double [][] unitout;
	double  Acoef,Bcoef,Dcoef;
	
	// 描画領域のサイズ
	int   axWidth,ayWidth;
	int   orgX,orgY;
	
	DPOINT [] cityxy;

	int      nlpattern,ptest;
	boolean  errFlag;
	int      maxIter;
	
	Random   rand;
	
	/* =============================================================================
	 | TSP
	 |   ホップフィールドニューラルネットワークモジュールのコンストラクタ
	 |   定数の初期設定を行う．
	 | 
	 */
	TSP( int wd,int ht,int cNo,long seed )
	{
		cityNo = cNo;
		axWidth = wd; ayWidth = ht;
		
		Acoef = ACOEF;
		Bcoef = BCOEF;
		Dcoef = DCOEF;

		distance = new double[cityNo][cityNo];
		unitout  = new double[cityNo][cityNo];
		cityxy   = new DPOINT[cityNo];		

		//Math.乱数の種
		rand = new Random( seed );
		initializeData();
		initializeNetwork();
	}

	/* =============================================================================
	 | initializeNetwork
	 |   ホップフィールドネットワークの初期化を行う．
	 |   
	 */
	public void  initializeNetwork()
	{
		initializeWeight();
	
	}
	
	
	public void initializeData()
	{
		int    i,j;
		double dtotal;
		
		double sc = 0.8;  // 描画範囲の80%を使用ということ
		orgX = axWidth/2; orgY = ayWidth/2;
		for( i = 0; i < cityNo; i++ )
		{ 
			cityxy[i] = new DPOINT();
			cityxy[i].x = sc*axWidth*rand.nextDouble() + 10;
			cityxy[i].y = sc*ayWidth*rand.nextDouble() + 20;
			cityxy[i].name = "c" + Integer.toString( i );
		}
		
		dtotal = 0.0;
		for( i = 0; i < cityNo; i++ )
			for( j = 0; j < cityNo; j++ )
			{ 
				distance[i][j] = 0.1*
				Math.sqrt( (cityxy[i].x - cityxy[j].x)*(cityxy[i].x - cityxy[j].x) +
						   (cityxy[i].y - cityxy[j].y)*(cityxy[i].y - cityxy[j].y) );
				dtotal += distance[i][j];
			}
		
		for( i = 0; i < cityNo; i++ )
			for( j = 0; j < cityNo; j++ )
				distance[i][j] = 10.0*distance[i][j]/dtotal;
		
		for( i = 0; i < cityNo; i++ )
			for( j = 0; j < cityNo; j++ )
			{ 
				unitout[i][j] = rand.nextDouble();
			}
	
	}
	
	/* =============================================================================
	 | UpdateState
	 |   ホップフィールドネットワークの状態を変化させる．
	 |   
	 */
	public void  updateState()
	{
		int     i,j,n,m,jm,jp;
		double  unitin;
		double  aterm,bterm,dterm;
		
		for( i = 0; i < cityNo; i++ )
			for( j = 0; j < cityNo; j++ )
			{ 
				aterm = bterm = dterm = 0.0;
				for( n = 0; n < cityNo; n++ ) aterm += unitout[i][n];
				aterm = -Acoef*( aterm - unitout[i][j] );
			
				for( m = 0; m < cityNo; m++ ) bterm += unitout[m][j];
				bterm = -Bcoef*( bterm - unitout[i][j] );
			
				if ( j-1 == -1 )     jm = cityNo - 1; else jm = j - 1;
				if ( j+1 == cityNo ) jp = 0; else jp = j + 1;
				for( m = 0; m < cityNo; m++ )
					dterm += distance[i][m]*(unitout[m][jp] + unitout[m][jm]);
				dterm = -Dcoef*dterm;
			
				unitin = aterm + bterm + dterm + Acoef + Bcoef;
				unitout[i][j] = 0.5*( 1.0 + Math.tanh( unitin/0.5 ) );
			}
	}
	
	/* == 課題関数 =================================================================
	 | CalcEnergy
	 |   ホップフィールドネットワークの状態が持つエネルギーを算出する．
	 |
	 | なし：
	 |   
	 */
	public double calcEnergy()
	{
		int    i,j,m,jp,jm;
		double term1,term2,term3;
		
		term1 = term2 = term3 = 0.0;
		
		for( i = 0; i < cityNo; i++ )
			for( j = 0; j < cityNo; j++ )
				for( m = 0; m < cityNo; m++ )
					if ( m != j ) term1 += unitout[i][j]*unitout[i][m];
		term1 = 0.5*Acoef*term1;
		
		for( j = 0; j < cityNo; j++ )
			for( i = 0; i < cityNo; i++ )
				for( m = 0; m < cityNo; m++ )
					if ( m != i ) term2 += unitout[i][j]*unitout[m][j];
		term2 = 0.5*Bcoef*term2;
		
		for( i = 0; i < cityNo; i++ )
			for( j = 0; j < cityNo; j++ )
			{ 
				if ( j-1 == -1 )     jm = cityNo - 1; else jm = j - 1;
				if ( j+1 == cityNo ) jp = 0; else jp = j + 1;
				for( m = 0; m < cityNo; m++ )
					term3 +=
						distance[i][m]*unitout[i][j]*(unitout[m][jp] + unitout[m][jm]);
			}
		
		term3 = 0.5*Dcoef*term3;
		
		return( term1 + term2 + term3 );
	}
	
	/* == 課題関数 =================================================================
	 | InitializeWeight
	 |
	 | 課題：ネットワークの重みを初期化する
	 |　　　 
	 | 引数：なし
	 */
	private void  initializeWeight()
	{
		for( int i = 0; i < cityNo; i++ )
			for( int j = 0; j < cityNo; j++ )
			{ 
				unitout[i][j] = rand.nextDouble();
			}
	}
	
	
	/* =============================================================================
	 | アクセッサーの定義
	 |
	 */
	
	public void   setAcoef( double d ) { Acoef = d; }
	public void   setBcoef( double d ) { Bcoef = d; }
	public void   setDcoef( double d ) { Dcoef = d; }
	
	public DPOINT  getCityXY( int id ) { return cityxy[id]; }
	public double getUintout( int i,int j ) { return unitout[i][j]; }
	
	public void   setMaxIter( int d ) { maxIter = d; }
	
}
