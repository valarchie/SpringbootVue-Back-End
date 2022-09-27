package com.agileboot.domain.system.post;

import com.agileboot.common.core.dto.PageDTO;
import com.agileboot.common.exception.ApiException;
import com.agileboot.common.exception.error.ErrorCode;
import com.agileboot.domain.common.BulkDeleteCommand;
import com.agileboot.infrastructure.web.domain.login.LoginUser;
import com.agileboot.infrastructure.web.util.AuthenticationUtils;
import com.agileboot.orm.entity.SysPostEntity;
import com.agileboot.orm.service.ISysPostService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author valarchie
 */
@Service
public class PostDomainService {

    @Autowired
    private ISysPostService postService;

    public PageDTO getPostList(PostQuery query) {
        Page<SysPostEntity> page = postService.page(query.toPage(), query.toQueryWrapper());
        List<PostDTO> records = page.getRecords().stream().map(PostDTO::new).collect(Collectors.toList());
        return new PageDTO(records, page.getTotal());
    }

    public PostDTO getPost(Long postId) {
        SysPostEntity byId = postService.getById(postId);
        return new PostDTO(byId);
    }

    public void addPost(AddPostCommand addCommand) {
        PostModel postModel = addCommand.toModel();

        // check这种全局唯一性的判断 不适合放在 model领域类当中， 所以放在db service中  比较合适
        if (postService.checkPostNameUnique(null, postModel.getPostName())) {
            throw new ApiException(ErrorCode.Business.POST_NAME_IS_NOT_UNIQUE, postModel.getPostName());
        }

        if (postService.checkPostCodeUnique(null, postModel.getPostCode())) {
            throw new ApiException(ErrorCode.Business.POST_CODE_IS_NOT_UNIQUE, postModel.getPostCode());
        }

        LoginUser loginUser = AuthenticationUtils.getLoginUser();

        postModel.setCreatorId(loginUser.getUserId());
        postModel.setCreateName(loginUser.getUsername());

        postModel.insert();
    }

    public void updatePost(UpdatePostCommand updateCommand) {
        PostModel postModel = updateCommand.toModel();

        // check这种全局唯一性的判断 不适合放在 model领域类当中， 所以放在db service中  比较合适
        if (postService.checkPostNameUnique(postModel.getPostId(), postModel.getPostName())) {
            throw new ApiException(ErrorCode.Business.POST_NAME_IS_NOT_UNIQUE, postModel.getPostName());
        }

        if (postService.checkPostCodeUnique(postModel.getPostId(), postModel.getPostCode())) {
            throw new ApiException(ErrorCode.Business.POST_CODE_IS_NOT_UNIQUE, postModel.getPostCode());
        }

        LoginUser loginUser = AuthenticationUtils.getLoginUser();

        postModel.setUpdaterId(loginUser.getUserId());
        postModel.setUpdateName(loginUser.getUsername());

        postModel.updateById();
    }


    public void deletePost(BulkDeleteCommand<Long> deleteCommand) {
        postService.removeBatchByIds(deleteCommand.getIds());
    }

}
