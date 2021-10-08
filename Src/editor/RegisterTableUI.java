package editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import jssc.SerialPort;

import javax.swing.JToggleButton;

import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import java.awt.GridLayout;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.event.MouseAdapter;

public class RegisterTableUI extends JFrame
{
	private SerialConnect serialConnect;
	private PacketProcessing packetProcess;
	
	private JPanel contentPane;
	private JButton[] btnValue;
	private static JTextArea textAreaLog;
	private static JTextField[][] arrTextField;
	
	
	private static int connectedState;
	private int selectedChip;
	private static int selectedAddress;
	private static int selectedValue=0;
	private static String[][] strRegisterTable = new String[16][16];


	
	public RegisterTableUI()
	{
		connectedState=0;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		changeTitle("EASTERN MASTEC Register Table");
		
		//sort to center
		Toolkit kit=Toolkit.getDefaultToolkit();
		Dimension frameSize = new Dimension(1000,800);
		Dimension screenSize = kit.getScreenSize();
		setBounds(screenSize.width/2 - frameSize.width/2 , screenSize.height/2 - frameSize.height/2, 1536 , 801);
		
		//change Icon
		Image icon=kit.getImage("image/logo.png");
		setIconImage(icon);
		
		//init object
		serialConnect = SerialConnect.getInstance();
		packetProcess = PacketProcessing.getInstance();

		//init gui
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panelSetting = new JPanel();
		panelSetting.setBounds(12, 10, 821, 182);
		contentPane.add(panelSetting);
		panelSetting.setLayout(null);
		
		//port
		JComboBox comboBoxPort = new JComboBox();
		comboBoxPort.addPopupMenuListener(new PopupMenuListener()
		{
			public void popupMenuCanceled(PopupMenuEvent arg0)
			{
			}
			public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0)
			{
			}
			public void popupMenuWillBecomeVisible(PopupMenuEvent arg0)
			{
				comboBoxPort.removeAllItems();
				//Add port list to comboBox
				try
				{
					String[] portList = SerialConnect.getPortList();
					
			        for(int i = 0; i < portList.length; i++)
			        {
			        	comboBoxPort.addItem(portList[i]);
			        }
				}
				catch (Exception e)
				{
					e.printStackTrace();
					//System.out.println("get Port List error.");
				}
			}
		});
		comboBoxPort.setBounds(12, 61, 220, 41);
		panelSetting.add(comboBoxPort);
		
		JLabel lblState = new JLabel("Select Port");
		lblState.setBounds(12, 10, 220, 41);
		panelSetting.add(lblState);
		
		JToggleButton btnConnect = new JToggleButton("Open");
		btnConnect.addActionListener(new ActionListener()
		{	
			public void actionPerformed(ActionEvent ae)
			{
				comboBoxPort.setEnabled(false);
				
				if( connectedState>0 ) // open -> close
				{
					try
					{
						serialConnect.disConnect();
						connectedState=0;
						
						changeTitle("EASTERN MASTEC Register Table - Disconnected");
						lblState.setText("Disconnected.");
						btnConnect.setText("Open");
						btnConnect.setSelected(false);
						comboBoxPort.setEnabled(true);
					}
					catch(Exception e)
					{
						e.printStackTrace();
						JOptionPane.showMessageDialog(null, "Disconnect failed.", "Error", JOptionPane.ERROR_MESSAGE);
						
						changeTitle("EASTERN MASTEC Register Table - Error ");
						lblState.setText("Disconnect failed.");
						btnConnect.setText("Close");
						btnConnect.setSelected(true);
						comboBoxPort.setEnabled(false);
					}
				}
				else if( connectedState==0 ) // close -> open
				{
					String port = comboBoxPort.getSelectedItem().toString();
					//int speed       = SerialPort.BAUDRATE_38400;
					int speed       = SerialPort.BAUDRATE_115200;
					int data		= SerialPort.DATABITS_8;
					int stop		= SerialPort.STOPBITS_1;
					int parity		= SerialPort.PARITY_NONE;
					
					try
					{
						serialConnect.connect(port, speed, data, stop, parity);
						connectedState = 1;
						
						changeTitle("EASTERN MASTEC Register Table - Connected");
						lblState.setText("Connected.");
						btnConnect.setText("Close");
						btnConnect.setSelected(true);
						comboBoxPort.setEnabled(false);
					}
					catch(Exception e)
					{
						e.printStackTrace();
						JOptionPane.showMessageDialog(null, "Connect failed.", "Error", JOptionPane.ERROR_MESSAGE);
						
						changeTitle("EASTERN MASTEC Register Table - Error");
						lblState.setText("Connect Failed.");
						btnConnect.setText("Open");
						btnConnect.setSelected(false);
						comboBoxPort.setEnabled(true);
					}
				}
			}
		});
		btnConnect.setBounds(12, 112, 220, 57);
		panelSetting.add(btnConnect);
		
