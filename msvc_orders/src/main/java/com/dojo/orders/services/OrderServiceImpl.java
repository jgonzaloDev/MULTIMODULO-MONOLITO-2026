package com.dojo.orders.services;

import com.dojo.customers.entities.Customer;
import com.dojo.customers.services.CustomerService;
import com.dojo.orders.entities.Order;
import com.dojo.orders.entities.OrderDetail;
import com.dojo.orders.repository.OrderRepository;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {
    Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    private CustomerService customerService;
    private OrderRepository orderRepository;
    private Tracer tracer;

    public OrderServiceImpl(CustomerService customerService, OrderRepository orderRepository,Tracer tracer) {
        this.customerService = customerService;
        this.orderRepository = orderRepository;
        this.tracer = tracer;
    }

    @Override
    public List<Order> listAllOrders() {
        Span serviceSpan = tracer.spanBuilder("order.service.listAllOrders").startSpan();

        try (Scope serviceScope = serviceSpan.makeCurrent()) {
            Span repoSpan = tracer.spanBuilder("order.repository.listAllOrders").startSpan();

            try (Scope repoScope = repoSpan.makeCurrent()) {
                return orderRepository.findAll();
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
    public List<Order> getOrdersByCustomer(Long id) {
        Span serviceSpan = tracer.spanBuilder("order.service.getOrdersByCustomer").startSpan();

        try (Scope serviceScope = serviceSpan.makeCurrent()) {
            Span repoSpan = tracer.spanBuilder("order.repository.getOrdersByCustomer").startSpan();

            try (Scope repoScope = repoSpan.makeCurrent()) {
                return orderRepository.findByCustomerId(id);
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
    public List<Order> getOrdersByUsername(String username) {
        Span serviceSpan = tracer.spanBuilder("order.service.getOrdersByUsername").startSpan();

        try (Scope serviceScope = serviceSpan.makeCurrent()) {
            Span repoSpan = tracer.spanBuilder("order.repository.getOrdersByUsername").startSpan();

            try (Scope repoScope = repoSpan.makeCurrent()) {
                Customer customer = customerService.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("Cliente no encontrado!"));
                return getOrdersByCustomer(customer.getId());
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
    public Optional<Order> getOrderById(Long id) {
        Span serviceSpan = tracer.spanBuilder("order.service.getOrderById").startSpan();

        try (Scope serviceScope = serviceSpan.makeCurrent()) {
            Span repoSpan = tracer.spanBuilder("order.repository.getOrderById").startSpan();

            try (Scope repoScope = repoSpan.makeCurrent()) {
                Optional<Order> optionalOrder = orderRepository.findById(id);
                return optionalOrder;
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
    public Order save(Long customerId, List<OrderDetail> details) {
        Span serviceSpan = tracer.spanBuilder("order.service.save").startSpan();

        try (Scope serviceScope = serviceSpan.makeCurrent()) {
            Span repoSpan = tracer.spanBuilder("order.repository.save").startSpan();

            try (Scope repoScope = repoSpan.makeCurrent()) {
                Customer acustomer = customerService.findById(customerId).orElse(null);
                Order order = new Order();
                order.setCustomerId(acustomer.getId());
                order.setCreateAt(LocalDate.now());
                order.setStatusOrder("Pendiente");
                details.forEach(detail -> detail.setOrder(order));
                order.setDetails(details);
                return orderRepository.save(order);

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
    public Order updateStatus(Long orderId, String status) {
        Span serviceSpan = tracer.spanBuilder("order.service.updateStatus").startSpan();

        try (Scope serviceScope = serviceSpan.makeCurrent()) {
            Span repoSpan = tracer.spanBuilder("order.repository.updateStatus").startSpan();

            try (Scope repoScope = repoSpan.makeCurrent()) {
                Order order = orderRepository.findById(orderId)
                        .orElseThrow(() -> new RuntimeException(String.format("Orden con id %d no existe!", orderId)));
                Optional<Customer> customer = customerService.findById(order.getCustomerId());
                if (customer.isPresent()) {
                    order.setStatusOrder(status);
                    return orderRepository.save(order);
                }
                throw new RuntimeException(String.format("No existe cliente con id %d", order.getCustomerId()));

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
    public void delete(Long id) {
        Span serviceSpan = tracer.spanBuilder("order.service.delete").startSpan();

        try (Scope serviceScope = serviceSpan.makeCurrent()) {
            Span repoSpan = tracer.spanBuilder("order.repository.delete").startSpan();

            try (Scope repoScope = repoSpan.makeCurrent()) {
                Optional<Order> optionalOrder = orderRepository.findById(id);
                orderRepository.deleteById(id);
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
