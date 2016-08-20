/*
 * Copyright 2016 John D. Ament
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ws.ament.hammock.web.tomcat;

import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.io.IOUtils;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.environment.servlet.Listener;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

public class TomcatWebServerTest {
    @Test
    public void shouldBootWebServer() throws Exception {
        try(WeldContainer weldContainer = new Weld().disableDiscovery()
                .beanClasses(DefaultServlet.class, MessageProvider.class)
                .initialize()) {
            String baseDir = "target/webapp-runner";
            Tomcat tomcat = new Tomcat();
            int port = 8080;
            tomcat.setPort(port);
            File base = new File(baseDir);
            if (!base.exists())
            {
                base.mkdirs();
            }
            tomcat.setBaseDir(baseDir);
            Context ctx = tomcat.addContext("/",base.getAbsolutePath());
            StandardContext standardContext = (StandardContext)ctx;
            standardContext.addApplicationListener(Listener.class.getName());

            Wrapper wrapper = Tomcat.addServlet(ctx,"RequestServlet",DefaultServlet.class.getName());
            wrapper.addMapping("/*");
            tomcat.start();
            try(InputStream stream = new URL("http://localhost:8080/").openStream()) {
                String data = IOUtils.toString(stream).trim();
                assertThat(data).isEqualTo(MessageProvider.DATA);
            }

            try(InputStream stream = new URL("http://localhost:8080/").openStream()) {
                String data = IOUtils.toString(stream).trim();
                assertThat(data).isEqualTo(MessageProvider.DATA);
            }
        }
    }
}
