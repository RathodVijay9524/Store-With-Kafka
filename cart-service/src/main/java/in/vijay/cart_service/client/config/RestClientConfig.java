package in.vijay.cart_service.client.config;

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
}
