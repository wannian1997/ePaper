import os
import re
import json

from process_doc import read_docx, list2str
from utils import minDistance, ChineseDate2Date
from Paper import Paper
"""抽取裁判文书关键信息并生成json文件"""
# 必要文件：'.\data\anyou.txt'


class PaperInfo(Paper):
    """对裁判文书进行信息抽取"""
    # 状态信息

    # paper_info 表格信息
    case_ID = ''  # 案号
    cause_of_action = ''  # 案由
    paper_name = ''  # 文件名
    court = ''
    paper_type = ''
    prosecution = ''   # 原告，检察院
    indictment_ID = ''  # 起诉书编号
    time_of_case = ''  # 指控段落中的案发时间
    date = ''  # 裁判文书判决日期

    # defendants 表格信息（被告人信息）
    defendants = []

    # judge

    # related_laws
    def __init__(self, docx_path):
        super().__init__(docx_path)  # 调用父类的构造函数
        self.extract_paper_info()

    def extract_paper_info(self):
        # paper_info 表格信息抽取
        self.case_ID = self.paras[self.dict_label['label30'][0]]
        self.cause_of_action = self.cause_of_action_func()
        self.paper_name = self.case_name
        self.court = self.paras[self.dict_label['label10'][0]]
        self.paper_type = self.paras[self.dict_label['label20'][0]]
        self.prosecution = self.prosecution_func()
        self.indictment_ID = self.indictment_ID_func()
        # 此处缺少一项内容的提取time_of_case,原因：可能没用
        self.date = self.date_func()

        # defendants 表格信息（被告人信息）
        self.defendants = self.defendants_func()
        pass

    def cause_of_action_func(self):
        """提取案由"""
        # 处理文件名，去掉路径信息
        dir, file_name = os.path.split(self.case_name)
        self.case_name = file_name.split('.')[0]

        # 通过文本匹配算法进行案由抽取
        anyou = ''
        f01 = open(r'D:\projects_pycharm\LawProcess\Data_Access\data\anyou.txt', "r")  # 设置文件对象，其中txt文件编码类型为ANSI
        all_cause_of_action = f01.readlines()  # 直接将文件中按行读到list里，效果与方法2一样
        f01.close()  # 关闭文件

        # 文本匹配算法（编辑距离）
        all_distance = []
        for ay in all_cause_of_action:
            ay = ay.replace('\n', '')  # 删除回车
            all_distance.append(minDistance(ay, self.case_name))
            if ay[1:len(ay)-1] in self.case_name:
                anyou = ay
        if anyou == '':
            miniDistance = 99999999
            miniDistance_index = -1
            for i in range(len(all_distance)):
                if all_distance[i]<miniDistance:
                    miniDistance_index = i
            anyou = all_cause_of_action[miniDistance_index]
        return anyou.replace('\n', '')

    def prosecution_func(self):
        """提取公诉机关"""
        para = self.paras[self.dict_label['label40'][0]]  # 定位指定段落
        para = para.replace('公诉机关', '')
        para = para.replace('暨附带民事公益诉讼起诉人', '')
        para = para.replace('暨附带公益诉讼起诉人', '')
        para = para.replace('：', '')
        return para

    def indictment_ID_func(self):
        # 获取指控段第一段
        para = str
        for label, paras_index in self.dict_label.items():
            if label[5] == '6':
                para = self.paras[paras_index[0]].split('。')[0]
                break
        pattern = r'以(.*)起诉'
        para = re.search(pattern, para).group(1)
        return para

    def date_func(self):
        """判决日期"""
        para_date = str
        paras_index = self.dict_label['label80']
        for p in paras_index:
            if not re.search(r'.*年.*月.*日', self.paras[p]) is None:
                para_date = self.paras[p]
                break
        return ChineseDate2Date(para_date)

    def defendants_func(self):
        """获取被告人信息:姓名，性别，出生年月，民族，教育背景，籍贯，出生地，居住地，辩护人，辩护人律师事务所
        未抽取：罪名，拘留时间，拘留地点，取保候审时间，取保候审机关"""
        defendants_list = []
        for index in self.dict_label["label50"]:
            defendants_list.append(self.paras[index])
        defendants_info = []  # 被告人抽取信息列表，嵌套字典
        for i in range(len(defendants_list)):
            if not re.search("被告人", defendants_list[i][0:10]):  # 辩护人段，跳过
                continue
            str = defendants_list[i]
            defendant_info = {}  # 单个被告的信息
            # 基本信息，抽取
            name = re.search(r"(?<=告人|单位)([\u4e00-\u9fa5]{1,5})(?=[，。])", str)
            if name is None:
                name = re.search(r"(?<=被告)([\u4e00-\u9fa5]{1,5})(?=[，。])", str)
            gender = re.search(r"([男女])", defendants_list[i])
            birthday = re.search(r"(\d{4}年\d{1,2}月\d{1,2}日)(?=出生)", str)
            nation = re.search(r"([\u4e00-\u9fa5]{1,6}族)(?=[，。])", str)
            education_level = re.search(r"([\u4e00-\u9fa5]{1,10})(?=文化|毕业)|(文盲)", str)
            register_residence = re.search(r"(?<=户籍地|所在地)([\u4e00-\u9fa5A-Za-z0-9]{1,20})(?=[，。])", str)  # 户籍所在地
            birthday_place = re.search(r"(?<=出生地|出生于)([\u4e00-\u9fa5A-Za-z0-9]{1,20})(?=[，。])",str)
            current_residence = re.search(r"(?<=住)([\u4e00-\u9fa5A-Za-z0-9]{1,20})(?=[，。])",str)
            # 存储（顺序存储）
            if name is None:
                defendant_info["name"] = ""
            else:
                defendant_info["name"] = name.group()
            if gender is None:
                defendant_info["gender"] = ""
            else:
                defendant_info["gender"] = gender.group()
            if birthday is None:
                defendant_info["birthday"] = ""
            else:
                defendant_info["birthday"] = birthday.group()
            if nation is None:
                defendant_info["nation"] = ""
            else:
                defendant_info["nation"] = nation.group()
            if education_level is None:
                defendant_info["education_level"] = ""
            else:
                defendant_info["education_level"] = education_level.group()
            if register_residence is None:
                defendant_info["register_residence"] = ""
            else:
                defendant_info["register_residence"] = register_residence.group()
            if birthday_place is None:
                defendant_info["birthday_place"] = ""
            else:
                defendant_info["birthday_place"] = birthday_place.group()
            if current_residence is None:
                defendant_info["current_residence"] = ""
            else:
                defendant_info["current_residence"] = current_residence.group()
            # 辩护人信息 抽取
            if i+1 < len(defendants_list):
                str_next = defendants_list[i + 1]
                name_of_advocate = re.search(r"(?<=辩护人)([\u4e00-\u9fa5]{1,5})(?=[，。])", str_next)
                law_offices = re.search(r"(?<=[，。]).*事务所(?=律师)", str_next)
            else:
                name_of_advocate = None
                law_offices = None
            # 存储
            if name_of_advocate is None:
                defendant_info["name_of_advocate"] = ""
            else:
                defendant_info["name_of_advocate"] = name_of_advocate.group()
            if law_offices is None:
                defendant_info["law_offices"] = ""
            else:
                defendant_info["law_offices"] = law_offices.group()
            defendants_info.append(defendant_info.copy()) # 将单个被告信息加入列表
        return defendants_info


