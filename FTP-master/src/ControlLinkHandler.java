import java.io.IOException;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 * 命令传输线程
 */
public class ControlLinkHandler implements Runnable {
	private static Random random;  // 产生随机数的对象
	private ServerSocket controlServerSocket;  // 控制socket
	private PrintWriter writer;
	static {
		random = new Random();
	}

	/**
	 * @param port 初始化端口号
	 * @throws IOException
	 */
	public ControlLinkHandler(int port) throws IOException {
		controlServerSocket = new ServerSocket(port,10,Inet4Address.getLocalHost());  // 在指定端口上初始化ServerSocket，最大等待连接数为10
		System.out.println("控制连接已经在 " + controlServerSocket.getInetAddress() +
				" 端口: " + controlServerSocket.getLocalPort()+"开启");
	}

	@Override
	public void run() {
		// 无限循环接收客户端的连接请求并处理请求
		while(true) {
			Socket socket;
			try {
				socket = controlServerSocket.accept();  // 接收客户端的连接请求，该方法会阻塞
				System.out.println("控制连接 接收到 来自 客户端"
						+ socket.getInetAddress() + " 端口: " + socket.getPort()+"的连接请求");
				socket.setSoTimeout(5000);  // 设置读取超时时间为5秒

				writer = new PrintWriter(socket.getOutputStream(),true);

				Object[] objects = new Object[2];  // 用于存储检查连接是否可以建立的结果
				if(IPManger.canConnect(socket, objects)){  // 判断连接是否可以建立
					Long connectionId = random.nextLong();  // 生成一个客户端ID识别码
					writer.println(true);  // 发送连接建立的结果给客户端
					writer.println(connectionId.toString());  // 发送客户端ID给客户端
					IPManger.connetingMap.put(connectionId, socket);  // 将连接保存到连接管理器中

				}else {
					writer.println(false);  // 发送连接建立的结果给客户端
					writer.println(objects[0]);  // 返回给客户端的错误信息
					System.out.printf(objects[1].toString());
					socket.close();
				}


			} catch (IOException e) {
				throw new IllegalStateException("客户端连接时出现IO错误:" + e);
			}
		}
	}
}