


- jre v1.7 설치가 필요 (실행 시 설치사이트로 이동 됨)
- 레지스터 1개의 read/write 만 가능, 미구현된 부분이나 버그가 많음
 * 미구현 : chip select, dump save , value(bit) 등
 * 버그의 경우 프로그램창을 닫고 재실행하면 대부분 해결됨
- 시리얼통신(uart)을 사용하여 터미널프로그램과 동시 사용 불가
- 사용법
 1) stm32 디버그포트(con11)에 uart모듈 연결
 2) 프로그램 실행 후 Select Port 콤보상자에서 연결한 시리얼포트 선택 후 open
 3) read all 버튼을 클릭하면 vs4210에서 0x00-0xFF까지 레지스터를 읽어옴
    (수동으로 동작, 자동으로 업데이트 안됨)
 4) 원하는 곳에 값을 입력 후 탭 또는 엔터를 치면 해당 주소에 값을 라이팅함
 5) 값 입력 도중 esc누르면 원래 값을 표시함
 6) 레지스터 덤프 적용의 경우 지정된 형식의 텍스트 파일을 사용
    파일 확장자 : *.txt, 내용 : VS4210 chip ID,주소,설정값
    ex> dump_test.txt
       40,02,00
       40,10,00
       ...


------------------------------------------------- 번역(Translation) -------------------------------------------------



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
- TP2824 Read 기능 추가

220121 v0.0.3
- 버그수정

211012 v0.0.2
- dump 기능 구현

211008 v0.0.1
- 레지스터 1개 read/write 기능 구현
