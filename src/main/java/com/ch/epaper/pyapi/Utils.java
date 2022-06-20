package com.ch.epaper.pyapi;

import com.alibaba.fastjson.JSONObject;
import java.io.*;

/**
 * @author songwannian
 */
public class Utils {

    public static void main(String[] srg){
        String docxPath = "E:\\docx\\82号黄晓娟非法采伐毁坏国家重点保护植物一审刑事判决书.docx";
        boolean res = docx2Json(docxPath);

        if (res){
            // 解析JSON文件返回JSONObject
            String jsonPath = getJsonPath(docxPath);
            String jsonStr = getStr(jsonPath);
            JSONObject jo = JSONObject.parseObject(jsonStr);

            // 解析labels标签，最终生成size为10的int数组
            String labelsStr = jo.getString("index");
            JSONObject labelsJo = JSONObject.parseObject(labelsStr);
            for (int i = 0; i <= 9; i++) {
                String indexStr = labelsJo.getString("index"+i);
                System.out.println(indexStr);
            }
            System.out.println(jo.getString("defendants").getClass().getName());
        }

//        String a = JsonFormatTool.formatJson(Objects.requireNonNull(getStr("D:\\projects_IDEA\\paperStorage\\ePaper\\src\\main\\webapp\\jsonFile\\曾林锋饶立明非法采伐毁坏国家重点保护植物罪一案刑事一审判决书.json")));
//        System.out.println(a);
    }

    public static boolean docx2Json(String docxPath){
        // 通过docx文件路径，生成Python代码解析过的json文件
        createPathJson(docxPath);
        RunPythonCode.run();  // 运行bat脚本，运行Python代码,生成json文件

        // 检查json文件是否生成
        String jsonPath = getJsonPath(docxPath);
        File file = new File(jsonPath);
        if (file.exists()) {
            return true;
        }else {
            return false;
        }
    }

    public static String getJsonPath(String docxPath){
        // 根据docx文件路径生成json文件路径
        String[] temp = docxPath.split("\\\\");
        String name = temp[temp.length - 1].split("\\.")[0];
        String jsonPath = "D:\\projects_IDEA\\paperStorage\\ePaper\\src\\main\\webapp\\jsonFile\\"+name + ".json";
        return jsonPath;
    }
    public static String getStr(String filePath) {
        /*  把一个json文件中的内容读取成一个String字符串  */
        try {
            File file = new File(filePath);
            if (file.isFile() && file.exists()) {
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), "GBK");
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = bufferedReader.readLine();
                while (lineTxt != null) {
                    return lineTxt;
                }
            }
        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            System.out.println("Cannot find the file specified!");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error reading file content!");
            e.printStackTrace();
        }
        return null;
    }

    private static void readJson(String jsonStr) {
        /* 解析json字符串，并将其存储到MySQL数据库中 */
        JSONObject jo = JSONObject.parseObject(jsonStr);
        // json中的所有信息
        jo.getString("case_ID");
        jo.getString("cause_of_action");
        jo.getString("paper_name");
        jo.getString("court");
        jo.getString("paper_type");
        jo.getString("prosecution");
        jo.getString("indictment_ID");
        jo.getString("time_of_case");
        jo.getString("date");
        // 被告人信息需要单独解析
        String defendantsStr =  jo.getString("defendants");
        // labels需要单独解析
        String labels = jo.getString("index");

    }

    public static boolean createPathJson(String docxPath){
        // 将docxPath路径写入path.json文件
        String oldFile = "D:\\projects_IDEA\\paperStorage\\ePaper\\src\\main\\java\\com\\ch\\epaper\\pyapi\\path.json";
        // 生成文件内容(docxPath)
        String jsonString = "{\"docxPath\":\""+docxPath.replace("\\","\\\\") + "\"}";
        // 生成json格式文件
        try {
            // 保证创建一个新文件
            File file = new File(oldFile);
            // 如果父目录不存在，创建父目录
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            // 如果已存在,删除旧文件
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();

            // 格式化json字符串
            jsonString = JsonFormatTool.formatJson(jsonString);

            // 将格式化后的字符串写入文件
            Writer write = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            write.write(jsonString);
            write.flush();
            write.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

class JsonFormatTool {
    /**
     * 单位缩进字符串。
     */
    private static String SPACE = "   ";

    /**
     * 返回格式化JSON字符串。
     *
     * @param json 未格式化的JSON字符串。
     * @return 格式化的JSON字符串。
     */
    public static String formatJson(String json) {
        StringBuffer result = new StringBuffer();

        int length = json.length();
        int number = 0;
        char key = 0;

        // 遍历输入字符串。
        for (int i = 0; i < length; i++) {
            // 1、获取当前字符。
            key = json.charAt(i);

            // 2、如果当前字符是前方括号、前花括号做如下处理：
            // if ((key == '[') || (key == '{')) {
            if ((key == '{')) {
                // （1）如果前面还有字符，并且字符为“：”，打印：换行和缩进字符字符串。
                if ((i - 1 > 0) && (json.charAt(i - 1) == ':')) {
                    result.append('\n');
                    result.append(indent(number));
                }

                // （2）打印：当前字符。
                result.append(key);

                // （3）前方括号、前花括号，的后面必须换行。打印：换行。
                result.append('\n');

                // （4）每出现一次前方括号、前花括号；缩进次数增加一次。打印：新行缩进。
                number++;
                result.append(indent(number));

                // （5）进行下一次循环。
                continue;
            }

            // 3、如果当前字符是后方括号、后花括号做如下处理：
            // if ((key == ']') || (key == '}')) {
            if ((key == '}')) {
                // （1）后方括号、后花括号，的前面必须换行。打印：换行。
                result.append('\n');

                // （2）每出现一次后方括号、后花括号；缩进次数减少一次。打印：缩进。
                number--;
                result.append(indent(number));

                // （3）打印：当前字符。
                result.append(key);

                // （4）如果当前字符后面还有字符，并且字符不为“，”，打印：换行。
                if (((i + 1) < length) && (json.charAt(i + 1) != ',')) {
                    result.append('\n');
                }

                // （5）继续下一次循环。
                continue;
            }

            // 4、如果当前字符是逗号。逗号后面换行，并缩进，不改变缩进次数。
            if ((key == ',')) {
                result.append(key);
                result.append('\n');
                result.append(indent(number));
                continue;
            }

            // 5、打印：当前字符。
            result.append(key);
        }

        return result.toString();
    }

    /**
     * 返回指定次数的缩进字符串。每一次缩进三个空格，即SPACE。
     *
     * @param number 缩进次数。
     * @return 指定缩进次数的字符串。
     */
    private static String indent(int number) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < number; i++) {
            result.append(SPACE);
        }
        return result.toString();
    }
}
