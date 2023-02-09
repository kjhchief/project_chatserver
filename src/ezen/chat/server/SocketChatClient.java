package ezen.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * 접속한 클라이언트와 1:1로 통신하는 역할의 쓰레드. 소켓으로 통신하는 
 * @author 김재훈
 * @Date   2023. 2. 7.
 */
public class SocketChatClient extends Thread {

	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out; // 바이트코드로 문자도 다른 정보도 송신 가능
	private String clientIp;
	private String clientNickName; // 대화명
	
	private ChatServer chatServer;

	public SocketChatClient(Socket socket, ChatServer chatServer) {
		try {
			this.socket = socket;
			this.chatServer = chatServer;
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
			clientIp = socket.getInetAddress().getHostAddress();
		} catch (IOException e) {}
		
	}
	
	public String getClientIp() {
		return clientIp;
	}

	public String getClientNickName() {
		return clientNickName;
	}

	// 클라이언트 메시지 수신 . 채팅 보내는 메세지 관련.
	public void receiveMessage() {
		try {
			while (true) {
				String clientMessage = in.readUTF();
				System.out.println("[클라이언트]로부터 수신한 메시지 : " + clientMessage);
//				sendMessage(clientMessage);
				// "CONNECT*방그리"
				String[] tokens = clientMessage.split("*");
				String messageType = tokens[0];
				
				switch (messageType) {
				// 최초 입장
				case "CONNECT":
					clientNickName = tokens[1];
					// 자기 포함해서 현재 접속한 모든 클라이언트에게 메세지 전송
					chatServer.sendAllMessage(clientMessage);
					
					break;

				}
				
				if (clientMessage.equalsIgnoreCase("q")) {
					break;
				}
			}
		} catch (IOException e) {} 
		finally { // 예외가 발생 하든 안하든(네트웍 끊어지는 사용자가 끄든).
			System.out.println("[클라이언트(" + socket.getInetAddress().getHostAddress() + ")] 연결 종료함...");
		}
	}
	
	// 클라이언트에게 메시지 전송 (먼저는 자기 자신에게 메세지 전송 기능인데 이걸 챗서버에서 전체에게 하는걸로 구현함)
	public void sendMessage(String message) {
		try {
			out.writeUTF(message);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// 연결 종료
	public void close() {
		try {
			if(socket != null) socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// 스레드의 실행 진입점
	@Override
	public void run() {
		receiveMessage();
	}

}
