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
            System.out.println("文件大小" + available);
            int dealThreadNum = 1;
            if(available > 1024){
                dealThreadNum = 3;
            }
            // 线程粗略开始位置
            int i = available / dealThreadNum;
            for (int j = 0; j < dealThreadNum; j++) {
                // 计算精确开始位置
                long startNum = j == 0 ? 0 : readFile.getStartNum(file, i * j);
                long endNum = j + 1 < dealThreadNum ? readFile.getStartNum(file, i * (j + 1)) : -2;

                DealDataAndInsertDB dealDataAndInsertDB = new DealDataAndInsertDB("UTF-8");
                new ReadFileThread(dealDataAndInsertDB, startNum, endNum, file,dealThreadNum).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}