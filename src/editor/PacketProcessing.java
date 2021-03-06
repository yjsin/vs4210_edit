package editor;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class PacketProcessing
{
	static SerialConnect serialConnect;
	ArrayList<Byte> rxBuffer=new ArrayList<Byte>();
	ArrayList<Byte> txBuffer=new ArrayList<Byte>();
	
	private int state;
	private int counter;
	private StringBuilder sb = new StringBuilder();
	
	private final int WAITTING_COUNT = 50;

	public PacketProcessing()
	{
		serialConnect=SerialConnect.getInstance();
		state=0;
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

	

    void updateRegisterTable(String str )
    {
//
//    	System.out.println(str);
//
    	if( (state==4210) || (state==2824) || (state==2912))
    	{
    		if( counter>0 )
    		{
    			counter--;

    			if( str.contains("end") )
    			{
    				sb.append(str);
    				String dump = sb.toString();
//
//    				System.out.println("\n dump \n"+ dump);
//
    				// cut
    				int startIdx, endIdx;
    				String temp;
    				String[][] table = RegisterTableUI.getStrRegisterTable();
    				
    				for(int i=0; i<16; i++)
    				{
    					startIdx = dump.indexOf(Integer.toHexString(i).toUpperCase()+":")+3; // 3 = "0: "
    					
    					if( i==15)
    					{
    						endIdx = dump.indexOf("end") - 29; //  30 = " \n ========== VS4210 register "
    						
    					}
    					else
    					{
    						endIdx = dump.indexOf(Integer.toHexString(i+1).toUpperCase()+":")-2; // 2 = " \n"
    					}
//
//    					System.out.println( "start=" + startIdx + "\t" + "end=" + endIdx);
//
    					if( startIdx > endIdx)
    					{
    						RegisterTableUI.showErrorMsgBox("Read Error!\nStart/End index error");
    	    				state=0;
    	    				counter=0;
    	    				sb.delete(0,sb.length());
    	    				//sb.setLength(0);
    						return;
    					}
    					
    					temp= dump.substring(startIdx, endIdx);

//
//    					System.out.println("temp="+temp);
//
    					
    					int j=0;
    					StringTokenizer stk = new StringTokenizer(temp, " ");
    					while(stk.hasMoreElements())
    					{
    						if( j>15 )
    						{
        						RegisterTableUI.showErrorMsgBox("Read Error!\nParsing error");
        	    				state=0;
        	    				counter=0;
        	    				sb.delete(0,sb.length());
        						return;
    					    }
    						String hexStr = stk.nextToken();
    						
    						//hexString?? ???????? ???????? ?????? ????
    						table[i][j++] = hexStr;

    					}
    				}
/*
    				// test
    				System.out.println("\n arr test");
    				for(int i=0; i<16; i++)
    				{
    					for(int j=0; j<16; j++)
    					{
    						System.out.print(table[i][j]+" ");
    					}
    					System.out.println("");
    				}
*/
    				
    				RegisterTableUI.updateRegisterTable();
    				
    				state=0;
    				counter=0;
    				sb.delete(0,sb.length());
    				
    				
    			}
    			else
    			{
    				sb.append(str);
    			}
    		}
    		else
    		{
    			state = 0;
    			counter=0;
    			sb.delete(0,sb.length());
    		}
    	}
    	else if( state == 1 ) // read???? ????
    	{
    		if( counter>0 )
    		{
    			counter--;
    			sb.append(str);
    			String temp = sb.toString();
//
//    			System.out.println("temp \n" + temp );
//
        		if( temp.contains("VS4210 register read") )
        		{
        			state = 4210;
        			counter=WAITTING_COUNT; //?????? ???????????? ?????? ???????? ?????? ???????? ???????? ??????
        		}
        		else if( temp.contains("TP2824 register read") )
        		{
        			state = 2824;
        			counter=WAITTING_COUNT;
        		}
        		else if( temp.contains("TP2912 register read") )
        		{
        			state = 2912;
        			counter=WAITTING_COUNT;
        		}
        		else if( str.contains("change") || str.contains("write") )
        		{
        			state = 0;
        			counter=0;
        			sb.delete(0,sb.length());
        		}
    		}
    		else
    		{
    			state = 0;
    			counter=0;
    			sb.delete(0,sb.length());
    		}
    	}
    	else
    	{
    		if( str.contains("===") ) // ???????? ???? ???? ??????  ex> "vs4210 re"+"gister"
    		{
    			sb.append(str);
    			state = 1;
    			counter = 3;
    		}
    	}
    }
    

    public static void writeRegister(int address, int value)
    {
    	int sleepTime = 20;
//    	
//    	System.out.println("writeRegister("+address+","+value+")");
//    	
    	serialConnect.write('w');
    	try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	serialConnect.write( Integer.toHexString(address/16)  );
    	
    	try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	serialConnect.write( Integer.toHexString(address%16)  );
    	
    	try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	serialConnect.write( Integer.toHexString(value/16)  );
    	
    	try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	serialConnect.write( Integer.toHexString(value%16)  );
    	try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }



//
    
    

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

	public static String decToBinary(int n)
    {
        int[] binaryNum = new int[32];
        int cnt = 0;
        String result="";
        
        while (n > 0)
        {
            binaryNum[cnt] = n % 2;
            n = n / 2;
            cnt++;
        }
        
        for (int i = cnt - 1; i >= 0; i--)
        {
            //System.out.print(binaryNum[i]);
        	result = result + Integer.toString(binaryNum[i]);
        }

		//make 8 bit
		while ( result.length() < 8 )
		{
			result = "0" + result;
		}

        return result;
    }
}
