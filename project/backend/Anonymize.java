import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Anonymize{
   
    public static void main(String[] args) {
        //設DB順序為 (userid,content,(topicid,commentid),time,randomid)
        
        String[][] database_array = new String[5][];      //虛擬資料實際應該超過5個
        database_array[0] = new String[]{"123"};
        database_array[1] = new String[]{"今天是禮拜三"};
        database_array[2] = new String[]{"114","514"};
        database_array[3] = new String[]{"20251213"};
        database_array[4] = new String[]{"0"};   //還沒給randomid 所以是0

        //讀取DB array

        long currentTime = Long.parseLong(database_array[3][0]);
        //long currentTime = System.currentTimeMillis();     測試random用到時候要拿掉，這邊應該要放在發文那邊，發文後直接透過這個紀錄時間


        Random random = new Random(currentTime);
        int randomid = random.nextInt(100000000);
        System.out.printf("%08d",randomid);
        
        //儲存資料
        String randomid_string =String.format("%08d", randomid);
        database_array[4] = new String[]{randomid_string};

        //回傳database_array到DB(不會用)
    }

}