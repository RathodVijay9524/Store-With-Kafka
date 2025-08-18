package in.vijay.bank.client.config;

import in.vijay.bank.client.service.IdGeneratorClient;
import in.vijay.bank.client.service.UserHttpClient;
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
    public IdGeneratorClient idGeneratorClientHttpClient(RestClient.Builder builder) {
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(
                        builder.baseUrl("http://localhost:8096//api/ids").build()
                ))
                .build()
                .createClient(IdGeneratorClient.class);
    }
}
