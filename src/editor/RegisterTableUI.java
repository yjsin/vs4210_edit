package editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.MaskFormatter;

import jssc.SerialPort;

import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;

import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import java.awt.GridLayout;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.event.MouseAdapter;
import javax.swing.JSpinner;


public class RegisterTableUI extends JFrame
{
	private SerialConnect serialConnect;
	private PacketProcessing packetProcess;
	
	private JPanel contentPane;
	private static JTextArea textAreaLog;
	private static JTextField[][] arrTextField;
	
	private static JButton btnApplyDump;
	private static JButton btnReadAll;
	
	private static JComboBox comboBoxPort;
	private static JComboBox comboBoxChip;
	
	private static JToggleButton btnConnect;
	private static JButton btnSaveDump;
	private static JButton[] btnValue;
	
	private static JSpinner spnAddr;
	private static JSpinner spnVal;
	
	private static int connectedState;
	private static int selectedChip=1; // 1:vs4210, 2:tp2824
	private static int selectedAddress=17;
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
		comboBoxPort = new JComboBox();
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
		comboBoxPort.setBounds(12, 61, 152, 41);
		panelSetting.add(comboBoxPort);
		
		JLabel lblState = new JLabel("Select Port");
		lblState.setBounds(12, 10, 152, 41);
		panelSetting.add(lblState);

		
		btnConnect = new JToggleButton("Open");
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
						
						setEnableBtn(false);
					}
					catch(Exception e)
					{
						e.printStackTrace();
						JOptionPane.showMessageDialog(null, "Disconnect failed.", "Error", JOptionPane.ERROR_MESSAGE);
						
						changeTitle("EASTERN MASTEC Register Table - Error ");
						lblState.setText("Disconnect failed.");
						btnConnect.setText("Close");
						btnConnect.setSelected(true);
						
						setEnableBtn(true);
					}
				}
				else if( connectedState==0 ) // close -> open
				{
					//String port = comboBoxPort.getSelectedItem().toString();
					String port="";
					//int speed       = SerialPort.BAUDRATE_38400;
					int speed       = SerialPort.BAUDRATE_115200;
					int data		= SerialPort.DATABITS_8;
					int stop		= SerialPort.STOPBITS_1;
					int parity		= SerialPort.PARITY_NONE;
					
					try
					{
						port = comboBoxPort.getSelectedItem().toString();
						if( port.length() == 0 )
						{
							throw new Exception();
						}
						serialConnect.connect(port, speed, data, stop, parity);
						connectedState = 1;
						
						changeTitle("EASTERN MASTEC Register Table - Connected");
						lblState.setText("Connected.");
						btnConnect.setText("Close");
						btnConnect.setSelected(true);
						
						setEnableBtn(true);
					}
					catch(Exception e)
					{
						e.printStackTrace();
						JOptionPane.showMessageDialog(null, "Connect failed.", "Error", JOptionPane.ERROR_MESSAGE);
						
						changeTitle("EASTERN MASTEC Register Table - Error");
						lblState.setText("Connect Failed.");
						btnConnect.setText("Open");
						btnConnect.setSelected(false);

						setEnableBtn(false);
					}
				}
			}
		});
		btnConnect.setBounds(12, 112, 220, 57);
		panelSetting.add(btnConnect);

		//chip
		JLabel lblChip = new JLabel("Chip");
		lblChip.setBounds(176, 10, 98, 41);
		panelSetting.add(lblChip);
		
		comboBoxChip = new JComboBox();
		comboBoxChip.setBounds(176, 61, 98, 41);
		panelSetting.add(comboBoxChip);
		comboBoxChip.setEnabled(false);

		comboBoxChip.addItem("VS4210");
		comboBoxChip.addItem("TP2824");
		comboBoxChip.addItem("TP2912");
		comboBoxChip.setSelectedIndex(0);
		comboBoxChip.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// TODO Auto-generated method stub
				selectedChip = comboBoxChip.getSelectedIndex()+1;
