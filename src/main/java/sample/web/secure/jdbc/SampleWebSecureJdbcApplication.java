/*
 * Copyright 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sample.web.secure.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.context.SecurityContextHolder;
import sample.web.secure.jdbc.Utils;

@SpringBootApplication
@Controller
public class SampleWebSecureJdbcApplication extends WebMvcConfigurerAdapter {

    static private Utils util = new Utils();

    /*
    @RequestMapping(value = "/folder/access", method = RequestMethod.POST)
    public String enterFolder(@RequestParam("folder")String folder) {
        return "home";
    }
    */



    @RequestMapping(value = "/folder/create", method = RequestMethod.POST)
    public String createFolder(@RequestParam("folder")String folder) {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String currentDir = "/";
        String path = "hdfs://localhost:9000/user/" + user.getUsername() + currentDir + folder ;
        util.mkdir(path);
        return "home";
    }

    @RequestMapping(value = "/file/upload", method = RequestMethod.POST)
    public String uploadFile(@RequestParam("file")MultipartFile uploadFile) {
        if (uploadFile.isEmpty()){
            //return new ResponseEntity("Please select a file!", HttpStatus.OK);
            return "error";
        }
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Path path = new Path("/user/" + user.getUsername() + "/" + uploadFile.getOriginalFilename());
        try {
            org.apache.hadoop.conf.Configuration config = new org.apache.hadoop.conf.Configuration();
            config.addResource(new Path("/usr/lib/hadoop/etc/hadoop/core-site.xml"));
            config.addResource(new Path("/usr/lib/hadoop/etc/hadoop/hdfs-site.xml"));
            config.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
            config.set("fs.file.impl",org.apache.hadoop.fs.LocalFileSystem.class.getName());

            FileSystem fileSystem = FileSystem.get(config);
            if (fileSystem.exists(path)) {
                //return new ResponseEntity("File already exists!", HttpStatus.OK);
                return "error";
            }
            InputStream is = uploadFile.getInputStream();
            OutputStream os = fileSystem.create(path);
            org.apache.commons.io.IOUtils.copy(is, os);
            is.close();
            os.close();
            fileSystem.close();
        } catch (IOException e) {
            //return new ResponseEntity("Failed to upload!", HttpStatus.OK);
            return "error";
        }
        //return new ResponseEntity("Successfully uploaded - " + uploadFile.getOriginalFilename(), new HttpHeaders(), HttpStatus.OK);
        return "home";
    }



    @RequestMapping(value = "/file/access", method = RequestMethod.GET)
    public void download(@RequestParam(value="file", required = false) String file, HttpServletResponse response) {
        try {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Transfer-Encoding", "binary");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + util.getFile(file, response.getOutputStream()) + "\"");
            response.flushBuffer();
        } catch (IOException ex){
            throw new RuntimeException("IOError writing file to output stream.");
        }
    }


    @GetMapping("/")
    public String root(Map<String, Object> model) {
        return home("/", model);
    }

    @GetMapping("/disk")
    public String disk(Map<String, Object> model) { return home("/", model); }

	@RequestMapping(value="/disk/access", method=RequestMethod.GET)
    public String home(@RequestParam(value="path", required = false) String path, Map<String, Object> model) {

        if (path == null) {
            path = "";
        }
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.put("username", user.getUsername());
        FileStatus[] fss = util.listFiles("/user/" +user.getUsername() + "/" + path);
        model.put("files", fss);
        return "home";
    }

    /*
    @RequestMapping("/offload", method=RequestMethod.POST)
    public String startOffload(@RequestParam("url")String url) {
        //OffloadScheduler.
        return "home";
    }
    */

	@RequestMapping("/foo")
	public String foo() {
		throw new RuntimeException("Expected exception in controller");
	}
    @Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/login").setViewName("login");
	}

	public static void main(String[] args) throws Exception {
		new SpringApplicationBuilder(SampleWebSecureJdbcApplication.class).run(args);
	}

	@Configuration
	@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
	protected static class ApplicationSecurity extends WebSecurityConfigurerAdapter {

        @Bean
        public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
            DefaultHttpFirewall firewall = new DefaultHttpFirewall();
            firewall.setAllowUrlEncodedSlash(true);
            return firewall;
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            web.httpFirewall(allowUrlEncodedSlashHttpFirewall());
        }

		@Autowired
		private DataSource dataSource;

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.authorizeRequests().antMatchers("/css/**").permitAll().anyRequest()
					.fullyAuthenticated().and().formLogin().loginPage("/login")
					.failureUrl("/login?error").permitAll().and().logout().permitAll();
		}

		@Override
		public void configure(AuthenticationManagerBuilder auth) throws Exception {
			auth.jdbcAuthentication().dataSource(this.dataSource);
		}

	}

}
