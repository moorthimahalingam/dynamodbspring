package org.moorthi.springboot.dynamodbspring.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.ParseException;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.exceptions.DynamoDBLocalServiceException;
import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;

/**
 * Created by MRomeh
 */
@Configuration
public class ApplicationConfig {
    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }
    
    @Bean
    AmazonDynamoDB getAmazonDynamoDB() {
    	
    	final String[] localArgs = {"-inMemory" , "-port", "9000"};
    	 System.setProperty("sqlite4java.library.path", "native-libs");
		DynamoDBProxyServer proxyServer = null;
		AmazonDynamoDB amazonDynamoDB = null;
		try {
			proxyServer = ServerRunner.createServerFromCommandLineArgs(localArgs);
			proxyServer.start();
			
			amazonDynamoDB = AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration(
					new AwsClientBuilder.EndpointConfiguration("http://localhost:9000", "eu-west-1")).build();

			createTable(amazonDynamoDB);
			
			for (String tableName : amazonDynamoDB.listTables().getTableNames()) {
				System.out.println("Table Name " + tableName);
			}
			System.out.println("TableName will be about to delete");
			amazonDynamoDB.deleteTable("Employee");
			System.out.println("Server is about to stop ");
			amazonDynamoDB.shutdown();
			proxyServer.stop();
			System.out.println("Server is stopped successfully ");
		} catch (DynamoDBLocalServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			System.out.println("Finally block has been executed ");
			try {
				if (proxyServer != null) {
					proxyServer.stop();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("End of the execution ");
		return amazonDynamoDB;
	}
	
	
	private static void createTable(AmazonDynamoDB amazonDynamoDB) {
		List<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
		AttributeDefinition attributeDefinition = new AttributeDefinition("EmployeeID", ScalarAttributeType.S);
		attributeDefinitions.add(attributeDefinition);
		List<KeySchemaElement> schemaElements = new ArrayList<KeySchemaElement>();
		KeySchemaElement keySchemaElement = new KeySchemaElement("EmployeeID", KeyType.HASH);
		schemaElements.add(keySchemaElement);
		ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput(50L, 50L);
		CreateTableRequest createTableRequest = new CreateTableRequest(
				attributeDefinitions, "Employee", schemaElements, provisionedThroughput);
		amazonDynamoDB.createTable(createTableRequest);
	}
}
