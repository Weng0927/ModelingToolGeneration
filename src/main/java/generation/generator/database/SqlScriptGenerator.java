package generation.generator.database;

import generation.models.TableColumn;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SqlScriptGenerator {
    private final String titleEn;

    private final String titleZh;

    private final ArrayList<TableColumn> tableColumns;
    public SqlScriptGenerator(String titleEn, String titleZh, ArrayList<TableColumn> tableColumns) {
        this.titleEn = titleEn;
        this.tableColumns = tableColumns;
        this.titleZh = titleZh;
        // 生成 SQL 脚本
        String script = generateSqlScript();
        // 将 SQL 脚本写入文件
        writeScriptToFile(script);
    }

    private String generateSqlScript() {
        System.out.println("开始生成建 " + titleZh + " 的 SQL 脚本......");
        StringBuilder script = new StringBuilder("CREATE TABLE " + titleEn + " (\n");
        // 添加 id 列
        script.append("  id INT NOT NULL AUTO_INCREMENT,\n");

        // 遍历 tableColumns 列表，生成每一列的定义
        for (TableColumn column : tableColumns) {
            script.append("  ").append(column.dataIndex).append(" ").append(column.type).append(" ").append("NOT NULL");
            script.append(",");
            script.append("\n");
        }

        script.append("  PRIMARY KEY (id)\n");
        script.append(");");
        System.out.println("建 " + titleZh + " SQL 脚本生成完毕");
        return script.toString();
    }

    private void writeScriptToFile(String script) {
        String directoryName = "./output/sql";
        File directory = new File(directoryName);

        if (!directory.exists()) {
            System.out.println("目录 " + directoryName + " 不存在，开始创建目录......");
            if(directory.mkdirs())
                System.out.println("创建目录 " + directoryName + " 成功");
        }

        System.out.println("开始将 建 " + titleZh + " SQL 脚本写入文件......");
        String fileName = "./output/sql/"+ titleEn + ".sql";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(script);
            System.out.println("SQL 脚本已经被写入 " + fileName + " 文件中");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
