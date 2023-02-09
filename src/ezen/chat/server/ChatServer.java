package ezen.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

public class ChatServer {
	private static final int PORT = 7777;
	private ServerSocket serverSocket;
	private boolean running; // 서버가 구동중인지 아닌지 확인.
	
	// 서버에 접속한 클라이언트들을 관리. 맵에는 소켓챗클이 저장. Hashtable왜 쓰냐. 쓰레드와 같이 각각이 동기화라서(비동기화 아님?)
	private Map<String, SocketChatClient> clients;

	/** ChatServer 구동 */
	public void startup() throws IOException {
		serverSocket = new ServerSocket(PORT);
		System.out.println("========= [ChatServer(" + PORT + ")] Start =========");
		running = true;
		
		clients = new Hashtable<String, SocketChatClient>();
		
		Thread thread = new Thread() {
			public void run() {
				try {
					while (running) {
						System.out.println("[ChatServer(" + PORT + ")] ChatClient Connect Listennign ..");
						Socket socket = serverSocket.accept();
						System.out.println("[ChatCleint(" + socket.getInetAddress().getHostAddress() + ")] 연결해옴...");

						// ChatClient와 데이터 송수신 스레드 생성 및 실행. 이제부터 소켓챗클이 알아서 해~
						SocketChatClient socketChatClient = new SocketChatClient(socket, ChatServer.this);
						addSocketChatClient(socketChatClient);
						socketChatClient.start();
					}
				} catch (IOException e) {
					System.err.println("[ChatServer(" + PORT + ")] 실행 중 아래와 같은 오류가 발생하였습니다.");
					System.err.println("오류 내용 :  " + e.getMessage());
				}
			}
		};
		thread.start();
	}
	
	/** 접속한 클라이언트를 콜렉션에 추가 */
	public void addSocketChatClient(SocketChatClient socketChatClient) {
		clients.put(socketChatClient.getClientNickName(), socketChatClient);
		System.out.println("[클라이언트] : 입장");
		System.out.println("[현재 채팅에 참여중인 클라이언트 수] : " + clients.size());
	}
	
	/** 접속한 모든 클라이언트에게 메세지 전송 */
	public void sendAllMessage(String message) {
		Collection<SocketChatClient> allList = clients.values();
		for (SocketChatClient socketChatClient : allList) {
			socketChatClient.sendMessage(message);
		}
	}
	
	
	
	/** ChatServer 종료 */
	public void shutdown() {
		try {
			serverSocket.close();
			System.err.println("[ChatServer(" + PORT + ")] 종료됨...");
		} catch (IOException e) {}
	}
	
	
}
