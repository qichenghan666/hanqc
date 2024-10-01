package com.itheima.controller;

import com.itheima.pojo.Article;
import com.itheima.pojo.PageBean;
import com.itheima.pojo.Result;
import com.itheima.service.ArticleService;
import com.itheima.utils.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/article")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @PostMapping
    public Result add(@RequestBody @Validated(Article.Add.class) Article article){
        articleService.add(article);
        return Result.success("文章添加成功");
    }
    @GetMapping
    public Result<PageBean<Article>> list(
            Integer pageNum,
            Integer pageSize,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String state){
        PageBean<Article> pageBean= articleService.list(pageNum,pageSize,categoryId,state);
        return  Result.success(pageBean);
    }
    @PutMapping
    public Result update(@RequestBody @Validated(Article.Update.class) Article article){
        articleService.update(article);
        return Result.success("文章修改成功");
    }
    @GetMapping("/detail")
    public Result<Article> detail(Integer id){
        Article article=articleService.findById(id);
        return Result.success(article);
    }
    @DeleteMapping
    public Result delete(Integer id){
        articleService.delete(id);
        return Result.success("文章删除成功");
    }
}
