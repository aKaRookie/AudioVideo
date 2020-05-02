package  com.fhl.test;

import android.util.LruCache;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @ProjectName: MyKotlinApplication
 * @Package: com.example.mykotlin
 * @ClassName: SortTest
 * @Description: java类作用描述
 * @Author: fenghl
 * @CreateDate: 2020/4/24 11:02
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/4/24 11:02
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class SortTest {
    int[] mArray = new int[]{43, 10, 30, 20, 90, 6, 80, 60, 54, 77, 25, 44, 0, 6, 3, 1, 44};
    private int bucketCount;

    @Test
    public void testBucket() {
        int[] arr = Arrays.copyOf(mArray, mArray.length);
        ArrayList<Integer> list = new ArrayList<>(mArray.length);
        for (int i = 0; i < arr.length; i++) {
            list.add(arr[i]);
        }
        ArrayList<Integer> result = bucketSort(list, 2);
        System.out.println("排序后:" + result);
        for (int i = 0; i < result.size(); i++) {
            System.out.println(result.get(i).intValue());
        }
    }

    ArrayList<Integer> bucketSort(ArrayList<Integer> array, int buckSize) {
        if (array == null || array.size() < 2) return null;
        int max = array.get(0);
        int min = array.get(0);

        for (int i = 0; i < array.size(); i++) {
            if (max < array.get(i)) {
                max = array.get(i);
            }
            if (min > array.get(i)) {
                min = array.get(i);
            }
        }
        ArrayList<Integer> resultArr = new ArrayList<>();
        //桶数
        bucketCount = (max - min) / buckSize + 1;
        ArrayList<ArrayList<Integer>> bucketArr = new ArrayList<>(bucketCount);
        for (int i = 0; i < bucketCount; i++) {
            bucketArr.add(new ArrayList<Integer>());
        }
        //将每个元素放入桶
        for (int i = 0; i < array.size(); i++) {
            int num = (array.get(i) - min) / buckSize;
            bucketArr.get(num).add(array.get(i));
        }
//        对每个桶进行排序
        for (int i = 0; i < bucketCount; i++) {
            if (buckSize == 1) {
                for (int j = 0; j < bucketArr.get(i).size(); i++) {
                    resultArr.add(bucketArr.get(i).get(j));
                }
            } else {
                if (bucketCount == 1) {
                    buckSize--;
                }
                ArrayList<Integer> temp = bucketSort(bucketArr.get(i), buckSize);
                for (int j = 0; j < temp.size(); j++) {
                    resultArr.add(temp.get(i));
                }
            }

        }
        return resultArr;
    }

    @Test
    public void test() {
        int[] arr = Arrays.copyOf(mArray, mArray.length);

        for (int i = 0; i < arr.length; i++) {
            int minIndex = i;
            for (int j = i; j < arr.length; j++) {
                if (arr[minIndex] > arr[j]) {
                    minIndex = j;
                }
            }
            int tmp = arr[i];
            arr[i] = arr[minIndex];
            arr[minIndex] = tmp;
        }
        System.out.println("排序:" + Arrays.toString(arr));
    }

    //单链表
    class Node {
        Node next;
        int value;

        Node(int value) {
            this.value = value;
        }
    }


    public Node createNode() {
        Node node1 = new Node(1);
        Node node2 = new Node(2);
        Node node3 = new Node(3);
        Node node4 = new Node(4);
        Node node5 = new Node(5);
        node1.next = node2;
        node2.next = node3;
        node3.next = node4;
        node4.next = node5;
        Node head = node1;
        while (head != null) {
            System.out.println(head.value);
            head = head.next;
        }
        System.out.println("-----------------------------");
        return node1;
    }

    @Test
    public void testReverse() {
        Node cur = createNode();
        Node reverse = reverse(cur);
        while (reverse != null) {
            System.out.print(reverse.value);
            reverse = reverse.next;
        }
    }

    Node reverse(Node node) {
        if (node == null) {
            return null;
        }
        Node pre = null;
        Node cur = node;
        Node tmp = null;
        while (cur != null) {
            tmp = cur.next;
            cur.next = pre;
            pre = cur;
            cur = tmp;
        }
        return pre;
    }

}
