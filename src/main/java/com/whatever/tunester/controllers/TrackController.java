package com.whatever.tunester.controllers;

import com.whatever.tunester.constants.Mappings;
import com.whatever.tunester.database.entities.TrackMetaCommentCut;
import com.whatever.tunester.services.track.TrackService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping(value = Mappings.API, produces = "application/json")
public class TrackController {

    @Autowired
    private TrackService trackService;

    @GetMapping(value = Mappings.TRACK + "/**", produces = "audio/mpeg")
    @ResponseBody
    public ResponseEntity<FileSystemResource> getTrackFile(HttpServletRequest request) {
        String uri = UriUtils.decode(request.getRequestURI(), StandardCharsets.UTF_8);
        String trackRelativePath = uri.substring(uri.indexOf(Mappings.TRACK) + Mappings.TRACK.length());

        return ResponseEntity.ok().body(trackService.getTrackResource(trackRelativePath));
    }

    @PatchMapping(Mappings.TRACK + Mappings.RATE)
    @ResponseStatus(HttpStatus.OK)
    public void rateTrack(@RequestParam String trackPath, @RequestParam int rating) {
        trackService.rateTrack(trackPath, rating);
    }

    @PatchMapping(Mappings.TRACK + Mappings.CUT)
    @ResponseStatus(HttpStatus.OK)
    public void cutTrack(@RequestParam String trackPath, @RequestBody TrackMetaCommentCut trackMetaCommentCut) {
        trackService.cutTrack(trackPath, trackMetaCommentCut);
    }
}
