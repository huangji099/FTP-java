import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * IP控制器,加入白名单,黑名单功能
 * 白名单黑名单只能开启一个
 */
public class IPManger {
    public static int maxLinkNum=3;
    public static boolean banOpen=false;
    public static boolean whiteOpen=false;
    public static ConcurrentHashMap<Long, Socket> connetingMap = new ConcurrentHashMap<Long,Socket>();
    public static CopyOnWriteArrayList<Socket> conSockets=new CopyOnWriteArrayList<>();
    private static List<InetAddress> banList=new ArrayList<>();
    private static List<InetAddress> whiteList=new ArrayList<>();
    private static String banListFileName="BanList.txt";
    private static String whiteListFileName="WhiteList.txt";
    public static void init(){//初始化列表
        initList(banListFileName,banList);
        initList(whiteListFileName,whiteList);
    }
    private static void  initList(String fileName,List<InetAddress> list){
        File dir=new File(getConfigureDir()); // 获取配置目录的 File 对象
        if (!dir.exists()){
            dir.mkdir(); // 如果目录不存在，创建一个新的目录
        }
        File file=new File(getConfigureDir()+"/"+fileName); // 获取配置文件的 File 对象
        if(file.exists()){ // 检查配置文件是否存在
            // 读取配置文件内容，并将其添加到列表中
            Scanner scanner = null;
            try {
                scanner=new Scanner(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            while (scanner.hasNext()){
                try {
                    list.add(Inet4Address.getByName(scanner.next())); // 将读取到的IP地址添加到列表中
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }else {
            try {
                file.createNewFile(); // 如果配置文件不存在，创建一个新的空文件
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static boolean canConnect(Socket socket,Object[] obj){
        if(conSockets.size()>=maxLinkNum){
            obj[0]="超出最大连接数";
            obj[1]="超出最大连接数";
            return false;
        }else {
            if(banOpen&&isBan(socket)){
                obj[0]="您已经被服务器拉入黑名单,无法连接";
                obj[1]="有黑名单中的IP试图连接服务器";
                return false;
            }

            if(whiteOpen){
                if(isWhite(socket)){
                    return true;
                }else {
                    obj[0]="服务器已经开启白名单模式,您的IP不在白名单中,无法连接";
                    obj[1]="有非白名单中的IP试图连接服务器";
                    return false;
                }
            }

            return true; // 默认情况下，允许连接
        }
    }

    public static boolean isBan(Socket socket){
        InetAddress socketAddr=socket.getInetAddress(); // 获取 socket 的 IP 地址
        for (InetAddress addr:banList) { // 遍历黑名单列表中的每个 IP 地址
            if (addr.equals(socketAddr)) { // 如果 IP 地址相等，则说明 socket 在黑名单中
                return true;
            }
        }
        return false; // 如果遍历完整个黑名单列表后，没有找到 socket 的 IP 地址，说明其不在黑名单中
    }

    public static boolean isWhite(Socket socket){
        InetAddress socketAddr=socket.getInetAddress(); // 获取 socket 的 IP 地址
        for (InetAddress addr:whiteList) { // 遍历白名单列表中的每个 IP 地址
            if (addr.equals(socketAddr)) { // 如果 IP 地址相等，则说明 socket 在白名单中
                return true;
            }
        }
        return false; // 如果遍历完整个白名单列表后，没有找到 socket 的 IP 地址，说明其不在白名单中
    }

    public static void ban(String ip){
        File file=new File(getConfigureDir()+"/"+banListFileName); // 构建黑名单文件的路径
        appendList(file,ip); // 将指定的 IP 地址追加到黑名单文件中
        init(); // 重新初始化黑名单列表
    }

    public static void white(String ip){
        File file=new File(getConfigureDir()+"/"+whiteListFileName); // 构建白名单文件的路径
        appendList(file,ip); // 将指定的 IP 地址追加到白名单文件中
        init(); // 重新初始化白名单列表
    }

    private static void appendList(File file, String s){
        try {
            FileWriter writer = new FileWriter(file, true); // 创建一个 FileWriter 对象，指定以追加模式写入文件
            writer.write(s); // 将字符串写入文件
            writer.write("\n"); // 写入换行符
            writer.flush(); // 刷新缓冲区
            writer.close(); // 关闭 FileWriter
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //获取配置文件的完整路径
    public static String getConfigureDir(){
       return FTPServer.getFileFullPath("conf");
    }
}