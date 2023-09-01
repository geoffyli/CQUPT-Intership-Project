import com.fasterxml.jackson.core.JsonProcessingException;
import com.sensonet.SensonetApplication;
import com.sensonet.dto.QuotaInfoDTO;
import com.sensonet.influxdb.InfluxRepository;
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

        QuotaInfoDTO quotaInfoDTO =new QuotaInfoDTO();
        quotaInfoDTO.setDeviceId("123456");
        quotaInfoDTO.setQuotaId("1");
        quotaInfoDTO.setQuotaName("温度");
        quotaInfoDTO.setReferenceValue("0-10");
        quotaInfoDTO.setUnit("摄氏度");
        quotaInfoDTO.setAlarm("1");
        quotaInfoDTO.setValue(11D);
        influxRepository.add(quotaInfoDTO);

    }


    @Test
    public void testFindLast(){
        List<QuotaInfoDTO> quotaList = quotaService.getLastQuotaList("10011");

        try {
            String json = JsonUtil.serialize(quotaList);
            System.out.println(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }



}
