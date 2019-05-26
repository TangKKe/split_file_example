import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


public class DealFile {


    public static void main(String[] args) throws Exception {


        File file = new File("/Users/tangke/Downloads/test.csv");
        FileInputStream fis = null;
        ReadFile readFile = new ReadFile();
        try {
            fis = new FileInputStream(file);
            int available = fis.available();
            int dealThreadNum = 1;
            //当文件大于1M时 处理线程的线程数为2，缓存大小定义为1M
            //小于1M一个线程处理
            //规定缓存的大小以减少从系统内存复制到用户内存的复制次数
            int buffSize = 1024;
            if(available > 1024 * 1024){
                dealThreadNum = 2;
                buffSize = 1024 * 1024;
            }else{
                dealThreadNum = 1;
                buffSize = 1024 * 10;

            }
            // 线程粗略开始位置
            int i = available / dealThreadNum;
            for (int j = 0; j < dealThreadNum; j++) {
                // 计算精确开始位置
                long startNum = j == 0 ? 0 : readFile.getStartNum(file, i * j);
                long endNum = j + 1 < dealThreadNum ? readFile.getStartNum(file, i * (j + 1)) : -2;

                DealDataAndInsertDB dealDataAndInsertDB = new DealDataAndInsertDB("UTF-8");
                new ReadFileThread(dealDataAndInsertDB, startNum, endNum, file,dealThreadNum,buffSize).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}