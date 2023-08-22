import com.fasterxml.jackson.core.JsonProcessingException;
import com.sensonet.SensonetApplication;
import com.sensonet.dto.DeviceInfoDTO;
import com.sensonet.service.AlarmService;
import com.sensonet.service.QuotaService;
import com.sensonet.util.JsonUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes = SensonetApplication.class)
@RunWith(SpringRunner.class)
public class TestQuota {

    @Autowired
    private QuotaService quotaService;

    @Autowired
    private AlarmService alarmService;

    /**
     * Test the analysis method in QuotaServiceImpl.java
     */
    @Test
    public void testQuotaAnalysis() {
        // Simulate a payload
        Map map = new HashMap<>();
        map.put("sn", "123456");
        map.put("temp", 10);
        DeviceInfoDTO deviceInfoDTO = quotaService.analysis("temperature", map);
        String json = null;
        try {
            json = JsonUtil.serialize(deviceInfoDTO);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println(json);


    }

    @Test
    public void testAlarm(){
        // Simulate a payload
        Map map = new HashMap<>();
        map.put("sn", "123456");
        map.put("temp", 10);
        // Encapsulate the payload into a DeviceInfoDTO object
        DeviceInfoDTO deviceInfoDTOUpdated = alarmService.verifyDeviceInfo(quotaService.analysis("temperature", map));
        String json = null;
        try {
            json = JsonUtil.serialize(deviceInfoDTOUpdated);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println(json);
    }


}
