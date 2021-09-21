package edu.kamshanski.sortgarbagerussia;

import java.util.Map;
import java.util.TreeMap;

public class Test {

    public void po() {
        solve(new int[][]{
                {1, 5   },
                {3, 8   },
                {4, 5   },
                {10, 13 },
                {15, 17 }
        });
    }

    public int solve(int[][] intervals) {
        if (intervals.length == 0) {
            return 0;
        } else if (intervals.length == 1) {
            return intervals[0][1] - intervals[0][0] + 1;
        }

        TreeMap<Integer, Integer> map = new TreeMap<>();

        for (int[] interval: intervals) {
            Integer open = map.get(interval[0]);
            if (open == null) {
                open = 1;
            } else {
                open += 1;
            }
            map.put(interval[0], open);

            Integer close = map.get(interval[1]);
            if (close == null) {
                close = -1;
            } else {
                close -= 1;
            }
            map.put(interval[1], close);
        }

        int start = 0;
        int end = 0;
        int counter = 0;
        boolean inside = false;

        int[] mergedInterval = new int[] {0,0};

        for (Map.Entry<Integer, Integer> point : map.entrySet()) {
            counter += point.getValue();
            if (counter > 0) {
                if (!inside) {
                    start = point.getKey();
                }
                inside = true;
            } else {
                if (inside) {
                    end = point.getKey();
                }
                inside = false;
                if (mergedInterval[1] - mergedInterval[0] < end - start) {
                    mergedInterval[0] = start;
                    mergedInterval[1] = end;
                }
            }
        }

        return mergedInterval[1] - mergedInterval[0] + 1;
    }
}
