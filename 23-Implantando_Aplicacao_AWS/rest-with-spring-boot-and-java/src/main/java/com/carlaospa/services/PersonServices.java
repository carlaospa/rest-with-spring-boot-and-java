package com.carlaospa.services;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import com.carlaospa.controllers.PersonController;
import com.carlaospa.data.vo.v1.PersonVO;
import com.carlaospa.exception.RequiredObjectIsNullException;
import com.carlaospa.exception.ResourceNotFoundException;
import com.carlaospa.mapper.DozerMapper;
import com.carlaospa.model.Person;
import com.carlaospa.repository.PersonRepository;

import jakarta.transaction.Transactional;

@Service
public class PersonServices {

	private Logger logger = Logger.getLogger(PersonServices.class.getName());

	@Autowired
	PersonRepository repository;
	
	@Autowired
	PagedResourcesAssembler<PersonVO> assembler;

	public PagedModel<EntityModel<PersonVO>> findAll(Pageable pageable) {

		logger.info("Finding all people!");
		
		var personPage = repository.findAll(pageable);
		
		var personVosPage = personPage.map(p -> DozerMapper.parseObject(p, PersonVO.class));
		
		personVosPage.map(
				p -> {
					try {
						return p.add(
								linkTo(methodOn(PersonController.class).findById(p.getKey())).withSelfRel());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return p;
				});
		
		Link link = linkTo(methodOn(PersonController.class).findAll(pageable.getPageNumber(), 
				pageable.getPageSize(), "asc")).withSelfRel();
		
		return assembler.toModel(personVosPage, link);
	}
	
	public PagedModel<EntityModel<PersonVO>> findPersonByName(String firstname, Pageable pageable) {
		
		logger.info("Finding all people name!");
		
		var personPage = repository.findPersonsByName(firstname, pageable);
		
		var personVosPage = personPage.map(p -> DozerMapper.parseObject(p, PersonVO.class));
		
		personVosPage.map(
				p -> {
					try {
						return p.add(
								linkTo(methodOn(PersonController.class).findById(p.getKey())).withSelfRel());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return p;
				});
		
		Link link = linkTo(methodOn(PersonController.class).findAll(pageable.getPageNumber(), 
				pageable.getPageSize(), "asc")).withSelfRel();
		
		return assembler.toModel(personVosPage, link);
	}

	public PersonVO findById(Long id) {

		logger.info("Finding one person!");

		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		var vo = DozerMapper.parseObject(entity, PersonVO.class);
		try {
			vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	public PersonVO create(PersonVO person) {

		if (person == null) throw new RequiredObjectIsNullException(); 
		
		logger.info("Creating one person!");
		
		var entity = DozerMapper.parseObject(person, Person.class);		
		var vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
		try {
			vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	public PersonVO update(PersonVO person) {
		
		if (person == null) throw new RequiredObjectIsNullException(); 

		logger.info("Updating one person!");

		var entity = repository.findById(person.getKey())
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

		entity.setFirstName(person.getFirstName());
		entity.setLastName(person.getLastName());
		entity.setAddress(person.getAddress());
		entity.setGender(person.getGender());

		var vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
		try {
			vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	@Transactional
	public PersonVO disablePerson(Long id) {

		logger.info("Disabling one person!");
		
		repository.disabledPerson(id);

		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		var vo = DozerMapper.parseObject(entity, PersonVO.class);
		try {
			vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}
	
	public void delete(Long id) {

		logger.info("Deleting one person!");

		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

		repository.delete(entity);
	}

}
