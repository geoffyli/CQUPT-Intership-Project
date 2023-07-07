package com.yikekong.emq;

import com.yikekong.config.EmqConfig;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class EmqClient {

    @Autowired
    private EmqConfig emqConfig;

    private MqttClient mqttClient;//客户端连接


    @Autowired
    private EmqMsgProcess emqMsgProcess;

    /**
     * 连接emq
     */
    public void  connect(){
        try {
            mqttClient=new MqttClient(emqConfig.getMqttServerUrl(),"monitor."+ UUID.randomUUID());
            mqttClient.setCallback(emqMsgProcess);
            mqttClient.connect();

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    /**
     * 发送消息
     * @param topic 主题
     * @param msg 消息
     */
    public void publish(String topic,String msg){
        MqttMessage mqttMessage=new MqttMessage( msg.getBytes());
        try {
            mqttClient.getTopic(topic).publish( mqttMessage );
        } catch (MqttException e) {
            e.printStackTrace();
            log.error("发送消息异常");
        }
    }


    /**
     * 主题订阅
     * @param topicName
     * @throws MqttException
     */
    public void subscribe(String topicName) throws MqttException {
        mqttClient.subscribe(topicName);
    }


}
