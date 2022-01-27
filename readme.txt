


- jre v1.7 ��ġ�� �ʿ� (���� �� ��ġ����Ʈ�� �̵� ��)
- �������� 1���� read/write �� ����, �̱����� �κ��̳� ���װ� ����
 * �̱��� : chip select, dump save , value(bit) ��
 * ������ ��� ���α׷�â�� �ݰ� ������ϸ� ��κ� �ذ��
- �ø������(uart)�� ����Ͽ� �͹̳����α׷��� ���� ��� �Ұ�
- ����
 1) stm32 �������Ʈ(con11)�� uart��� ����
 2) ���α׷� ���� �� Select Port �޺����ڿ��� ������ �ø�����Ʈ ���� �� open
 3) read all ��ư�� Ŭ���ϸ� vs4210���� 0x00-0xFF���� �������͸� �о��
    (�������� ����, �ڵ����� ������Ʈ �ȵ�)
 4) ���ϴ� ���� ���� �Է� �� �� �Ǵ� ���͸� ġ�� �ش� �ּҿ� ���� ��������
 5) �� �Է� ���� esc������ ���� ���� ǥ����
 6) �������� ���� ������ ��� ������ ������ �ؽ�Ʈ ������ ���
    ���� Ȯ���� : *.txt, ���� : VS4210 chip ID,�ּ�,������
    ex> dump_test.txt
       40,02,00
       40,10,00
       ...


------------------------------------------------- ����(Translation) -------------------------------------------------



- Requires jre v1.7 installation (moved to the installation site when executed)
- Only read/write of one register is possible, there are many unimplemented parts or bugs
  * Unimplemented: chip select, dump, value(bit), etc.
  * Most bugs are resolved by closing the program window and re-launching it
- Cannot be used simultaneously with terminal program using serial communication (uart)
- How to use
  1) Connect uart module to stm32 debug port (con11)
  2) After executing the program, select the connected serial port in the Select Port combo box and open
  3) Click the read all button to read registers from vs4210 to 0x00-0xFF
     (Manually operated, not automatically updated)
  4) After entering a value in the desired place, press Tab or Enter to write the value to the address.
  5) If you press esc while entering a value, the original value is displayed.
  6) Register dump apply function uses specified format.
     File extension: *.txt, Content: chip ID,address,setting value
    ex> dump_test.txt
       40,02,00
       40,10,00
       ...


---------------------------------------------------------------------------------------------------------------------


Release Note

220127 v0.0.4
- TP2824 Read ��� �߰�

220121 v0.0.3
- ���׼���

211012 v0.0.2
- dump ��� ����

211008 v0.0.1
- �������� 1�� read/write ��� ����
