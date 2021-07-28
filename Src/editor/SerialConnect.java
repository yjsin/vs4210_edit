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
		
		SerialPortReader reader=new SerialPortReader(serialPort);
        serialPort.addEventListener(reader);
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
					byte[] buffer = serialPort.readBytes();
					for(int i=0; i<buffer.length; i++)
					{
//print
						System.out.println("Serial rxBuffer[" + i + "]: " + PacketProcessing.byteHexToStringHex(buffer[i]) + " "+ PacketProcessing.byteHexToAsciiCode(buffer[i]));
						//MainGui.setTextArea("buffer[" + i + "]: " + byteToHex(buffer[i]) + " "+ hexToAscii(byteToHex(buffer[i]))+"\n");
//
						//packetProcess.rxProcessing(buffer[i]);
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

