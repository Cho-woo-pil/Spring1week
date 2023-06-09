package com.sparta.spring1week.service;

import com.sparta.spring1week.dto.ResponseCodeDto;
import com.sparta.spring1week.dto.BlogRequestDto;
import com.sparta.spring1week.dto.BlogResponseDto;
import com.sparta.spring1week.entity.Blog;
import com.sparta.spring1week.entity.LikeBlog;
import com.sparta.spring1week.entity.User;
import com.sparta.spring1week.entity.UserRoleEnum;
import com.sparta.spring1week.exception.BusinessException;
import com.sparta.spring1week.exception.ErrorCode;
import com.sparta.spring1week.repository.BlogRepository;
import com.sparta.spring1week.repository.LikeBlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogService {
    private final BlogRepository blogRepository;
    private final LikeBlogRepository likeRepository;

    //BlogRensponseDto를 사용하여 password빼고 추출
    @Transactional
    public BlogResponseDto createList(BlogRequestDto requestDto, User user) {

        // username 받아온 값을 추가, user값 추가(user의 주소?)
        Blog blog =  blogRepository.saveAndFlush(new Blog(requestDto, user));

        return new BlogResponseDto(blog);

    }

    public List<BlogResponseDto> getlist(){
        List<Blog> bloglist = blogRepository.findAllByOrderByModifiedAtDesc();
        //List<Comment> commnet = commentRepository.findAll();
        List<BlogResponseDto> blogResponseDto = new ArrayList<>();
        //내림차순된 정보를 가지고와서 responsedto에 넣어줌
        for (Blog blog : bloglist){
            BlogResponseDto a = new BlogResponseDto(blog);
            blogResponseDto.add(a);
        }
        return blogResponseDto;
    }

    public BlogResponseDto getidlist(Long id) {
        Blog blog = checkblog(id);

        return new BlogResponseDto(blog);
    }


    @Transactional
    public BlogResponseDto updateBlog(Long id, BlogRequestDto requestDto, User user) {


            UserRoleEnum userRoleEnum = user.getRole();

            Blog blog = checkblog(id);
            if(user.getUsername().equals(blog.getUsername())) {
                blog.update(requestDto);
                return new BlogResponseDto(blog);
            } else if(userRoleEnum == UserRoleEnum.ADMIN){
                blog.update(requestDto);
                return new BlogResponseDto(blog);
            }
            else{
                throw new BusinessException(ErrorCode.MODIFY_ERROR);
            }

    }

    private Blog checkblog(Long id) {
        return blogRepository.findById(id).orElseThrow(
                () -> new BusinessException(ErrorCode.BLOG_ERROR)
        );
    }


    @Transactional
    public ResponseCodeDto deleteBlog(Long id, User user) {


            Blog blog = checkblog(id);
            UserRoleEnum userRoleEnum = user.getRole();
            if(user.getUsername().equals(blog.getUsername())) {
                blogRepository.deleteById(id);
                return new ResponseCodeDto("게시글 삭제 성공.", 200);
            }
            else if(userRoleEnum == UserRoleEnum.ADMIN){
                blogRepository.deleteById(id);
                return new ResponseCodeDto("관리자가 게시글 삭제 성공.", 200);
            }
            else{
                throw new BusinessException(ErrorCode.MODIFY_ERROR);
            }

        }

    @Transactional
    public BlogResponseDto likeblog(Long id, User user) {

        // username 받아온 값을 추가, user값 추가(user의 주소?)
        Blog blog = blogRepository.findById(id).orElseThrow(
                () -> new BusinessException(ErrorCode.BLOG_ERROR)
        );

        boolean exists = likeRepository.existsByBlogAndUser(blog, user);
        if (exists){
            likeRepository.deleteByBlogAndUser(blog, user);
        }else{
            LikeBlog likeBlog = new LikeBlog();
            likeBlog.setBlog(blog);
            likeBlog.setUser(user);
            likeRepository.save(likeBlog);
        }
        //리스트 불러오는 부분이아니라 해당액션에 따라 blog에서 로직부분에서 해결해보면 어떨까?
        //db 리소스 낭비가 있을것
        List<LikeBlog> count =  likeRepository.findAllByBlog(blog);
        blog.count(count.size());

        return new BlogResponseDto(blog);

    }

    }


