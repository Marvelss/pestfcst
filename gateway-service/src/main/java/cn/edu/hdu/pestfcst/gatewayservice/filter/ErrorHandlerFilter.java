package cn.edu.hdu.pestfcst.gatewayservice.filter;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
@Order(-1)
public class ErrorHandlerFilter implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        if (ex instanceof NotFoundException) {
            response.setStatusCode(HttpStatus.NOT_FOUND);
            return response.writeWith(Mono.fromSupplier(() -> {
                DataBufferFactory bufferFactory = response.bufferFactory();
                return bufferFactory.wrap(("{\"code\": 404, \"message\": \"服务未找到\"}").getBytes());
            }));
        } else if (ex instanceof ResponseStatusException) {
            ResponseStatusException responseStatusException = (ResponseStatusException) ex;
            response.setStatusCode(responseStatusException.getStatus());
            return response.writeWith(Mono.fromSupplier(() -> {
                DataBufferFactory bufferFactory = response.bufferFactory();
                return bufferFactory.wrap(("{\"code\": " + response.getStatusCode().value() + 
                    ", \"message\": \"" + ex.getMessage() + "\"}").getBytes());
            }));
        }
        
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.writeWith(Mono.fromSupplier(() -> {
            DataBufferFactory bufferFactory = response.bufferFactory();
            return bufferFactory.wrap(("{\"code\": 500, \"message\": \"服务器内部错误\"}").getBytes());
        }));
    }
} 