# coding:utf-8
"""批量处理doc文件，能批量输出docx文件"""
import os
import win32com.client as wc
import time
import docx

# 创建子文件夹
def create_folder(path_temp):
    isExists = os.path.exists(path_temp)
    if not isExists:
        os.makedirs(path_temp)


# 实现单个doc文件转换为docx文件
def doc2docx_1(doc_path):
    # 打开word并设置
    word = wc.Dispatch("Word.Application")
    word.Visible = 0  # 1程序可见，0不可见
    print(doc_path)
    doc = word.Documents.Open(doc_path)
    docx_save_path = doc_path + 'x'
    doc.SaveAs(docx_save_path, 12, False, "", True, "", False, False, False, False)  # 转换后的文件,12代表转换后为docx文件
    doc.Close()
    word.Quit()


# 批量将doc转换为docx
def doc2docx_p(folder_path_temp):   # 输入doc文件所在文件夹路径
    doc_name_list = os.listdir(folder_path_temp)  # doc文件名列表(包含其他格式文件）
    docx_path_list = []
    # 提示信息
    print('doc文件存储文件夹地址：' + folder_path_temp)
    print('docx文件存储文件夹地址：' + folder_path_temp + '\\docx')
    # 检查docx文件夹是否存在，不存在则创建
    create_folder(folder_path_temp + '\\docx')
    # 打开word程序并设置
    word = wc.Dispatch("Word.Application")
    word.Visible = 1  # 1程序可见，0不可见
    for doc_name in doc_name_list:
        if not os.path.splitext(doc_name)[1] == ".doc":  # 筛选文件类型,注意”.“
            continue
        docx_path_temp = os.path.join(folder_path_temp, 'docx', (doc_name + 'x'))
        if os.path.exists(docx_path_temp):  # 判断docx文件是否已经存在
            continue
        doc_path_temp = os.path.join(folder_path_temp, doc_name)
        doc = word.Documents.Open(doc_path_temp)
        docx_save_path = os.path.join(folder_path_temp, "docx", doc_name + 'x')
        print(docx_save_path)   # 用于检测出错的文件
        doc.SaveAs(docx_save_path, 12, False, "", True, "", False, False, False, False)  # 转换后的文件,12代表转换后为docx文件
        docx_path_list.append(doc_name + 'x')
        doc.Close()
        print(docx_save_path + "  -------->>>>转换成功。")
    word.Quit()


# docx文件批量转txt文件,传入docx文件夹路径,并批量转换为txt并存在doc文件夹下
def docx2txt(doc_path):
    docx_name_list = os.listdir(doc_path)
    for dn in docx_name_list:
        if not os.path.splitext(dn)[1] == ".doc":  # 筛选文件类型,注意”.“
            continue
        wordapp = wc.Dispatch('Word.Application')
        path1= os.path.join(doc_path, dn)
        doc = wordapp.Documents.Open(path1)
        output = os.path.splitext(dn)[0]
        output = os.path.join(doc_path, output)
        doc.SaveAs(output, 4)  # 为了让python可以在后续操作中r方式读取txt和不产生乱码，参数为4
        doc.Close()


# 读取docx文档,并将每段存入一个列表
def read_docx(path):
    """2021年7月29日，兼容txt格式"""
    if not os.path.exists(path):  # 判断文件是否存在
        print('发生错误：\n'+path+'文件不存在！')
        exit(0)  # 无错误结束程序
    # 解析文件路径
    remove_flag = False
    dir, file_name = os.path.split(path)

    if os.path.splitext(file_name)[1] == ".txt":  # 直接读取
        with open(path, "r") as f:  # 打开文件
            data = f.readlines()  # 读取文件
        paras_temp = []  # 用来存储段落
        for p in data:
            p = p.replace(" ", "")
            p1 = p.replace("\u3000\u3000", "")  # 去除空格,并以字符串的形式存储在列表中
            if len(p1) > 1:  # 去除空行（有的空行为一个空格）
                paras_temp.append(p1)
        return paras_temp

    if os.path.splitext(file_name)[1] == ".doc":  # 需要先转换
        doc2docx_1(path)  # 生成同文件目录下docx文件
        path = path + 'x'  # 更新路径
        remove_flag = True

    doc = docx.Document(path)
    paras_temp = []  # 用来存储段落
    for p in doc.paragraphs:
        p1 = p.text.replace(" ", "")  # 去除空格,并以字符串的形式存储在列表中
        if len(p1) > 1:   # 去除空行（有的空行为一个空格）
            paras_temp.append(p1)
    if remove_flag:
        os.remove(path)  # 将docx文件删除
    return paras_temp


# 判断文件是否属于人民法院认为不宜在互联网公布的其他情形、表格（待补充）,并返回列表
def useless_list(docx_folder_path):  # 输入docx文件所在文件夹路径
    print("请先批量转换为docx再使用，doc格式文件批量运行速度慢！")
    docx_name_list = os.listdir(docx_folder_path)  # docx文件名列表(包含其他格式文件）
    u_list = []  # 文件名列表
    for docx_name in docx_name_list:
        if not os.path.splitext(docx_name)[1] == ".docx":  # 筛选文件类型,注意”.“
            if not os.path.splitext(docx_name)[1] == ".doc":
                continue
        docx_path_temp = os.path.join(docx_folder_path, docx_name)
        print("正在读取："+docx_path_temp)
        paras = read_docx(docx_path_temp)
        # 将表格形式的文件包括在内
        if len(paras) <= 4:
            u_list.append(docx_name)
            print(docx_name)
    return u_list


