package generation.generator.table;

import generation.models.DataType;
import generation.models.TableColumn;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
public class TablePageGenerator {
    // 表英文名
    private final String titleEn;

    // 表中文名
    private final String titleZh;
    private final ArrayList<TableColumn> tableColumns;

    public TablePageGenerator(String titleEn, String titleZh, ArrayList<TableColumn> tableColumns) {
        this.titleEn = titleEn;
        this.titleZh = titleZh;
        this.tableColumns = tableColumns;
        String pageStr = generateTablePage();
        writePageToFile(pageStr);
    }

    private String generateTablePage() {
        System.out.println("开始生成 " + titleZh + " 的 前端表单页面......");
        StringBuilder page = new StringBuilder(importSentence());
        System.out.println(" - 添加表单组件依赖成功");
        // 添加表单类型定义
        addDataType(page);
        System.out.println(" - 添加表单数据类型定义成功");

        // 添加表单组件表头定义
        addColumns(page);
        System.out.println(" - 添加表单组件表头定义成功");

        // 添加表单组件定义
        addReactTable(page);
        System.out.println(" - 添加表单组件定义成功");
        System.out.println("生成 " + titleZh + " 的 前端表单页面成功");
        return page.toString();
    }

    private void writePageToFile(String page) {
        String directoryName = "./output/pages/"+ titleEn;
        File directory = new File(directoryName);

        if (!directory.exists()) {
            System.out.println("目录 " + directoryName + " 不存在，开始创建目录......");
            if(directory.mkdirs())
                System.out.println("创建目录 " + directoryName + " 成功");
        }

        System.out.println("开始将 " + titleZh + " 的 前端表单页面写入文件......");
        String fileName =  directoryName + "/index.tsx";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(page);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("将 " + titleZh + " 的 前端表单页面写入文件成功");
    }

    private String importSentence() {
        return """
                import { FC } from "react";
                import { Table, Button, Space, Breadcrumb } from "antd";
                import type { TableProps } from "antd";
                
                """;
    }

    private void addDataType(StringBuilder page) {
        page.append("interface DataType {\n");
        for(TableColumn column : tableColumns) {
            page.append("  ").append(column.dataIndex).append(": ").append(changeDataType(column.type)).append(";\n");
        }
        page.append("}\n\n");
    }

    private void addColumns(StringBuilder page) {
        // 添加表单组件表头定义
        page.append("const columns: TableProps<DataType>[\"columns\"] = [\n");
        for(TableColumn column : tableColumns) {
            page.append("  { title: \"").append(column.titleZh).append("\",\n dataIndex: \"").append(column.dataIndex).append("\"\n },\n");
        }
        page.append(opColumn());
        page.append("];\n\n");
    }

    private String opColumn() {
        return """
                {
                  title: "操作",
                  render: () => (
                    <Space className="table-operation">
                      <Button type="primary">编辑</Button>
                      <Button danger>删除</Button>
                    </Space>
                  ),
                },
                """;
    }

    private void addReactTable(StringBuilder page) {
        String componentName = titleEn.substring(0, 1).toUpperCase() + titleEn.substring(1);
        page.append("const ").append(componentName).append(": FC = () => {\n");
        page.append("  return (\n");
        page.append("    <>\n");
        page.append(breadcrumb());
        page.append(makeTableJSX());
        page.append("</>\n");
        page.append("  );\n");
        page.append("};\n\n");
        page.append("export default ").append(componentName).append(";\n");
    }

    private String breadcrumb() {
        return """
              <div className="breadcrumb">
                <Breadcrumb
                  items={[
                    { title: "Home" },
                    { title: "requirement" },
                    { title: "modeling" },
                  ]}
                ></Breadcrumb>
              </div>
               """;
    }

    private String makeTableJSX() {
        return "<Table\n" +
               "className=\"table\"\n" +
                "columns={columns}\n" +
                "dataSource={data}\n" +
                "bordered\n" +
                "title={() => <div style={{ fontWeight: 600 }}>"+ titleZh +"</div>}\n" +
                "/>\n";
    }

    private String changeDataType(DataType type) {
        return switch (type) {
            case TEXT -> "string";
            case INT -> "number";
        };
    }
}
