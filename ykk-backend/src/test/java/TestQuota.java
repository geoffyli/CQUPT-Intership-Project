import com.fasterxml.jackson.core.JsonProcessingException;
import com.yikekong.YkkApplication;
import com.yikekong.dto.DeviceInfoDTO;
import com.yikekong.service.AlarmService;
import com.yikekong.service.QuotaService;
import com.yikekong.util.JsonUtil;
import org.influxdb.InfluxDB;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes = YkkApplication.class)
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
