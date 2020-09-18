package com.filemanager.fm;

import com.filemanager.fm.config.CustomMultipartResolver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.MultipartResolver;

import java.io.IOException;

//注意取消自动Multipart配置，否则可能在上传接口中拿不到file的值
@SpringBootApplication
@EnableAutoConfiguration(exclude = { MultipartAutoConfiguration.class })
public class FmApplication extends SpringBootServletInitializer{

    //注入自定义的文件上传处理类
    @Bean(name = "multipartResolver")
    public MultipartResolver multipartResolver() {
        CustomMultipartResolver customMultipartResolver = new CustomMultipartResolver();
        return customMultipartResolver;
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(FmApplication.class);
    }

    public static void main(String[] args) throws IOException {
        SpringApplication.run(FmApplication.class, args);
        //System.setProperty("tomcat.util.http.parser.HttpParser.requestTargetAllow","=|{}:\\.%");
    }

}
