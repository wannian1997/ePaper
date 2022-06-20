import re


def minDistance(word1, word2):
    """编辑距离"""
    if not word1:
        return len(word2 or '') or 0
    if not word2:
        return len(word1 or '') or 0
    size1 = len(word1)
    size2 = len(word2)
    last = 0
    # tmp = range(size2 + 1) # 原代码，有点问题
    tmp = list()
    for i in range(size2 + 1):
        tmp.append(i)
    value = None
    for i in range(size1):
        tmp[0] = i + 1
        last = i
        # print word1[i], last, tmp
        for j in range(size2):
            if word1[i] == word2[j]:
                value = last
            else:
                value = 1 + min(last, tmp[j], tmp[j + 1])
                # print(last, tmp[j], tmp[j + 1], value)
            last = tmp[j + 1]
            tmp[j + 1] = value
        # print tmp
    return value


def number_C2E(ChineseNumber):
    """中文数字转整形"""
    map = dict(〇=0, 零=0, 一=1, 二=2, 三=3, 四=4, 五=5, 六=6, 七=7, 八=8, 九=9, 十=10)
    bit_map = dict(十=10, 百=100, 千=1000)
    bit_map_w = dict(万=10000, 亿=100000000)
    size = len(ChineseNumber)
    if size == 0: return 0
    if size < 2:
        return map[ChineseNumber]

    if '亿' in ChineseNumber:
        numbers = ChineseNumber.split('亿')
        print(numbers)
        left = number_C2E(numbers[0])*bit_map_w['亿']
        right = number_C2E(numbers[1])
        print(left)
        print(right)
        return left + right
    ans = 0
    continue_flag = False  # 连续进两个的标志位
    for i in range(size):
        if continue_flag:
            continue_flag = False
            continue
        if ChineseNumber[i] in bit_map_w.keys():
            ans = ans * bit_map_w[ChineseNumber[i]]
            continue_flag = True
            continue
        if i + 1 < size:
            if ChineseNumber[i + 1] in bit_map.keys():
                ans += map[ChineseNumber[i]] * bit_map[ChineseNumber[i + 1]]
                continue_flag = True
                continue
        ans += map[ChineseNumber[i]]
    return ans


def ChineseDate2Date(ChineseDate):
    map = dict(〇=0, 一=1, 二=2, 三=3, 四=4, 五=5, 六=6, 七=7, 八=8, 九=9)
    r = re.search(r'(.*)年(.*)月(.*)日', ChineseDate)
    year = r.group(1)
    month = r.group(2)
    day = r.group(3)
    for s, n in map.items():
        year = year.replace(s, str(n))
    month = number_C2E(month)  # 中文转整型
    day = number_C2E(day)

    # 整型转字符串
    if month < 10:
        month = "0" + str(month)
    else:
        month = str(month)
    if day < 10:
        day = "0" + str(day)
    else:
        day = str(day)
    return year + '-' + month + '-' + day