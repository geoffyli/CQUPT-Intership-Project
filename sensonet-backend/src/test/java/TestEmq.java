import com.sensonet.SensonetApplication;
import com.sensonet.emq.EmqClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = SensonetApplication.class)
@RunWith(SpringRunner.class)
public class TestEmq {


    @Autowired
    private EmqClient emqClient;

    @Test
    public void testSend(){
        // Connect to the MQTT server
        emqClient.connect();
        // Publish a message to the MQTT server
        emqClient.publish("test_topic","test_content");

    }

}
