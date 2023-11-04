package com.whatever.tunester.services.ffmpeg;

public class FfmpegServiceFactory {
    public static FfmpegService newFfmpegService() {
        return new FfmpegServiceImpl();
    }
}
