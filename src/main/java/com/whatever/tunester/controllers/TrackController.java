package com.whatever.tunester.controllers;

import com.whatever.tunester.constants.Mappings;
import com.whatever.tunester.database.entities.TrackMetaCommentCut;
import com.whatever.tunester.services.track.TrackService;
import com.whatever.tunester.services.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static com.whatever.tunester.util.UriUtils.getPathAfterSubstring;

@RestController
@RequestMapping(value = Mappings.API, produces = "application/json")
public class TrackController {

    @Autowired
    private UserService userService;

    @Autowired
    private TrackService trackService;

    @GetMapping(value = Mappings.TRACK + "/**", produces = "audio/mpeg")
    @ResponseBody
    public ResponseEntity<FileSystemResource> getTrackFile(
        @AuthenticationPrincipal UserDetails userDetails,
        HttpServletRequest request
    ) {
        String trackRelativePath = getPathAfterSubstring(request, Mappings.TRACK);
        String rootPath = userService.getUserRootPath(userDetails.getUsername());

        return ResponseEntity.ok().body(trackService.getTrackResource(trackRelativePath, rootPath));
    }

    @PatchMapping(Mappings.TRACK + Mappings.RATE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rateTrack(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestParam("trackPath") String trackRelativePath,
        @RequestParam int rating
    ) {
        String rootPath = userService.getUserRootPath(userDetails.getUsername());

        trackService.rateTrack(trackRelativePath, rootPath, rating);
    }

    @PatchMapping(Mappings.TRACK + Mappings.CUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cutTrack(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestParam("trackPath") String trackRelativePath,
        @RequestBody TrackMetaCommentCut trackMetaCommentCut
    ) {
        String rootPath = userService.getUserRootPath(userDetails.getUsername());

        trackService.cutTrack(trackRelativePath, rootPath, trackMetaCommentCut);
    }
}
