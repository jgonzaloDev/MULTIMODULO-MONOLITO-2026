package com.dojo.orders.controllers;


import com.dojo.orders.entities.Order;
import com.dojo.orders.entities.OrderDetail;
import com.dojo.orders.services.OrderService;
import com.dojo.orders.services.OrderServiceImpl;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/order")
@CrossOrigin(origins = "*")
public class OrderController {
    private Logger logger = LoggerFactory.getLogger(OrderController.class);
    private OrderService orderService;
    private Tracer tracer;

    public OrderController(OrderService orderService,Tracer tracer) {
        this.orderService = orderService;
        this.tracer = tracer;
    }

    @GetMapping
    public ResponseEntity<?> listAll() {
        Span span = tracer.spanBuilder("order.listAll")
                .setAttribute("http.method", "GET")
                .setAttribute("http.route", "/order")
                .startSpan();

        try(Scope scope = span.makeCurrent()) {
            return ResponseEntity.ok(orderService.listAllOrders());
        }catch(Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR,"Error al obtener ordenes");
            throw e;
        }finally{
            span.end();
        }
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<?> listOrdersByUsername(@PathVariable String username) {
        Span span = tracer.spanBuilder("order.listOrdersByUsername")
                .setAttribute("http.method", "GET")
                .setAttribute("http.route", "/order/username/{username}")
                .startSpan();

        try(Scope scope = span.makeCurrent()) {
            return ResponseEntity.ok(orderService.getOrdersByUsername(username));
        }catch(Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR,"Error al obtener listado de ordenes por usuario");
            throw e;
        }finally{
            span.end();
        }
    }

    @GetMapping("/customer/{id}")
    public ResponseEntity<?> listOrdersByCustomer( @PathVariable Long id){
        Span span = tracer.spanBuilder("order.listOrdersByCustomer")
                .setAttribute("http.method", "GET")
                .setAttribute("http.route", "/order/username/{id}")
                .startSpan();

        try(Scope scope = span.makeCurrent()) {
            return ResponseEntity.ok(orderService.getOrdersByCustomer(id));
        }catch(Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR,"Error al obtener listado de ordenes por id de usuario");
            throw e;
        }finally{
            span.end();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@PathVariable Long id){
        Span span = tracer.spanBuilder("order.getOrder")
                .setAttribute("http.method", "GET")
                .setAttribute("http.route", "/order/username/{id}")
                .startSpan();

        try(Scope scope = span.makeCurrent()) {
            Optional<Order> optional =orderService.getOrderById(id);
            if(optional.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("Mensaje","Orden no econtrado!"));
            }
            return ResponseEntity.ok(optional.get());
        }catch(Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR,"Error al obtener orden por id");
            throw e;
        }finally{
            span.end();
        }
    }

    @PostMapping("/{customerId}")
    public ResponseEntity<Order> createOrder(@PathVariable Long customerId, @RequestBody List<OrderDetail> details) {
        Span span = tracer.spanBuilder("order.createOrder")
                .setAttribute("http.method", "POST")
                .setAttribute("http.route", "/order/{customerId}")
                .startSpan();

        try(Scope scope = span.makeCurrent()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(orderService.save(customerId, details));
        }catch(Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR,"Error al obtener orden creado para cliente con id: " + customerId);
            throw e;
        }finally{
            span.end();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id ) {
        Span span = tracer.spanBuilder("order.deleteOrder")
                .setAttribute("http.method", "DELETE")
                .setAttribute("http.route", "/order/{id}")
                .startSpan();

        try(Scope scope = span.makeCurrent()) {
            Optional<Order> optionalOrder = orderService.getOrderById(id);
            if(optionalOrder.isPresent()){
                orderService.delete(id);
                return ResponseEntity.ok("Orden eliminado con éxito!");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("Mensaje","Orden no econtrado!"));
        }catch(Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR,"Error orden eliminado con id " + id);
            throw e;
        }finally{
            span.end();
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable Long id,@RequestBody Order order){
        Span span = tracer.spanBuilder("order.updateOrder")
                .setAttribute("http.method", "PATCH")
                .setAttribute("http.route", "/order/{id}")
                .startSpan();

        try(Scope scope = span.makeCurrent()) {
            Order orderUpdated = orderService.updateStatus(id, order.getStatusOrder());
            return ResponseEntity.ok(orderUpdated);
           }catch(Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR,"Error al obtener orden actualizdo con id: " + id);
            throw e;
        }finally{
            span.end();
        }
    }

}
