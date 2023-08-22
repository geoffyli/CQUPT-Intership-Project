import com.fasterxml.jackson.core.JsonProcessingException;
import com.sensonet.SensonetApplication;
import com.sensonet.dto.QuotaInfo;
import com.sensonet.influx.InfluxRepository;
import com.sensonet.service.QuotaService;
import com.sensonet.util.JsonUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest(classes = SensonetApplication.class)
@RunWith(SpringRunner.class)
public class TestInfluxDb {


    @Autowired
    private InfluxRepository influxRepository;


    @Autowired
    private QuotaService quotaService;


    @Test
    public void testAdd(){

        QuotaInfo quotaInfo=new QuotaInfo();
        quotaInfo.setDeviceId("123456");
        quotaInfo.setQuotaId("1");
        quotaInfo.setQuotaName("温度");
        quotaInfo.setReferenceValue("0-10");
        quotaInfo.setUnit("摄氏度");
        quotaInfo.setAlarm("1");
        quotaInfo.setValue(11D);
        influxRepository.add(quotaInfo);

    }


    @Test
    public void testFindLast(){
        List<QuotaInfo> quotaList = quotaService.getLastQuotaList("10011");

        try {
            String json = JsonUtil.serialize(quotaList);
            System.out.println(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }



}
