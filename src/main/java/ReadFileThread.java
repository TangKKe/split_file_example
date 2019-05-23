import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created with IntelliJ IDEA.
 * User: okey
 * Date: 14-4-2
 * Time: 下午4:50
 * To change this template use File | Settings | File Templates.
 */
public class ReadFileThread extends Thread {

    private DealFileService dealFileService;
    private File file;
    private long start;
    private long end;

    private int  dealThreadNum = 0;

    private int buffSize = 1024;
    // 换行符
    private byte key = "\n".getBytes()[0];
    // 当前行数
    private long lineNum = 0;
    // 文件编码,默认为UTF-8
    private String encode = "UTF-8";

    private static volatile boolean flag = true;
    private static int successThread  = 0;



    public ReadFileThread(DealFileService dealFileService,long start,long end, File file,int dealThreadNum) {
        this.setName(this.getName()+"-ReadFileThread");
        this.start = start;
        this.end = end;
        this.file = file;
        this.dealThreadNum = dealThreadNum;
        this.dealFileService = dealFileService;
    }

    @Override
    public void run() {
//        ReadFile readFile = new ReadFile();
//        readFile.setDealFileService(dealFileService);
//        readFile.setEncode(dealFileService.getEncode());

//            readFile.readFileByLine(file, start, end + 1,dealThreadNum,successThread,flag,Thread.currentThread().getName());
        FileChannel channel = null;
        try {
            Long time = System.currentTimeMillis();
            System.out.println("start---" + start);
            System.out.println("end---" + end);

            String name = Thread.currentThread().getName();

            if (file.exists()) {
                channel = new RandomAccessFile(file, "r").getChannel();
                channel.position(start);
                System.out.println("aaaaaaaaaaaaaaaa" + channel.size());

                ByteBuffer buffer = ByteBuffer.allocate(buffSize);
                // 每次读取的内容
                byte[] temp = new byte[buffSize];
                // 缓存
                byte[] cachedBuffer = new byte[0];
                String line = "";
                // 当前读取文件位置
                long currentPositon = start;
                while (channel.read(buffer) != -1) {
                    System.out.println("bbbbbbbbbbbbbb");
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
                        dealFileService.outLine(line.trim(), lineNum, false, name);
                        //进行数据的组装
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

                dealFileService.outLine(lineStr.trim(), lineNum, true, name);
                successThread++;
                System.out.println("啊啊啊啊successThread-----" + successThread);

                //自旋等待其它线程
                while (flag) {
                    if (dealThreadNum == successThread) {
                        break;
                    }
                }
                if(!flag){
                    throw new FileNotFoundException("文件异常");

                }
                System.out.println("successThread-----" + successThread);
                //到这里
            } else {
                throw new FileNotFoundException("文件异常");
            }

        }catch (Exception e) {
                    flag = false;
                    e.printStackTrace();
                } finally {
                   try{
                       channel.close();

                   }catch (Exception e){
                       System.out.println("wanwanwan");


                   }

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

    private static  synchronized  int update(int i){
        ++i;
        return i;
    }
}