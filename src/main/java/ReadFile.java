import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Observable;


public class ReadFile{

    private int buffSize = 1024;
    // 换行符
    private byte key = "\n".getBytes()[0];
    // 当前行数
    private long lineNum = 0;
    // 文件编码,默认为UTF-8
    private String encode = "UTF-8";
    // 数据的具体处理逻辑
    private DealFileService dealFileService;

    public void setEncode(String encode) {
        this.encode = encode;
    }

    public void setReaderListener(DealFileService dealFileService) {
        this.dealFileService = dealFileService;
    }

    /**
     * 获取准确开始位置
     * @param file
     * @param position
     * @return
     * @throws Exception
     */
    public long getStartNum(File file, long position) throws Exception {
        long startNum = position;
        FileChannel channel = new RandomAccessFile(file, "r").getChannel();
        channel.position(position);
        try {
            ByteBuffer buffer = ByteBuffer.allocate(buffSize);
            // 每次读取的内容
            byte[] temp = new byte[buffSize];
            // 缓存
            byte[] cachedBuffer = new byte[0];
            String line = "";
            while (channel.read(buffer) != -1) {
                int size = buffer.position();
                //当前位置置0
                buffer.rewind();
                //写入temp里
                buffer.get(temp);
                //当前写位置置为最前端下标为0处
                buffer.clear();
                byte[] newByteArray = temp;
                // 把上次的内容放到这次读取的前面，形成新的读取内容
                if (null != cachedBuffer) {
                    int L = cachedBuffer.length;
                    newByteArray = new byte[size + L];
                    //arraycopy(Object src,int srcPos,Object dest,int destPos,int length)
                    //src:源数组；srcPos:源数组要复制的起始位置；dest:目的数组；destPos:目的数组放置的起始位置；
                    //length:复制的长度。
                    System.arraycopy(cachedBuffer, 0, newByteArray, 0, L);
                    System.arraycopy(temp, 0, newByteArray, L, size);
                }
                // 获取开始位置之后的第一个换行符
                int endIndex = indexOf(newByteArray, 0);
                if (endIndex != -1) {
                    return startNum + endIndex;
                }
                //newByteArray复制到cachedBuffer作为上次读取的内容
                cachedBuffer = dealBuffer(newByteArray, 0, newByteArray.length);
                //没有读到换行 startNum累加buffSize
                startNum += buffSize;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            channel.close();
        }
        return position;
    }

    /**
     * 从设置的开始位置读取文件，一直到结束为止。如果 end设置为负数,刚读取到文件末尾
     * @param file
     * @param start
     * @param end
     * @throws Exception
     */
    public void readFileByLine(File file, long start, long end, String name) throws Exception {
        Long time = System.currentTimeMillis();
        if (file.exists()) {
            FileChannel channel = new RandomAccessFile(file, "r").getChannel();
            channel.position(start);
            try {
                ByteBuffer buffer = ByteBuffer.allocate(buffSize);
                // 每次读取的内容
                byte[] temp = new byte[buffSize];
                // 缓存
                byte[] cachedBuffer = new byte[0];
                String line = "";
                // 当前读取文件位置
                long currentPositon = start;
                while (channel.read(buffer) != -1) {
                    //从起始位置开始累加buffSize
                    currentPositon += buffSize;
                    //buffer当前所在的操作位置
                    int size = buffer.position();
                    //当前位置置0
                    buffer.rewind();
                    //写入temp里
                    buffer.get(temp);
                    //当前写位置置为最前端下标为0处
                    buffer.clear();
                    byte[] newByteArray = temp;
                    // 如果发现有上次未读完的缓存,则将它加到当前读取的内容前面
                    if (null != cachedBuffer) {
                        int L = cachedBuffer.length;
                        newByteArray = new byte[size + L];
                        System.arraycopy(cachedBuffer, 0, newByteArray, 0, L);
                        System.arraycopy(temp, 0, newByteArray, L, size);
                    }
                    // 是否已经读到最后一位
                    boolean isEnd = false;
                    // 如果当前读取的位数已经比设置的结束位置大的时候，将读取的内容截取到设置的结束位置
                    if (end > 0 && currentPositon > end) {
                        // 缓存长度 - 当前已经读取位数 - 最后位数
                        int LL = newByteArray.length - (int) (currentPositon - end);
                        newByteArray = dealBuffer(newByteArray, 0, LL);
                        isEnd = true;
                    }
                    int fromIndex = 0;
                    int endIndex = 0;
                    // 每次读一行内容，以 key（默认为\n） 作为结束符
                    while ((endIndex = indexOf(newByteArray, fromIndex)) != -1) {
                        byte[] bLine = dealBuffer(newByteArray, fromIndex, endIndex);
                        line = new String(bLine, 0, bLine.length, encode);
                        lineNum++;
                        // 输出一行内容，处理方式由调用方提供
                        dealFileService.outLine(line.trim(), lineNum, false,name);
                        fromIndex = endIndex + 1;
                    }
                    // 将未读取完成的内容放到缓存中
                    cachedBuffer = dealBuffer(newByteArray, fromIndex, newByteArray.length);
                    if (isEnd) {
                        break;
                    }
                }
                // 将剩下的最后内容作为一行，输出，并指明这是最后一行
                String lineStr = new String(cachedBuffer, 0, cachedBuffer.length, encode);
                dealFileService.outLine(lineStr.trim(), lineNum, true,name);
                System.out.println(name + "内部方法耗时 + " +(System.currentTimeMillis()-time));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                channel.close();
            }

        } else {
            throw new FileNotFoundException("文件异常");
        }
    }

    /**
     * 查找一个byte[]从指定位置之后的一个换行符位置
     * @param src
     * @param fromIndex
     * @return
     * @throws Exception
     */
    private int indexOf(byte[] src, int fromIndex) throws Exception {

        for (int i = fromIndex; i < src.length; i++) {
            if (src[i] == key) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 从指定开始位置读取一个byte[]直到指定结束位置为止生成一个全新的byte[]
     * @param src
     * @param fromIndex
     * @param endIndex
     * @return
     * @throws Exception
     */
    private byte[] dealBuffer(byte[] src, int fromIndex, int endIndex) throws Exception {
        int size = endIndex - fromIndex;
        byte[] bytes = new byte[size];
        System.arraycopy(src, fromIndex, bytes, 0, size);
        return bytes;
    }

}