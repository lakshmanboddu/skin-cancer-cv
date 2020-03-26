package org.finalProj;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


@Controller
//public class WebController implements WebMvcConfigurer {
public class WebController {
    //https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/servlet/config/annotation/WebMvcConfigurerAdapter.html

//    @Override
//    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/predict").setViewName("results");
//    }

    public static String fileBasePath = System.getProperty("user.dir")+"/src/data/upload";
    @RequestMapping("/")
    public String showForm(Model model) {
        return "index";
    }

    @PostMapping("/predict")
    public String predictLesion(Model model, @RequestParam("files") MultipartFile image) {

        Path path = Paths.get(fileBasePath, image.getOriginalFilename());
        String fileName= image.getOriginalFilename();

        try {
            Files.write( path, image.getBytes());
            String prediction = ModelPredict.predictLabel(path.toString());
//            OutputHtml.htmlOutput(prediction);
            model.addAttribute("msg", prediction);
            return "results";
        }
        catch (Exception e){
            e.printStackTrace();
            return "error";
        }

    }
}
