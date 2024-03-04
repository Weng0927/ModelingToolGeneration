package generation.models;

import java.util.ArrayList;

public class Requirement {
    // 需求类型
    public ReqType type;

    // 英文名称
    public String titleEn;

    // 中文标题
    public String titleZh;
    public ArrayList<TableColumn> table;
    public ArrayList<ReqTemplate> reqTemplates;

    public Requirement(ArrayList<ReqTemplate> reqTemplates) {
        type = ReqType.COMPLEX;
        titleEn = "";
        titleZh = "";
        this.reqTemplates = reqTemplates;
    }

    // 不可删除，解析JSON字符串时需要默认构造函数
    public Requirement() {

    }
}
