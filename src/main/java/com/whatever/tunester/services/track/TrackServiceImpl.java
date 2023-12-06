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
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import java.util.stream.IntStream;

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
    public Track getPreviousRandomTrack(String trackRelativePath, String rootPath, int rating, int seed) {
        // TODO: Use rating
        // TODO: Store absolute path in db for getting only tracks available for user
        // TODO: Implement it in SQL with LEAD/LAG after switch to modern version db

        return getPreviousAndNextTracks(trackRelativePath, rootPath, rating, seed)[0];
    }

    @Override
    public Track getNextRandomTrack(String trackRelativePath, String rootPath, int rating, int seed) {
        // TODO: Use rating
        // TODO: Store absolute path in db for getting only tracks available for user
        // TODO: Implement it in SQL with LEAD/LAG after switch to modern version db

        return getPreviousAndNextTracks(trackRelativePath, rootPath, rating, seed)[1];
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

    private Track[] getPreviousAndNextTracks(String trackRelativePath, String rootPath, int rating, int seed) {
        Random random = new Random(seed);

        List<Track> tracks = trackRepository
            .findAllByOrderByPath()
            .stream()
            .sorted((_1, _2) -> random.nextInt(-1, 2))
            .toList();

        if (tracks.isEmpty()) {
            return new Track[]{null, null};
        }

        OptionalInt trackIdxOptional = IntStream
            .range(0, tracks.size())
            .filter(i -> tracks.get(i).getPath().equals(trackRelativePath))
            .findFirst();

        int trackIdx = trackIdxOptional.isPresent()
            ? trackIdxOptional.getAsInt()
            : new Random(seed).nextInt(0, tracks.size());

        Track previousTrack = trackIdx > 0
            ? tracks.get(trackIdx - 1)
            : tracks.get(tracks.size() - 1);

        Track nextTrack = trackIdx < tracks.size() - 1
            ? tracks.get(trackIdx + 1)
            : tracks.get(0);

        return new Track[]{previousTrack, nextTrack};
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
