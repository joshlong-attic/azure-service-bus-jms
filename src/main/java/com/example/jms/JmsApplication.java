package com.example.jms;

import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.time.Instant;
import java.util.Hashtable;

@Log4j2
@SpringBootApplication
public class JmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(JmsApplication.class, args);
	}

	@Bean
	ApplicationRunner run(JmsTemplate template) {
		return args -> template.send("messages", session -> session.createTextMessage("Hello world @ " + Instant.now()));
	}

	@Bean
	ConnectionFactory connectionFactory(@Value("${azsb-uri}") String connectionString) throws Exception {

		ConnectionStringBuilder csb = new ConnectionStringBuilder(connectionString);
		log.info(csb.getEndpoint().toString());

		Hashtable<String, String> hashtable = new Hashtable<>();
		hashtable.put("connectionfactory.SBCF", "amqps://" + csb.getEndpoint().getHost() + "?amqp.idleTimeout=120000&amqp.traceFrames=true");
		hashtable.put("queue.QUEUE", "BasicQueue");
		hashtable.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.qpid.jms.jndi.JmsInitialContextFactory");
		Context context = new InitialContext(hashtable);
		return (ConnectionFactory) context.lookup("SBCF");
	}
}
