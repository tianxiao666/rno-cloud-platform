package com.hgicreate.rno.service;

import com.hgicreate.rno.service.dto.LteCapicityOptimizationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class LteCapicityOptimizationService {

    public List<LteCapicityOptimizationDTO> queryAllCells(String districtName, int cellIdLowerLimit) {
        List<LteCapicityOptimizationDTO> list = new ArrayList<>();
        Random random = new Random();
        double problemCellRate = 0.2;
        String[] baseChinese = getDistrictRoadName(districtName);
        String[] relativePosition = {"东", "南", "西", "北", "中"};
        String baseUperEnglish = "QWERTYUIOPASDFGHJKLZXCVBNM";
        String baseNumber = "1234567890";

        for (String roadName : baseChinese) {
            for (String relPosition : relativePosition) {
                int randomID = 0;
                int randomPreId = 0;
                boolean checkID = true;
                while (checkID) {
                    //循环检查ID是否重复
                    randomPreId = random.nextInt(2000) + cellIdLowerLimit;
                    if (list.size() == 0) {
                        checkID = false;
                        randomID = randomPreId;
                    } else if (list.size() > 0) {
                        int eqCount = 0;
                        for (LteCapicityOptimizationDTO aList : list) {
                            if (randomPreId == Integer.parseInt(aList.getCellId())) {
                                eqCount += 1;
                            }
                        }
                        if (eqCount == 0) {
                            checkID = false;
                            randomID = randomPreId;
                            break;
                        }
                    }
                }
                String subName = "";
                subName += roadName;
                String subNameCell = "";
                subNameCell += baseUperEnglish.charAt(new Random().nextInt(baseUperEnglish.length())) + "";
                subNameCell += "-";
                subNameCell += baseUperEnglish.charAt(new Random().nextInt(baseUperEnglish.length())) + "";
                subNameCell += baseUperEnglish.charAt(new Random().nextInt(baseUperEnglish.length())) + "";
                subNameCell += baseUperEnglish.charAt(new Random().nextInt(baseUperEnglish.length())) + "";
                subNameCell += "-";
                subNameCell += baseNumber.charAt(new Random().nextInt(baseNumber.length())) + "";
                subNameCell += baseNumber.charAt(new Random().nextInt(baseNumber.length())) + "";
                subNameCell += baseNumber.charAt(new Random().nextInt(baseNumber.length())) + "";

                double randomAdviceRate = Math.random();
                int randomAdvice = 0;
                int randomUpRate = 0;
                int randomDownRate = 0;
                int randomUpFlow = 0;
                int randomDownFlow = 0;
                //小区有问题概率
                double randomUpProblemRate = 0.5;
                int[] randomOperation = new int[8];
                if (randomAdviceRate < problemCellRate) {
                    //有问题
                    randomAdvice = 1;
                    double randomProblemOnUpOrDown = Math.random();
                    if (randomProblemOnUpOrDown < randomUpProblemRate) {
                        randomUpRate = 100;
                        randomDownRate = random.nextInt(80);
                        randomUpFlow = random.nextInt(2) + 4;
                        randomDownFlow = random.nextInt(2) + 1;
                    } else {
                        randomUpRate = random.nextInt(80);
                        randomDownRate = 100;
                        randomUpFlow = random.nextInt(2) + 1;
                        randomDownFlow = random.nextInt(2) + 4;
                    }
                    for (int i = 0; i < randomOperation.length; i++) {
                        randomOperation[i] = random.nextInt(2);
                    }
                    int operationCount = 0;
                    for (int one : randomOperation) {
                        if (one == 0) {
                            operationCount += 1;
                        }
                    }
                    if (operationCount == randomOperation.length) {
                        randomAdvice = 0;
                    }

                } else if (randomAdviceRate > problemCellRate) {
                    //无问题
                    randomAdvice = 0;
                    randomUpRate = random.nextInt(70);
                    randomDownRate = random.nextInt(70);
                    randomUpFlow = random.nextInt(2) + 1;
                    randomDownFlow = random.nextInt(2) + 1;
                    for (int i = 0; i < randomOperation.length; i++) {
                        randomOperation[i] = 0;
                    }
                }
                String isVipStation = "";
                if (Math.random() > 0.5) {
                    isVipStation += "是";
                } else {
                    isVipStation += "否";
                }
                int randomRRC = random.nextInt(40) + 40;
                list.add(new LteCapicityOptimizationDTO(randomID + "", districtName + subName + relPosition + subNameCell, randomAdvice + "", randomRRC + "",
                        randomUpRate + "", randomDownRate + "", randomUpFlow + "", randomDownFlow + "", randomAdvice + "",
                        "ABC", random.nextInt(100000) + "", String.format("%.4f",random.nextInt(2)+112+Math.random())+"",
                        String.format("%.4f",random.nextInt(2)+22+Math.random())+ "",
                        "40", "90", "5", "4G", "20", "10", "indoor",
                        "10", "5", "400", "3", isVipStation + "", "123",
                        randomRRC + "", "20", randomUpRate + "", randomDownRate + "", "10", "ABC-C1",
                        "2017-11-17", "100", "是", "roof",
                        randomOperation[0] + "", randomOperation[1] + "", randomOperation[2] + "", randomOperation[3] + "",
                        randomOperation[4] + "", randomOperation[5] + "", randomOperation[6] + "", randomOperation[7] + ""));
            }
        }
        return list;
    }

    private String[] getDistrictRoadName(String districtName) {
        String[] resultRoads = {};
        if ("广州荔湾区".equals(districtName)) {
            resultRoads = new String[]{"海龙路", "龙溪西路", "东漖镇文昌路", "凤西二路", "龙溪大道", "缤纷大道", "兰花街", "艺林西街", "映翠街", "物流三路",
                    "物流六路", "采芳街", "索芳路西", "观光路", "花绿路", "海中北路", "海中八路", "观海路", "天嘉大道",
                    "翠园道", "水秀二路", "洪石坊", "水口东一路", "增南路", "百合路", "红棉路", "白兰路", "水仙路",
                    "西埠路", "西约一路", "棉高路", "棉盛路", "地塘三路", "大地塘路", "海南一路", "花博大道", "高岗路",
                    "东塱大街", "开拓路", "创业路", "坑口村", "怡康苑", "中海花湾国际", "鹤洞路小区", "坑口工业区", "芳和花园",
                    "艺和广场", "茶滘路", "花地人家商业中心", "花地城广场", "1850创意园", "兰兴苑", "古桥茶街", "石围塘",
                    "新世界逸彩庭园", "富力环市西苑"};
        }
        if ("广州越秀区".equals(districtName)) {
            resultRoads = new String[]{"一德路", "海珠广场", "北京路", "团一大广场", "东湖", "东山口", "动物园", "杨箕", "三育路", "御东和府",
                    "执信南路小区", "中山大学北校区", "广东省人民医院", "东华西新街小区", "大东文化社区", "元运街大院", "东方文德广场", "光明广场", "广州公社旧址",
                    "庆福里社区", "大新中社区", "怡乐里社区", "将军东社区", "广州外贸大楼", "流花湖公园", "越秀公园", "广州火车站", "华美苑小区",
                    "明德创业园", "麓湖", "田心村", "黄花岗", "黄花岗公园", "二沙岛", "东湖社区", "元岗村小区", "培正中学",
                    "万科金色家园", "建设横马路大院", "东风大酒店", "芳草街小区", "黄华北社区", "中环广场", "麓苑阁", "麓湖阁", "广东电视中心",
                    "小北", "农讲所"};
        }
        if ("广州海珠区".equals(districtName)) {
            resultRoads = new String[]{"洲咀大院2号大院", "天誉半岛", "南华西社区", "万科华庭", "敬和里", "君华天汇", "兰亭御园", "供电滨江大院", "海幢寺", "宝岗大道",
                    "滨江1933", "同庆社区", "永龙社区", "海运社区", "太平东约围闭式小区", "朗晴居二期", "中海名都", "大元帅府纪念馆", "华标涛景湾",
                    "沙地直街小区", "远安社区", "仲恺农业工程学院海珠小区", "广州城市职业学院滨江校区", "海印公园", "海珠半岛花园", "蓝色康园", "宜利苑", "珠江广场",
                    "丽景湾", "中海锦苑", "广州电视台", "广州塔", "珠江帝景", "琶洲安置新社区", "丽景花苑", "电力设计院宿舍", "畔江花园",
                    "国际会展中心", "琶洲塔", "琶洲新村", "万胜围", "雅郡花园", "沥滘", "世家公关别墅区", "绿地滨江汇", "石溪渡口",
                    "石岗路大院", "石岗花园"};
        }
        if ("广州天河区".equals(districtName)) {
            resultRoads = new String[]{"华穗路", "金穗路", "华夏路", "华强路", "天荣路", "天强路", "广利路", "天河路", "悦新街", "天尚街",
                    "亿泉街", "天河直街", "天河北路", "萌横路", "泰兴路", "泰昌街", "林和西路", "林和西横路", "紫荆路",
                    "林和中路", "林乐路", "林和东路", "天寿路", "沾溢直街", "瘦狗岭路", "禺东西路", "兴华路", "燕岭路",
                    "粤垦路", "侨燕街", "侨源大街", "金坤街", "燕都路", "银定塘前街", "燕富路", "科韵路", "燕塘路",
                    "马蹄岗大街", "陶庄路", "东燕街", "东莞庄路", "伟逸街", "长江南路", "长福路", "紫薇西路", "粤汉路",
                    "华山路", "黄河路", "黄河西路", "长江路", "洪泽路", "巢湖路", "岳洲路", "大丰一街", "大丰路",
                    "华南东侧路", "科韵路", "岑村东街、岑村新南街、行云街", "红花岗西街", "沐陂西街", "沐陂西路",
                    "沐陂南路", "沐陂中路、沐陂大街", "沐陂东路", "合景路", "新塘大街", "大观南路"};
        }
        if ("广州白云区".equals(districtName)) {
            resultRoads = new String[]{"飞翔公园", "沙北", "横沙", "浔峰岗", "白云公园", "白云文化广场", "萧岗", "江夏", "黄村", "嘉禾望岗",
                    " 白云大道北", "永泰", "同和", "京溪南方医院", "龙归", "人和", "白云神山车站", "东岭口", "大白田",
                    "陇北", "金土地", "岗厦", "江村", "罗岗村", "南湖高尔夫", "鹤嘴", "连鱼山", "摩天岭",
                    "帽峰山森林公园", "观音山脚", "高塘", "仲恺农业工程学院", "钟兴广场", "下果园", "车庄", "落水湖", "大布",
                    "大尖咀", "九田", "水口岭", "冷水坑"};
        }
        if ("广州黄埔区".equals(districtName)) {
            resultRoads = new String[]{"知识城", "菠萝岭", "过龙岭", "马头庄", "枫下", "旺村", "康大", "广州商学院", "镇龙", "金坑",
                    " 长平", "水西", "暹岗", "金峰", "黄陂", "萝岗", "香雪", "鱼珠", "大沙地",
                    "文冲", "裕丰围", "南海神庙", "夏园", "南岗", "丹水坑风景旅游区", "龙头山森林公园", "莲潭村", "广州义务植树公园",
                    "萝岗香雪公园", "甘竹山", "禾丰新村", "万科山景城", "南华高尔夫俱乐部", "金坑森林公园", "大山背", "上东坑", "天麓湖森林公园",
                    "将军山", "大田山", "黄埔客运站"};
        }
        if ("广州番禺区".equals(districtName)) {
            resultRoads = new String[]{"洛溪", "南浦", "会江", "广州南站", "石壁", "谢村", "钟村", "汉溪长隆", "南村万博", "员岗",
                    " 板桥", "大学城科学广场", "大学城城市公园", "大学城体育中心", "大学城华南理工大学", "大学城圣堂山公园", "草堂立交", "莲花山旅游区", "新造",
                    "暨南大学南校区", "石基", "东村", "仁德公园", "小龙村", "三级岗", "君子岗", "南门岗", "官侨村",
                    "岳溪村", "灵兴工业园", "赤山东村", "石二村", "卫星村", "明星村", "东星村", "群星村", "联围村",
                    "宇丰村", "头围", "东盛", "九屯围", "三德", "观龙"};
        }
        if ("广州花都区".equals(districtName)) {
            resultRoads = new String[]{"广州白云机场", "清布", "莲塘村", "马鞍山公园", "花都广场", "花都区卫生局", "花果山公园", "花城路", "广州北站", "花都汽车城",
                    " 飞鹅岭", "九龙湖度假区", "大龙村卫生站", "万大广场", "港头村", "吉星村", "保良村", "大塘村", "大岭山",
                    "大桥头", "大窝岭", "东湖", "长岗", "东边村", "白水窝", "东方村", "复兴", "大王岭",
                    "厚岭", "邦和庄", "大湖", "将军湖", "横北", "巴岭", "大虎坑", "东边岭", "白石",
                    "百夫田", "冯村", "军田", "官田", "联合村", "陈屋"};
        }
        if ("广州南沙区".equals(districtName)) {
            resultRoads = new String[]{"东涌", "庆盛", "黄阁汽车城", "黄阁", "蕉门", "金洲", "游船码头", "南沙湿地公园", "玫瑰园", "工程村",
                    " 民兴", "民建", "九涌", "红湖村", "立新", "新三围", "成安围", "广兴围", "年丰村",
                    "大元村", "大岗沥", "安生围", "才份", "东隆", "蕉门村", "飞云顶", "九王庙村", "大山",
                    "鹿山", "东丫", "北流村", "二村", "大生涌", "大马", "长联", "克沙", "掂口",
                    "留东村", "大三沙"};
        }
        if ("广州从化区".equals(districtName)) {
            resultRoads = new String[]{"红荔新村", "江埔", "锦绣花园", "荔枝花园", "向阳村", "市政公寓", "流溪小学", "北帝古庙", "从化中学", "东宇胶带厂",
                    "富城路", "宝城路", "职教路", "新庄埔", "湖南塘", "白水塘", "岭尾", "新屋", "上围",
                    "老围", "大路脚", "林寨", "黄竹田", "新开洞", "新三", "水尾", "暖水", "响水",
                    "山腰", "下村", "车步村", "瓦埔", "低村", "大油", "新止", "川龙", "山下",
                    "上路", "湖屋"};
        }
        if ("广州增城区".equals(districtName)) {
            resultRoads = new String[]{"塘头", "横岭", "郑田", "仙塘", "顾屋", "基岗", "麻车", "修屋", "石头村", "街心村",
                    "明星村", "金星村", "陆村", "尾岗", "城丰村", "金竹岗", "莲塘村", "陈桥头", "对田",
                    "马屋", "汤屋", "三联村", "光明村", "增城广场", "荔枝文化公园", "东汇城", "增城公园", "夏街村",
                    "挂绿广场", "光明车站", "泰富广场", "人人乐", "城西市场", "大1978文化创意园", "增城体育馆", "郑中钧中学", "高级中学",
                    "荔城中学", "东湖", "礁石岭"};
        }
        return resultRoads;
    }
}
