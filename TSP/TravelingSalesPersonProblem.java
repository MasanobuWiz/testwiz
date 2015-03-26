package TSP;

/* --------------------------------------------------------------------------------------------
 | 巡回セールスパーソン問題(TSP)
 | 　相互結合型ネットワークを巡回セールスパーソン問題に適用する．
 |   ◆ プログラムタイプ　アプレット
 |   ◆ 構成
 |　　　　Applet：TravelingSalesPersonProblem.java
 |      　NN部　：TSP.java
 | 
 | アプレットプログラム
 | 　相互結合型ネットワークのニューロンの状況を描画する．
 | 
 */

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class TravelingSalesPersonProblem  extends Applet 
		implements Runnable, ActionListener
{
	private Thread runner = null;

	// アプレットサイズ
	private final int FSizeWidth  = 600;
	private final int FSizeHeight = 400;

	// キャンバスサイズ
	private final int MenueSizeWidth = 150;  // 左メニュー領域の幅
	private final int CanSizeWidth   = FSizeWidth - MenueSizeWidth;
	private final int CanSizeHeight  = FSizeHeight;

	private TSP pTsp;

	// 更新回数
	private int nUpDate;
		
	// 描画クラス
	private Image    uBuffer;
	private Graphics uGraphics;

	// アプレットサイズを決める
	private Dimension fieldSize = new Dimension( FSizeWidth,FSizeHeight );
	// セルサイズ
	private Dimension cellSize = new Dimension( 1,1 ); // とりあえずのサイズ

	// 描画領域のサイズ
	private int width;
	private int height;	

	// 描画領域の格子数
	private final int nRow    = 10;  // 行数
	private final int nColumn = 10;  // 列数

	// 描画頻度をゆっくりしたい場合
	private final boolean onDuration = true;   // 設定時間間隔の有効/無効
	private final int      vDuration = 5;       // 設定時間間隔(mSec)

	private Button   startStop; 
	private boolean kicker = false;

	private Button   showNode; 

	private int   cityNo = 10;
	private long  seed   = 3;
	
	TextField textNoCity = new TextField( "10",5 );  // 都市数
	TextField textNoSeed = new TextField( "3",5 );   // 乱数の種
	Label     labelNo    = new Label( " 回数 " );

    Canvas sCanvas = new StatusCanvas();
	Color  bgColor = new Color(200,200,220);  // 背景色
	Color  ndColor = new Color(242,120,70);   // ノードの描画色
	Frame  stateWindow = new Frame();

	// 状態描画ウインドウのサイズ
	private int wStateWidth  = 300;
	private int wStateHeight = 300;
	private int pitchX = 25;
	private int pitchY = 25;

	class StatusCanvas extends Canvas 
	{
		public void paint( Graphics g ) 
		{
			drawGrid( g );
			drawPath( g );
		}
	}

	// stateWindow用の閉じる処理
	class WindowListener extends WindowAdapter
	{
		// ウィンドウの閉じるボタンをクリックされた
		public void windowClosing( WindowEvent e )
		{
			stateWindow.setVisible( false );
		}
	}

	class stateWindowCanvas extends Canvas
	{
		public void paint( Graphics g )
		{
	    	setBackground( bgColor );
			drawNode( g );
		}
	}

	// システムコール．アプレットの初期化処理
	public void init()
	{
		resize( fieldSize );
		width  = getSize().width;
		height = getSize().height;

		textNoCity.addActionListener( this );
		textNoSeed.addActionListener( this );

		setBackground( Color.lightGray );

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();

		setLayout(gridbag);

		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth= 1;

		// メニュー部の構成
		// １行目．パラメータ都市数の入力
		Label labelNoCity = new Label(" 都市数 ") ;
		gridbag.setConstraints( labelNoCity,constraints );
		add( labelNoCity );

		constraints.gridx = GridBagConstraints.RELATIVE;
		gridbag.setConstraints( textNoCity,constraints );
		add( textNoCity );

		// ２行目．パラメータ乱数の種の入力
		Label labelNoSeed = new Label(" 乱数の種 ") ;
		constraints.gridx = 0;
		constraints.gridy = 1;
		gridbag.setConstraints( labelNoSeed,constraints );
		add( labelNoSeed );

		constraints.gridx = GridBagConstraints.RELATIVE;
		gridbag.setConstraints( textNoSeed,constraints );
		add( textNoSeed );

		// ３行目．繰り返し回数の表示
		String noR = Integer.toString( nUpDate );
        
		labelNo.setText( " 回数 " + noR );
		constraints.gridx = 0;
		constraints.gridy = 2;
		gridbag.setConstraints( labelNo,constraints );
		add( labelNo );

		// ４行目．スタート・ストップボタン
		startStop = new Button( "Start/Stop" ); 
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 2;
		gridbag.setConstraints( startStop,constraints );
        add( startStop );

		// ５行目．スタート・ストップボタン
		showNode = new Button( "ノード状態" ); 
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.gridwidth = 2;
		gridbag.setConstraints( showNode,constraints );
        add( showNode );

		// 空白部の確保
		Label labelSpace = new Label("   ") ;
		constraints.gridx = 0;
		constraints.gridy = 5;
		gridbag.setConstraints( labelSpace,constraints );
		add( labelSpace );

		// 状態描画部
		constraints.gridheight = GridBagConstraints.REMAINDER;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
    	sCanvas.setSize( new Dimension( CanSizeWidth,CanSizeHeight ) );
    	sCanvas.setBackground( bgColor );
		constraints.gridx = 2;
		constraints.gridy = 0;
		gridbag.setConstraints( sCanvas,constraints );
    	add( sCanvas );

		setBackground( bgColor );

		uBuffer = createImage( width,height );
		uGraphics = uBuffer.getGraphics();

		// セルサイズを決める
		cellSize.width  = CanSizeWidth/nColumn;
		cellSize.height = CanSizeHeight/nRow;

		// 状態描画ウインドウの表示
	    // タイトル設定
		stateWindow.setTitle( "ノードの状態" );
	    //ウィンドウサイズ設定
		wStateWidth  = pitchX*cityNo;
		wStateHeight = pitchY*cityNo;
	    stateWindow.setSize( wStateWidth,wStateHeight );
	    //ウィンドウメッセージ取得用設定
	    WindowListener e = new WindowListener();
	    stateWindow.addWindowListener( e );
	    //キャンバスを配置
	    stateWindow.add( new stateWindowCanvas() );
	}

	public boolean action( Event e,Object what )
	{  
    	if ( e.target == startStop )
        { 
        	if ( kicker ) { kicker = false; stop(); }
            else          { kicker = true; start(); }
            return true;
        }
    	
    	if ( e.target == showNode )
        { 
    	    //ウィンドウ表示
    	 	stateWindow.setVisible( true );    		
        }
    	
        
        return false;
	}

    
	// 改行キーを伴ったキー入力の場合のイベント処理
	// Start/Stopボタンに反応した入力情報の取得は，start()関数で処理
	public void actionPerformed( ActionEvent e )
    {
    	if ( e.getSource() == textNoCity )
    	{
    		cityNo = Integer.parseInt( textNoCity.getText() );
    		if( cityNo < 0 )
    		{
    			cityNo = 5;
    			textNoCity.setText( "" + cityNo );  //文字列として設定
    		}
    	}
    	
    	if ( e.getSource() == textNoSeed )
    	{
    		seed = Integer.parseInt( textNoSeed.getText() );
    		if( seed < 0 )
    		{
    			seed = 1;
    			textNoSeed.setText( "" + seed );  //文字列として設定
    		}
    	}
    }
 
	// システムコール．スレッドの開始処理
	public void start()
	{
		runner = new Thread( this );
		runner.setPriority( Thread.MIN_PRIORITY );
		if ( nUpDate != 0 ) runner.start();

		// キー入力エリアからの情報取得
		cityNo = Integer.parseInt( textNoCity.getText() );
        seed = Integer.parseInt( textNoSeed.getText() );
        
        initializeAP();
		nUpDate++;
	}
	
	// システムコール．アプレットの実行
	public void run()
	{
		while( runner != null )
		{
			pTsp.updateState();
			nUpDate++;
			sCanvas.repaint();
			
			if ( onDuration )
			{
				try
				{
					Thread.currentThread().sleep( vDuration ); 
				}
				catch( InterruptedException e )
				{
					System.out.println( e ); 
				}
			}
		}
	}
	
	// システムコール．アプレットの停止処理
	public void stop()
	{
		runner = null;
	}
	
	// システムコール．アプレットの更新処理
	public void update( Graphics g )
	{
		nUpDate++;
		paint( uGraphics );
		g.drawImage( uBuffer,0,0,this );
	}
	
	private void initializeAP()
	{
		pTsp = new TSP( CanSizeWidth,CanSizeHeight,cityNo,seed );
		nUpDate = 0; 
	}
	
	public void drawGrid( Graphics g )
	{
		g.clearRect( 0,0,width-1,height-1 );
		
		g.setColor( Color.lightGray );
		g.drawRect( 0,0,width-1,height-1 );
		
		g.setColor( Color.lightGray );
		for ( int i = 0; i < width; i += cellSize.width )
			g.drawLine( i,0,i,height-1 );
		for ( int j = 0; j < height; j += cellSize.height )
			g.drawLine( 0,j,width,j );
	}
	
	public void drawPath( Graphics g )
	{
		boolean  find = false;
		DPOINT    pos;
		int      x,y,x1,y1;
		int      r = 3;
		
		g.setColor( Color.black );
		for( int j = 0; j < cityNo; j++ )
		{
			pos = pTsp.getCityXY( j );
			x = 10 + (int)(pos.x); y = 10 + (int)(pos.y);
			g.fillOval( x-r,y-r,2*r,2*r );
		}
		
		x1 = y1 = 0; // 初期化対策
		for( int j = 0; j < cityNo; j++ )
			for( int i = 0; i < cityNo; i++ )
			{
				double u = pTsp.getUintout( i,j );
				if ( u >= 0.5 )
				{
					pos = pTsp.getCityXY( i );
					x = 10 + (int)(pos.x); y = 10 + (int)(pos.y);
					if ( !find )
					{ 
						x1 = x; y1 = y;
						g.setColor( ndColor );
						g.drawString( pos.name,x1+4,y1 );
						g.setColor( Color.black );
					}
					else 
					{
						g.drawLine( x1,y1,x,y );
						x1 = x; y1 = y; 
						g.setColor( ndColor );
						g.drawString( pos.name,x1+4,y1 );
						g.setColor( Color.black );
					}
					find = true;
				}
			}
		
		String noR = Integer.toString( nUpDate );
		labelNo.setText( " 回数 " + noR );		
	}

	public void drawNode( Graphics g )
	{
		int r = 10;
		int xOrg = 40;
		int yOrg = 40;

		wStateWidth  = pitchX*(cityNo-1);
		wStateHeight = pitchY*(cityNo-1);

		g.clearRect( 0,0,wStateWidth-1,wStateHeight-1 );
		
		g.setColor( Color.black );
		g.drawString( "都市番号",xOrg-10,yOrg-28 );
		for ( int i = 0; i < cityNo; i++ )
		{
			int  ix = i*pitchX;
			g.setColor( Color.black );
			g.drawString( Integer.toString( i ),ix+xOrg-4,yOrg-13 );
			g.setColor( Color.lightGray );
			g.drawLine( ix+xOrg,yOrg,ix+xOrg,wStateHeight+yOrg );
		}
		
		g.setColor( Color.black );
		g.drawString( "順序",3,yOrg-13 );
		for ( int i = 0; i < cityNo; i++ )
		{
			int  iy = i*pitchY;
			g.setColor( Color.black );
			g.drawString( Integer.toString( i ),15,iy+yOrg+4 );
			g.setColor( Color.lightGray );
			g.drawLine( xOrg,iy+yOrg,wStateWidth+xOrg,iy+yOrg );
		}

		
		g.setColor( ndColor );
		for( int y = 0; y < cityNo; y++ )
		{
			for( int x = 0; x < cityNo; x++ )
			{
				int  ix = x*pitchX;
				int  iy = y*pitchY;
				double u = pTsp.getUintout( x,y );
				int  ur = (int)(r*u);
				g.fillOval( ix-ur+xOrg,iy-ur+yOrg,2*ur,2*ur );
			}
		}
	}
	
}
