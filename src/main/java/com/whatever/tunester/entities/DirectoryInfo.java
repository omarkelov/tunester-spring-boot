package com.whatever.tunester.entities;

import com.whatever.tunester.database.entities.Track;

import java.util.List;

public record DirectoryInfo(List<String> directories, List<Track> tracks) {}
