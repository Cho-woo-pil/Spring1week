package com.sparta.spring1week.controller;

import com.sparta.spring1week.dto.ResponseCodeDto;
import com.sparta.spring1week.dto.BlogRequestDto;
import com.sparta.spring1week.dto.BlogResponseDto;
import com.sparta.spring1week.security.UserDetailsImpl;
import com.sparta.spring1week.service.BlogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    @PostMapping("/api/post")
    public BlogResponseDto createList(@RequestBody BlogRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){

        return blogService.createList(requestDto, userDetails.getUser());
    }

    //조회부분은 수정할 필요가 없음
    @GetMapping("/api/posts")
    public List<BlogResponseDto> getlist(){
        return blogService.getlist();
    }

    @GetMapping("/api/post/{id}")
    public BlogResponseDto getidList(@PathVariable Long id) {
        return (BlogResponseDto) blogService.getidlist(id);
    }

    @PutMapping("/api/post/{id}")
    public BlogResponseDto updateCourse(@PathVariable Long id, @RequestBody BlogRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return blogService.updateBlog(id, requestDto,userDetails.getUser());
    }

    @DeleteMapping("/api/post/{id}")
    public ResponseCodeDto deleteblog(@PathVariable Long id,  @AuthenticationPrincipal UserDetailsImpl userDetails){
        return blogService.deleteBlog(id, userDetails.getUser());
    }




}