//
//				System.out.println("selected Chip = " + selectedChip);
//
				if( selectedChip == 1 )	{ btnApplyDump.setEnabled(true);	}
				else 					{ btnApplyDump.setEnabled(false);	}

				try
				{
					serialConnect.write('c'); // change
					Thread.sleep(20);

					serialConnect.write(Integer.toHexString(selectedChip));
					Thread.sleep(20);
					
					serialConnect.write('r'); // read
					Thread.sleep(20);

				} catch (InterruptedException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		//value
		JLabel lblNewLabel = new JLabel("Value (bit)");
		lblNewLabel.setBounds(447, 10, 79, 41);
		panelSetting.add(lblNewLabel);

		btnValue = new JButton[8];

		for(int i=0; i<8; i++)
		{
			int size=45;
			btnValue[i] = new JButton("0");
			btnValue[i].setBounds(760-(size*i), 60, size, size);
			panelSetting.add(btnValue[i]);
			btnValue[i].setEnabled(false);
			btnValue[i].addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					// TODO Auto-generated method stub
					JButton focusBtn = (JButton) e.getSource();
					focusBtn.getText();
					switch( Integer.parseInt(focusBtn.getText()))
					{
						case 0 :
							focusBtn.setText("1");
							break;
						case 1 :
							focusBtn.setText("0");
							break;
					}
					
					// update selected value
					int sum=0;
					for(int i=0; i<8; i++)
					{
						sum =  sum + Integer.parseInt(btnValue[i].getText()) * (int)(Math.pow(2, i)) ;
					}
					setSelectedValue(sum);

					// write register
					if( RegisterTableUI.getConnectedState() == 1 )
					{
						PacketProcessing.writeRegister(selectedAddress-16-1, selectedValue);
					}
				}
			});
		}

		// function
		//JButton btnSaveDump = new JButton("Save Dump"); //???? ????????
		btnSaveDump = new JButton("N/A");
		btnSaveDump.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				
			}
		});
		btnSaveDump.setBounds(244, 112, 180, 57);
		panelSetting.add(btnSaveDump);
		btnSaveDump.setEnabled(false);

		
		//JButton btnApplyDump = new JButton("Apply Dump");
		btnApplyDump = new JButton("Apply Dump");

		btnApplyDump.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent arg0)
			{
				DumpProgress dpBar = new DumpProgress();
				new Thread(dpBar).start();
			}
		});

		btnApplyDump.setBounds(436, 112, 180, 57);
		panelSetting.add(btnApplyDump);
		btnApplyDump.setEnabled(false);
		
		//JButton btnReadAll = new JButton("Read All");
		btnReadAll = new JButton("Read All");
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
		btnReadAll.setEnabled(false);

		//spinner
		spnAddr = new JSpinner(new SpinnerNumberModel(0,0,255,1));
		spnAddr.setBounds(286, 61, 73, 41);
		panelSetting.add(spnAddr);
		spnAddr.setEnabled(false);

		JSpinner.DefaultEditor editorAddr = (JSpinner.DefaultEditor)spnAddr.getEditor();
		JFormattedTextField tfAddr = editorAddr.getTextField();
		tfAddr.setFormatterFactory(new hexFormattedFactory());

		spnVal = new JSpinner(new SpinnerNumberModel(0,0,255,1));
		spnVal.setBounds(362, 61, 73, 41);
		panelSetting.add(spnVal);
		spnVal.setEnabled(false);

		JSpinner.DefaultEditor editorVal = (JSpinner.DefaultEditor)spnVal.getEditor();
		JFormattedTextField tfVal = editorVal.getTextField();
		tfVal.addKeyListener(new TextFieldKeyHandler()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{
				super.keyTyped(e);
				
				if( (e.getKeyChar() >= '0' && e.getKeyChar() <= '9' )
					|| (e.getKeyChar() >='a' && e.getKeyChar() <= 'f' )
					|| (e.getKeyChar() >='A' && e.getKeyChar() <= 'F' ) )
					{
						JTextField src = (JTextField) e.getSource();
						if( src.getText().length() >=2 )
						{
							src.setText("");
						}
					}
				else
				{
					e.consume();
				}
			}

			@Override
			public void keyReleased(KeyEvent e)
			{
				// TODO Auto-generated method stub
				//super.keyReleased(e);
				
				if( (e.getKeyCode() == KeyEvent.VK_ENTER) )
				{
					setSelectedValue( (Integer) spnVal.getValue() );
					
					if( RegisterTableUI.getConnectedState() == 1 )
					{
						PacketProcessing.writeRegister(selectedAddress-16-1, selectedValue);
					}
				}
			}
			
			
		});
		tfVal.setFormatterFactory(new hexFormattedFactory());

		spnVal.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				// TODO Auto-generated method stub
				//System.out.println("changed = " + spnVal.getValue());
