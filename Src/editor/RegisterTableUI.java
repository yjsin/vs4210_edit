package editor;

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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.peer.KeyboardFocusManagerPeer;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import java.awt.GridLayout;

public class RegisterTableUI extends JFrame
{
	private SerialConnect serialConnect;
	private PacketProcessing packetProcess;
	
	private JPanel contentPane;
	
	private boolean connectedState;
	private JTextField textField;
	private JTextField textField_1;
	
	public RegisterTableUI()
	{
		connectedState=false;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		changeTitle("EASTERN MASTEC Register Table");
		
		//sort to center
		Toolkit kit=Toolkit.getDefaultToolkit();
		Dimension frameSize = new Dimension(1000,800);
		Dimension screenSize = kit.getScreenSize();
		setBounds(screenSize.width/2 - frameSize.width/2 , screenSize.height/2 - frameSize.height/2, frameSize.width , frameSize.height);
		
		//change Icon
		Image icon=kit.getImage("image/logo.png");
		setIconImage(icon);
		
		
		serialConnect = SerialConnect.getInstance();
		packetProcess= PacketProcessing.getInstance();

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panelSetting = new JPanel();
		panelSetting.setBounds(12, 10, 960, 182);
		contentPane.add(panelSetting);
		panelSetting.setLayout(null);
		
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
				
				if( connectedState ) // open -> close
				{
					try
					{
						serialConnect.disConnect();
						connectedState=false;
						
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
				else // close -> open
				{
					String port = comboBoxPort.getSelectedItem().toString();
					//int speed		= SerialPort.BAUDRATE_9600;
					int speed       = SerialPort.BAUDRATE_38400;
					int data		= SerialPort.DATABITS_8;
					int stop		= SerialPort.STOPBITS_1;
					int parity		= SerialPort.PARITY_NONE;
					
					try
					{
						serialConnect.connect(port, speed, data, stop, parity);
						connectedState = true;
						
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
		btnConnect.setBounds(244, 10, 92, 92);
		panelSetting.add(btnConnect);
		
		JLabel lblChip = new JLabel("Chip");
		lblChip.setBounds(348, 10, 115, 41);
		panelSetting.add(lblChip);
		
		JComboBox comboBoxChip = new JComboBox();
		comboBoxChip.setBounds(348, 61, 115, 41);
		panelSetting.add(comboBoxChip);
		
		JLabel lblNewLabel = new JLabel("Value (bit)");
		lblNewLabel.setBounds(598, 10, 350, 41);
		panelSetting.add(lblNewLabel);
		
		JButton btn0 = new JButton("0");
		btn0.setBounds(598, 61, 41, 41);
		panelSetting.add(btn0);
		
		JButton btn1 = new JButton("1");
		btn1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btn1.setBounds(639, 61, 41, 41);
		panelSetting.add(btn1);
		
		JButton btn2 = new JButton("2");
		btn2.setBounds(682, 61, 41, 41);
		panelSetting.add(btn2);
		
		JButton btn3 = new JButton("3");
		btn3.setBounds(724, 61, 41, 41);
		panelSetting.add(btn3);
		
		JButton btn4 = new JButton("4");
		btn4.setBounds(768, 61, 41, 41);
		panelSetting.add(btn4);
		
		JButton btn5 = new JButton("5");
		btn5.setBounds(811, 61, 41, 41);
		panelSetting.add(btn5);
		
		JButton btn6 = new JButton("6");
		btn6.setBounds(852, 61, 41, 41);
		panelSetting.add(btn6);
		
		JButton btn7 = new JButton("7");
		btn7.setBounds(895, 61, 41, 41);
		panelSetting.add(btn7);
		
		JButton btnSaveDump = new JButton("Save Dump");
		btnSaveDump.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnSaveDump.setBounds(12, 115, 226, 57);
		panelSetting.add(btnSaveDump);
		
		JButton btnApplyDump = new JButton("Apply Dump");
		btnApplyDump.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnApplyDump.setBounds(254, 112, 226, 57);
		panelSetting.add(btnApplyDump);
		
		JButton btnReadAll = new JButton("Read All");
		btnReadAll.setBounds(492, 112, 226, 57);
		panelSetting.add(btnReadAll);
		
		JPanel panelTable = new JPanel();
		panelTable.setBounds(12, 202, 960, 550);
		contentPane.add(panelTable);
		panelTable.setLayout(new GridLayout(16, 16, 0, 0));
		
		textField = new JTextField();
		panelTable.add(textField);
		textField.setColumns(10);

		
		textField.addMouseListener(new TextFieldMouseHandler());
		textField.addKeyListener(new TextFieldKeyHandler());
		

	}
	
	public void changeTitle(String s)
	{
		this.setTitle(s);
	}
}



class TextFieldMouseHandler implements MouseInputListener
{

	@Override
	public void mouseClicked(MouseEvent e)
	{
		// 마우스 버튼이 클릭된 경우 발생되는 이벤트 
		System.out.println("test");
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		// 마우스 커서가 컴포넌트 영역으로 들어왔을 때 발생되는 이벤트
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		// 마우스 커서가 컴포넌트 영역 밖으로 나가면 발생되는 이벤트
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		// 마우스 버튼이 눌러졌을 때 발생하는 이벤트 
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		// 마우스 버튼이 눌렀다 띄었졌을 때 발생하는 이벤트 
	}

	@Override
	public void mouseDragged(MouseEvent arg0)
	{
		// 마우스 버튼이 클릭된 상태에서 움직일 때 발생되는 이벤트 
	}

	@Override
	public void mouseMoved(MouseEvent arg0)
	{
		// 마우스 커서가 움직일 때 발생되는 이벤트 
	}
}

class TextFieldKeyHandler implements KeyListener
{

/*
	KeyEvent메소드

	getKeyChar - 타이핑된 key값을 반환. 사실상 키보드랑은 무관하게 타이핑된 값을 반환.
	getKeyCode - 타이핑된 키보드의 배치에 따른 값을 정수로 반환. 이 메소드덕분에 왼쪽 ctrl과 오른쪽 ctrl들을 구별가능
	getkeyLocation -  키보드의 배치를 총 4군데로 나누는데 1이 키보드 전체, 2가 왼쪽 보조키, 3이 오른쪽 보조키, 4가 키패드
	paramString - key보드에 관련된 전체 파라메터 반환
	getWhen - 얼마나 눌러졌는지
	isControlDown - Control이 눌러졌는지
	isShiftDown - Shift가 눌러졌는지
	isAltDown - Alt가 눌러졌는지
	isMetaDown = Meta키가 눌러졌는지(맥으로 따지면 cmd, 윈도우에서는 window,몇몇 키보드에 존재하는 meta키)
*/
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		// keyTyped - 키가 타이핑 됬을때 발생하는 이벤트. 말그대로 타이핑이 되야하므로 타이핑이 되지 않는 보조키는 작동치 않음.

	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		// keyReleased - 어떤 키던 상관없이 키가 놓아졌을 때 발생하는 이벤트
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
		// keyPressed - 어떤 키던 상관없이 키가 눌러졌을 때 발생하는 이벤트
		
		if(e.getKeyCode() == KeyEvent.VK_TAB)
		{
			System.out.println("typed");
			//textField.requestFocus();
		}

	}
	
}


