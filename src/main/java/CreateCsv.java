

import com.csvreader.CsvWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

public class CreateCsv {
    public static void main(String[] args) {
        // 定义一个CSV路径
        String file = "/Users/tangke/Downloads/test.csv";
        try {
            // 创建CSV读对象(文件路径，分隔符，编码格式)
            CsvWriter csvWriter = new CsvWriter(file, ',', Charset.forName("UTF-8"));

            // 设置标题
            String[] csvHeaders = { "id", "name1","name2","name3","name4","name1","name2","name3","name4","name1","name2","name3","name4"};
            csvWriter.writeRecord(csvHeaders);

            for(int i = 0; i < 5; i++){

                String id = String.valueOf(i);


                String name1 = "xxxx44ffff2222dd";

                String name2 = "aaxsxs";

                String name3 = "name3";

                String name4 = "name4";

                String name5 = "xxxxxx";

                String name6 = "vvvvvv";

                String name7 = "nam123cedcee4";

                String name8 = "xsxdcdcd";

                String name9 = "naswe23me4";

                String name10 = "cccdcwxa";

                String name11 = "cececec";

                String name12 = "ecececsoqnsuxn";


                String [] insert = {id,name1,name2,name3,name4,name5,name6,name7,name8,name9,name10,name11,name12};

                csvWriter.writeRecord(insert);

            }


            // 关闭
            csvWriter.close();


        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}