		//chip
		JLabel lblChip = new JLabel("Chip");
		lblChip.setBounds(244, 10, 115, 41);
		panelSetting.add(lblChip);
		
		JComboBox comboBoxChip = new JComboBox();
		comboBoxChip.setBounds(244, 61, 115, 41);
		panelSetting.add(comboBoxChip);
		
		//value
		JLabel lblNewLabel = new JLabel("Value (bit)");
		lblNewLabel.setBounds(475, 10, 350, 41);
		panelSetting.add(lblNewLabel);

		btnValue = new JButton[8];
		for(int i=0; i<8; i++)
		{
			int size=45;
			btnValue[i] = new JButton("0");
			btnValue[i].setBounds(436+size*i, 60, size, size);
			panelSetting.add(btnValue[i]);
		}

		// function
		JButton btnSaveDump = new JButton("Save Dump");
		btnSaveDump.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnSaveDump.setBounds(244, 112, 180, 57);
		panelSetting.add(btnSaveDump);
		
		JButton btnApplyDump = new JButton("Apply Dump");
		btnApplyDump.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnApplyDump.setBounds(436, 112, 180, 57);
		panelSetting.add(btnApplyDump);
		
		JButton btnReadAll = new JButton("Read All");
		btnReadAll.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent arg0)
			{
				try
				{
					serialConnect.write('r');
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		btnReadAll.setBounds(628, 112, 179, 57);
		panelSetting.add(btnReadAll);
		
		//log
		textAreaLog = new JTextArea();
		
		JScrollPane scroll = new JScrollPane(textAreaLog);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setBounds(840, 10, 660, 740);
		contentPane.add(scroll);

		// table
		JPanel panelTable = new JPanel();
		panelTable.setBounds(12, 202, 821, 550);
		contentPane.add(panelTable);
		panelTable.setLayout(new GridLayout(17, 17, 0, 0));
		
		arrTextField = new JTextField[17][17];
		for(int i=0; i<17; i++)
		{
			for(int j=0; j<17; j++)
			{
				arrTextField[i][j] = new JTextField("00");
				arrTextField[i][j].setColumns(2);

				//arrTextField[i][j].setDragEnabled(true);
				
				arrTextField[i][j].addKeyListener(new TextFieldKeyHandler());
				arrTextField[i][j].setFocusTraversalKeysEnabled(false);	// tab key
				arrTextField[i][j].addFocusListener(new TextFieldFocusHandler());				
				arrTextField[i][j].setHorizontalAlignment(JTextField.CENTER);
				
				panelTable.add(arrTextField[i][j]);
				
				if(i==0 && j!=0)
				{
					arrTextField[i][j].setText( (Integer.toHexString(j-1)).toUpperCase());
					arrTextField[i][j].setEditable(false);
					arrTextField[i][j].setFocusable(false);
				}
			}
			
			if(i!=0)
			{
				arrTextField[i][0].setText( (Integer.toHexString(i-1)).toUpperCase());
				arrTextField[i][0].setEditable(false);
				arrTextField[i][0].setFocusable(false);
			}
		}

	}
	
	

	public void changeTitle(String s)
	{
		this.setTitle(s);
	}

	public static void addTextArea(String s)
	{
		textAreaLog.append(s);
		textAreaLog.setCaretPosition(textAreaLog.getDocument().getLength());
	}
	
	public static void showErrorMsgBox(String msg)
	{
		JOptionPane.showMessageDialog(null, msg , "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	public static JTextField[][] getArrTextField()
	{
		return arrTextField;
	}

	public static void updateRegisterTable()
	{
		for(int i=0; i<16; i++)
		{
			for(int j=0; j<16; j++)
			{
				arrTextField[i+1][j+1].setText( strRegisterTable[i][j].toUpperCase() );
			}
		}

	}
	
	public static void setSelectedAddress(int addr)
	{
		selectedAddress = addr;
	}
	public static int getSelectedAddress()
	{
		return selectedAddress;
	}
	
	public static void setSelectedValue(int val)
	{
		selectedValue = val;
	}
	public static int getSelectedValue()
	{
		return selectedValue;
	}

	public static String[][] getStrRegisterTable()
	{
		return strRegisterTable;
	}

}



class TextFieldMouseHandler implements MouseInputListener
{

	@Override
	public void mouseClicked(MouseEvent e)
	{
		// ���콺 ��ư�� Ŭ���� ��� �߻��Ǵ� �̺�Ʈ 

	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		// ���콺 Ŀ���� ������Ʈ �������� ������ �� �߻��Ǵ� �̺�Ʈ
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		// ���콺 Ŀ���� ������Ʈ ���� ������ ������ �߻��Ǵ� �̺�Ʈ
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		// ���콺 ��ư�� �������� �� �߻��ϴ� �̺�Ʈ 
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		// ���콺 ��ư�� ������ ������� �� �߻��ϴ� �̺�Ʈ 
	}

	@Override
	public void mouseDragged(MouseEvent arg0)
	{
		// ���콺 ��ư�� Ŭ���� ���¿��� ������ �� �߻��Ǵ� �̺�Ʈ 
	}

	@Override
	public void mouseMoved(MouseEvent arg0)
	{
		// ���콺 Ŀ���� ������ �� �߻��Ǵ� �̺�Ʈ 
	}
}

class BtnValueMouseHandler implements MouseListener
{

	@Override
	public void mouseClicked(MouseEvent e)
	{
		// TODO Auto-generated method stub
		JButton jb = (JButton) e.getSource();

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}

class TextFieldKeyHandler implements KeyListener
{

/*
	KeyEvent�޼ҵ�

	getKeyChar - Ÿ���ε� key���� ��ȯ. ��ǻ� Ű������� �����ϰ� Ÿ���ε� ���� ��ȯ.
	getKeyCode - Ÿ���ε� Ű������ ��ġ�� ���� ���� ������ ��ȯ. �� �޼ҵ���п� ���� ctrl�� ������ ctrl���� ��������
	getkeyLocation -  Ű������ ��ġ�� �� 4������ �����µ� 1�� Ű���� ��ü, 2�� ���� ����Ű, 3�� ������ ����Ű, 4�� Ű�е�
	paramString - key���忡 ���õ� ��ü �Ķ���� ��ȯ
	getWhen - �󸶳� ����������
	isControlDown - Control�� ����������
	isShiftDown - Shift�� ����������
	isAltDown - Alt�� ����������
	isMetaDown = MetaŰ�� ����������(������ ������ cmd, �����쿡���� window,��� Ű���忡 �����ϴ� metaŰ)
*/
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		// keyTyped - Ű�� Ÿ���� ������ �߻��ϴ� �̺�Ʈ. ���״�� Ÿ������ �Ǿ��ϹǷ� Ÿ������ ���� �ʴ� ����Ű�� �۵�ġ ����.
		//System.out.println("keyPressed : "+e.getKeyCode() );
		
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		// keyReleased - � Ű�� ������� Ű�� �������� �� �߻��ϴ� �̺�Ʈ
		//System.out.println("keyRelease : "+e.getKeyCode() );
		
		int addr = RegisterTableUI.getSelectedAddress();
		int val = RegisterTableUI.getSelectedValue();
		JTextField[][] arrtf =  RegisterTableUI.getArrTextField();
		//System.out.println("addr/16 = " + addr/16 + "\t addr%16 = " + addr%16);
		
		if( (e.getKeyCode() == KeyEvent.VK_TAB) )
		{
			//System.out.println("tab");
			
			int calAddr = addr-16-1;
			int calVal = Integer.parseInt( arrtf[addr/16][addr%16].getText() , 16 );

			if( calVal < 0x10 )
			{
				arrtf[addr/16][addr%16].setText("0"+Integer.toHexString(calVal).toUpperCase());
			}
			else
			{
				arrtf[addr/16][addr%16].setText(Integer.toHexString(calVal).toUpperCase());
			}
			
			PacketProcessing.writeRegister(calAddr, calVal);

			//System.out.println("tab addr = " + Integer.toHexString(addr));
			//System.out.println("tab val = " + Integer.toHexString(val));
			
			arrtf[addr/16][addr%16+1].requestFocus(); // move focus

		}
		else if( (e.getKeyCode() == KeyEvent.VK_ENTER) )
		{
			//System.out.println("enter");
			
			int calAddr = addr-16-1;
			int calVal = Integer.parseInt( arrtf[addr/16][addr%16].getText() , 16 );
			
			if( calVal < 0x10 )
			{
				arrtf[addr/16][addr%16].setText("0"+Integer.toHexString(calVal).toUpperCase());
			}
			else
			{
				arrtf[addr/16][addr%16].setText(Integer.toHexString(calVal).toUpperCase());
			}
			
			PacketProcessing.writeRegister(calAddr, calVal);

		}
		else if( (e.getKeyCode() == KeyEvent.VK_ESCAPE) )
		{
			//System.out.println("esc");
			if( val < 0x10 )
			{
				arrtf[addr/16][addr%16].setText("0"+Integer.toHexString(val).toUpperCase());
			}
			else
			{
				arrtf[addr/16][addr%16].setText(Integer.toHexString(val).toUpperCase());
			}
		}
		
		else if( (e.getKeyChar() >= '0' && e.getKeyChar() <= '9') ||
				 (e.getKeyChar() >= 'a' && e.getKeyChar() <= 'f') )
		{
			if( arrtf[addr/16][addr%16].getText().length()>=3)
			{
				//arrtf[addr/16][addr%16].setText(Integer.toHexString(val));
				arrtf[addr/16][addr%16].setText( Character.toString(e.getKeyChar()).toUpperCase() );
			}
		}
		//else if( e.getKeyChar() >= 'g' && e.getKeyChar() <= 'z' )
		else if( (e.getKeyCode() >= KeyEvent.VK_G && e.getKeyCode() <= KeyEvent.VK_Z ) ||
				 (e.getKeyChar() >= ' ' && e.getKeyChar() <= '/' ) ||	// 33 ~ 47
				 (e.getKeyChar() >= ':' && e.getKeyChar() <= '@' ) ||	// 58 ~ 40
				 (e.getKeyChar() >= '[' && e.getKeyChar() <= '`' ) )	// 91 ~ 96
		{
			if( val < 0x10 )
			{
				arrtf[addr/16][addr%16].setText("0"+Integer.toHexString(val).toUpperCase());
			}
			else
			{
				arrtf[addr/16][addr%16].setText(Integer.toHexString(val).toUpperCase());
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
		// keyPressed - � Ű�� ������� Ű�� �������� �� �߻��ϴ� �̺�Ʈ
		//System.out.println("keyTyped : "+Character.toString( e.getKeyChar() ) );

	}
}

class TextFieldFocusHandler implements FocusListener
{
	@Override
	public void focusGained(FocusEvent e)
	{
		// TODO Auto-generated method stub
		JTextField focusTf = (JTextField) e.getSource();
		focusTf.setBackground(Color.YELLOW);

		JTextField[][] arrtf =  RegisterTableUI.getArrTextField();
		for(int i=0; i<17; i++)
		{
			for(int j=0; j<17; j++)
			{
				 if( focusTf==arrtf[i][j] )
				 {
					 //System.out.print("i="+i+"\t j="+j);
					 //System.out.println("\t pos="+Integer.toHexString(((i-1)*16+(j-1))));
					 //RegisterTableUI.setSelectedAddress( ((i-1)*16+(j-1)) );
					 //System.out.println("selectedAddress = "+ Integer.toHexString(RegisterTableUI.getSelectedAddress()));
					 
					 
					 //RegisterTableUI.setSelectedAddress(i*16+j);
					 //System.out.println("selectedAddress = " + Integer.toHexString(RegisterTableUI.getSelectedAddress()) + "\t"+RegisterTableUI.getSelectedAddress());
					 
					 int addr = i*16+j;
					 RegisterTableUI.setSelectedAddress(addr);
					 //System.out.println("focus addr = " + Integer.toHexString(RegisterTableUI.getSelectedAddress()));

					if( arrtf[addr/16][addr%16].getText().length() == 2 )
					{
						int val = Integer.parseInt( arrtf[addr/16][addr%16].getText(), 16);
						RegisterTableUI.setSelectedValue(val);
						//System.out.println("before val = " + Integer.toHexString(val) );
					}

				 }
			}
		}
	}

	@Override
	public void focusLost(FocusEvent e)
	{
		// TODO Auto-generated method stub
		JTextField jf = (JTextField) e.getSource();
		jf.setBackground(Color.WHITE);
	}
}


