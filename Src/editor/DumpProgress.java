package editor;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;





class DumpProgress implements ActionListener, Runnable
{
	JFileChooser chooser;
	JFrame fileWindow;
	JFrame pbBar;

	JProgressBar jpBar;
	JButton btnClose;

	boolean doingFlag = true;
	int percent=0;

	public DumpProgress()
	{
		// file read
		fileWindow = new JFrame();
		chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory()); // 디렉토리 설정
		//chooser.setCurrentDirectory(new File("/")); // 현재 사용 디렉토리를 지정
		chooser.setDialogTitle("File Open"); // 창의 제목
		chooser.setAcceptAllFileFilterUsed(true);   // Fileter 모든 파일 적용 
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES); // 파일 선택 모드
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Text File", "txt"); // filter 확장자 추가
		chooser.setFileFilter(filter); // 파일 필터를 추가

		// progress bar
		pbBar = new JFrame("Loading");
		pbBar.setLocationRelativeTo(null);
		jpBar= new JProgressBar (JProgressBar.HORIZONTAL, 0,100);  

		percent = 0;
		
		Container con = pbBar.getContentPane();
		con.setLayout(new BorderLayout());
		con.add("North", new JLabel("Dump Writing.."));
		con.add("Center", jpBar);
		
		jpBar.setStringPainted(true);
		jpBar.setString("0%");

		JPanel jp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		btnClose= new JButton("cancel");
		jp.add(btnClose);

		btnClose.addActionListener(this);

		con.add("South",jp);
		pbBar.setSize(300,150);
		pbBar.setVisible(false);
	}

	public void setPercent(int n)
	{
		percent = n;
		//System.out.println("setPercent = " + percent);
		
		// update
		jpBar.setValue(percent);
		jpBar.setString(percent+"%");
	}

    @Override
	public void run()
    {

    	while(doingFlag)
    	{
    		int result = chooser.showOpenDialog(fileWindow);
    		String filePath;
    		
    		if(result==JFileChooser.APPROVE_OPTION )
    		{
    			//String filePath = chooser.getSelectedFile().toString();
    			filePath = chooser.getSelectedFile().toString();
    			//System.out.println("path = " + filePath);

    			Path path = Paths.get(filePath);
    			
    			try
    			{
    				int FileLineCount = (int)(Files.lines(path).count());
    				//System.out.println("FileLineCount = " + FileLineCount);
    				int readLineCount = 0;
    				
    				BufferedReader br = new BufferedReader(new FileReader(filePath));
    				String strReadLine;

    				/*
    				bt656_720p_customized720p_mode0_w0.txt
    				40,01,00
    				40,02,00
    				40,03,71
    				...
    				*/

    				
    				while( ((strReadLine=br.readLine()) != null) && doingFlag )
    				{
    					//System.out.println(strReadLine);

    					//update percent
    					readLineCount++;
    					//System.out.println("count : " + readLineCount + "\t / " + FileLineCount );
    					
    					int progress =  readLineCount*100 / FileLineCount;
    					//System.out.println("progress = " + progress);
    					
    					pbBar.setVisible(true);
    					setPercent(progress);

    					
    					//check format
    					String id = strReadLine.substring(0,3);
    					if( !(id.equals("40,")) ) { throw new Exception(); }
    					
    					String addr = strReadLine.substring(3,5);	// 3="40,"
    					String val = strReadLine.substring(6,8); 	// 6="40,01," 
    					//System.out.println("VS4210_WriteI2C(0x"+addr+",0x"+val+");");
    					
    					int addrHex = Integer.parseInt(addr, 16);
    					int valHex = Integer.parseInt(val, 16);
    					if( (addrHex>=0 && addrHex<=255) && (valHex>=0 && valHex<=255) )
    					{
    						// send
    						//System.out.printf("write(%02X,%02X);\n", addrHex, valHex);
    						PacketProcessing.writeRegister(addrHex, valHex);
    					}
    					else
    					{
    						throw new Exception();
    					}
    					
    				}
    				
    				//dump end
    				br.close();
    				doingFlag=false;
    				
    			} catch (Exception e)
    			{
    				// TODO: handle exception
    				//e.printStackTrace();
    				JOptionPane.showMessageDialog(null, "Dump file Read Error.", "Dump Error", JOptionPane.ERROR_MESSAGE);
    				doingFlag=false;
    			}
    		}
    		else
    		{
    			doingFlag=false;
    		}

    	}
    	
    	//close
    	fileWindow.dispose();
    	pbBar.dispose();
	}
    
    @Override
	public void actionPerformed(ActionEvent e)
    {
		if(e.getSource()==btnClose)
		{
			doingFlag=false;
		}
	}

}