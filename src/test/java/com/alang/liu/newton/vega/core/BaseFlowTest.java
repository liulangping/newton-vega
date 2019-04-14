package com.alang.liu.newton.vega.core;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static org.springframework.util.ObjectUtils.isEmpty;

/**
 * @author liulangping
 * @email langping.liu@gmail.com
 * @date 2019-04-13 10:52
 */
@Slf4j
public class BaseFlowTest {
    private BaseFlow flow;
    private static List<Line> Lines = Lists.newArrayList();
    private static int l = 0;

    private HashMap<Integer, Set<Line>> leavlEndLines = Maps.newHashMap();
//    private Set<Line> ines = Maps.newHashMap();


    @Before
    public void initMap() {
        flow = new BaseFlow() {
            @Override
            public void configFlowMap() {
                this.start("A").next("B").next("c")
                        .switchNode("B").next("d").next("i")
                        .switchNode("c").next("f").next("h").next("j")
                        .switchNode("c").next("g").next("h").next("i")
                ;
            }
        };
        flow.initFlowMap();

    }


    @Test
    public void plantNode() {


        Map<String, Set<String>> flowMap = flow.getNormalFlowMap();
        Set<Map.Entry<String, Set<String>>> entries = flowMap.entrySet();

        write_start();


        String startNode = flow.getStartNode();

        parseMap(startNode);

        write_stop();

        $echoLines();


    }

    public void parseMap(String node) {

        Set<String> set = flow.getNormalFlowMap().get(node);
        if (isEmpty(set)) {
            write_node(node);
        } else if (set.size() == 1) {
            write_node(node);
            String nextNode = set.iterator().next();
            parseMap(nextNode);
        } else if (set.size() > 1) {
            write_node(node);
            l++;
            write_split();
            Iterator<String> iterator = set.iterator();
            Line lastLine = null;
            while (iterator.hasNext()) {
                String next = iterator.next();
                parseMap(next);
                if (iterator.hasNext()) {
//                    recodLastLine();
                    write_split_again();
                }
            }
//            recodLastLine();
//            lastLine = deleLastLine();
            write_end_split();
//            if (!Objects.isNull(lastLine)) {
//                write_node(lastLine.getOut());
//            }
            l--;
        }


    }


    public void write_start() {
        Lines.add(new Line(0, Type.START));
    }

    public void write_stop() {
        Lines.add(new Line(0, Type.STOP));
    }

    public void write_split() {
        Lines.add(new Line(l, Type.SPLIT));
    }

    public void write_split_again() {
        Lines.add(new Line(l, Type.SPLIT_AGAIN));
    }

    public void write_end_split() {
        Lines.add(new Line(l, Type.END_SPLIT));
    }

    public void write_node(String node) {
        Lines.add(new Line(l, Type.NODE, node));
    }

    public void $echoLines() {
        Lines.stream().forEach(line -> {
            switch (line.getType()) {
                case START:
                    System.out.println("@startuml");
                    System.out.println("start");
                    break;
                case STOP:
                    System.out.println("stop");
                    System.out.println("@enduml");
                    break;
                case NODE:
                    System.out.println(getTab(line.getLevel()) + ":(" + line.getLevel() + ")-" + line.getOut() + ";");
                    break;
                case SPLIT:
                    System.out.println(getTab(line.getLevel()) + "split");
                    break;
                case SPLIT_AGAIN:
                    System.out.println(getTab(line.getLevel()) + "split again");
                    break;
                case END_SPLIT:
                    System.out.println(getTab(line.getLevel()) + "end split");
                    break;
                default:
                    System.out.println("error");
            }

        });
    }

    public String getTab(int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append("\t");
        }
        return sb.toString();
    }

    @Data
    @RequiredArgsConstructor
    @AllArgsConstructor
    @ToString
    class Line {
        @NonNull
        int level;//层次

        @NonNull
        Type type;

        String out = "";


    }

    /**
     * line 类型
     */
    enum Type {
        START, STOP, NODE, SPLIT, SPLIT_AGAIN, END_SPLIT;

    }

    public void log(String pattern, Object... arguments) {
        String str = String.format(pattern, arguments);
        System.out.println(str);

    }

    public void recodLastLine() {
        Line lastLine = Lines.get(Lines.size() - 1);
        if (!Objects.isNull(lastLine) && lastLine.getOut() != null) {
            System.out.println("lastLine" + lastLine);
            Set<Line> lines = leavlEndLines.get(lastLine.getLevel());
            if (isEmpty(lines)) {
                lines = Sets.newHashSet();
            }
            lines.add(lastLine);
            leavlEndLines.put(lastLine.getLevel(), lines);
        }
    }


    public Line deleLastLine() {
        Line lastLine = Lines.get(Lines.size() - 1);
        if (!Objects.isNull(lastLine) && lastLine.getOut() != null) {
            System.out.println("lastLine" + lastLine);
            Set<Line> lines = leavlEndLines.get(lastLine.getLevel());
            if (isNotEmpty(lines) && lines.contains(lastLine)) {
                List<Line> newLines = Lists.newArrayList();
                for (int i = 0; i < Lines.size(); i++) {
                    Line line = Lines.get(i);
                    if (!line.getOut().equals(lastLine.getOut())) {
                        newLines.add(line);
                    }
                }
                Lines = newLines;

            }
        }
        return lastLine;

    }

    public boolean isNotEmpty(Set<Line> lines) {
        return !CollectionUtils.isEmpty(lines);
    }


}