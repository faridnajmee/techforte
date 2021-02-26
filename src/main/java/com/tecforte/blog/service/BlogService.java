package com.tecforte.blog.service;

import com.tecforte.blog.domain.Blog;
import com.tecforte.blog.repository.BlogRepository;
import com.tecforte.blog.service.dto.BlogDTO;
import com.tecforte.blog.service.dto.EntryDTO;
import com.tecforte.blog.service.mapper.BlogMapper;
import com.tecforte.blog.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link Blog}.
 */
@Service
@Transactional
public class BlogService {

    private final Logger log = LoggerFactory.getLogger(BlogService.class);

    private final BlogRepository blogRepository;

    private final BlogMapper blogMapper;

    private final EntryService entryService;

    private static final String ENTITY_NAME = "blog";

    public BlogService(BlogRepository blogRepository, BlogMapper blogMapper, EntryService entryService) {
        this.blogRepository = blogRepository;
        this.blogMapper = blogMapper;
        this.entryService = entryService;
    }

    /**
     * Save a blog.
     *
     * @param blogDTO the entity to save.
     * @return the persisted entity.
     */
    public BlogDTO save(BlogDTO blogDTO) {
        log.debug("Request to save Blog : {}", blogDTO);
        Blog blog = blogMapper.toEntity(blogDTO);
        blog = blogRepository.save(blog);
        return blogMapper.toDto(blog);
    }

    /**
     * Get all the blogs.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<BlogDTO> findAll() {
        log.debug("Request to get all Blogs");
        return blogRepository.findAll().stream()
            .map(blogMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one blog by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BlogDTO> findOne(Long id) {
        log.debug("Request to get Blog : {}", id);
        return blogRepository.findById(id)
            .map(blogMapper::toDto);
    }

    /**
     * Delete the blog by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Blog : {}", id);
        blogRepository.deleteById(id);
    }

    public void deleteBlogByKeyword(String keywords) {
        //%2C == ',' ASCII
        String[] keys = keywords.split(",");
        List<BlogDTO> blogList = this.findAll();
        for(BlogDTO blog : blogList){
            List<EntryDTO> entryDtoList = entryService.findAllByBlog(blog.getId());
            for(EntryDTO entryDTO : entryDtoList){
                int matchedCounter = 0;
                for(String key:keys){
                    if(entryDTO.getContent().toLowerCase().contains(key.toLowerCase())){
                        matchedCounter++;
                    }
                }
                //all key need to be matched (use AND condition)
                if(matchedCounter == keys.length){
                    entryService.delete(entryDTO.getId());
                }
            }
        }
    }

    public void deleteSpecificBlogByKeyword(Long id, String keywords) {

        String[] keys = keywords.split(",");
        Optional<BlogDTO> blogDTO = this.findOne(id);
        if (blogDTO.get() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

            List<EntryDTO> entryDtoList = entryService.findAllByBlog(blogDTO.get().getId());
            for(EntryDTO entryDTO : entryDtoList){
                int matchedCounter = 0;
                for(String key:keys){
                    if(entryDTO.getContent().toLowerCase().contains(key.toLowerCase())){
                        matchedCounter++;
                    }
                }
                //all key need to be matched (use AND condition)
                if(matchedCounter == keys.length){
                    entryService.delete(entryDTO.getId());
                }
            }

    }
}
