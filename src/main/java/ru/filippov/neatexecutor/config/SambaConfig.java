package ru.filippov.neatexecutor.config;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.filippov.neatexecutor.samba.SambaWorker;

import java.io.IOException;

@Configuration
@Data
@Log4j2
public class SambaConfig {
    @Value("${samba.host}")
    private String URL;
    @Value("${samba.username}")
    private String USER_NAME;
    @Value("${samba.password}")
    private String PASSWORD;
    @Value("${samba.shared_directory}")
    private String SHARED_DIRECTORY;

    @Bean
    public SambaWorker getSambaWorker() throws IOException {
        return new SambaWorker(URL, SHARED_DIRECTORY, USER_NAME, PASSWORD);
    }

}
