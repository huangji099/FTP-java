import java.io.File;
import java.io.IOException;
import java.util.Scanner;


public class FTPServer {

	private static String fileDir="E://FTP//server-directory/";

	/**
	 * 入口
	 * @param args
	 */
	public static void main(String[] args) {

		initPath();
		IPManger.init();

		try {
			//参考FTP的模型,启动两个线程,一个数据传输,一个命令传输
			new Thread(new ControlLinkHandler(25060)).start();
			new Thread(new DataLinkHandler(25061)).start();
			new Thread(new ServerUI()).start();
		} catch (IOException e) {
			throw new IllegalStateException("初始化FTP服务器失败" + e);
		}
	}

	private static void initPath(){//当给定的目录不存在时,创建一个新的目录来初始化路径
		File dir=new File(fileDir);//创建文件对象,表示给定的路径
		if (!dir.exists()){//判定路径是否存在
			dir.mkdir();//建立一个新的目录
		}

	}
	public static String getFileFullPath(String fileName){
		return fileDir+fileName;
	}//接收一个文件名作为输入，并返回该文件的完整路径
}