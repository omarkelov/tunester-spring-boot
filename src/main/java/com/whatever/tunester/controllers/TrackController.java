package com.whatever.tunester.controllers;

import com.whatever.tunester.constants.Mappings;
import com.whatever.tunester.database.entities.TrackMetaCommentCut;
import com.whatever.tunester.services.track.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = Mappings.API, produces = "application/json")
public class TrackController {

    @Autowired
    private TrackService trackService;

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
