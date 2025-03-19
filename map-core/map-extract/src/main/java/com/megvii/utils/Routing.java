package com.megvii.utils;

import lombok.Data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class Routing{
    public static void main(String[] args) {
        Road root1 = new Road();
        root1.setUid("1");
        Road root2 = new Road();
        root2.setUid("2");
        Road root3 = new Road();
        root3.setUid("3");
        Road root4 = new Road();
        root4.setUid("4");
        Road root5 = new Road();
        root5.setUid("5");
        Road root6 = new Road();
        root6.setUid("6");
        Road root7 = new Road();
        root7.setUid("7");
        Road root8 = new Road();
        root8.setUid("8");

        root1.setSucList(List.of(root2));
        root2.setSucList(List.of(root3,root4,root5));
        root3.setSucList(List.of(root5));
        root5.setSucList(List.of(root6));
        root6.setSucList(List.of(root7));
        root7.setSucList(List.of(root8));

        Search search = new Search();
        search.setTagPath("8");
        search.searchPath(root1,search.getPathList());
    }
}
@Data
class Search {
    private List<String> pathList=new ArrayList<>();
    private String tagPath;

    public void printPathList() {
        StringBuilder sb = new StringBuilder();
        pathList.stream().limit(pathList.size() - 1).forEach(path -> sb.append(path).append("->"));
        System.out.println(sb.append(pathList.get(pathList.size() - 1)));
    }

    public void searchPath(Road root, List<String> pathList) {
        if (!root.getUid().equals(tagPath) && root.getSucList()==null) {
            pathList.remove(root.getUid());
            return;
        }
        pathList.add(root.getUid());
        if (root.getUid().equals(tagPath)){
            printPathList();
            pathList.remove(root.getUid());
            return;
        }
        for (Road road : root.getSucList()) {
            searchPath(road, pathList);
        }
        pathList.remove(root.getUid());
    }


}

@Data
class Road {
    private String uid;
    private List<Road> sucList;
}