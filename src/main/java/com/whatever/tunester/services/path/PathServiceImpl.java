package com.whatever.tunester.services.path;

import com.whatever.tunester.constants.Mappings;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

@Service
public class PathServiceImpl implements PathService {
    @Override
    public Path getSystemPath(String rootPathName, String requestURI) {
        String uri = UriUtils.decode(requestURI, StandardCharsets.UTF_8);
        String cutUri = uri.substring(uri.indexOf(Mappings.MUSIC) + Mappings.MUSIC.length());
        List<String> pathParts = Arrays.stream(cutUri.split("/")).filter(Predicate.not(String::isBlank)).toList();
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
