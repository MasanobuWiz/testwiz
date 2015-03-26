package TSP;

/* --------------------------------------------------------------------------------------------
 | ����Z�[���X�p�[�\�����(TSP)
 | �@���݌����^�l�b�g���[�N������Z�[���X�p�[�\�����ɓK�p����D
 |   �� �v���O�����^�C�v�@�A�v���b�g
 |   �� �\��
 |�@�@�@�@Applet�FTravelingSalesPersonProblem.java
 |      �@NN���@�FTSP.java
 | 
 | �A�v���b�g�v���O����
 | �@���݌����^�l�b�g���[�N�̃j���[�����̏󋵂�`�悷��D
 | 
 */

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class TravelingSalesPersonProblem  extends Applet 
		implements Runnable, ActionListener
{
	private Thread runner = null;

	// �A�v���b�g�T�C�Y
	private final int FSizeWidth  = 600;
	private final int FSizeHeight = 400;

	// �L�����o�X�T�C�Y
	private final int MenueSizeWidth = 150;  // �����j���[�̈�̕�
	private final int CanSizeWidth   = FSizeWidth - MenueSizeWidth;
	private final int CanSizeHeight  = FSizeHeight;

	private TSP pTsp;

	// �X�V��
	private int nUpDate;
		
	// �`��N���X
	private Image    uBuffer;
	private Graphics uGraphics;

	// �A�v���b�g�T�C�Y�����߂�
	private Dimension fieldSize = new Dimension( FSizeWidth,FSizeHeight );
	// �Z���T�C�Y
	private Dimension cellSize = new Dimension( 1,1 ); // �Ƃ肠�����̃T�C�Y

	// �`��̈�̃T�C�Y
	private int width;
	private int height;	

	// �`��̈�̊i�q��
	private final int nRow    = 10;  // �s��
	private final int nColumn = 10;  // ��

	// �`��p�x��������肵�����ꍇ
	private final boolean onDuration = true;   // �ݒ莞�ԊԊu�̗L��/����
	private final int      vDuration = 5;       // �ݒ莞�ԊԊu(mSec)

	private Button   startStop; 
	private boolean kicker = false;

	private Button   showNode; 

	private int   cityNo = 10;
	private long  seed   = 3;
	
	TextField textNoCity = new TextField( "10",5 );  // �s�s��
	TextField textNoSeed = new TextField( "3",5 );   // �����̎�
	Label     labelNo    = new Label( " �� " );

    Canvas sCanvas = new StatusCanvas();
	Color  bgColor = new Color(200,200,220);  // �w�i�F
	Color  ndColor = new Color(242,120,70);   // �m�[�h�̕`��F
	Frame  stateWindow = new Frame();

	// ��ԕ`��E�C���h�E�̃T�C�Y
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

	// stateWindow�p�̕��鏈��
	class WindowListener extends WindowAdapter
	{
		// �E�B���h�E�̕���{�^�����N���b�N���ꂽ
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

	// �V�X�e���R�[���D�A�v���b�g�̏���������
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

		// ���j���[���̍\��
		// �P�s�ځD�p�����[�^�s�s���̓���
		Label labelNoCity = new Label(" �s�s�� ") ;
		gridbag.setConstraints( labelNoCity,constraints );
		add( labelNoCity );

		constraints.gridx = GridBagConstraints.RELATIVE;
		gridbag.setConstraints( textNoCity,constraints );
		add( textNoCity );

		// �Q�s�ځD�p�����[�^�����̎�̓���
		Label labelNoSeed = new Label(" �����̎� ") ;
		constraints.gridx = 0;
		constraints.gridy = 1;
		gridbag.setConstraints( labelNoSeed,constraints );
		add( labelNoSeed );

		constraints.gridx = GridBagConstraints.RELATIVE;
		gridbag.setConstraints( textNoSeed,constraints );
		add( textNoSeed );

		// �R�s�ځD�J��Ԃ��񐔂̕\��
		String noR = Integer.toString( nUpDate );
        
		labelNo.setText( " �� " + noR );
		constraints.gridx = 0;
		constraints.gridy = 2;
		gridbag.setConstraints( labelNo,constraints );
		add( labelNo );

		// �S�s�ځD�X�^�[�g�E�X�g�b�v�{�^��
		startStop = new Button( "Start/Stop" ); 
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 2;
		gridbag.setConstraints( startStop,constraints );
        add( startStop );

		// �T�s�ځD�X�^�[�g�E�X�g�b�v�{�^��
		showNode = new Button( "�m�[�h���" ); 
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.gridwidth = 2;
		gridbag.setConstraints( showNode,constraints );
        add( showNode );

		// �󔒕��̊m��
		Label labelSpace = new Label("   ") ;
		constraints.gridx = 0;
		constraints.gridy = 5;
		gridbag.setConstraints( labelSpace,constraints );
		add( labelSpace );

		// ��ԕ`�敔
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

		// �Z���T�C�Y�����߂�
		cellSize.width  = CanSizeWidth/nColumn;
		cellSize.height = CanSizeHeight/nRow;

		// ��ԕ`��E�C���h�E�̕\��
	    // �^�C�g���ݒ�
		stateWindow.setTitle( "�m�[�h�̏��" );
	    //�E�B���h�E�T�C�Y�ݒ�
		wStateWidth  = pitchX*cityNo;
		wStateHeight = pitchY*cityNo;
	    stateWindow.setSize( wStateWidth,wStateHeight );
	    //�E�B���h�E���b�Z�[�W�擾�p�ݒ�
	    WindowListener e = new WindowListener();
	    stateWindow.addWindowListener( e );
	    //�L�����o�X��z�u
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
    	    //�E�B���h�E�\��
    	 	stateWindow.setVisible( true );    		
        }
    	
        
        return false;
	}

    
	// ���s�L�[�𔺂����L�[���͂̏ꍇ�̃C�x���g����
	// Start/Stop�{�^���ɔ����������͏��̎擾�́Cstart()�֐��ŏ���
	public void actionPerformed( ActionEvent e )
    {
    	if ( e.getSource() == textNoCity )
    	{
    		cityNo = Integer.parseInt( textNoCity.getText() );
    		if( cityNo < 0 )
    		{
    			cityNo = 5;
    			textNoCity.setText( "" + cityNo );  //������Ƃ��Đݒ�
    		}
    	}
    	
    	if ( e.getSource() == textNoSeed )
    	{
    		seed = Integer.parseInt( textNoSeed.getText() );
    		if( seed < 0 )
    		{
    			seed = 1;
    			textNoSeed.setText( "" + seed );  //������Ƃ��Đݒ�
    		}
    	}
    }
 
	// �V�X�e���R�[���D�X���b�h�̊J�n����
	public void start()
	{
		runner = new Thread( this );
		runner.setPriority( Thread.MIN_PRIORITY );
		if ( nUpDate != 0 ) runner.start();

		// �L�[���̓G���A����̏��擾
		cityNo = Integer.parseInt( textNoCity.getText() );
        seed = Integer.parseInt( textNoSeed.getText() );
        
        initializeAP();
		nUpDate++;
	}
	
	// �V�X�e���R�[���D�A�v���b�g�̎��s
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
	
	// �V�X�e���R�[���D�A�v���b�g�̒�~����
	public void stop()
	{
		runner = null;
	}
	
	// �V�X�e���R�[���D�A�v���b�g�̍X�V����
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
		
		x1 = y1 = 0; // �������΍�
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
		labelNo.setText( " �� " + noR );		
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
		g.drawString( "�s�s�ԍ�",xOrg-10,yOrg-28 );
		for ( int i = 0; i < cityNo; i++ )
		{
			int  ix = i*pitchX;
			g.setColor( Color.black );
			g.drawString( Integer.toString( i ),ix+xOrg-4,yOrg-13 );
			g.setColor( Color.lightGray );
			g.drawLine( ix+xOrg,yOrg,ix+xOrg,wStateHeight+yOrg );
		}
		
		g.setColor( Color.black );
		g.drawString( "����",3,yOrg-13 );
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
