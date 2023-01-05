package com.vibrent.milestone.integration;

import com.vibrent.milestone.UserMilestoneApplication;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;

@ActiveProfiles({"dev", "test"})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan(basePackages = {"com.vibrent.milestone", "com.vibrent.usermilestone", "com.vibrent.vrp.oidc"})
@AutoConfigureMockMvc
@Import(UserMilestoneApplication.class)
@TestPropertySource(properties = {"spring.datasource.driver-class-name = org.testcontainers.jdbc.ContainerDatabaseDriver"})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public abstract class IntegrationTestBase {

        public static KafkaContainer startKafkaContainer() {
                KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"))
                        .withReuse(true);
                kafka.start();

                System.setProperty("kafka.server", kafka.getBootstrapServers());

                return kafka;
        }
        public static <T> KafkaTemplate<String, T> getKafkaTemplate(KafkaContainer kafkaContainer) {
                Map<String, Object> props = new HashMap<>();
                props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
                props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
                props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

                return new KafkaTemplate<String, T>(new DefaultKafkaProducerFactory<>(props));
        }
}
