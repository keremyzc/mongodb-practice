package com.yazici.mongodb.practice.bind;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by keremyzc on 07/12/2015.
 */
public class ConfigModule extends AbstractModule{
    @Override
    protected void configure() {
        final Properties appProps = new Properties();


        InputStream is = null;
        try {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream("props/app.properties");
            appProps.load(is);
        } catch (IOException e) {
//            log
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                //log
            }
        }


        Names.bindProperties(binder(), appProps);
    }
}
