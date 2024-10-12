package com.ex.jakartalearn.service;

import com.ex.jakartalearn.entity.Tag;
import com.ex.jakartalearn.repository.TagRepository;
import com.ex.jakartalearn.repository.TaskRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

@Stateless
public class TagService {
    @Inject
    private TaskRepository taskRepository;

    @Inject
    private TagRepository tagRepository;

    public Tag createTag(Tag tag) {
        return tagRepository.save(tag);
    }

    public Optional<Tag> getTagById(Long id) {
        return tagRepository.findById(id);
    }

    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    public Tag updateTag(Tag tag) {
        return tagRepository.update(tag);
    }

    public void deleteTag(Long id) {
        tagRepository.deleteById(id);
    }

    public List<Tag> getTagsByTaskId(Long taskId) {
        return tagRepository.getTagsByTaskId(taskId);
    }

    public Tag findOrCreateTag(String tagName) {
        return tagRepository.findByName(tagName)
                .orElseGet(() -> {
                    Tag newTag = new Tag();
                    newTag.setName(tagName);
                    return tagRepository.save(newTag);
                });
    }

}
