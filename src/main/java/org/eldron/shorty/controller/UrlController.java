package org.eldron.shorty.controller;

import org.eldron.shorty.exception.InvalidUrlException;
import org.eldron.shorty.exception.UrlNotFoundException;
import org.eldron.shorty.service.UrlService;
import org.eldron.shorty.vo.Url;
import org.eldron.shorty.vo.request.ShortenUrlRequest;
import org.eldron.shorty.vo.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shorten")
public class UrlController {
    private final UrlService urlService;

    public UrlController(final UrlService urlService) {
        this.urlService = urlService;
    }

    @GetMapping("/{shortenedUrlId}")
    public ResponseEntity<BaseResponse> getOriginalUrl(@PathVariable("shortenedUrlId") final String shortenedUrlId) {
        try {
            final Url url = urlService.getOriginalUrl(shortenedUrlId);
            return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>("ok", url));
        } catch (final UrlNotFoundException e) {
            final var response = new BaseResponse("not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PostMapping
    public ResponseEntity<BaseResponse> shortenUrl(@RequestBody final ShortenUrlRequest shortenUrlRequest) {
        try {
            final Url shortenedUrl = urlService.shortenUrl(shortenUrlRequest.getUrl());
            final var response = new BaseResponse<>("Created", shortenedUrl);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (final InvalidUrlException e) {
            final var response = new BaseResponse<>("Invalid url");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
