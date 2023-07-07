import com.fasterxml.jackson.core.JsonProcessingException;
import com.yikekong.YkkApplication;
import com.yikekong.dto.TrendPoint;
import com.yikekong.dto.TrendPoint2;
import com.yikekong.service.ReportService;
import com.yikekong.util.JsonUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest(classes = YkkApplication.class)
@RunWith(SpringRunner.class)
public class TestReport {


    @Autowired
    private ReportService reportService;

    @Test
    public void testAlarmTrend(){

        List<TrendPoint> trendPointList = reportService.getAlarmTrend("2020-09-01", "2020-09-30", 3);

        for(TrendPoint trendPoint:trendPointList){
            try {
                System.out.println(JsonUtil.serialize(trendPoint));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

    }


    @Test
    public void testQuotaTrend(){
        List<TrendPoint2> trendPoint2List = reportService.getQuotaTrend("2020-09-01 00:00:00", "2020-09-30 23:59:59"
                , "1", "100001", 3);

        for( TrendPoint2 trendPoint2:trendPoint2List ){
            try {
                System.out.println( JsonUtil.serialize(trendPoint2) );
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

    }


}
