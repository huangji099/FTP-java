import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;

/**
 * 数据传输线程
 */
public class DataLinkHandler implements Runnable {

	private ServerSocket dataServerSocket; // 数据服务器套接字

	private Scanner scanner; // 用于从数据流中读取数据

	/**
	 * 构造函数
	 *
	 * @param port 端口号
	 * @throws IOException
	 */
	public DataLinkHandler(int port) throws IOException {
		// 初始化数据服务器套接字
		dataServerSocket = new ServerSocket(port, 10, Inet4Address.getLocalHost());
		System.out.println("数据连接已经在 " + dataServerSocket.getInetAddress() + " 端口: "
				+ dataServerSocket.getLocalPort() + "开启");
	}

	@Override
	public void run() {
		// 无限循环用于接收客户端的连接请求
		while (true) {
			Socket dataSocket = null; // 数据套接字
			Socket ctrlSocket = null; // 控制套接字

			try {
				// 接收客户端的数据连接请求
				dataSocket = dataServerSocket.accept();
				System.out.println("数据连接 接收到 来自 客户端 " + dataSocket.getInetAddress() + " 端口: "
						+ dataSocket.getPort() + "的连接请求");
				dataSocket.setSoTimeout(5000); // 设置读取数据的超时时间为5秒
				scanner = new Scanner(dataSocket.getInputStream()); // 使用Scanner读取数据流
				Long connectionId = scanner.nextLong(); // 从输入流中读取客户端ID标识

				// 根据控制连接返回的ID标识从连接管理器中获取控制套接字
				ctrlSocket = IPManger.connetingMap.get(connectionId);

				if (ctrlSocket == null) {
					// 如果控制套接字为null，表示客户端未连接到服务端的控制连接，关闭数据套接字
					System.out.println("客户端未连接到服务端的控制连接 " + dataSocket.getInetAddress() + " 端口: "
							+ dataSocket.getPort());
					dataSocket.close();
				} else {
					// 客户端数据连接成功，添加数据套接字到连接管理器，删除控制连接，开启用户服务线程
					System.out.println("客户端数据连接成功 " + dataSocket.getInetAddress() + " 端口 "
							+ dataSocket.getPort());
					IPManger.connetingMap.remove(connectionId); // 删除控制连接
					IPManger.conSockets.add(dataSocket); // 将数据套接字添加到连接管理器的数据连接列表中
					new Thread(new ServerSession(ctrlSocket, dataSocket)).start(); // 开启用户服务线程
				}

			} catch (SocketTimeoutException e) {
				// 数据连接超时，关闭数据套接字
				System.out.println("数据连接超时，关闭连接");
				try {
					if (dataSocket != null && !dataSocket.isClosed())
						dataSocket.close();
				} catch (IOException e1) {
					throw new IllegalStateException("关闭连接出错 " + e1);
				}
			} catch (IOException e) {
				// 数据连接异常，抛出异常并打印错误信息
				throw new IllegalStateException("数据连接出错: " + e);
			}
		}
	}
}