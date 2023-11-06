package com.whatever.tunester.services.ffmpeg;

import com.whatever.tunester.database.entities.TrackMetaCommentCut;

import java.util.StringJoiner;

public class Util {

    /*
    * Example: "-af \"afade=type=in:st=40:d=10,afade=type=out:st=50:d=10\""
    */
    public static String getFading(TrackMetaCommentCut trackMetaCommentCut) {
        StringJoiner fadeJoiner = new StringJoiner(",", "-af \"", "\"");

        if (trackMetaCommentCut.getFadeInStart() != null) {
            fadeJoiner.add(String.format(
                "afade=type=in:st=%s:d=%s",
                trackMetaCommentCut.getFadeInStart(),
                trackMetaCommentCut.getFadeInDuration()
            ));
        }

        if (trackMetaCommentCut.getFadeOutStart() != null) {
            fadeJoiner.add(String.format(
                "afade=type=out:st=%s:d=%s",
                trackMetaCommentCut.getFadeOutStart(),
                trackMetaCommentCut.getFadeOutDuration()
            ));
        }

        return trackMetaCommentCut.getFadeInStart() != null || trackMetaCommentCut.getFadeOutStart() != null
            ? fadeJoiner.toString()
            : "";
    }
}
