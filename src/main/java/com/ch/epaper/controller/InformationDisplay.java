package com.ch.epaper.controller;

import com.alibaba.fastjson.JSONObject;
import com.ch.epaper.model.Paper;
import com.ch.epaper.service.PaperService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ch.epaper.pyapi.Utils.getStr;

/**
 * @author songwannian
 */
@Controller
public class InformationDisplay {
    @Autowired
    private PaperService paperService;
    private Log log = LogFactory.getLog(FileUploadController.class);

    @RequestMapping("informationDisplay")
    public String display(Model model, String jsonPath, HttpServletRequest request){
        // jsonPath是json文件的路径，由控制器FileUploadController转发
        String username = (String) request.getSession().getAttribute("userName");
        log.info("用户："+username+"  在操作系统");
        log.info("jsonPath:"+jsonPath);
        // 生成Paper实体
        Paper paper = json2Paper(jsonPath);
        // 存入数据库 检查是否存在→不存在则存-》存在则更新
        Paper findPaper = paperService.selectPaperByCaseId(paper.getCaseId());
        if (findPaper == null){
            int i = paperService.insert(paper);
            log.warn("用户："+username+"  往数据库增加了"+ i +"条数据");
        }else {
            int i = paperService.updatePaperByCaseId(paper);
            log.warn("用户："+username+"  更新了"+ i +"条数据");
        }

        // thymeleaf属性（）
        model.addAttribute("paperName",paper.getPaperName());
        model.addAttribute("caseId",paper.getCaseId());
        model.addAttribute("causeOfAction",paper.getCauseOfAction());
        model.addAttribute("court",paper.getCourt());
        model.addAttribute("paperType",paper.getPaperType());
        model.addAttribute("prosecution",paper.getProsecution());
        model.addAttribute("defendants",paper.getDefendants());
        model.addAttribute("date",paper.getDate());

        // 正文部分
        Map<String, List<String>> map = getDisplayPaper(paper.getTextIndex(),paper.getText());
        model.addAttribute("part1",map.get("part1"));
        model.addAttribute("part2",map.get("part2"));
        model.addAttribute("part3",map.get("part3"));
        model.addAttribute("part4",map.get("part4"));
        model.addAttribute("part5",map.get("part5"));

        return "information";
    }

    public Paper json2Paper(String jsonPath){
        //读取json文件中的信息，返回Paper类
        log.info("读取json文件中信息");
        Paper paper = new Paper();
        // 解析JSON文件返回JSONObject
        JSONObject jo = JSONObject.parseObject(getStr(jsonPath));

        // 将信息传输给paper
        paper.setCaseId(jo.getString("case_ID"));
        paper.setCauseOfAction(jo.getString("cause_of_action"));
        paper.setPaperName(jo.getString("paper_name"));
        paper.setCourt(jo.getString("court"));
        paper.setPaperType(jo.getString("paper_type"));
        paper.setProsecution(jo.getString("prosecution"));
        paper.setIndictmentId(jo.getString("indictment_ID"));
        // 日期格式特殊，为空时不能为其赋值，否则存数据库时会出错
        if (!jo.getString("time_of_case").equals("")){
            paper.setTimeOfCase(jo.getString("time_of_case"));
        }
        if (!jo.getString("date").equals("")){
            paper.setDate(jo.getString("date"));
        }

        // 解析被告人信息，仅保存人名
        String defendantsStr = jo.getString("defendants");
        JSONObject defendantsJo = JSONObject.parseObject(defendantsStr);
        // 取出所有姓名
        StringBuilder names = new StringBuilder();
        for (int i = 1; i <= defendantsJo.size(); i++) {
            String defendantStr = defendantsJo.getString("defendant"+i);
            JSONObject defendantJo = JSONObject.parseObject(defendantStr);
            String name = defendantJo.getString("name");
            names.append(name).append("；");
        }
        paper.setDefendants(names.toString());
        paper.setText(jo.getString("fulltext"));

        // 解析labels标签，最终生成size为10的int数组
        String labelsStr = jo.getString("index");
        JSONObject labelsJo = JSONObject.parseObject(labelsStr);
        int[] index = new int[10];
        for (int i = 0; i <= 9; i++) {
            String indexStr = labelsJo.getString("index"+i);
            index[i] = Integer.parseInt(indexStr);
        }
        StringBuilder textIndex = new StringBuilder("" + index[0]);
        for (int i = 1; i <= 9; i++) {
            textIndex.append(",").append(index[i]);
        }
        paper.setTextIndex(textIndex.toString());
        log.info("json文件解析成功");
        return paper;
    }

