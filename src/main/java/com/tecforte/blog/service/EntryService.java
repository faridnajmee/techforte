package com.tecforte.blog.service;

import com.tecforte.blog.domain.Blog;
import com.tecforte.blog.domain.Entry;
import com.tecforte.blog.repository.BlogRepository;
import com.tecforte.blog.repository.EntryRepository;
import com.tecforte.blog.service.dto.EntryDTO;
import com.tecforte.blog.service.mapper.EntryMapper;
import com.tecforte.blog.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service Implementation for managing {@link Entry}.
 */
@Service
@Transactional
public class EntryService {

    private final Logger log = LoggerFactory.getLogger(EntryService.class);

    private final EntryRepository entryRepository;

    private final EntryMapper entryMapper;

    private final BlogRepository blogRepository;


    private static final String ENTITY_NAME = "entry";

    public EntryService(EntryRepository entryRepository, EntryMapper entryMapper, BlogRepository blogRepository) {
        this.entryRepository = entryRepository;
        this.entryMapper = entryMapper;
        this.blogRepository = blogRepository;
    }

    /**
     * Save a entry.
     *
     * @param entryDTO the entity to save.
     * @return the persisted entity.
     */
    public EntryDTO save(EntryDTO entryDTO) {
        log.debug("Request to save Entry : {}", entryDTO);
        Entry entry = entryMapper.toEntity(entryDTO);
        entry = entryRepository.save(entry);
        return entryMapper.toDto(entry);
    }

    /**
     * Get all the entries.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<EntryDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Entries");
        return entryRepository.findAll(pageable)
            .map(entryMapper::toDto);
    }


    /**
     * Get one entry by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<EntryDTO> findOne(Long id) {
        log.debug("Request to get Entry : {}", id);
        return entryRepository.findById(id)
            .map(entryMapper::toDto);
    }

    /**
     * Delete the entry by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Entry : {}", id);
        entryRepository.deleteById(id);
    }

    public void checkEmoji(EntryDTO entryDTO) {

        Blog blog = blogRepository.getOne(entryDTO.getBlogId());

        switch (entryDTO.getEmoji()){
            case HAHA:
            case LIKE:
            case WOW:
                        if(!blog.isPositive()){
                            throw new BadRequestAlertException("Invalid Emoji", ENTITY_NAME, "invalidEmoji");
                        }
                        break;
            case SAD:
            case ANGRY: if(blog.isPositive()){
                            throw new BadRequestAlertException("Invalid Emoji", ENTITY_NAME, "invalidEmoji");
                        }
                        break;

        }

    }

    public void checkContent(EntryDTO entryDTO) {
        Blog blog = blogRepository.getOne(entryDTO.getBlogId());

        String[] positiveEmotions = {" like "," love "," happy "," haha "," laugh "};
        String[] negativeEmotions = {" angry "," sad "," fear "," cry "," lonely "};

        if(blog.isPositive()){
            for(String s : negativeEmotions){
                if(entryDTO.getContent().toLowerCase().contains(s)){
                    throw new BadRequestAlertException("Invalid Content", ENTITY_NAME, "invalidContent");
                }
            }
        } else{
            for(String s : positiveEmotions){
                if(entryDTO.getContent().toLowerCase().contains(s)){
                    throw new BadRequestAlertException("Invalid Content", ENTITY_NAME, "invalidContent");
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public List<EntryDTO> findAllByBlog(Long blogId) {
        log.debug("Request to get all Entries");
        return entryRepository.findAllByBlogId(blogId).stream()
            .map(entryMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }
}
