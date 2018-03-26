package pl.piotrowski;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Beans {
    @Bean
    public char[] password(){
        return new char[]{'1','2','3','4'};
    }
}
