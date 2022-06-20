package com.ch.epaper.service;

import com.ch.epaper.model.Paper;

import java.util.List;

/**
 * @author songwannian
 */
public interface PaperService {

    List<Paper> listPapers();
    Paper selectPaperByCaseId(String caseId);
    int insert(Paper paper);
    int updatePaperByCaseId(Paper paper);
    int deletePaper(String caseId);

}