# 查找乱码文件并返回列表
def messy_code_list(docx_folder_path):
    docx_name_list = os.listdir(docx_folder_path)  # docx文件名列表(包含其他格式文件）
    u_list = []  # 文件名列表
    for docx_name in docx_name_list:
        if not os.path.splitext(docx_name)[1] == ".doc":  # 筛选文件类型,注意”.“
            continue
        docx_path_temp = os.path.join(docx_folder_path, docx_name)
        print("正在读取：" + docx_path_temp)
        paras = read_docx(docx_path_temp)
        judge_day_index = 0
        for p in paras:
            if '年' and '月' and '日' in p:
                if len(p) <= 12:
                    judge_day_index = paras.index(p)
                    print(p)
        if judge_day_index == 0:
            u_list.append(docx_name)
    return u_list


# 批量删除folder_path路径下的无用文件,传入无用文件名列表
def remove_useless(u_file_list, folder_path):
    all_file_list = os.listdir(folder_path)
    for afl in all_file_list:
        for fl in u_file_list:
            if fl in afl:
                file_path_temp = os.path.join(folder_path,afl)
                os.remove(file_path_temp)
                continue


# 传入paras，将段落元素分解成成句的元素
def paras2sentences(paras):
    sentences_dict = enumerate(paras)
    sentences = paras.copy()
    list01 = []
    list02 = []
    for i, t in sentences_dict:
        c0 = t.count('。')
        if c0 > 1:
            x = t.split('。')
            if len(x[-1]) < 2:
                x[-2] = x[-2] + x[-1]
                x.pop()
            list01.append(i)  # 存储索引
            list02.append(x)

    ki = len(list01) - 1
    while ki >= 0:
        i = list01[ki]
        list_temp = list02[ki]
        del sentences[i]
        list_temp.reverse()
        for le in list_temp:
            sentences.insert(i, le)
        ki -= 1
    return sentences



# 将列表存储为txt格式文件
def list2txt(list, path):
    file = open(path, 'w', encoding="utf-8")
    for l in list:
        l = str(l)  # 强制转换
        if l[-1] != '\n':
            l = l + '\n'
        file.write(l)
    file.close()
    print(f"{path}文件存储成功")


# 将列表转换为字符串
def list2str(list):
    # 带回车
    string = ''
    for l in list:
        l = str(l)  # 强制转换
        if l[-1] != '\n':
            l = l + '\n'
        string += l
    return string


# 批量将docx文件批量转换为txt文件（按句子分开）
def txt2(docx_path):
    file_list = os.listdir(docx_path)
    for fl in file_list:
        if not os.path.splitext(fl)[1] == ".docx":  # 筛选文件类型,注意”.“
            continue
        docx_path_temp = os.path.join(docx_path, fl)
        paras = read_docx(docx_path_temp)
        sentences = paras2sentences(paras)
        txt_path_temp = os.path.join(txt_path, os.path.splitext(fl)[0] + '.txt')
        f = open(txt_path_temp, "wt", encoding='utf-8')
        for ss in sentences:
            f.writelines(ss + '\n')
        f.close()


# 批量将docx文件批量转换为1个txt文件（按句子分开）
def txt21(docx_path):
    txt_path_temp = "C:\\Users\\songwannian\\Desktop\\txt.txt"
    f = open(txt_path_temp, "wt", encoding='utf-8')
    file_list = os.listdir(docx_path)
    for fl in file_list:
        if not os.path.splitext(fl)[1] == ".docx":  # 筛选文件类型,注意”.“
            continue
        docx_path_temp = os.path.join(docx_path, fl)
        paras = read_docx(docx_path_temp)
        sentences = paras2sentences(paras)
        for ss in sentences:
            f.writelines(ss + '\n')
    f.close()


if __name__ == "__main__":
    folder_path0 = r"E:\Document\Z盗窃罪"  # doc文件所在文件夹路径

    # 创建存储docx文件的文件夹(若存在则不创建）
    # docx_path = os.docxPath.join(folder_path0, 'docx')
    docx_path = r"E:\Document\盗伐林木罪\待处理"
    # create_folder(docx_path)
    # 创建存储json文件的文件夹
    # json_path = os.docxPath.join(folder_path0, 'json')
    # create_folder(json_path)
    # 创建存储txt文件的文件夹
    # txt_path = os.docxPath.join(folder_path0, 'txt')
    # create_folder(txt_path)
    # # 显示转换用时
    time_start = time.time()
    # 转换
    doc2docx_p(folder_path0)
    # 初步检查无效文件
    # u_list = useless_list(docx_path)
    # print("\n无效文件有：")
    # for ui in u_list:
    #     print(ui)
    # 删除docx中无效文件
    # remove_useless(u_list, os.docxPath.join(docx_path)
    # 查看乱码文件
    # m_list = messy_code_list(docx_path)
    # nk = 0
    # for ui in m_list:
    #     nk += 1
    #     print(ui)
    # print(nk)
    # # 删除无用文件
    # remove_useless(m_list, os.docxPath.join(docx_path))

    time_end = time.time()
    print('\ntotally time cost:', time_end - time_start)  # 用时

