package com.whatever.tunester.controllers;

import com.whatever.tunester.constants.Mappings;
import com.whatever.tunester.entities.DirectoryInfo;
import com.whatever.tunester.services.directoryinfo.DirectoryInfoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping(value = Mappings.API, produces = "application/json")
public class MusicController {

    @Autowired
    private DirectoryInfoService directoryInfoService;

    @GetMapping(Mappings.MUSIC + "/**")
    @ResponseStatus(HttpStatus.OK)
    public DirectoryInfo getMusic(HttpServletRequest request, @RequestParam(defaultValue = "0") int rating) {
        String uri = UriUtils.decode(request.getRequestURI(), StandardCharsets.UTF_8);
        String relativePath = uri.substring(uri.indexOf(Mappings.MUSIC) + Mappings.MUSIC.length());

        return directoryInfoService.getDirectoryInfo(relativePath, rating);
    }
}
