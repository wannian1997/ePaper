package com.ch.epaper.mapper;
import com.ch.epaper.model.Paper;

import java.util.List;


/**
 * @author songwannian
 */
public interface PaperMapper {
    List<Paper> listAll();
    Paper selectPaperByCaseId(String caseId);  // 案号查询
    int insert(Paper paper);
    int updatePaperByCaseId(Paper paper);
    int deletePaper(String caseId);
}
