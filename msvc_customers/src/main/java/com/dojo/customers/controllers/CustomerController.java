package com.dojo.customers.controllers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dojo.customers.entities.Customer;
import com.dojo.customers.services.CustomerService;

@RestController
@RequestMapping("/customer")
public class CustomerController {
	private Logger logger = LoggerFactory.getLogger(CustomerController.class);
	private CustomerService service;
	private Tracer tracer;

	public CustomerController(CustomerService service,Tracer tracer) {
		this.service = service;
		this.tracer = tracer;
	}

	@RequestMapping(method =  RequestMethod.HEAD,path = "/{id}")
	public ResponseEntity<Void> existsById(@PathVariable("id") Long id) {
		if(service.exists(id)) {
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.notFound().build();
	}

	@GetMapping
	public ResponseEntity<List<Customer>> findAll() {
//		logger.info("Clientes : "+service.findAll().toString());
		Span span = tracer.spanBuilder("customer.findAll")
				.setAttribute("http.method", "GET")
				.setAttribute("http.route", "/customer")
				.startSpan();

		try(Scope scope = span.makeCurrent()) {
			List<Customer> customers =service.findAll();
			return ResponseEntity.ok(customers);
		}catch(Exception e) {
			span.recordException(e);
			span.setStatus(StatusCode.ERROR,"Error al obtener clientes");
			throw e;
		}finally{
			span.end();
		}
	}

	@GetMapping("search")
	public ResponseEntity<List<Customer>> findByLikeUsername(@RequestParam String username) {
		Span span = tracer.spanBuilder("customer.findByLikeUsername")
				.setAttribute("http.method", "GET")
				.setAttribute("http.route", "/customer/search")
				.startSpan();
		try(Scope scope = span.makeCurrent()) {
			return ResponseEntity.ok(service.findByLikeUsername(username));
		}catch (Exception e) {
			span.recordException(e);
			span.setStatus(StatusCode.ERROR,"Error cliente por username");
			throw e;
		}finally{
			span.end();
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> findById(@PathVariable Long id) {
		Span span = tracer.spanBuilder("customer.findById")
				.setAttribute("http.method", "GET")
				.setAttribute("http.route", "/customer/{id}")
				.startSpan();

		try(Scope scope = span.makeCurrent()) {
			Optional<Customer> optionalCustomer =service.findById(id);
			if(optionalCustomer.isPresent()) {
				logger.info("Cliente : "+optionalCustomer.get());
				return ResponseEntity.ok(optionalCustomer.get());
			}
			logger.warn("Cliente con id: "+id+" no encontrado!");
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Collections.singletonMap("Cliente","No encontrado"));
		}catch (Exception e) {
			span.recordException(e);
			span.setStatus(StatusCode.ERROR,"Error al obtener cliente por id");
			throw e;
		}finally{
			span.end();
		}
	}


	@GetMapping("/by-user/{username}")
	public ResponseEntity<?> findByUsername(@PathVariable String username) {
		Span span = tracer.spanBuilder("customer.findByUsername")
				.setAttribute("http.method", "GET")
				.setAttribute("http.route", "/customer/by-user/{username}")
				.startSpan();

		try(Scope scope = span.makeCurrent()) {
			Optional<Customer> optional=service.findByUsername(username);
			if(optional.isEmpty()) {
				return new ResponseEntity<>(Collections.singletonMap("Cliente","No encontrado"), HttpStatus.NOT_FOUND);
			}
			return ResponseEntity.ok(optional.get());
		}catch (Exception e) {
			span.recordException(e);
			span.setStatus(StatusCode.ERROR,"Error al obtener cliente por username");
			throw e;
		}finally{
			span.end();
		}
	}

	@PostMapping
	public ResponseEntity<Customer> save(@RequestBody Customer customer) {
		Span span = tracer.spanBuilder("customer.save")
				.setAttribute("http.method", "POST")
				.setAttribute("http.route", "/customer")
				.startSpan();
		try(Scope scope = span.makeCurrent()) {
			return ResponseEntity.ok(service.save(customer));
		}catch (Exception e) {
			span.recordException(e);
			span.setStatus(StatusCode.ERROR,"Error al obtener cliente nuevo ");
			throw e;
		}finally{
			span.end();
		}
	}

}
