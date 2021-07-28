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
		// ���콺 ��ư�� Ŭ���� ��� �߻��Ǵ� �̺�Ʈ 
		System.out.println("test");
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

	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		// keyReleased - � Ű�� ������� Ű�� �������� �� �߻��ϴ� �̺�Ʈ
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
		// keyPressed - � Ű�� ������� Ű�� �������� �� �߻��ϴ� �̺�Ʈ
		
		if(e.getKeyCode() == KeyEvent.VK_TAB)
		{
			System.out.println("typed");
			//textField.requestFocus();
		}

	}
	
}