/*				// bug occurred when changing the chip
				setSelectedValue( (Integer) spnVal.getValue() );
				
				// write register
				if( RegisterTableUI.getConnectedState() == 1 )
				{
					PacketProcessing.writeRegister(selectedAddress-16-1, selectedValue);
				}
*/
			}
		});
		
		spnVal.addMouseWheelListener(new MouseWheelListener()
		{
			@Override
			public void mouseWheelMoved(MouseWheelEvent mwe)
			{
				// TODO Auto-generated method stub
				// rotation wheel dn = 1, wheel up = -1
				int temp = (Integer) spnVal.getValue() - mwe.getWheelRotation();
				if( temp>255 )		{ temp = 0;		}
				else if( temp<0)	{ temp = 255;	}

				//spnVal.setValue( temp );
				
				setSelectedValue( temp );
				
				if( RegisterTableUI.getConnectedState() == 1 )
				{
					PacketProcessing.writeRegister(selectedAddress-16-1, selectedValue);
				}
			}
		});

		JLabel lblIndexhex = new JLabel("Index(hex)");
		lblIndexhex.setBounds(286, 10, 73, 41);
		panelSetting.add(lblIndexhex);
		
		JLabel lblValuehex = new JLabel("Value(hex)");
		lblValuehex.setBounds(362, 10, 73, 41);
		panelSetting.add(lblValuehex);
		
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


		// test button
		JPanel panelTest = new JPanel();
		panelTest.setBounds(12, 762, 821, 69);
		contentPane.add(panelTest);
		panelTest.setLayout(null);
		
		JButton[] btnTest = new JButton[10];
		for(int i=0; i<10; i++)
		{
			int size = 50;
			btnTest[i] = new JButton(Integer.toString(i+1));
			btnTest[i].setEnabled(true);
			btnTest[i].setBounds(10+size*i, 10, size, size);
			btnTest[i].addMouseListener(new BtnTestMouseHandler() );
			
			panelTest.add(btnTest[i]);
			
			if( i==9 )
			{
				btnTest[i].setText("0");
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
		//update table
		for(int i=0; i<16; i++)
		{
			for(int j=0; j<16; j++)
			{
				arrTextField[i+1][j+1].setText( strRegisterTable[i][j].toUpperCase() );
			}
		}
		
		//update selected value
		int changedVal;
		if( selectedAddress%16==0 )
		{
			changedVal = Integer.parseInt(arrTextField[selectedAddress/16-1][selectedAddress%16+16].getText(), 16);
		}
		else
		{
			changedVal = Integer.parseInt(arrTextField[selectedAddress/16][selectedAddress%16].getText(), 16);
		}
		
		setSelectedValue(changedVal);
	}
	
	public static void setSelectedAddress(int addr)
	{
		selectedAddress = addr;
		spnAddr.setValue(selectedAddress-16-1);
	}
	public static int getSelectedAddress()
	{
		return selectedAddress;
	}
	
	public static void setSelectedValue(int val)
	{
		selectedValue = val;
		updateSelectedValue();
	}
	public static int getSelectedValue()
	{
		return selectedValue;
	}
	public static int getSelectedChip()
	{
		return selectedChip;
	}

	public static String[][] getStrRegisterTable()
	{
		return strRegisterTable;
	}

	public static void setEnableBtn(boolean state)
	{
		comboBoxPort.setEnabled(!state);
		comboBoxChip.setEnabled(state);

		if( selectedChip == 1 )	{ btnApplyDump.setEnabled(state);	}
		else 					{ btnApplyDump.setEnabled(false);	}
		
		btnReadAll.setEnabled(state);
		
		for(int i=0; i<8; i++)
		{
			btnValue[i].setEnabled(state);
		}
		
		//spnAddr.setEnabled(state);
		spnVal.setEnabled(state);
	}
	
	public static int getConnectedState()
	{
		return connectedState;
	}
	
	private static void updateSelectedValue()
	{
		// update text field
		if( selectedAddress%16 == 0 )
		{
			if( selectedValue < 0x10 )
			{
				arrTextField[selectedAddress/16-1][selectedAddress%16+16].setText("0"+Integer.toHexString(selectedValue).toUpperCase());
			}
			else
			{
				arrTextField[selectedAddress/16-1][selectedAddress%16+16].setText(Integer.toHexString(selectedValue).toUpperCase());
			}
		}
		else
		{
			if( selectedValue < 0x10 )
			{
				arrTextField[selectedAddress/16][selectedAddress%16].setText("0"+Integer.toHexString(selectedValue).toUpperCase());
			}
			else
			{
				arrTextField[selectedAddress/16][selectedAddress%16].setText(Integer.toHexString(selectedValue).toUpperCase());
			}
		}
		
		// update value button
		String valueStr = PacketProcessing.decToBinary(selectedValue);
		
		for(int i=0; i<8; i++)
		{
			btnValue[i].setText( valueStr.substring(7-i, 8-i) );
		}
		
		// update spinner
		spnVal.setValue(selectedValue);
	}
}


