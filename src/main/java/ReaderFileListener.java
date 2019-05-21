
import java.util.ArrayList;
import java.util.List;

/**
 * Created with Okey
 * User: Okey
 * Date: 13-3-14
 * Time: 下午3:19
 * NIO逐行读数据回调方法
 */
public abstract class ReaderFileListener {

    // 一次读取行数，默认为5
    private int readColNum = 5;

    private String encode;

    public ReaderFileListener(String encode){
        this.encode = encode;
    }


    private List<String> list = new ArrayList<String>();

    /**
     * 设置一次读取行数
     * @param readColNum
     */
    protected void setReadColNum(int readColNum) {
        this.readColNum = readColNum;
    }

    public String getEncode() {
        return encode;
    }



    /**
     * 每读取到一行数据，添加到缓存中
     * @param lineStr 读取到的数据
     * @param lineNum 行号
     * @param over 是否读取完成
     * @throws Exception
     */
    public void outLine(String lineStr, long lineNum, boolean over, String name) throws Exception {


    }

    /**
     * 批量输出
     *
     * @param stringList
     * @throws Exception
     */
    public abstract void output(List<String> stringList, String name) throws Exception;

}