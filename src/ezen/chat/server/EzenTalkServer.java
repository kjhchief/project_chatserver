package ezen.chat.server;

import java.io.IOException;
import java.util.Scanner;

public class EzenTalkServer {

	public static void main(String[] args) {
		try {
			ChatServer chatServer = new ChatServer();
			// 서버 구동
			chatServer.startup();
			
			System.out.println("-----------------------------------------------");
			System.out.println("※※※ 서버를 종료하려면 q 명령어를 입력하세요 ※※※");
			System.out.println("-----------------------------------------------");
			
			Scanner scanner = new Scanner(System.in);
			while (true) {
				String command = scanner.nextLine();
				if (command.equalsIgnoreCase("q")) {
					break;
				}
			}
			scanner.close();
			chatServer.shutdown();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
