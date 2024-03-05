package generation.models;

import generation.generator.tree.TreeGenerator;

import java.util.ArrayList;
import java.util.UUID;

public class TreeData {
    public String key;

    public Requirement requirement;

    public ArrayList<TreeData> reqChildren;

    public ArrayList<TreeData> tableChildren;

    public TreeData(Requirement requirement, String key) {
        this.key = key;
        this.requirement = requirement;

        // 若为复合要求，则需要将这一级的子表单和子复合要求提取出来
        if (requirement.type == ReqType.COMPLEX) {
            reqChildren = new ArrayList<>();
            tableChildren = new ArrayList<>();
        }
    }

    /**
     * 递归添加树形目录数据, 将需求模板转换为树形目录数据
     * @param reqTemplates 需求模板
     * @param father 父节点的key
     */
    public static void addTreeData(ArrayList<ReqTemplate> reqTemplates, String father) {
        for (int index = 0; index < reqTemplates.size(); index++) {
            Requirement req = reqTemplates.get(index).requirement;
            UUID uuid = UUID.randomUUID();
            TreeData node = new TreeData(req, uuid.toString());
            addTreeData(node, father);

            if (req.type == ReqType.COMPLEX) {
                addTreeData(req.reqTemplates, uuid.toString());
            }
        }
    }

    private static void addTreeData(TreeData treeData, String father) {
        Requirement req = treeData.requirement;
        TreeData root = getTreeDataByKeyFromRoot(father);
        assert root != null;
        if (req.type == ReqType.COMPLEX) {
            root.reqChildren.add(treeData);
        } else if (req.type == ReqType.SIMPLE) {
            root.tableChildren.add(treeData);
        }
    }

    public static TreeData getTreeDataByKeyFromRoot(String key) {
        TreeData root = TreeGenerator.treeData;
        if (root.key.equals(key)) {
            return root;
        }

        for (TreeData treeData : root.reqChildren) {
            TreeData val = treeData.getTreeDataByKeyFromChildren(key);
            if (val != null) {
                return val;
            }
        }

        for (TreeData treeData : root.tableChildren) {
            if (treeData.key.equals(key)) {
                return treeData;
            }
        }

        System.out.println("ERROR: 找不到 key = " + key + " 的TreeData");
        return null;
    }

    private TreeData getTreeDataByKeyFromChildren(String key) {
        if (this.key.equals(key)) {
            return this;
        }

        for (TreeData treeData : reqChildren) {
            if (treeData.key.equals(key)) {
                return treeData;
            } else {
                TreeData val = treeData.getTreeDataByKeyFromChildren(key);
                if (val != null) {
                    return val;
                }
            }
        }

        for (TreeData treeData : tableChildren) {
            if (treeData.key.equals(key)) {
                return treeData;
            }
        }

        return null;
    }
}
