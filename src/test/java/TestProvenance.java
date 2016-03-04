
import com.thinkbiganalytics.activemq.config.ActiveMqConfig;
import com.thinkbiganalytics.nifi.activemq.NifiActiveMqConfig;
import com.thinkbiganalytics.nifi.config.NifiProvenanceConfig;
import com.thinkbiganalytics.nifi.provenance.writer.ProvenanceEventActiveMqWriter;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.nifi.provenance.ProvenanceEventRecord;
import org.apache.nifi.provenance.ProvenanceEventType;
import org.apache.nifi.provenance.StandardProvenanceEventRecord;
import org.apache.nifi.web.api.dto.provenance.ProvenanceEventDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.jms.JMSException;
import javax.jms.Topic;
import java.util.Date;
import java.util.UUID;

/**
 * Created by sr186054 on 2/24/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {NifiActiveMqConfig.class,ActiveMqConfig.class,NifiProvenanceConfig.class,TestConfig.class  })
public class TestProvenance {


@Autowired
private ProvenanceEventActiveMqWriter activeMqWriter;

    @Test
    public void sendSimpleMessage() throws InterruptedException, JMSException {

       Thread runner = new Thread(new TestProducer());
        runner.start();
        while(true) {
            //block
        }

    }

    private class TestProducer implements Runnable {

        int counter = 0;

        @Override
        public void run() {
            while(true){
                counter++;
                ProvenanceEventRecord record = new StandardProvenanceEventRecord.Builder().setEventType(ProvenanceEventType.CONTENT_MODIFIED)
                        .setComponentId(UUID.randomUUID().toString())
                        .setFlowFileUUID(UUID.randomUUID().toString())
                        .setComponentType("PROCESSOR")
                 .setCurrentContentClaim("container","section","identifier",0L,1000L)
                        .build();



                activeMqWriter.writeEvent(record);
                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }



}
