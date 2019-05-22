import java.io.File;

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

    public ReadFileThread(DealFileService dealFileService,long start,long end, File file) {
        this.setName(this.getName()+"-ReadFileThread");
        this.start = start;
        this.end = end;
        this.file = file;
        this.dealFileService = dealFileService;
    }

    @Override
    public void run() {
        ReadFile readFile = new ReadFile();
        readFile.setDealFileService(dealFileService);
        readFile.setEncode(dealFileService.getEncode());
        try {
            readFile.readFileByLine(file, start, end + 1,Thread.currentThread().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}