def temp(path):
    # 单文档标注测试
    paper = PaperInfo(path)
    for key, value in paper.dict_label.items():
        print(key, value)
    for p in paper.paras:
        print(p)


def analysisOfLabels(dict_labels: dict, size) -> dict:
    # 分析分段信息

    labelsIndex = {}
    labelsIndex["index0"] = dict_labels["label00"][-1]
    labelsIndex["index1"] = dict_labels["label10"][-1]
    labelsIndex["index2"] = dict_labels["label20"][-1]
    labelsIndex["index3"] = dict_labels["label30"][-1]
    labelsIndex["index4"] = dict_labels["label40"][-1]
    labelsIndex["index5"] = dict_labels["label50"][-1]

    index6 = 0
    for key, value in dict_labels.items():
        if key[5] == "6":
            index6 = max(value[-1], index6)
    labelsIndex["index6"] = index6
    labelsIndex["index7"] = dict_labels["label70"][-1]
    labelsIndex["index8"] = dict_labels["label80"][-1]
    labelsIndex["index9"] = size-1

    return labelsIndex


def analysisOfDefendants(defendants:list)->dict:
    """将列表型的被告人信息转换为字典型"""
    size = len(defendants)
    defendantsDict = {}
    for i in range(1, size+1):
        key = "defendant" + str(i)
        value = defendants[i-1]
        defendantsDict[key] = value
    return defendantsDict

