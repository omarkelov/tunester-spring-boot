package com.whatever.tunester.services.path;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

@Service
public class PathServiceImpl implements PathService {
    @Override
    public Path getSystemPath(String rootPathName, String relativePath) {
        List<String> pathParts = Arrays.stream(relativePath.split("/")).filter(Predicate.not(String::isBlank)).toList();
        Path systemPath = Path.of(rootPathName).resolve(String.join(File.separator, pathParts)).normalize();

        if (!systemPath.startsWith(rootPathName)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        if (!Files.exists(systemPath)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return systemPath;
    }
}
