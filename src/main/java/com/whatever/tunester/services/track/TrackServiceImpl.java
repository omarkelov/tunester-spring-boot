package com.whatever.tunester.services.track;

import com.whatever.tunester.database.entities.Track;
import com.whatever.tunester.database.entities.TrackMetaCommentCut;
import com.whatever.tunester.database.repositories.TrackRepository;
import com.whatever.tunester.services.ffmpeg.FfmpegService;
import com.whatever.tunester.services.path.PathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Path;
import java.util.UUID;

import static com.whatever.tunester.util.FileFormatUtils.isAudioFile;
import static com.whatever.tunester.util.TimestampUtils.getLastUpdatedTimestamp;

@Service
public class TrackServiceImpl implements TrackService {

    @Autowired
    private PathService pathService;

    @Autowired
    private FfmpegService ffmpegService;

    @Autowired
    private TrackRepository trackRepository;

    @Override
    public FileSystemResource getTrackResource(String trackRelativePath, String rootPath) {
        Path trackSystemPath = pathService.getSystemPath(rootPath, trackRelativePath);

        validate(trackSystemPath);

        return new FileSystemResource(trackSystemPath);
    }

    @Override
    public void rateTrack(String trackRelativePath, String rootPath, int rating) {
        Path trackSystemPath = pathService.getSystemPath(rootPath, trackRelativePath);

        validate(trackSystemPath);

        ffmpegService.rateTrack(trackSystemPath, rating);
        rescanTrack(trackSystemPath, trackRelativePath);
    }

    @Override
    public void cutTrack(String trackRelativePath, String rootPath, TrackMetaCommentCut trackMetaCommentCut) {
        Path trackSystemPath = pathService.getSystemPath(rootPath, trackRelativePath);

        validate(trackSystemPath);

        ffmpegService.cutTrack(trackSystemPath, trackMetaCommentCut);
        rescanTrack(trackSystemPath, trackRelativePath);
    }

    private void validate(Path path) {
        if (!isAudioFile(path)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    private void rescanTrack(Path systemPath, String relativePath) {
        Track track = trackRepository.findByPath(relativePath);

        track = Track.builder()
            .id(track.getId())
            .path(relativePath)
            .lastUpdated(getLastUpdatedTimestamp(systemPath))
            .trackMeta(ffmpegService.getTrackMeta(systemPath))
            .build();

        trackRepository.save(track);
    }
}