def dict2json(jsonDict, docxPath, jsonFileFolder):
    """将字典类型存储为json文件"""
    try:
        # 生成json文件名
        jsonPath = docxPath.split('\\')[-1]
        jsonPath = jsonPath.split('.')[0] + '.json'
        jsonPath = jsonFileFolder + "\\" + jsonPath
    except:
        print(f'docxPath:{docxPath}\njsonPath{jsonPath}')
        print("jsonPath文件路径解析出错，请检查")
    # 写入文件
    with open(jsonPath, 'w') as file_obj:
        json.dump(jsonDict, file_obj, ensure_ascii=False)
    # print(f"成功生成《{jsonPath}》文件")


def toJson(docxPath, jsonFileFolder = ""):
    """将抽取的信息存储为json文件"""
    # jsonFileFolder 为空时，保存在相同文件夹中
    paper_info = PaperInfo(docxPath)
    jsonDict = {}
    # paper_info 表格信息
    jsonDict["case_ID"] = paper_info.case_ID  # 案号
    jsonDict["cause_of_action"] = paper_info.cause_of_action  # 案由
    jsonDict["paper_name"] = paper_info.paper_name  # 文件名
    jsonDict["court"] = paper_info.court
    jsonDict["paper_type"] = paper_info.paper_type
    jsonDict["prosecution"] = paper_info.prosecution   # 原告，检察院
    jsonDict["indictment_ID"] = paper_info.indictment_ID  # 起诉书编号
    jsonDict["time_of_case"] = paper_info.time_of_case  # 指控段落中的案发时间
    jsonDict["date"] = paper_info.date  # 裁判文书判决日期

    # defendants 解析表格信息（被告人信息）
    # defendants = [{}]
    jsonDict["defendants"] = analysisOfDefendants(paper_info.defendants)  # 被告人信息

    # index 分段信息解析
    labels = analysisOfLabels(paper_info.dict_label,len(paper_info.paras))
    jsonDict["index"] = labels

    # 全文
    paras = read_docx(docxPath)
    fulltext = list2str(paras)
    jsonDict["fulltext"] = fulltext

    # 存文件
    dict2json(jsonDict, docxPath, jsonFileFolder)
    return jsonDict


def toJsonES(docxPath, jsonFileFolder = ""):
    """将抽取的信息存储为为ES使用的数据类型"""
    # jsonFileFolder 为空时，保存在相同文件夹中
    paper_info = PaperInfo(docxPath)
    jsonDict = {}
    # paper_info 表格信息
    jsonDict["case_ID"] = paper_info.case_ID  # 案号
    jsonDict["cause_of_action"] = paper_info.cause_of_action  # 案由
    jsonDict["paper_name"] = paper_info.paper_name  # 文件名
    jsonDict["court"] = paper_info.court
    jsonDict["paper_type"] = paper_info.paper_type
    jsonDict["prosecution"] = paper_info.prosecution   # 原告，检察院
    jsonDict["indictment_ID"] = paper_info.indictment_ID  # 起诉书编号
    jsonDict["date"] = paper_info.date  # 裁判文书判决日期

    # defendants 解析表格信息（被告人信息）
    # defendants = [{}]
    defendants = []
    for defendant in paper_info.defendants:
        defendants.append(defendant["name"])
    jsonDict["defendants"] = defendants  # 被告人信息

    # 全文
    paras = read_docx(docxPath)
    fulltext = list2str(paras)
    jsonDict["fulltext"] = fulltext

    # dict2json(jsonDict, docxPath, jsonFileFolder)
    # for k,v in jsonDict.items():
    #     print(k+':'+str(v))
    return jsonDict


def main():
    docxPath = r'E:\docx\曾林锋饶立明非法采伐毁坏国家重点保护植物罪一案刑事一审判决书.docx'
    jsonFileFolder = r'D:\projects_IDEA\paperStorage\ePaper\src\main\webapp\jsonFile'  # json文件所在文件夹,
    toJson(docxPath, jsonFileFolder)
    # toJsonES(docxPath, jsonFileFolder)


if __name__ == '__main__':
    main()