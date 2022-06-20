package com.ch.epaper.service.Impl;

import com.ch.epaper.mapper.PaperMapper;
import com.ch.epaper.model.Paper;
import com.ch.epaper.service.PaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaperServiceImpl implements PaperService {
    @Autowired
    private PaperMapper paperMapper;

    @Override
    public List<Paper> listPapers(){
        return paperMapper.listAll();
    }
    @Override
    public Paper selectPaperByCaseId(String caseId){
        return paperMapper.selectPaperByCaseId(caseId);
    }
    @Override
    public int insert(Paper paper){
        return paperMapper.insert(paper);
    }
    @Override
    public int updatePaperByCaseId(Paper paper){
        return paperMapper.updatePaperByCaseId(paper);
    }
    @Override
    public int deletePaper(String caseId){
        return paperMapper.deletePaper(caseId);
    }


}
