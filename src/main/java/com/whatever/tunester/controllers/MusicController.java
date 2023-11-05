package com.whatever.tunester.controllers;

import com.whatever.tunester.constants.Mappings;
import com.whatever.tunester.entities.DirectoryInfo;
import com.whatever.tunester.services.directoryinfo.DirectoryInfoService;
import com.whatever.tunester.services.track.TrackService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = Mappings.API, produces = "application/json")
public class MusicController {

    @Autowired
    private DirectoryInfoService directoryInfoService;

    @Autowired
    private TrackService trackService;

    @GetMapping(Mappings.MUSIC + "/**")
    @ResponseStatus(HttpStatus.OK)
    public DirectoryInfo getMusic(HttpServletRequest request, @RequestParam(defaultValue = "0") int rating) {
        return directoryInfoService.getDirectoryInfo(request.getRequestURI(), rating);
    }

    @PatchMapping(Mappings.MUSIC + "/**")
    @ResponseStatus(HttpStatus.OK)
    public void rateTrack(HttpServletRequest request, @RequestParam int rating) {
        trackService.rateTrack(request.getRequestURI(), rating);
    }
}
