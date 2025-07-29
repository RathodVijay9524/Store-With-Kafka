package in.vijay.order_service.client.config;

import in.vijay.order_service.client.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class RestClientConfig {

    @Bean
    public UserHttpClient userHttpClient(RestClient.Builder restClientBuilder){
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClientBuilder.build()))
                .build()
                .createClient(UserHttpClient.class);

    }
    @Bean
    public ProductHttpClient productHttpClient(RestClient.Builder builder) {
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(
                        builder.baseUrl("http://localhost:8087/api/products").build()
                ))
                .build()
                .createClient(ProductHttpClient.class);
    }
    @Bean
    public CartHttpClient cartHttpClient(RestClient.Builder builder) {
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(
                        builder.baseUrl("http://localhost:8086/api/carts").build()
                ))
                .build()
                .createClient(CartHttpClient.class);
    }
    @Bean
    public InventoryHttpClient inventoryHttpClient(RestClient.Builder builder) {
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(
                        builder.baseUrl("http://localhost:8091/api/carts").build()
                ))
                .build()
                .createClient(InventoryHttpClient.class);
    }
    @Bean
    public IdGeneratorClient idGeneratorClientHttpClient(RestClient.Builder builder) {
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(
                        builder.baseUrl("http://localhost:8096//api/ids").build()
                ))
                .build()
                .createClient(IdGeneratorClient.class);
    }
}