    public Map<String, List<String>> getDisplayPaper(String textIndex, String text){
        // 解析索引,字符串转换成数组
        String[] indexArray = textIndex.split(",");
        int[] index = new int[10];
        for (int i = 0; i < 10; i++) {
            index[i]=Integer.parseInt(indexArray[i]);
        }
        // 全文数组
        String[] paras = text.split("\n");
        Map<String, List<String>> map = new HashMap<>();

        // 法院，文书类型：居中
        List<String> part1 = new ArrayList<>();
        for (int i = 0;i<=index[1];i++){
            part1.add(paras[i]);
        }
        map.put("part1",part1);

        // 案号：靠右
        List<String> part2 = new ArrayList<>();
        part2.add(paras[index[2]]);
        map.put("part2",part2);

        // 正文
        List<String> part3 = new ArrayList<>();
        for (int i = index[3]; i < index[7]; i++){
            part3.add(paras[i]);
        }
        map.put("part3",part3);

        // 审判人员和日期
        List<String> part4 = new ArrayList<>();
        for (int i = index[7]; i < index[8]; i++){
            part4.add(paras[i]);
        }
        map.put("part4",part4);

        // 相关法律
        List<String> part5 = new ArrayList<>();
        if (index[9]>index[8]){
            for (int i = index[8]; i < index[9]; i++){
                part5.add(paras[i]);
            }
            map.put("part5",part5);
        }else {
            map.put("part5",part5);
        }

        return map;
    }

    @RequestMapping("changeInformation")
    public String change(Model model,String caseId){
        log.info(caseId);
        // 根据案号从数据库中取出数据
        Paper paper = paperService.selectPaperByCaseId(caseId);
        // thymeleaf属性（）
        model.addAttribute("paperName",paper.getPaperName());
        model.addAttribute("caseId",paper.getCaseId());
        model.addAttribute("causeOfAction",paper.getCauseOfAction());
        model.addAttribute("court",paper.getCourt());
        model.addAttribute("paperType",paper.getPaperType());
        model.addAttribute("prosecution",paper.getProsecution());
        model.addAttribute("defendants",paper.getDefendants());
        model.addAttribute("date",paper.getDate());

        return "changeInformation";
    }

    @RequestMapping("saveChangedInformation")
    public String saveChangedInformation(String caseId,String causeOfAction,String court,
                                         String paperType,String prosecution,String defendants,
                                         String date){
        //从数据库中取出数据
        Paper paper = paperService.selectPaperByCaseId(caseId);
        // 修改
        paper.setCauseOfAction(causeOfAction);
        paper.setCourt(court);
        paper.setPaperType(paperType);
        paper.setProsecution(prosecution);
        paper.setDefendants(defendants);
        paper.setDate(date);
        // 存入数据库
        int i = paperService.updatePaperByCaseId(paper);
        log.warn("通过提交表单方式修改了"+i+"条数据");
        // 重定向到信息显示界面
        log.warn("转发前："+caseId);
        return "forward:displayByCaseId?id="+caseId;
    }

    @RequestMapping("displayByCaseId")
    public String displayByCaseId(Model model,String id){
        //从数据库中取出数据
        Paper paper = paperService.selectPaperByCaseId(id);
        log.warn("转发后："+id);
        if (paper == null){
            log.warn("未从数据库中读取到信息");
        }else {
            // thymeleaf属性（）
            model.addAttribute("paperName",paper.getPaperName());
            model.addAttribute("caseId",paper.getCaseId());
            model.addAttribute("causeOfAction",paper.getCauseOfAction());
            model.addAttribute("court",paper.getCourt());
            model.addAttribute("paperType",paper.getPaperType());
            model.addAttribute("prosecution",paper.getProsecution());
            model.addAttribute("defendants",paper.getDefendants());
            model.addAttribute("date",paper.getDate());

            // 正文部分
            Map<String, List<String>> map = getDisplayPaper(paper.getTextIndex(),paper.getText());
            model.addAttribute("part1",map.get("part1"));
            model.addAttribute("part2",map.get("part2"));
            model.addAttribute("part3",map.get("part3"));
            model.addAttribute("part4",map.get("part4"));
            model.addAttribute("part5",map.get("part5"));
        }

        return "information";
    }

    @RequestMapping("searchCase")
    public String searchCase(Model model, String searchText){
        Paper paper = paperService.selectPaperByCaseId(searchText);

        if (paper == null){
            return "searchPage";
        }else {
            // thymeleaf
            model.addAttribute("paperName",paper.getPaperName());
            model.addAttribute("court",paper.getCourt());
            model.addAttribute("caseId",paper.getCaseId());
            model.addAttribute("date",paper.getDate());
            model.addAttribute("text",paper.getText().substring(20,250)); // 随意抽取一段
        }
        return "searchPage";
    }
}
