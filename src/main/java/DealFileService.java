
import java.util.ArrayList;
import java.util.List;


public abstract class DealFileService {

    //默认文件编码
    private String encode = "UTF-8";

    public DealFileService(String encode){
        this.encode = encode;
    }


    private List<String> list = new ArrayList<String>();



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