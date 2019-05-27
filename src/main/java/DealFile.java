import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


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
            ExecutorService executorService = Executors.newFixedThreadPool(dealThreadNum);
            ArrayList<Future<String>> resultList = new ArrayList<Future<String>>();

            // 线程粗略开始位置
            int i = available / dealThreadNum;
            for (int j = 0; j < dealThreadNum; j++) {
                // 计算精确开始位置
                long startNum = j == 0 ? 0 : readFile.getStartNum(file, i * j);
                long endNum = j + 1 < dealThreadNum ? readFile.getStartNum(file, i * (j + 1)) : -2;

                DealDataAndInsertDB dealDataAndInsertDB = new DealDataAndInsertDB("UTF-8");
                resultList.add(executorService.submit(new ReadFileThread(dealDataAndInsertDB, startNum, endNum, file,dealThreadNum,buffSize,j)));
            }

            if(null != resultList && resultList.size() > 0){
                System.out.println("111111111111"+resultList.get(1).get());
                System.out.println("111111111111"+resultList.get(0).get());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("主线程成功捕捉到异常"+e.getMessage());
            e.printStackTrace();
        }
    }

}