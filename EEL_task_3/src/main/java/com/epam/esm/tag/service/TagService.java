package com.epam.esm.tag.service;

import com.epam.esm.exceptionhandler.exceptions.rest.NoSuchItemException;
import com.epam.esm.exceptionhandler.exceptions.rest.ObjectAlreadyExistsException;
import com.epam.esm.exceptionhandler.exceptions.rest.ObjectIsInvalidException;
import com.epam.esm.giftcertificate.model.GiftCertificate;
import com.epam.esm.tag.model.Tag;
import com.epam.esm.tag.model.TagDTO;
import com.epam.esm.tag.repository.TagRepository;
import com.epam.esm.utils.datavalidation.ParamsValidation;
import com.epam.esm.utils.mappers.EntityToDtoMapper;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.epam.esm.utils.Constants.*;

@Service
public class TagService {
    private final TagRepository tagRepository;
    private final EntityToDtoMapper entityToDtoMapper;

    public TagService(TagRepository tagRepository, EntityToDtoMapper entityToDtoMapper) {
        this.tagRepository = tagRepository;
        this.entityToDtoMapper = entityToDtoMapper;
    }

    @Transactional
    public TagDTO createTag(TagDTO tagDTO) {
        Tag tag = entityToDtoMapper.toTag(tagDTO);

        if (tagRepository.exists(Example.of(tag)))
            throw new ObjectAlreadyExistsException(TAG_WITH_NAME + tag.getName() + ALREADY_EXISTS);
        if (!ParamsValidation.isTagValid(tag))
            throw new ObjectIsInvalidException(TAG_WITH_NAME + tag.getName() + IS_INVALID);

        Tag tagResult = tagRepository.save(tag);
        return entityToDtoMapper.toTagDTO(tagResult);
    }

    public Page<TagDTO> getAllTags(Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(--page, size);
        Page<Tag> allTags = tagRepository.findAll(pageRequest);
        return ParamsValidation.isListIsNotEmptyOrElseThrowNoSuchItem(allTags).map(entityToDtoMapper::toTagDTO);
    }

    public TagDTO getTagById(long id) {
        Optional<Tag> tag = tagRepository.findById(id);
        if (tag.isEmpty()) throw new NoSuchItemException(TAG_WITH_ID + id + DOESN_T_EXIST);
        return entityToDtoMapper.toTagDTO(tag.get());
    }

    public TagDTO getMostUsedTag() {
        Optional<Tag> tag = tagRepository.getMostUsedTag();
        if (tag.isEmpty())
            throw new NoSuchItemException(THERE_IS_NO_TAGS_IN_THIS_PURCHASE);
        return entityToDtoMapper.toTagDTO(tag.get());
    }

    public boolean deleteTag(long id) {
        if (!tagRepository.existsById(id)) throw new NoSuchItemException(THERE_IS_NO_GC_WITH_ID + id );
        tagRepository.deleteById(id);
        return true;
    }

    @Transactional
    public List<Tag> checkTagsAndSaveIfDontExist(GiftCertificate giftCertificate) {
        List<Tag> tagsFromGc = giftCertificate.getTags();
        if (tagsFromGc != null) {
            List<Tag> tags = new ArrayList<>();
            for (Tag tag : tagsFromGc) {
                tags.add(isTagsExistOrElseCreate(tag));
            }
            return tags;
        }
        return Collections.emptyList();
    }

    @Transactional
    public Tag isTagsExistOrElseCreate(Tag tag) {
        if (tagRepository.exists(Example.of(tag))) {
            Optional<Tag> existingTag = tagRepository.findOne(Example.of(tag));
            existingTag.ifPresent(value -> tag.setId(value.getId()));
        } else tagRepository.save(tag);
        return tag;

    }

    public boolean existsByName(String tagName) {
        return tagRepository.existsByName(tagName);
    }
}