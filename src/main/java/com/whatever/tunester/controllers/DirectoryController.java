package com.whatever.tunester.controllers;

import com.whatever.tunester.constants.Mappings;
import com.whatever.tunester.database.entities.Directory;
import com.whatever.tunester.services.directory.DirectoryService;
import com.whatever.tunester.services.path.PathService;
import com.whatever.tunester.services.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;

import static com.whatever.tunester.util.UriUtils.getPathAfterSubstring;

@RestController
@RequestMapping(value = Mappings.API, produces = "application/json")
public class DirectoryController {

    @Autowired
    private UserService userService;

    @Autowired
    private PathService pathService;

    @Autowired
    private DirectoryService directoryService;

    @GetMapping(Mappings.DIRECTORY + "/**")
    @ResponseStatus(HttpStatus.OK)
    public Directory getDirectory(
        @AuthenticationPrincipal UserDetails userDetails,
        HttpServletRequest request,
        @RequestParam(defaultValue = "0") int rating
    ) {
        String rootPath = userService.getUserRootPath(userDetails.getUsername());
        String directoryRelativePath = getPathAfterSubstring(request, Mappings.DIRECTORY);

        Path directorySystemPath = pathService.getSystemPath(rootPath, directoryRelativePath);

        return directoryService.getDirectory(Path.of(rootPath), directorySystemPath, rating);
    }
}
