package com.krebai.cucumbercassandra;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@org.springframework.context.annotation.Configuration
@PropertySource(value = "classpath:system.properties")
@ComponentScan("com.krebai.cucumbercassandra")
public class Configuration {

}
