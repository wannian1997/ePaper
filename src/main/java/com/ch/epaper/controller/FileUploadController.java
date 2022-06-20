package com.ch.epaper.controller;

import com.alibaba.fastjson.JSONObject;
import com.ch.epaper.pyapi.Utils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static com.ch.epaper.pyapi.Utils.getJsonPath;
import static com.ch.epaper.pyapi.Utils.getStr;

/**
 * @author songwannian
 */
@Controller
public class FileUploadController {
    private Log log = LogFactory.getLog(FileUploadController.class);
    /**
     * 进入文件选择页面
     */
    @RequestMapping("/uploadFile")
    public String uploadFile() {
        return "uploadFile";
    }
    /**
     * 上传文件自动绑定到MultipartFile对象中，
     * 在这里使用处理方法的形参接收请求参数。
     */
    // @RequestMapping("/upload")  返回映射
    @RequestMapping("/upload")
    public String upload(
            HttpServletRequest request,
            @RequestParam("description") String description,
            @RequestParam("myfile") MultipartFile myfile)
            throws IllegalStateException, IOException {
        log.info("文件描述："+description);
        //如果选择了上传文件，将文件上传到指定的目录uploadFiles
        boolean res = false;
        if(!myfile.isEmpty()) {
            String fileName = myfile.getOriginalFilename();
            if (check(fileName)){
                log.info("正确的文件类型");
            }else {
                log.info("错误的文件类型");
                return "blankPage";
            }
            //上传文件路径
            //获得上传文件原名
            //设置文件上传保存文件路径：保存在项目运行目录下的uploadFile文件夹+当前日期
            String savepath = request.getServletContext().getRealPath("/uploadFiles/");
            // 本地文件夹地址
            String realLocalFolder = "D:\\projects_IDEA\\paperStorage\\ePaper\\src\\main\\webapp\\uploadFiles\\";
            //创建文件夹,当文件夹不存在时，创建文件夹
            File folder = new File(savepath);
            //文件保存操作
            try {
                myfile.transferTo(new File(folder,fileName));
                //建立新文件路径,在前端可以直接访问如http://localhost:8080/uploadFile/文件名(带后缀)
                String filepath=request.getScheme()+"://"+request.getServerName()+":"+
                        request.getServerPort()+"/uploadFile/"+"/"+fileName;
                //生成json文件
                res = Utils.docx2Json(realLocalFolder + fileName);
                log.info("生成json文件");
            }catch (IOException ex){
                //操作失败报错
                ex.printStackTrace();
            }

            // 修改path.json
        }else {
            log.info("未上传文件");
            return "blankPage";
        }

        if (res){
            log.info("json文件存在，转发：informationDisplay。");
            // 解析JSON文件返回JSONObject
            String jsonPath = getJsonPath(myfile.getOriginalFilename());
            String jsonStr = getStr(jsonPath);
            JSONObject jo = JSONObject.parseObject(jsonStr);
            log.info("解析成功");
            System.out.println( jo.getString("date"));
            String message = "抽取的信息\n";
            message+= jo.getString("case_ID")+"\n";
            message+= jo.getString("cause_of_action")+"\n";
            // 转发
            return "forward:informationDisplay?jsonPath=" + jsonPath;
        }else {
            return "noPage";
        }
    }

    public Boolean check(String filename){
        // 检查文件类型
        if (filename == null) {
            return false;
        }
        String[] temp = filename.split("\\.");
        String ans = temp[temp.length-1];
        Set<String> types = new HashSet<>();  // 指定文件类型
        types.add("docx");
        if (types.contains(ans)){
            return true;
        }else {
            return false;
        }
    }

}