class TextFieldMouseHandler implements MouseInputListener
{

	@Override
	public void mouseClicked(MouseEvent e)
	{
		// ?????? ?????? ?????? ???? ???????? ?????? 

	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		// ?????? ?????? ???????? ???????? ???????? ?? ???????? ??????
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		// ?????? ?????? ???????? ???? ?????? ?????? ???????? ??????
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		// ?????? ?????? ???????? ?? ???????? ?????? 
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		// ?????? ?????? ?????? ???????? ?? ???????? ?????? 
	}

	@Override
	public void mouseDragged(MouseEvent arg0)
	{
		// ?????? ?????? ?????? ???????? ?????? ?? ???????? ?????? 
	}

	@Override
	public void mouseMoved(MouseEvent arg0)
	{
		// ?????? ?????? ?????? ?? ???????? ?????? 
	}
}


class BtnTestMouseHandler implements MouseListener
{

	@Override
	public void mouseClicked(MouseEvent e)
	{
		// TODO Auto-generated method stub
		JButton jb = (JButton) e.getSource();
		//System.out.println("click = " + jb.getText());
		
		SerialConnect serialConnect = SerialConnect.getInstance();
		serialConnect.write(jb.getText().getBytes(StandardCharsets.US_ASCII));
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

//	KeyEvent??????
//
//	getKeyChar - ???????? key???? ????. ?????? ?????????? ???????? ???????? ???? ????.
//	getKeyCode - ???????? ???????? ?????? ???? ???? ?????? ????. ?? ???????????? ???? ctrl?? ?????? ctrl???? ????????
//	getkeyLocation -  ???????? ?????? ?? 4?????? ???????? 1?? ?????? ????, 2?? ???? ??????, 3?? ?????? ??????, 4?? ??????
//	paramString - key?????? ?????? ???? ???????? ????
//	getWhen - ?????? ??????????
//	isControlDown - Control?? ??????????
//	isShiftDown - Shift?? ??????????
//	isAltDown - Alt?? ??????????
//	isMetaDown = Meta???? ??????????(?????? ?????? cmd, ???????????? window,???? ???????? ???????? meta??)


	@Override
	public void keyPressed(KeyEvent e)
	{
		// keyTyped - ???? ?????? ?????? ???????? ??????. ???????? ???????? ?????????? ???????? ???? ???? ???????? ?????? ????.
		//System.out.println("keyPressed : "+e.getKeyCode() );
		
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		// keyReleased - ???? ???? ???????? ???? ???????? ?? ???????? ??????
		//System.out.println("keyRelease : "+e.getKeyCode() );

		int addr = RegisterTableUI.getSelectedAddress();
		int val = RegisterTableUI.getSelectedValue();
		JTextField[][] arrtf =  RegisterTableUI.getArrTextField();
		//System.out.println("addr/16 = " + addr/16 + "\t addr%16 = " + addr%16);
		//System.out.println("addr = " + addr + "\t val = " + val);
		if( (e.getKeyCode() == KeyEvent.VK_TAB) )
		{
			//System.out.println("tab");
			
			int calAddr = addr-16-1;
			int calVal;
			if( addr%16 == 0 )
			{
				calVal = Integer.parseInt( arrtf[addr/16-1][addr%16+16].getText() , 16 );
				RegisterTableUI.setSelectedValue(calVal);

				if( calVal < 0x10 )
				{
					arrtf[addr/16-1][addr%16+16].setText("0"+Integer.toHexString(calVal).toUpperCase());
				}
				else
				{
					arrtf[addr/16-1][addr%16+16].setText(Integer.toHexString(calVal).toUpperCase());
				}
			}
			else
			{
				calVal = Integer.parseInt( arrtf[addr/16][addr%16].getText() , 16 );
				RegisterTableUI.setSelectedValue(calVal);
				
				if( calVal < 0x10 )
				{
					arrtf[addr/16][addr%16].setText("0"+Integer.toHexString(calVal).toUpperCase());
				}
				else
				{
					arrtf[addr/16][addr%16].setText(Integer.toHexString(calVal).toUpperCase());
				}
			}

			if( RegisterTableUI.getConnectedState() == 1 )
			{
				PacketProcessing.writeRegister(calAddr, calVal);
			}
			
			arrtf[addr/16][addr%16+1].requestFocus(); // move focus
		}
		else if( (e.getKeyCode() == KeyEvent.VK_ENTER) )
		{
			//System.out.println("enter");
			
			int calAddr = addr-16-1;
			int calVal;
			if( addr%16 == 0 )
			{
				calVal = Integer.parseInt( arrtf[addr/16-1][addr%16+16].getText() , 16 );
				RegisterTableUI.setSelectedValue(calVal);
				
				if( calVal < 0x10 )
				{
					arrtf[addr/16-1][addr%16+16].setText("0"+Integer.toHexString(calVal).toUpperCase());
				}
				else
				{
					arrtf[addr/16-1][addr%16+16].setText(Integer.toHexString(calVal).toUpperCase());
				}
			}
			else
			{
				calVal = Integer.parseInt( arrtf[addr/16][addr%16].getText() , 16 );
				RegisterTableUI.setSelectedValue(calVal);
				
				if( calVal < 0x10 )
				{
					arrtf[addr/16][addr%16].setText("0"+Integer.toHexString(calVal).toUpperCase());
				}
				else
				{
					arrtf[addr/16][addr%16].setText(Integer.toHexString(calVal).toUpperCase());
				}
			}
			
			//System.out.printf( "calAddr = %d(=%x) \t calVal = %d(=%x)\n", calAddr, calAddr, calVal, calVal);

			if( RegisterTableUI.getConnectedState() == 1 )
			{
				PacketProcessing.writeRegister(calAddr, calVal);
			}
		}
		else if( (e.getKeyCode() == KeyEvent.VK_ESCAPE) )
		{
			//System.out.println("esc");
			if( addr%16 == 0 )
			{
				if( val < 0x10 )
				{
					arrtf[addr/16-1][addr%16+16].setText("0"+Integer.toHexString(val).toUpperCase());
				}
				else
				{
					arrtf[addr/16-1][addr%16+16].setText(Integer.toHexString(val).toUpperCase());
				}
			}
			else
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
		
		else if( (e.getKeyChar() >= '0' && e.getKeyChar() <= '9') ||
				 (e.getKeyChar() >= 'a' && e.getKeyChar() <= 'f') )
		{
			if( addr%16 == 0 )
			{
				if( arrtf[addr/16-1][addr%16+16].getText().length()>=3 )
				{
					//System.out.println("addr=" + addr + " arrtf["+(addr/16-1)+"]["+(addr%16+16)+"].getText()="+arrtf[addr/16-1][addr%16+16].getText());
					arrtf[addr/16-1][addr%16+16].setText( Character.toString(e.getKeyChar()).toUpperCase() );
				}
			}
			else
			{
				if( arrtf[addr/16][addr%16].getText().length()>=3 )
				{
					//System.out.println("addr=" + addr + " arrtf["+addr/16+"]["+addr%16+"].getText()="+arrtf[addr/16][addr%16].getText());
					arrtf[addr/16][addr%16].setText( Character.toString(e.getKeyChar()).toUpperCase() );
				}
			}
		}
		else if( (e.getKeyCode() >= KeyEvent.VK_G && e.getKeyCode() <= KeyEvent.VK_Z ) ||
				 (e.getKeyChar() >= ' ' && e.getKeyChar() <= '/' ) ||	// 33 ~ 47
				 (e.getKeyChar() >= ':' && e.getKeyChar() <= '@' ) ||	// 58 ~ 40
				 (e.getKeyChar() >= '[' && e.getKeyChar() <= '`' ) )	// 91 ~ 96
		{
			if( addr%16==0 )
			{
				if( val < 0x10 )
				{
					//System.out.println("1 addr=" + addr + " arrtf[" + (addr/16-1) + "][" + (addr%16+16) + "].getText()=" + arrtf[addr/16-1][addr%16+16].getText());
					arrtf[addr/16-1][addr%16+16].setText("0"+Integer.toHexString(val).toUpperCase());
				}
				else
				{
					//System.out.println("2 addr=" + addr + " arrtf[" + (addr/16-1) + "][" + (addr%16+16) + "].getText()=" + arrtf[addr/16-1][addr%16+16].getText());
					arrtf[addr/16-1][addr%16+16].setText(Integer.toHexString(val).toUpperCase());
				}
			}
			else
			{
				if( val < 0x10 )
				{
					//System.out.println("3 addr=" + addr + " arrtf[" + addr/16 + "][" + addr%16 + "].getText()=" + arrtf[addr/16][addr%16].getText());
					arrtf[addr/16][addr%16].setText("0"+Integer.toHexString(val).toUpperCase());
				}
				else
				{
					//System.out.println("4 addr=" + addr + " arrtf[" + addr/16 + "][" + addr%16 + "].getText()=" + arrtf[addr/16][addr%16].getText());
					arrtf[addr/16][addr%16].setText(Integer.toHexString(val).toUpperCase());
				}
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
		// keyPressed - ???? ???? ???????? ???? ???????? ?? ???????? ??????
		//System.out.println("keyTyped : "+Character.toString( e.getKeyChar() ) );
		if( (e.getKeyChar() >= '0' && e.getKeyChar() <= '9')
				|| (e.getKeyChar() >= 'a' && e.getKeyChar() <= 'f' )
				|| (e.getKeyChar() >= 'A' && e.getKeyChar() <= 'F' ) )
		{
			JTextField src = (JTextField) e.getSource();
			if( src.getText().length() >= 2 )
			{
				src.setText("");
			}
		}
		else
		{
			e.consume();
		}
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

					int val;
					if( addr%16 == 0 )
					{
						if( arrtf[addr/16-1][addr%16+16].getText().length() == 2 )
						{
							val = Integer.parseInt( arrtf[addr/16-1][addr%16+16].getText(), 16);
							RegisterTableUI.setSelectedValue(val);
						}
					}
					else
					{
						if( arrtf[addr/16][addr%16].getText().length() == 2 )
						{
							val = Integer.parseInt( arrtf[addr/16][addr%16].getText(), 16);
							RegisterTableUI.setSelectedValue(val);
						}
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


class hexFormattedFactory extends AbstractFormatterFactory
{
	@Override
	public AbstractFormatter getFormatter(JFormattedTextField arg0)
	{
		// TODO Auto-generated method stub
		return new HexFormatter();
	}
}


class HexFormatter extends DefaultFormatter
{
	public Object stringToValue(String text) throws ParseException
	{
		MaskFormatter formatter1 = new MaskFormatter("HH");

		try
		{
			//System.out.println("String to Value");

			return Integer.valueOf(text, 16);
		}
		catch (NumberFormatException nfe)
		{
			throw new ParseException(text,0);
		}
	}
	
	public String valueToString(Object value) throws ParseException
	{
		//System.out.println("Value to String");
		return Integer.toHexString( ((Integer)value).intValue()).toUpperCase(); 
	}
}
