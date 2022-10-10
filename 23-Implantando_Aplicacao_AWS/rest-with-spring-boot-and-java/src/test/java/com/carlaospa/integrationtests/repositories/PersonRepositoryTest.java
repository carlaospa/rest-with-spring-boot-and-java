package com.carlaospa.integrationtests.repositories;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.carlaospa.integrationtestcontainers.AbstractIntegrationTest;
import com.carlaospa.model.Person;
import com.carlaospa.repository.PersonRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(OrderAnnotation.class)
public class PersonRepositoryTest  extends AbstractIntegrationTest{
	
	@Autowired
	public PersonRepository repository;
	
	private static Person person;
	
	@BeforeAll
	public static void setup() {
		person = new Person();
	}
	
	@Test
	@Order(0)
	public void testFindByName() throws JsonMappingException, JsonProcessingException {
				
		Pageable pageable = PageRequest.of(0, 6, Sort.by(Direction.ASC, "firstName"));
		
	    person = repository.findPersonsByName("Al", pageable).getContent().get(0);
		
		assertNotNull(person.getId());
		assertNotNull(person.getFirstName());
		assertNotNull(person.getLastName());
		assertNotNull(person.getAddress());
		assertNotNull(person.getGender());
		
		assertTrue(person.getEnabled());
		
		assertEquals(585, person.getId());
		
		assertEquals("Alan", person.getFirstName());
		assertEquals("Hampshire", person.getLastName());
		assertEquals("2677 Hauk Circle", person.getAddress());
		assertEquals("Male", person.getGender());
	}	
	
	
	@Test
	@Order(1)
	public void testDisablePerson() throws JsonMappingException, JsonProcessingException {
		
		repository.disabledPerson(person.getId());		
		
		Pageable pageable = PageRequest.of(0, 6, Sort.by(Direction.ASC, "firstName"));
		
		person = repository.findPersonsByName("Al", pageable).getContent().get(0);
		
		assertNotNull(person.getId());
		assertNotNull(person.getFirstName());
		assertNotNull(person.getLastName());
		assertNotNull(person.getAddress());
		assertNotNull(person.getGender());
		
		assertFalse(person.getEnabled());
		
		assertEquals(585, person.getId());
		
		assertEquals("Alan", person.getFirstName());
		assertEquals("Hampshire", person.getLastName());
		assertEquals("2677 Hauk Circle", person.getAddress());
		assertEquals("Male", person.getGender());
	}	
	

}
