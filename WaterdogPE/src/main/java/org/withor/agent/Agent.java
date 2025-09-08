package org.withor.agent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.withor.mixins.MixinTransformer;
import org.withor.voidline.communicator.mixins.EntityMapMixin;
import org.withor.voidline.communicator.mixins.ProtocolCodecsMixin;

public class Agent {
    private static final Logger logger = AgentLogger.getLogger(Agent.class);
    @Getter private static Map<String, Object> config;

    @SneakyThrows
    public static void premain(String agentArgs, Instrumentation inst) {
        agentmain(agentArgs, inst);
    }

    @SneakyThrows
    public static void agentmain(String agentArgs, Instrumentation inst) {
        File dataFolder = new File(System.getProperty("user.dir"), "plugins\\Voidline communicator");
        File configFile = new File(dataFolder, "config.json");
        dataFolder.mkdirs();

        if (!configFile.exists()) {
            config = new LinkedHashMap<>();
            config.put("block", new ArrayList<>());

            try (FileWriter writer = new FileWriter(configFile)) {
                new Gson().toJson(config, writer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Type type = new TypeToken<LinkedHashMap<String, Object>>() {}.getType();
            try (FileReader reader = new FileReader(configFile)) {
                config = new Gson().fromJson(reader, type);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Set<Class<?>> mixins = Set.of(
                EntityMapMixin.class,
                ProtocolCodecsMixin.class
        );

        try {
            MixinTransformer transformer = new MixinTransformer(mixins);
            inst.addTransformer(transformer, true);
            transformer.loaded(inst);
            logger.info("Inited!");
            logger.warn("TO MAKE MODULE BLOCKING WORK PROPERLY SET \"unblockAllModulesOnJoin\" TO FALSE ON ALL BACKEND SERVERS!!!!!");
        } catch (Exception e) {
            logger.error("Error while applying mixins: ", e);
            throw new RuntimeException(e);
        }
    }
}