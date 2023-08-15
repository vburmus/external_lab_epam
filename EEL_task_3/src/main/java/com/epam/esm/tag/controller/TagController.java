package com.epam.esm.tag.controller;


import com.epam.esm.tag.model.TagDTO;
import com.epam.esm.tag.service.TagService;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.epam.esm.utils.Constants.DEFAULT_PAGE;
import static com.epam.esm.utils.Constants.DEFAULT_SIZE;

@RestController
@RequestMapping(value = "/tag", produces = MediaType.APPLICATION_JSON_VALUE)
@Profile("default")
public class TagController {
    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping
    public ResponseEntity<TagDTO> createTag(@RequestBody TagDTO tagDTO) {
        return new ResponseEntity<>(tagService.createTag(tagDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<TagDTO>> showAllTags(@RequestParam(required = false, defaultValue = DEFAULT_PAGE) Integer page,
                                                    @RequestParam(required = false, defaultValue = DEFAULT_SIZE) Integer size) {
        return new ResponseEntity<>(tagService.getAllTags(--page, size), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagDTO> getById(@PathVariable("id") long id) {
        return new ResponseEntity<>(tagService.getTagById(id), HttpStatus.OK);
    }

    @GetMapping("/search/most-used")
    public ResponseEntity<TagDTO> getMostUsed() {
        return new ResponseEntity<>(tagService.getMostUsedTag(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) {
        tagService.deleteTag(id);
        return ResponseEntity.ok().build();
    }
}