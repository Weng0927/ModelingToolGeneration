package generation.generator.tree;

import generation.models.ReqType;
import generation.models.Requirement;
import generation.models.TreeData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class TreeGenerator {
    public static TreeData treeData = new TreeData(new Requirement(new ArrayList<>()), "");

    public TreeGenerator() {
        String treeString = generateReactTreeNode();
        writeTreeStringToFile(treeString);
    }

    /**
     * 生成树形目录组件
     */
    private String generateReactTreeNode() {
        ArrayList<TreeData> reqNodes = treeData.reqChildren;
        StringBuilder treeNodeString = new StringBuilder("const treeData: TreeDataNode[] = [\n");
        for (TreeData node : reqNodes) {
            Requirement req = node.requirement;
            if (req.type == ReqType.COMPLEX) {
                UUID uuid = UUID.randomUUID();
                generateTreeDataObject(treeNodeString, uuid.toString(), node, req);
                for (int sonIndex = 0; sonIndex < node.reqChildren.size(); sonIndex++) {
                    UUID uuidSon = UUID.randomUUID();
                    String child = generateReactTreeNode(node.reqChildren.get(sonIndex), uuidSon.toString());
                    treeNodeString.append(child);
                }
                treeNodeString.append("],\n");
                treeNodeString.append("},\n");
            }
        }
        treeNodeString.append("]\n");
        return treeNodeString.toString();
    }

    /**
     * 生成树形目录组件
     * @param fatherNode 父节点
     * @param key 父节点的key,用于组件内部唯一区分
     */
    private String generateReactTreeNode(TreeData fatherNode, String key) {
        StringBuilder treeNodeString = new StringBuilder();
        ArrayList<TreeData> reqNodes = fatherNode.reqChildren;
        generateTreeDataObject(treeNodeString, key, fatherNode, fatherNode.requirement);

        for (TreeData node : reqNodes) {
            Requirement req = node.requirement;
            if (req.type == ReqType.COMPLEX) {
                UUID uuid = UUID.randomUUID();
                generateTreeDataObject(treeNodeString, uuid.toString(), node, req);
                  for(TreeData sonNode : node.reqChildren) {
                    UUID uuidSon = UUID.randomUUID();
                    generateReactTreeNode(sonNode, uuidSon.toString());
                }
                treeNodeString.append("],\n");
                treeNodeString.append("},\n");
            }
        }
        treeNodeString.append("],\n");
        treeNodeString.append("},\n");
        return treeNodeString.toString();
    }

    /**
     * 生成树形目录组件某节点主体
     * @param treeNodeString 用于存储树形目录组件的字符串
     * @param key 当前节点的key,用于组件内部唯一区分
     * @param node 当前节点
     * @param req 当前节点对应的需求
     */
    private void generateTreeDataObject(StringBuilder treeNodeString, String key, TreeData node, Requirement req) {
        treeNodeString.append("{\n");
        treeNodeString.append("key: \"").append(key).append("\",\n");
        treeNodeString.append("title: (<Dropdown menu={{ items: ").
                append(generateItemsForBranch(node.tableChildren)).
                append("}} trigger={[\"contextMenu\"]}>\n");
        treeNodeString.append("<span>").append(req.titleZh).append("</span>\n");
        treeNodeString.append("</Dropdown>\n),\n");
        treeNodeString.append("children: [\n");
    }

    /**
     * 生成分支节点右键菜单可操作选项
     * @param tables 当前分支节点下包含的子表单数组
     */
    private String generateItemsForBranch(ArrayList<TreeData> tables) {
        StringBuilder itemsString = new StringBuilder("""
                [
                  {
                    key: "1",
                    label: "新建表",
                    type: "group",
                    children: [""");
        for(int index = 0; index < tables.size(); index++) {
            Requirement req = tables.get(index).requirement;
            itemsString.append("{\n");
            itemsString.append("key: ").append("\"1-").append(index).append("\",\n");
            itemsString.append("label: \"").append(req.titleZh).append("\",\n");
            itemsString.append("},\n");
        }
        itemsString.append("]\n}\n]\n");
        return itemsString.toString();
    }

    /**
     * 将树形目录组件写入文件
     * @param treeString 树形目录组件字符串
     */
    private void writeTreeStringToFile(String treeString) {
        String directoryName = "./output/pages/tree";
        File directory = new File(directoryName);

        if (!directory.exists()) {
            System.out.println("目录 " + directoryName + " 不存在，开始创建目录......");
            if(directory.mkdirs())
                System.out.println("创建目录 " + directoryName + " 成功");
        }

        System.out.println("开始将 树形目录组件 写入文件......");
        String fileName = "./output/pages/tree/index.tsx";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(treeString);
            System.out.println("树形目录组件已经被写入 " + fileName + " 文件中");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * 生成叶子节点右键菜单可操作选项
     */
    private String generateItemsForLeaf() {
        return """
                [
                  {
                    key: "1",
                    label: (
                      <Popconfirm
                        title="确定删除该表吗？"
                        description="警告：删除不可逆！"
                        okText="是"
                        cancelText="否"
                      >
                        删除表单
                      </Popconfirm>
                    ),
                  },
                ]
                """;
    }

}
