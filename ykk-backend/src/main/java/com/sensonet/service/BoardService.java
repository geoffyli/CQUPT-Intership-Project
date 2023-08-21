package com.sensonet.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sensonet.entity.BoardEntity;

public interface BoardService extends IService<BoardEntity>{

    /**
     * 删除看板
     * @param boardId
     * @return
     */
    Boolean disable(Integer boardId);
}
