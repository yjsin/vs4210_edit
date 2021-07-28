package editor;

import java.util.ArrayList;

public class PacketProcessing
{
	SerialConnect serialConnect;
	ArrayList<Byte> rxBuffer=new ArrayList<Byte>();
	ArrayList<Byte> txBuffer=new ArrayList<Byte>();
	
	public PacketProcessing()
	{
		serialConnect=SerialConnect.getInstance();	
	}

	private static class PacketProcessingHolder
	{
		private static final PacketProcessing Processor = new PacketProcessing();
	}
	
	public static PacketProcessing getInstance()
	{
		return PacketProcessingHolder.Processor;
	}
	
    public static String byteHexToStringHex(byte byteHex)
    {
    	return String.format("%02X", byteHex);
    }

	

    public static String byteHexToAsciiCode(byte byteHex)
    {
    	String hexString=String.format("%02X", byteHex);
    	StringBuilder output = new StringBuilder("");
    	
    	for (int i=0; i<hexString.length(); i+=2)
        {
            String str=hexString.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
         
        return output.toString();
    }
    
	public static int byteHexToIntDecimal(byte byteHex)
	{
		String hexString=String.format("%02X", byteHex);
		return Integer.parseInt(hexString,16);
	}
	
	public static byte intDecimalToByteHex(int n)
	{		
		String s = Integer.toHexString(n);
		return stringHexToByteHex(s);
		
		//return Byte.parseByte(Integer.toHexString(n), 16);
	}
	
	byte checksum(ArrayList<Byte> buffer, int startIndex, int dataLength)
	{
		byte checksum=(byte)0x00;

		for(int i=startIndex; i<startIndex+dataLength; i++)	//except ID, Command, DataCount
		{
			checksum+=buffer.get(i);
		}

		return checksum;
	}
	
	public static byte[] stringToByteHexArray(String asciiString)
    {
    	char[] chars = asciiString.toCharArray();
        byte[] b=new byte[chars.length];
        
        for (int i=0; i<chars.length; i++)
        {
            b[i]=(byte)chars[i];
        }
        return b;
    }
	
	public static byte stringHexToByteHex(String stringhex)
	{
		byte result =(byte)0x00;
		
		if( stringhex.length()>2)
		{
			System.err.println("stringHexToByteHex : input less than 255.");
			return result;
		}

		for(int i=0; i<stringhex.length(); i++)
		{
			if		(stringhex.substring(i, i+1).toUpperCase().equals("1"))	{ result=(byte) ((byte) result*16 + 0x01); }
			else if	(stringhex.substring(i, i+1).toUpperCase().equals("2"))	{ result=(byte) ((byte) result*16 + 0x02); }
			else if	(stringhex.substring(i, i+1).toUpperCase().equals("3"))	{ result=(byte) ((byte) result*16 + 0x03); }
			else if	(stringhex.substring(i, i+1).toUpperCase().equals("4"))	{ result=(byte) ((byte) result*16 + 0x04); }
			else if	(stringhex.substring(i, i+1).toUpperCase().equals("5"))	{ result=(byte) ((byte) result*16 + 0x05); }
			else if	(stringhex.substring(i, i+1).toUpperCase().equals("6"))	{ result=(byte) ((byte) result*16 + 0x06); }
			else if	(stringhex.substring(i, i+1).toUpperCase().equals("7"))	{ result=(byte) ((byte) result*16 + 0x07); }
			else if	(stringhex.substring(i, i+1).toUpperCase().equals("8"))	{ result=(byte) ((byte) result*16 + 0x08); }
			else if	(stringhex.substring(i, i+1).toUpperCase().equals("9"))	{ result=(byte) ((byte) result*16 + 0x09); }
			else if	(stringhex.substring(i, i+1).toUpperCase().equals("A"))	{ result=(byte) ((byte) result*16 + 0x0A); }
			else if	(stringhex.substring(i, i+1).toUpperCase().equals("B"))	{ result=(byte) ((byte) result*16 + 0x0B); }
			else if	(stringhex.substring(i, i+1).toUpperCase().equals("C"))	{ result=(byte) ((byte) result*16 + 0x0C); }
			else if	(stringhex.substring(i, i+1).toUpperCase().equals("D"))	{ result=(byte) ((byte) result*16 + 0x0D); }
			else if	(stringhex.substring(i, i+1).toUpperCase().equals("E"))	{ result=(byte) ((byte) result*16 + 0x0E); }
			else if	(stringhex.substring(i, i+1).toUpperCase().equals("F"))	{ result=(byte) ((byte) result*16 + 0x0F); }
			else															{ result=(byte) ((byte) result*16 + 0x00); }
		}

		return result;
	}
}
