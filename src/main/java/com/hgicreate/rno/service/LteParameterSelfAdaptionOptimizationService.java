package com.hgicreate.rno.service;

import com.hgicreate.rno.service.dto.LteParameterSelfAdaptionOptimizationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class LteParameterSelfAdaptionOptimizationService {

    /**
     * @param districtName     小区名
     * @param cellIdLowerLimit 小区Id 的开始值
     * @param cellName         查询指定的小区名
     * @return
     */
    public List<LteParameterSelfAdaptionOptimizationDTO> queryAllTargetCells(String districtName, int cellIdLowerLimit, String cellName) {
        List<LteParameterSelfAdaptionOptimizationDTO> list = new ArrayList<>();
        if (cellName.length() > 0) {
            return getStaticCellInfo(districtName, cellName);
        } else {
            List<LteParameterSelfAdaptionOptimizationDTO> staticList = getStaticCellInfo(districtName, cellName);
            list.addAll(staticList);
            Random random = new Random();
            String[] baseChinese = getDistrictRoadName(districtName);
            String[] relativeDirection = {"东", "南", "西", "北", "中"};
            String baseUperAlphabet = "QWERTYUIOPASDFGHJKLZXCVBNM";
            String baseNumber = "1234567890";
            for (String roadName : baseChinese) {
                for (String relPosition : relativeDirection) {
                    int randomId = 0;
                    int randomPreId = 0;
                    boolean checkID = true;
                    while (checkID) {
                        // 循环检查ID是否重复
                        randomPreId = random.nextInt(2000) + cellIdLowerLimit;
                        if (list.size() == 0) {
                            checkID = false;
                            randomId = randomPreId;
                        } else if (list.size() > 0) {
                            int eqCount = 0;
                            for (LteParameterSelfAdaptionOptimizationDTO aList : list) {
                                if (randomPreId == Integer.parseInt(aList.getCellId())) {
                                    eqCount += 1;
                                }
                            }
                            if (eqCount == 0) {
                                randomId = randomPreId;
                                break;
                            }
                        }
                    }
                    // 拼凑小区后面的英文数字串
                    StringBuilder englishName = new StringBuilder();
                    englishName.append(baseUperAlphabet.charAt(new Random().nextInt(baseUperAlphabet.length())));
                    englishName.append("-");
                    englishName.append(baseUperAlphabet.charAt(new Random().nextInt(baseUperAlphabet.length())));
                    englishName.append(baseUperAlphabet.charAt(new Random().nextInt(baseUperAlphabet.length())));
                    englishName.append(baseUperAlphabet.charAt(new Random().nextInt(baseUperAlphabet.length())));
                    englishName.append("-");
                    englishName.append(baseNumber.charAt(new Random().nextInt(baseNumber.length())));
                    englishName.append(baseNumber.charAt(new Random().nextInt(baseNumber.length())));
                    englishName.append(baseNumber.charAt(new Random().nextInt(baseNumber.length())));

                    float anotherHalf = (float) Math.random() - 0.5f;
                    if (anotherHalf < 0) {
                        anotherHalf = 0f;
                    }
                    float radioAccessRate = (float) (98 + Math.random() + anotherHalf);
                    float erabSetUpSuccessRate = (float) (98 + Math.random() + anotherHalf);
                    float rrcConnectionSetUpSuccessRate = (float) (98 + Math.random() + anotherHalf);

                    float radioDropRate = (float) (0.5f + Math.random() + anotherHalf);
                    if ((radioAccessRate + radioDropRate) < 100) {
                        float gap = 100 - radioAccessRate - radioDropRate;
                        radioAccessRate += gap * 0.5f;
                        radioDropRate += gap * 0.5f;
                    }

                    int radioDropCount = (int) ((random.nextInt(15000) + 70000) * radioDropRate / 100);

                    float erabDropRate = (float) (0.5f + Math.random() + anotherHalf);
                    int switchRequestCount = random.nextInt(2000) + 3000;

                    float switchSuccessRate = (float) (98 + Math.random() + anotherHalf);
                    int switchSuccessCount = (int) (switchRequestCount * switchSuccessRate / 100);
                    switchSuccessRate = (new BigDecimal(100f * switchSuccessCount / switchRequestCount).setScale(2, BigDecimal.ROUND_HALF_UP)).floatValue();

                    String cellPriority = random.nextInt(2) == 1 ? "小区重选优先级已从7级调整为" + (random.nextInt(4) + 3) + "级" : "";
                    String cellChangeSwitchDifficulty = random.nextInt(2) == 1 ? "小区切换难易度已从1级调整到" + (random.nextInt(2) + 2) + "级" : "";
                    String decreaseHighStressCellRechooseDelay = random.nextInt(2) == 1 ? (random.nextInt(2) == 1 ? "降低高负荷小区的重选迟滞" : "升高低负荷小区重选迟滞") : "";
                    String decreaseHighStressCellFrequencyGapFrequencyOffset = random.nextInt(2) == 1 ? "降低高负荷小区频间频率偏移" : "";
                    if (cellPriority.length() == 0 && cellChangeSwitchDifficulty.length() == 0 && decreaseHighStressCellRechooseDelay.length() == 0 && decreaseHighStressCellFrequencyGapFrequencyOffset.length() == 0) {
                        decreaseHighStressCellFrequencyGapFrequencyOffset = "降低高负荷小区频间频率偏移";
                    }
                    list.add(new LteParameterSelfAdaptionOptimizationDTO(randomId + "", districtName + roadName + relPosition + englishName,
                            String.format("%.2f", radioAccessRate) + "", String.format("%.2f", erabSetUpSuccessRate) + "",
                            String.format("%.2f", rrcConnectionSetUpSuccessRate) + "", String.format("%.2f", radioDropRate) + "",
                            radioDropCount + "", String.format("%.2f", erabDropRate) + "", switchRequestCount + "",
                            switchSuccessCount + "", switchSuccessRate + "", "1", cellPriority + "",
                            cellChangeSwitchDifficulty + "", decreaseHighStressCellRechooseDelay + "",
                            decreaseHighStressCellFrequencyGapFrequencyOffset + ""));
                }
            }
        }
        return list;
    }

    /**
     * @param districtName
     * @return
     */
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

    private List<LteParameterSelfAdaptionOptimizationDTO> getStaticCellInfo(String districtName, String cellName) {
        List<LteParameterSelfAdaptionOptimizationDTO> list = new ArrayList<>();
        if ("广州荔湾区".equals(districtName)) {
            if (cellName.length() == 0) {
                list.add(new LteParameterSelfAdaptionOptimizationDTO(31001 + "", districtName + "翠圆小区A-NKI-112",
                        "98.77", "98.73",
                        "98.65", "1.33",
                        "1064", "0.85", "4305",
                        "4214", "97.89", "1", "小区重选优先级已从7级调整为5级",
                        "小区切换难易度已从1级调整到3级", "降低高负荷小区的重选迟滞",
                        "降低高负荷小区频间频率偏移"));
                list.add(new LteParameterSelfAdaptionOptimizationDTO(31002 + "", districtName + "金宇B-SMS-122",
                        "99.27", "98.73",
                        "98.65", "1.63",
                        "1386", "0.86", "3886",
                        "3835", "98.69", "1", "小区重选优先级已从7级调整为6级",
                        "小区切换难易度已从1级调整到2级", "降低高负荷小区的重选迟滞",
                        "降低高负荷小区频间频率偏移"));
                list.add(new LteParameterSelfAdaptionOptimizationDTO(31003 + "", districtName + "新东小学Z-ERS-212",
                        "99.47", "98.93",
                        "98.95", "0.79",
                        "656", "0.89", "4414",
                        "4369", "98.99", "1", "小区重选优先级已从7级调整为4级",
                        "小区切换难易度已从1级调整到3级", "",
                        "降低高负荷小区频间频率偏移"));
            } else {
                if (cellName.endsWith("A-NKI-112")) {
                    list.add(new LteParameterSelfAdaptionOptimizationDTO(31001 + "", districtName + "翠圆小区A-NKI-112",
                            "98.77", "98.73",
                            "98.65", "1.33",
                            "1064", "0.85", "4305",
                            "4214", "97.89", "1", "小区重选优先级已从7级调整为5级",
                            "小区切换难易度已从1级调整到3级", "降低高负荷小区的重选迟滞",
                            "降低高负荷小区频间频率偏移"));
                }
                if (cellName.endsWith("B-SMS-122")) {
                    list.add(new LteParameterSelfAdaptionOptimizationDTO(31002 + "", districtName + "金宇B-SMS-122",
                            "99.27", "98.73",
                            "98.65", "1.63",
                            "1386", "0.86", "3886",
                            "3835", "98.69", "1", "小区重选优先级已从7级调整为6级",
                            "小区切换难易度已从1级调整到2级", "降低高负荷小区的重选迟滞",
                            "降低高负荷小区频间频率偏移"));
                }
                if (cellName.endsWith("Z-ERS-212")) {
                    list.add(new LteParameterSelfAdaptionOptimizationDTO(31003 + "", districtName + "新东小学Z-ERS-212",
                            "99.47", "98.93",
                            "98.95", "0.79",
                            "656", "0.89", "4414",
                            "4369", "98.99", "1", "小区重选优先级已从7级调整为4级",
                            "小区切换难易度已从1级调整到3级", "",
                            "降低高负荷小区频间频率偏移"));
                }
            }

        } else if ("广州越秀区".equals(districtName)) {
            if (cellName.length() == 0) {
                list.add(new LteParameterSelfAdaptionOptimizationDTO(31004 + "", districtName + "淘金北小区T-AJM-412",
                        "98.74", "98.94",
                        "98.64", "1.44",
                        "1224", "1.45", "4631",
                        "4582", "98.95", "1", "小区重选优先级已从7级调整为4级",
                        "小区切换难易度已从1级调整到2级", "",
                        "升高高负荷小区频间频率偏移"));
                list.add(new LteParameterSelfAdaptionOptimizationDTO(31005 + "", districtName + "如意大楼R-SSR-243",
                        "98.75", "99.35",
                        "98.93", "1.65",
                        "1435", "1.15", "3956",
                        "3917", "99.01", "1", "",
                        "", "降低高负荷小区的重选迟滞",
                        "降低高负荷小区频间频率偏移"));
                list.add(new LteParameterSelfAdaptionOptimizationDTO(31006 + "", districtName + "花旗银行H-SBC-721",
                        "99.49", "98.66",
                        "98.63", "0.69",
                        "590", "1.23", "4730",
                        "4682", "98.99", "1", "小区重选优先级已从7级调整为4级",
                        "", "",
                        "降低高负荷小区频间频率偏移"));
            } else {
                if (cellName.endsWith("T-AJM-412")) {
                    list.add(new LteParameterSelfAdaptionOptimizationDTO(31004 + "", districtName + "淘金北小区T-AJM-412",
                            "98.74", "98.94",
                            "98.64", "1.44",
                            "1224", "1.45", "4631",
                            "4582", "98.95", "1", "小区重选优先级已从7级调整为4级",
                            "小区切换难易度已从1级调整到2级", "",
                            "升高高负荷小区频间频率偏移"));
                }
                if (cellName.endsWith("R-SSR-243")) {
                    list.add(new LteParameterSelfAdaptionOptimizationDTO(31005 + "", districtName + "如意大楼R-SSR-243",
                            "98.75", "99.35",
                            "98.93", "1.65",
                            "1435", "1.15", "3956",
                            "3917", "99.01", "1", "",
                            "", "降低高负荷小区的重选迟滞",
                            "降低高负荷小区频间频率偏移"));
                }
                if (cellName.endsWith("H-SBC-721")) {
                    list.add(new LteParameterSelfAdaptionOptimizationDTO(31006 + "", districtName + "花旗银行H-SBC-721",
                            "99.49", "98.66",
                            "98.63", "0.69",
                            "590", "1.23", "4730",
                            "4682", "98.99", "1", "小区重选优先级已从7级调整为4级",
                            "", "",
                            "降低高负荷小区频间频率偏移"));
                }
            }

        } else if ("广州天河区".equals(districtName)) {
            if (cellName.length() == 0) {
                list.add(new LteParameterSelfAdaptionOptimizationDTO(31007 + "", districtName + "江景豪庭北J-BCS-412",
                        "98.44", "98.66",
                        "99.24", "1.74",
                        "1504", "1.33", "4221",
                        "4177", "98.95", "1", "",
                        "小区切换难易度已从1级调整到2级", "升高高负荷小区的重选迟滞",
                        ""));
                list.add(new LteParameterSelfAdaptionOptimizationDTO(31008 + "", districtName + "程介坑C-KMS-852",
                        "98.75", "98.95",
                        "98.93", "1.65",
                        "1478", "1.85", "3652",
                        "3616", "99.03", "1", "",
                        "", "降低高负荷小区的重选迟滞",
                        "降低高负荷小区频间频率偏移"));
                list.add(new LteParameterSelfAdaptionOptimizationDTO(31009 + "", districtName + "鸿发大楼南H-FBC-474",
                        "98.69", "98.46",
                        "98.64", "1.61",
                        "1440", "1.39", "4813",
                        "4761", "98.92", "1", "小区重选优先级已从5级调整为6级",
                        "小区切换难易度已从2级调整到1级", "",
                        "降低高负荷小区频间频率偏移"));
            } else {
                if (cellName.endsWith("J-BCS-412")) {
                    list.add(new LteParameterSelfAdaptionOptimizationDTO(31007 + "", districtName + "江景豪庭北J-BCS-412",
                            "98.44", "98.66",
                            "99.24", "1.74",
                            "1504", "1.33", "4221",
                            "4177", "98.95", "1", "",
                            "小区切换难易度已从1级调整到2级", "升高高负荷小区的重选迟滞",
                            ""));
                }
                if (cellName.endsWith("C-KMS-852")) {
                    list.add(new LteParameterSelfAdaptionOptimizationDTO(31008 + "", districtName + "程介坑C-KMS-852",
                            "98.75", "98.95",
                            "98.93", "1.65",
                            "1478", "1.85", "3652",
                            "3616", "99.03", "1", "",
                            "", "降低高负荷小区的重选迟滞",
                            "降低高负荷小区频间频率偏移"));
                }
                if (cellName.endsWith("H-FBC-474")) {
                    list.add(new LteParameterSelfAdaptionOptimizationDTO(31009 + "", districtName + "鸿发大楼南H-FBC-474",
                            "98.69", "98.46",
                            "98.64", "1.61",
                            "1440", "1.39", "4813",
                            "4761", "98.92", "1", "小区重选优先级已从5级调整为6级",
                            "小区切换难易度已从2级调整到1级", "",
                            "降低高负荷小区频间频率偏移"));
                }
            }
        }
        return list;
    }
}
