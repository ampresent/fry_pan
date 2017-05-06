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

package sample.web.secure.jdbc.Controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.commons.io.FilenameUtils;
import org.apache.hadoop.fs.FileStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.context.SecurityContextHolder;
import sample.web.secure.jdbc.Service.Inter.FileUploadSer;
import sample.web.secure.jdbc.Service.Inter.OfflineSer;

@SpringBootApplication
@org.springframework.stereotype.Controller
public class Controller extends WebMvcConfigurerAdapter {

    private FileUploadSer fileUploadSer;
    private OfflineSer offlineSer;

    @Autowired
    public Controller(FileUploadSer fileUploadSer, OfflineSer offlineSer) {
        this.fileUploadSer = fileUploadSer;
        this.offlineSer = offlineSer;
    }

    @RequestMapping(value = "/folder/create", method = RequestMethod.POST)
    public String createFolder(@RequestParam String current, @RequestParam String folder) {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String path = "/user/" + user.getUsername() + current + "/" + folder ;
        fileUploadSer.mkdir(path);
        return "home";
    }

    @RequestMapping(value = "/file/upload", method = RequestMethod.POST)
    public String uploadFile(@RequestParam String current, @RequestParam MultipartFile file) {
        if (file.isEmpty()){
            //return new ResponseEntity("Please select a file!", HttpStatus.OK);
            return "error";
        }
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String path = "/user/" + user.getUsername() + current + "/" + file.getOriginalFilename();
        try {
            fileUploadSer.putFile(path, file.getInputStream());
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
            User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            file = "/user/" + user.getUsername() + "/" + file;
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Transfer-Encoding", "binary");
            String filename = FilenameUtils.getName(file);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            fileUploadSer.getFile(file, response.getOutputStream());
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
        FileStatus[] fss = fileUploadSer.listFiles("/user/" +user.getUsername() + "/" + path);
        model.put("files", fss);
        return "home";
    }

    @RequestMapping(value="/offline/download", method=RequestMethod.POST)
    public String startOffline(@RequestParam String current, @RequestParam String url) {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        offlineSer.append(user.getUsername(), current, url);
        return "home";
    }

    @RequestMapping(value="/offline/pop", method=RequestMethod.POST)
    public String popOffline(@RequestParam long[] ids) {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        offlineSer.pop(user.getUsername(), ids);
        return "offline";
    }

    @RequestMapping(value="/offline/clear", method=RequestMethod.GET)
    public String clearOffline() {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        offlineSer.clear(user.getUsername());
        return "offline";
    }

    @RequestMapping(value="/offline", method=RequestMethod.GET)
    public String offline(Map<String, Object> model) {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.put("tasks", offlineSer.getAll(user.getUsername()));
        return "offline";
    }

	@RequestMapping("/foo")
	public String foo() {
		throw new RuntimeException("Expected exception in controller");
	}
}
