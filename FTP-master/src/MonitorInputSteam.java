import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by qianzise on 2017/5/4 0004.
 */
public class MonitorInputSteam extends FilterInputStream {


    private long timestamp;
    private int maxbps;
    private int currentbps;
    private int bytesread;


    public MonitorInputSteam(InputStream in, int maxbps){
        super(in); // 调用父类的构造函数，将输入流对象传递给父类
        this.maxbps = maxbps; // 设置最大读取速率（每秒字节数）
        this.currentbps = 0; // 当前读取速率（每秒字节数）
        this.bytesread = 0; // 已读取的字节数
        this.timestamp = System.currentTimeMillis(); // 当前时间戳
    }

    public MonitorInputSteam(InputStream in){
        this(in,Integer.MAX_VALUE);
    }

    @Override
    public int read() throws IOException {
        synchronized(in){
            int available = check(); // 检查当前可用的字节数
            if(available == 0){ // 如果没有可读取的数据
                waitForAvailable(); // 等待数据的可用性
                available = check(); // 再次检查可用的字节数
            }
            int value = in.read(); // 从输入流中读取一个字节
            update(1); // 更新已读取的字节数
            return value; // 返回读取的字节值
        }
    }
    @Override
    public int read(byte[] b) throws IOException{
        return read(b, 0, b.length);

    }
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        synchronized (in) {
            int available = check(); // 检查当前可用的字节数
            if (available == 0) { // 如果没有可读取的数据
                waitForAvailable(); // 等待数据的可用性
                available = check(); // 再次检查可用的字节数
            }
            int n = in.read(b, off, len); // 从输入流中将数据读取到字节数组中的指定位置
            update(n); // 更新已读取的字节数
            return n; // 返回实际读取的字节数
        }
    }

    private int check() {
        long now = System.currentTimeMillis(); // 获取当前时间
        if (now - timestamp >= 1000) { // 如果距离上次检查超过1秒
            timestamp = now; // 更新时间戳
            bytesread = 0; // 重置已读取的字节数为0
            return maxbps; // 返回最大字节数
        } else {
            return maxbps - bytesread; // 返回剩余可读取的字节数
        }
    }

    private void waitForAvailable(){
        long time = System.currentTimeMillis() - timestamp;
        boolean isInterrupted = false;
        while(time < 1000){
            try{
                Thread.sleep(1000 - time);
            }catch(InterruptedException e){
                isInterrupted = true;
            }
            time = System.currentTimeMillis() - timestamp;
        }
        if(isInterrupted)
            Thread.currentThread().interrupt();
        return;

    }

    private void update(int n){
        bytesread += n;
        if (System.currentTimeMillis()!=timestamp){
            currentbps= (int) (bytesread/(System.currentTimeMillis()-timestamp));
        }
    }

    public int getCurrentbps(){
        return currentbps;
    }



}
