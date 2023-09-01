import com.fasterxml.jackson.core.JsonProcessingException;
import com.sensonet.SensonetApplication;
import com.sensonet.dto.TrendPointDTO;
import com.sensonet.service.ReportService;
import com.sensonet.util.JsonUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest(classes = SensonetApplication.class)
@RunWith(SpringRunner.class)
public class TestReport {


    @Autowired
    private ReportService reportService;

    @Test
    public void testAlarmTrend(){

        List<TrendPointDTO> trendPointDTOList = reportService.getAlarmTrend("2023-08-01", "2023-08-07", 3);

        for(TrendPointDTO trendPointDTO : trendPointDTOList){
            try {
                System.out.println(JsonUtil.serialize(trendPointDTO));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

    }


}
