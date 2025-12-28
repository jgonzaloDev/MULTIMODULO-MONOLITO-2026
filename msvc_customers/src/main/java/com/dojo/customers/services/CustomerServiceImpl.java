package com.dojo.customers.services;

import java.util.List;
import java.util.Optional;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.springframework.stereotype.Service;

import com.dojo.customers.entities.Customer;
import com.dojo.customers.repository.CustomerRepository;

@Service
public class CustomerServiceImpl implements CustomerService{
	private CustomerRepository repository;
	private Tracer tracer;
	public CustomerServiceImpl(CustomerRepository repository,Tracer tracer) {
		this.repository = repository;
		this.tracer = tracer;
	}

	@Override
	public boolean exists(Long id) {
		return repository.existsById(id);
	}

	@Override
	public List<Customer> findAll() {
		Span serviceSpan = tracer.spanBuilder("customer.service.findAll").startSpan();

		try (Scope serviceScope = serviceSpan.makeCurrent()) {
			Span repoSpan = tracer.spanBuilder("customer.repository.findAll").startSpan();

			try (Scope repoScope = repoSpan.makeCurrent()) {
				return repository.findAll();
			} catch (Exception e) {
				repoSpan.recordException(e);
				repoSpan.setStatus(StatusCode.ERROR);
				throw e;
			} finally {
				repoSpan.end();
			}

		} finally {
			serviceSpan.end();
		}
	}

	@Override
	public List<Customer> findByLikeUsername(String username) {
		Span serviceSpan = tracer.spanBuilder("customer.service.findByLikeUsername").startSpan();

		try (Scope serviceScope = serviceSpan.makeCurrent()) {
			Span repoSpan = tracer.spanBuilder("customer.repository.findByLikeUsername").startSpan();

			try (Scope repoScope = repoSpan.makeCurrent()) {
				return repository.findByLikeUsername(username);
			} catch (Exception e) {
				repoSpan.recordException(e);
				repoSpan.setStatus(StatusCode.ERROR);
				throw e;
			} finally {
				repoSpan.end();
			}
		} finally {
			serviceSpan.end();
		}
	}

	@Override
	public Optional<Customer> findById(Long id) {
		Span serviceSpan = tracer.spanBuilder("customer.service.findById").startSpan();

		try (Scope serviceScope = serviceSpan.makeCurrent()) {
			Span repoSpan = tracer.spanBuilder("customer.repository.findById").startSpan();

			try (Scope repoScope = repoSpan.makeCurrent()) {
				return repository.findById(id);
			} catch (Exception e) {
				repoSpan.recordException(e);
				repoSpan.setStatus(StatusCode.ERROR);
				throw e;
			} finally {
				repoSpan.end();
			}
		} finally {
			serviceSpan.end();
		}
	}

	@Override
	public Optional<Customer> findByUsername(String username) {
		Span serviceSpan = tracer.spanBuilder("customer.service.findByUsername").startSpan();

		try (Scope serviceScope = serviceSpan.makeCurrent()) {
			Span repoSpan = tracer.spanBuilder("customer.repository.findByUsername").startSpan();

			try (Scope repoScope = repoSpan.makeCurrent()) {
				return repository.findByUsername(username);
			} catch (Exception e) {
				repoSpan.recordException(e);
				repoSpan.setStatus(StatusCode.ERROR);
				throw e;
			} finally {
				repoSpan.end();
			}
		} finally {
			serviceSpan.end();
		}
	}

	@Override
	public Customer save(Customer customer) {
		Span serviceSpan = tracer.spanBuilder("customer.service.save").startSpan();

		try (Scope serviceScope = serviceSpan.makeCurrent()) {
			Span repoSpan = tracer.spanBuilder("customer.repository.save").startSpan();

			try (Scope repoScope = repoSpan.makeCurrent()) {
				return repository.save(customer);
			} catch (Exception e) {
				repoSpan.recordException(e);
				repoSpan.setStatus(StatusCode.ERROR);
				throw e;
			} finally {
				repoSpan.end();
			}
		} finally {
			serviceSpan.end();
		}
	}

}
