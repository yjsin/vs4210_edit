package editor;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class SerialConnect
{
	SerialPort serialPort;
	
	public SerialConnect()
	{
		serialPort=null;
	}
	
	private static class SerialConnectHolder
	{
		private static final SerialConnect Connect = new SerialConnect();
	}
	
	public static SerialConnect getInstance()
	{
		return SerialConnectHolder.Connect;
	}
	
	void connect(String portNum, int speed, int dataBit, int stopBit, int parityBit) throws Exception
	{
		serialPort= new SerialPort(portNum);

		serialPort.openPort();
		serialPort.setParams(speed, dataBit, stopBit, parityBit);
		
		//SerialPortReader reader=new SerialPortReader(serialPort);
        //serialPort.addEventListener(reader);
		
	    new ReadThread(serialPort).start();
	    //new WriteThread(serialPort).start();
	}
	
	void disConnect()
	{
		try
		{
			serialPort.closePort();
		} catch (SerialPortException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static String[] getPortList()
	{	
		return SerialPortList.getPortNames();
	}

	void write(byte b)
	{
		try
		{
			serialPort.writeByte(b);
		} catch (SerialPortException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void write(byte[] byteArray)
	{
		try
		{
			serialPort.writeBytes(byteArray);
		} catch (SerialPortException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void write(String s)
	{
		try
		{
			serialPort.writeString(s);
		} catch (SerialPortException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	void write(int n)
	{
		try
		{
			serialPort.writeInt(n);
		} catch (SerialPortException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}



class ReadThread extends Thread
{
	SerialPort serial;
	PacketProcessing packetProcess;
	
	
	
	ReadThread(SerialPort serial)
	{
		this.serial = serial;
		packetProcess=PacketProcessing.getInstance();
	}

	public void run()
	{
		try
		{
			while (true)
			{
				byte[] buffer = serial.readBytes();
				if(buffer != null && buffer.length > 0)
				{
					//System.out.print(new String(buffer));
					
					String msg = new String(buffer);
					//System.out.println(msg);
					RegisterTableUI.addTextArea(msg);
					packetProcess.updateRegisterTable(msg);
				}
			}
		}
		catch (Exception e)
		{
				e.printStackTrace();
		}
	}
}

class WriteThread extends Thread
{
	SerialPort serial;
	WriteThread(SerialPort serial)
	{
		this.serial = serial;
	}
	
	public void run()
	{
		try
		{
			int c = 0;
	
			//System.out.println("\nKeyborad Input Read!!!!");
			while ((c = System.in.read()) > -1)
			{
				serial.writeInt(c);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}

/*
class SerialPortReader implements SerialPortEventListener
{
	SerialPort serialPort;
	PacketProcessing packetProcess;

	public SerialPortReader(SerialPort port)
	{
		this.serialPort=port;
		packetProcess=PacketProcessing.getInstance();
	}

	public void serialEvent(SerialPortEvent event)
	{
		if(event.isRXCHAR()) //If data is available
		{
			if(event.getEventValue() > 0) //Check bytes count in the input buffer
			{
				try
				{
					while (true)
					{
						byte[] buffer = serialPort.readBytes();
						if( buffer != null && buffer.length>0 )
						{
							//System.out.print(new String(buffer));
							//RegisterTableUI.addTextArea(new String(buffer));
							
							String msg = new String(buffer);
							
							RegisterTableUI.addTextArea(msg);
							packetProcess.updateRegisterTable(msg);
						}
					}
				}
				catch (SerialPortException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
*/

