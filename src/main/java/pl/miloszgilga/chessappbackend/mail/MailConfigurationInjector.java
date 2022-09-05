/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: MailConfigurationInjector.java
 * Last modified: 05/09/2022, 15:24
 * Project name: chess-app-backend
 *
 * Licensed under the MIT license; you may not use this file except in compliance with the License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * THE ABOVE COPYRIGHT NOTICE AND THIS PERMISSION NOTICE SHALL BE INCLUDED IN ALL
 * COPIES OR SUBSTANTIAL PORTIONS OF THE SOFTWARE.
 */

package pl.miloszgilga.chessappbackend.mail;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

//----------------------------------------------------------------------------------------------------------------------

@Configuration
public class MailConfigurationInjector {

    private static final String TEMPLATES_PATH = "classpath:/templates";

    @Primary
    @Bean
    public FreeMarkerConfigurationFactoryBean factoryBean() {
        final var bean = new FreeMarkerConfigurationFactoryBean();
        bean.setTemplateLoaderPath(TEMPLATES_PATH);
        return bean;
    }
}