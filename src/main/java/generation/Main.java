package generation;

import com.fasterxml.jackson.databind.ObjectMapper;
import generation.generator.database.SqlScriptGenerator;
import generation.generator.table.TablePageGenerator;
import generation.generator.tree.TreeGenerator;
import generation.models.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            System.out.println("----------------------------");
            System.out.println("读取定义模板的JSON字符串");
            String jsonString = readInputFile();
            System.out.println("读取JSON字符串成功!");
            System.out.println("----------------------------");

            // 解析 JSON 字符串为 ModelingToolTemplate 对象
            System.out.println("解析JSON字符串为ModelingToolTemplate对象");
            ModelingToolTemplate modelingToolTemplate = objectMapper.readValue(jsonString, ModelingToolTemplate.class);
            System.out.println("解析JSON字符串成功!");
            System.out.println("----------------------------");

            // 表生成
            tableGenerator(modelingToolTemplate.reqTemplates);
            // 树形目录生成
            TreeData.addTreeData(modelingToolTemplate.reqTemplates, "");
            new TreeGenerator();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static String readInputFile() throws Exception {
        // 使用 NIO 读取文件内容
        Path path = Paths.get("input.json");
        byte[] bytes = Files.readAllBytes(path);
        return new String(bytes);
    }

    private static void tableGenerator(ArrayList<ReqTemplate> reqTemplates) {
        for (ReqTemplate reqTemplate : reqTemplates) {
            Requirement req = reqTemplate.requirement;
            // 若为简单需求
            if (req.type == ReqType.SIMPLE) {
                generateSimpleReq(req);
                System.out.println("----------------------------");
            }

            // 若为复合需求
            else if (req.type == ReqType.COMPLEX) {
                // 生成子需求
                tableGenerator(req.reqTemplates);
                System.out.println("----------------------------");
            }
        }
    }

    private static void generateSimpleReq(Requirement req) {
        // 生成SQL脚本
        new SqlScriptGenerator(req.titleEn, req.titleZh, req.table);
        // 生成前端表单页面
        new TablePageGenerator(req.titleEn, req.titleZh, req.table);
    }
}