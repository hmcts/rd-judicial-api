package uk.gov.hmcts.reform.judicialapi.configuration;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.openfeign.support.FeignHttpMessageConverters;
import org.springframework.cloud.openfeign.support.HttpMessageConverterCustomizer;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.http.converter.autoconfigure.ClientHttpMessageConvertersCustomizer;

@Configuration
@Slf4j
public class FeignConfiguration {

    @Bean
    public FeignHttpMessageConverters feignHttpMessageConverters(
        ObjectProvider<ClientHttpMessageConvertersCustomizer> customizers,
        ObjectProvider<HttpMessageConverterCustomizer> cloudCustomizers) {
        return new FeignHttpMessageConverters(customizers, cloudCustomizers);
    }

    @Bean
    @Primary
    public Encoder feignFormEncoder(ObjectProvider<FeignHttpMessageConverters> messageConverters) {
        return new SpringFormEncoder(new SpringEncoder(messageConverters));
    }
}
