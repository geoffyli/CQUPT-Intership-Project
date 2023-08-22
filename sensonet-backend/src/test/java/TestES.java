import com.fasterxml.jackson.core.JsonProcessingException;
import com.sensonet.SensonetApplication;
import com.sensonet.dto.DeviceDTO;
import com.sensonet.dto.DeviceLocation;
import com.sensonet.es.ESRepository;
import com.sensonet.util.JsonUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest(classes = SensonetApplication.class)
@RunWith(SpringRunner.class)
public class TestES {

    @Autowired
    private ESRepository esRepository;


    @Test
    public void testAdd() {
        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setDeviceId("00001");
        deviceDTO.setAlarm(true);
        deviceDTO.setAlarmName("Temperature too high");
        deviceDTO.setLevel(1);
        deviceDTO.setOnline(true);
        deviceDTO.setTag("Mongo");
        deviceDTO.setStatus(true);
        esRepository.addDevices(deviceDTO);
    }


    @Test
    public void testSearchById() {

        DeviceDTO deviceDTO = esRepository.searchDeviceById("123456");
        try {
            String json = JsonUtil.serialize(deviceDTO);
            System.out.println(json);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testUpdateStatus() {
        try {
            Boolean newStatus = false;
            esRepository.updateStatus("123456", newStatus);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testAlarm() {
        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setDeviceId("123456");
        deviceDTO.setAlarm(true);
        deviceDTO.setLevel(1);
        deviceDTO.setAlarmName("Mechanical failure");

        esRepository.updateDevicesAlarm(deviceDTO);

    }


    @Test
    public void testOnline() {

        esRepository.updateOnline("123456", false);

    }


    @Test
    public void testCount() {

        Long allDeviceCount = esRepository.getAllDeviceCount();//设备总数
        System.out.println("设备总数：" + allDeviceCount);

        Long offlineCount = esRepository.getOfflineCount();//离线设备数量
        System.out.println("离线设备：" + offlineCount);

        Long alarmCount = esRepository.getAlarmCount();//告警设备数量
        System.out.println("告警设备：" + alarmCount);

    }


    @Test
    public void testGEO() {

        List<DeviceLocation> deviceLocationList = esRepository.searchDeviceLocation(40.722, -73.989, 10);
        try {
            System.out.println(JsonUtil.serialize(deviceLocationList));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


}
