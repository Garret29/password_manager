package pl.piotrowski;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.piotrowski.util.SimplePasswordProperty;

@Configuration
public class Beans {
    @Bean
    public SimplePasswordProperty loginPassword(){
        return new SimplePasswordProperty(new char[]{'1', '2', '3', '4'});
    }
}
