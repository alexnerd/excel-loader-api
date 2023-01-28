/*
 * Copyright 2023 Aleksey Popov <alexnerd.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.alexnerd.excelloader.controllers;


import com.alexnerd.excelloader.dto.LoadResponseDto;
import com.alexnerd.excelloader.dto.StuffPageableDto;
import com.alexnerd.excelloader.exception.BusinessException;
import com.alexnerd.excelloader.services.StuffMappingService;
import org.apache.tomcat.util.http.fileupload.FileItemFactory;
import org.apache.tomcat.util.http.fileupload.FileItemIterator;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 1800L,
        methods = {RequestMethod.POST, RequestMethod.GET})
public class StuffController {

    @Autowired
    private StuffMappingService stuffMappingService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public LoadResponseDto upload(HttpServletRequest request) {
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        try {
            FileItemIterator itemIterator = upload.getItemIterator(request);
            return stuffMappingService.upload(itemIterator);
        } catch (IOException ex) {
            throw new BusinessException("Error during load file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/stuff")
    public StuffPageableDto getData(@RequestParam("page") int page,
                                    @RequestParam("size") int size,
                                    @RequestParam(value = "sort", required = false) String sortColumn,
                                    @RequestParam(value = "like", required = false) String like) {
        return stuffMappingService.getStuff(page, size, sortColumn, like);
    }
}
