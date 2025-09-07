package org.withor.agent;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

public class AgentLogger {
    private static final LoggerContext CONTEXT;

    static {
        CONTEXT = new LoggerContext("AgentContext");
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
        builder.setStatusLevel(Level.WARN);
        builder.setConfigurationName("AgentConfig");

        AppenderComponentBuilder console = builder.newAppender("Console", "CONSOLE")
                .addAttribute("target", "SYSTEM_OUT");

        console.add(builder.newLayout("PatternLayout")
                .addAttribute("pattern", "%style{%d{HH:mm:ss.SSS}}{cyan} %highlight{%-5level} %style{[%t]}{magenta} %style{%c{1.}}{yellow}: %msg%n%throwable")
                .addAttribute("disableAnsi", false)
        );

        builder.add(console);
        builder.add(builder.newRootLogger(Level.INFO).add(builder.newAppenderRef("Console")));
        CONTEXT.start(builder.build());
    }

    public static Logger getLogger(Class<?> clazz) {
        return CONTEXT.getLogger(clazz.getName());
    }
}