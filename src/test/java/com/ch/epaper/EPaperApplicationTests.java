package com.ch.epaper;

import com.ch.epaper.mapper.PaperMapper;
import com.ch.epaper.model.Paper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class EPaperApplicationTests {

    @Autowired
    PaperMapper paperMapper;

    @Test
    void contextLoads() {

//        Paper p2 = paperMapper.selectPaperByCaseId("重庆2号");
//        System.out.println(p2.getId());
//        System.out.println(p2.getDate());
        List<Paper> list = paperMapper.listAll();
        for (Paper paper : list) {
            System.out.println(paper.getPaperName());
        }

        Paper p = new Paper();
        p.setCaseId("重庆2号");
        p.setCauseOfAction("案由");
        p.setPaperName("文件名");
        p.setCourt("Court");
        p.setPaperType("setPaperType");
        p.setProsecution("setProsecution");
        p.setIndictmentId("id2");
        p.setTimeOfCase("2020-1-2");
        p.setDate("2012-2-3");
        p.setDefendants("defendants");
        p.setText("全文修改");
        p.setTextIndex("0,1,2,3,4,5,6,7,8,9");


        int j = paperMapper.deletePaper("重庆2号");
        System.out.println("删除了"+j+"条数据");

        int i = paperMapper.insert(p);
        System.out.println("增加了"+i+"条数据");
        p.setPaperType("修改测试");
        int k = paperMapper.updatePaperByCaseId(p);
        System.out.println("修改了"+k+"条数据");


    }

}
